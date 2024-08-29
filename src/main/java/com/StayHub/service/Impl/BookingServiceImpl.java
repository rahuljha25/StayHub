package com.StayHub.service.Impl;

import com.StayHub.controller.BookingController;
import com.StayHub.entity.Booking;
import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import com.StayHub.exception.BookingException;
import com.StayHub.exception.RoomException;
import com.StayHub.payload.BookingDto;
import com.StayHub.repository.BookingRepository;
import com.StayHub.repository.PropertyRepository;
import com.StayHub.repository.RoomRepository;
import com.StayHub.service.BookingService;
import com.StayHub.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;
    private PDFService pdfService;
    private RoomService roomService;
    private BucketService bucketService;
    private PropertyRepository propertyRepository;
    private TwilioService twilioService;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository, PDFService pdfService, RoomService roomService, BucketService bucketService, PropertyRepository propertyRepository, TwilioService twilioService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.pdfService = pdfService;
        this.roomService = roomService;
        this.bucketService = bucketService;
        this.propertyRepository = propertyRepository;
        this.twilioService = twilioService;
    }
    @Override
    public BookingDto createBooking(long roomId, User user, BookingDto dto) {
        // Retrieve room by ID, throw exception if not found
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new RoomException("Room not found with id: " + roomId)
        );

        LocalDate checkIn = dto.getCheckIn();
        LocalDate checkOut = dto.getCheckOut();

        // Validate that the check-out date is after the check-in date
        if (!checkOut.isAfter(checkIn)) {
            throw new BookingException("Check-out date must be after the check-in date.");
        }

        // Check if the room is available for the entire range of dates
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            Boolean isAvailable = room.getAvailability().get(date);
            if (isAvailable == null || !isAvailable) {
                throw new IllegalStateException("Room is not available on " + date);
            }
        }

        // Fetch the list of active bookings for the room
        List<Booking> bookings = bookingRepository.findByRoomAndStatus(room, "BOOKED");

        // Check for booking conflicts with existing reservations
        for (Booking existingBooking : bookings) {
            if (checkIn.isBefore(existingBooking.getCheckOut()) && checkOut.isAfter(existingBooking.getCheckIn())) {
                throw new BookingException("Room is not available for the selected dates. Your payment will be refunded.");
            }
        }

        // Update the availability map to reflect the new booking
        Map<LocalDate, Boolean> availability = room.getAvailability();
        LocalDate currentDate = checkIn;
        while (!currentDate.isAfter(checkOut.minusDays(1))) {
            availability.put(currentDate, false); // Mark the date as unavailable
            currentDate = currentDate.plusDays(1);
        }
        room.setAvailability(availability);

        // Update the room status to unavailable if there are no available dates left
        boolean hasAvailableDates = availability.values().contains(true);
        room.setStatus(hasAvailableDates);

        // Set booking details in BookingDto
        dto.setRoom(room);
        dto.setUser(user);
        //calculate nights
        long night = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
        dto.setNights(night);
        Booking booking = dtoToEntity(dto);

        // Assign the booking to the room, property, and user
        room.getBookings().add(booking);
        room.getProperty().getBookings().add(booking);
        booking.setProperty(room.getProperty());
        user.getBookings().add(booking);

        // Calculate total price including tax
            BigDecimal nights = new BigDecimal(night);
            BigDecimal price = room.getPricePerNight().multiply(nights);
            BigDecimal gst = new BigDecimal("0.18"); // 18% GST
            BigDecimal taxAmount = price.multiply(gst);
            BigDecimal finalPrice = price.add(taxAmount);
            booking.setPrice(finalPrice);

        // Save the booking in the database
        Booking savedBooking = bookingRepository.save(booking);

        boolean b = pdfService.generatePdf("D://SK//" + "booking-confirmation-id" + savedBooking.getId() + ".pdf", dto);
        if (b) {
            try {
                MultipartFile file = BookingController.convert("D://SK//" + "booking-confirmation-id" + savedBooking.getId() + ".pdf");
                //upload the booking confirmation PDF to S3
                String uploadedFileUrl = bucketService.uploadFile(file, "sachin2002");
                System.out.println(uploadedFileUrl);
               // for whatsapp notification
                String whatsappId = twilioService.sendWhatsAppMessage(dto.getMobile(), "Your booking is confirmed..Click for more details: " + uploadedFileUrl);
                System.out.println(whatsappId);
                //for sms notification
                String smsId = twilioService.sendSms(dto.getMobile(), "Your booking is confirmed..Click for more details: " + uploadedFileUrl);
                System.out.println(smsId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Save the updated room with new availability status
        roomRepository.save(room);
        return entityToDto(savedBooking);

    }


    @Override
    public void cancelBooking(long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingException("Booking not found with id:" + bookingId)
        );
        if (booking.getCheckIn().isEqual(LocalDate.now())
        ||booking.getCheckIn().isBefore(LocalDate.now())){
            throw new BookingException("Booking can't be cancelled now!");
        }
        //remove booking from room
        Room room = booking.getRoom();
        List<Booking> roomBookings = room.getBookings();
        roomBookings.remove(booking);

        //remove booking from Property
        Property property = booking.getProperty();
        List<Booking> propertyBookings = property.getBookings();
        propertyBookings.remove(booking);

        //remove booking from user
        List<Booking> userBookings = user.getBookings();
        userBookings.remove(booking);
        //checking before deleting
        if (booking != null && bookingRepository.existsById(bookingId)) {
            // Delete the booking
            bookingRepository.delete(booking);
        } else {
            throw new BookingException("Booking does not exist or has already been deleted.");
        }

    }

    @Override
    public List<BookingDto> getAllBookings(User user, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable=null;
        if (sortDir.equalsIgnoreCase("asc")){
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        } else if (sortDir.equalsIgnoreCase("desc")) {
            pageable=PageRequest.of(pageNo,pageSize,Sort.by(sortBy).descending());
        }else {
            pageable= PageRequest.of(pageNo,pageSize);
        }

        Page<Booking> all = bookingRepository.findAll(pageable);
        List<Booking> content = all.getContent();
        List<BookingDto> collect = content.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<BookingDto> getAllBookingByPropertyId(User user, long propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new BookingException("Property not found with id: " + propertyId)
        );
        List<Booking> byProperty = bookingRepository.findByProperty(property);
        List<BookingDto> collect = byProperty.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(User user) {
        List<Booking> byUser = bookingRepository.findByUser(user);
        List<BookingDto> collect = byUser.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public BookingDto getBookingById(long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingException("Booking not found with this id: " + bookingId)
        );
        return entityToDto(booking);
    }


    //dto to entity
   private Booking dtoToEntity(BookingDto dto){
        Booking booking=new Booking();
        booking.setCheckIn(dto.getCheckIn());
        booking.setCheckOut(dto.getCheckOut());
        booking.setEmail(dto.getEmail());
        booking.setMobile(dto.getMobile());
        booking.setRoom(dto.getRoom());
        booking.setPrice(dto.getPrice());
        booking.setUser(dto.getUser());
        booking.setFirstName(dto.getFirstName());
        booking.setLastName(dto.getLastName());
        booking.setNoPersons(dto.getNoPersons());
        booking.setProperty(dto.getProperty());
        booking.setStatus("BOOKED");
        booking.setNights(dto.getNights());
        return booking;
    }
    //entity to Dto
   private BookingDto entityToDto(Booking booking){
        BookingDto dto1=new BookingDto();
        dto1.setId(booking.getId());
        dto1.setCheckIn(booking.getCheckIn());
        dto1.setCheckOut(booking.getCheckOut());
        dto1.setEmail(booking.getEmail());
        dto1.setMobile(booking.getMobile());
        dto1.setRoom(booking.getRoom());
        dto1.setPrice(booking.getPrice());
        dto1.setUser(booking.getUser());
        dto1.setFirstName(booking.getFirstName());
        dto1.setLastName(booking.getLastName());
        dto1.setNoPersons(booking.getNoPersons());
        dto1.setProperty(booking.getProperty());
        dto1.setStatus(booking.getStatus());
        dto1.setNights(booking.getNights());
        return dto1;
    }
}
