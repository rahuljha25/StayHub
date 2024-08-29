package com.StayHub.payload;

import com.StayHub.entity.Property;
import com.StayHub.entity.User;
import lombok.Data;

@Data
public class ReviewDto {

    private Long id;
    private Integer ratings;
    private String description;
    private User user;
    private Property property;
}
