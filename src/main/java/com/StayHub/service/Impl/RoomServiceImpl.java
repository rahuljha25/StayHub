package com.StayHub.service.Impl;

import com.StayHub.entity.Booking;
import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import com.StayHub.exception.PropertyException;
import com.StayHub.exception.RoomException;
import com.StayHub.payload.DateDto;
import com.StayHub.payload.RoomDto;
import com.StayHub.repository.BookingRepository;
import com.StayHub.repository.PropertyRepository;
import com.StayHub.repository.RoomRepository;
import com.StayHub.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;
    private PropertyRepository propertyRepository;
    private BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, PropertyRepository propertyRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
    }


    @Override
    public RoomDto addRooms(long propertyId, User user, RoomDto dto) throws RoomException {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                ()->new PropertyException("Property not found with id: "+propertyId)
        );

        boolean roomExists = roomRepository.existsByRoomNoAndPropertyId(dto.getRoomNo(), propertyId);
        if (roomExists){
            throw new RoomException("A room with room number " + dto.getRoomNo() + " already exists for this property.");
        }
        dto.setProperty(property);
        dto.setUser(user);
        Room room = dtoToEntity(dto);
        Room save = roomRepository.save(room);
        log.info("Room saved successfully");
        return entityToDTo(save);

    }

    @Override
    public List<RoomDto> getAllAvailableRoomsByPropertyId(long propertyId, DateDto dto, User user) {
        // Fetch the property by ID
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyException("Property not found with id: " + propertyId));

        // Fetch rooms with status = available
        List<Room> availableRooms = property.getRooms().stream()
                .filter(Room::getStatus) // Only consider rooms with a status of available
                .collect(Collectors.toList());

        LocalDate checkIn = dto.getCheckIn();
        LocalDate checkOut = dto.getCheckOut();

        // Filter rooms based on the availability in the date range
        List<Room> filteredRooms = new ArrayList<>();

        for (Room room : availableRooms) {
            // Check if room is available for the entire date range
            boolean isRoomAvailable = true;

            // Iterate over all dates in the range from checkIn to checkOut
            LocalDate currentDate = checkIn;
            while (!currentDate.isAfter(checkOut)) {
                Boolean isAvailable = room.getAvailability().get(currentDate);
                if (isAvailable == null || !isAvailable) {
                    isRoomAvailable = false;
                    break; // If any date is unavailable, stop checking
                }
                currentDate = currentDate.plusDays(1);
            }
            // If room is available for the entire range, add it to the list
            if (isRoomAvailable) {
                filteredRooms.add(room);
            }
        }

        // Convert the filtered rooms to RoomDto and return
        return filteredRooms.stream().map(this::entityToDTo).collect(Collectors.toList());
    }
    @Override
    public RoomDto getRoomById(long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new RoomException("Room not found with id:" + roomId)
        );
        return entityToDTo(room);
    }

    @Override
    public void deleteRoom(User user, long roomId) {
        roomRepository.deleteById(roomId);
    }

    @Override
    public List<RoomDto> getAllRooms(User user, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable=null;
        if (sortDir.equalsIgnoreCase("asc")){
             pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        } else if (sortDir.equalsIgnoreCase("desc")) {
            pageable=PageRequest.of(pageNo,pageSize,Sort.by(sortBy).descending());
        }else {
            pageable=PageRequest.of(pageNo,pageSize);
        }
        Page<Room> all = roomRepository.findAll(pageable);
        List<Room> content = all.getContent();
        List<RoomDto> collect = content.stream().map(e -> entityToDTo(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public RoomDto updateRoomDetails(User user,long propertyId, long roomId, RoomDto dto) {
        Room room=null;
        room = roomRepository.findById(roomId).orElseThrow(
                () -> new RoomException("Room not found with id: " + roomId)
        );
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new RoomException("Room not found with id:" + propertyId)
        );
        dto.setProperty(property);
        dto.setUser(user);

        room=dtoToEntity(dto);
        room.setId(roomId);

        Room save = roomRepository.save(room);
        return entityToDTo(save);
    }

    // @Scheduled(cron = "0 0 8 * * *") // Runs every day at 8 AM
    @Override
    public void updateRoomStatusAfterBookingEnds(long roomId, LocalDate checkIn, LocalDate checkOut) {
        // Retrieve room by ID
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new RoomException("Room not found with id: " + roomId)
        );

        // Get the current date
        LocalDate today = LocalDate.now();

        // Validate that fromDate is not in the past
        if (checkIn.isBefore(today)) {
            throw new IllegalArgumentException("Cannot update room availability for past dates.");
        }

        // Ensure checkIn is before checkOut
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        }

        // Check for existing bookings that overlap with the new date range
        List<Booking> existingBookings = bookingRepository.findByRoomAndStatus(room, "BOOKED");
        for (Booking booking : existingBookings) {
            // If the booking overlaps with the date range we are trying to mark as available, throw an exception
            if (checkIn.isBefore(booking.getCheckOut()) && checkOut.isAfter(booking.getCheckIn())) {
                throw new IllegalStateException("Cannot mark room available for the selected date range as it overlaps with an existing booking.");
            }
        }

        // Update the availability map to mark the room as available for the date range
        Map<LocalDate, Boolean> availability = room.getAvailability();
        LocalDate currentDate = checkIn;

        while (!currentDate.isAfter(checkOut.minusDays(1))) {
            availability.put(currentDate, true);  // Mark as available
            currentDate = currentDate.plusDays(1);
        }

        // Save the updated availability status
        room.setAvailability(availability);
        roomRepository.save(room);
    }
    //dto to entity
   private Room dtoToEntity(RoomDto dto){
        Room room=new Room();
        room.setRoomNo(dto.getRoomNo());
        room.setRoomType(dto.getRoomType());
        room.setRoomAmenities(dto.getRoomAmenities());
        room.setProperty(dto.getProperty());
        room.setUser(dto.getUser());
        room.setCurrency("INR");
        room.setBedTypes(dto.getBedTypes());
        room.setSleeps(dto.getSleeps());
        room.setNoBathrooms(dto.getNoBathrooms());
        room.setNoBedrooms(dto.getNoBedrooms());
        room.setPricePerNight(dto.getPricePerNight());
        room.setStatus(true);
        return room;
    }
    //entity to dto
    private RoomDto entityToDTo(Room room){
        RoomDto dto=new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNo(room.getRoomNo());
        dto.setRoomType(room.getRoomType());
        dto.setRoomAmenities(room.getRoomAmenities());
        dto.setProperty(room.getProperty());
        dto.setUser(room.getUser());
        dto.setCurrency(room.getCurrency());
        dto.setBedTypes(room.getBedTypes());
        dto.setSleeps(room.getSleeps());
        dto.setNoBathrooms(room.getNoBathrooms());
        dto.setNoBedrooms(room.getNoBedrooms());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setStatus(room.getStatus());
        return dto;

    }
}
