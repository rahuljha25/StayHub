package com.StayHub.payload;

import com.StayHub.entity.Address;
import com.StayHub.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class PropertyDto {
    private Long id;
    private String name;
    private String propertyType;
    private Integer units;
    private String legalName;
    private List<String> propertyAmenities;
    private List<String> propertyRules;
    private User user;
    private Address address;
}
