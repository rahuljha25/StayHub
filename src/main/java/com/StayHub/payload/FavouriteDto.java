package com.StayHub.payload;

import com.StayHub.entity.Property;
import com.StayHub.entity.User;
import lombok.Data;

@Data
public class FavouriteDto {

    private Long id;
    private Boolean status;
    private User user;
    private Property property;
}
