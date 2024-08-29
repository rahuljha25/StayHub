package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.BookingDto;
import com.StayHub.service.BookingService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create/booking")
    public ResponseEntity<BookingDto> createBooking(@RequestParam long roomId,
                                                    @AuthenticationPrincipal User user,
                                                    @RequestBody BookingDto dto){
        BookingDto createdBooking = bookingService.createBooking(roomId, user, dto);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    //for canceling booking
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable long bookingId,
                                                @AuthenticationPrincipal User user){
        bookingService.cancelBooking(bookingId,user);
        return new ResponseEntity<>("Booking Cancel",HttpStatus.ACCEPTED);

    }

    //for converting filePath into MultiPartFile
    public static MultipartFile convert(String filePath) throws IOException {
        // Load the file from the specified path
        File file = new File(filePath);

        // Read the file content into array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Convert byte array to a ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        // Create MultipartFile from Resource
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public String getOriginalFilename() {
                return file.getName();
            }

            @Override
            public String getContentType() {
                // You can determine and set the content type here
                try {
                    return Files.probeContentType(file.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isEmpty() {
                return file.length() == 0;
            }

            @Override
            public long getSize() {
                return file.length();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return fileContent;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(fileContent);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (InputStream inputStream = new ByteArrayInputStream(fileContent);
                     OutputStream outputStream = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        };

        return multipartFile;
    }

    @GetMapping("/get/bookings-property")
    public ResponseEntity<List<BookingDto>> getAllBookingByPropertyId(@AuthenticationPrincipal User user,
                                                                      @RequestParam long propertyId){
        List<BookingDto> allBookingByPropertyId = bookingService.getAllBookingByPropertyId(user, propertyId);
        return new ResponseEntity<>(allBookingByPropertyId,HttpStatus.OK);
    }

    @GetMapping("/get/allBookings")
    public ResponseEntity<List<BookingDto>> getAllBookings(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "pageNo",defaultValue = "0",required = false)int pageNo,
            @RequestParam(value = "pageSize",defaultValue = "5",required = false)int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "id",required = false)String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "id",required = false)String sortDir
    ){
        List<BookingDto> allBookings = bookingService.getAllBookings(user,pageNo,pageSize,sortBy,sortDir);
        if (sortDir.equalsIgnoreCase("asc")){
            PageRequest.of(pageNo,pageSize, Sort.by(sortBy).ascending());
        }
        return new ResponseEntity<>(allBookings,HttpStatus.OK);
    }

    @GetMapping("/get/bookings-user")
    public ResponseEntity<List<BookingDto>> getAllBookingsByUser(@AuthenticationPrincipal User user){
        List<BookingDto> allBookingsByUser = bookingService.getAllBookingsByUser(user);
        return new ResponseEntity<>(allBookingsByUser,HttpStatus.OK);
    }

    @GetMapping("/get/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable long bookingId,
                                                     @AuthenticationPrincipal User user){
        BookingDto bookingById = bookingService.getBookingById(bookingId, user);
        return new ResponseEntity<>(bookingById,HttpStatus.OK);
    }
}
