package com.StayHub.payload;

import com.StayHub.entity.User;
import lombok.Data;

@Data
public class AddressDto {

    private Long id;
    private String area;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private User user;
}
