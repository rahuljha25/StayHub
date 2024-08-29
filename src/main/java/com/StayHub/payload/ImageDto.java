package com.StayHub.payload;

import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import lombok.Data;

@Data
public class ImageDto {

    private Long id;
    private String imageUrl;
    private Property property;
    private User user;
    private Room room;
}
