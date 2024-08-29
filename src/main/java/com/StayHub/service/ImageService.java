package com.StayHub.service;

import com.StayHub.entity.User;
import com.StayHub.payload.ImageDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageDto uploadPropertyImages(long propertyId, MultipartFile file, User user, String bucketName);

    ImageDto uploadRoomImages(long roomId, MultipartFile file, String bucketName, User user);

    String deleteRoomImage(long roomId, String bucketName, String fileName, User user);

    String deletePropertyImage(long propertyId, String bucketName, String fileName, User user);

    ImageDto updatePropertyImage(long propertyId, MultipartFile file, String bucketName, String fileName, User user);

    ImageDto updateRoomImage(long roomId, MultipartFile file, String bucketName, String fileName, User user);
}
