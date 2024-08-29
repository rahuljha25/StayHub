package com.StayHub.payload;

import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private BigDecimal price;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private Integer noPersons;
    private User user;
    private Property property;
    @JsonIgnore
    private Room room;
    private Long nights;
}
