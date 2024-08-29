package com.StayHub.payload;

import com.StayHub.entity.Property;
import com.StayHub.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomDto {
    private Long id;
    private Integer roomNo;
    private BigDecimal pricePerNight;
    private Integer noBedrooms;
    private Integer noBathrooms;
    private Integer sleeps;
    private String roomType;
    private List<String> bedTypes;
    private Boolean status;
    private List<String> roomAmenities;
    private String currency;
    private User user;
    private Property property;
}
