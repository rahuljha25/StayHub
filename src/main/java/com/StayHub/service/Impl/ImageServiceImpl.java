package com.StayHub.service.Impl;

import com.StayHub.entity.Image;
import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import com.StayHub.exception.ImageException;
import com.StayHub.payload.ImageDto;
import com.StayHub.repository.ImageRepository;
import com.StayHub.repository.PropertyRepository;
import com.StayHub.repository.RoomRepository;
import com.StayHub.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {

    private ImageRepository imageRepository;
    private PropertyRepository propertyRepository;
    private BucketService bucketService;
    private RoomRepository roomRepository;

    public ImageServiceImpl(ImageRepository imageRepository, PropertyRepository propertyRepository, BucketService bucketService, RoomRepository roomRepository) {
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
        this.bucketService = bucketService;
        this.roomRepository = roomRepository;
    }

    @Override
    public ImageDto uploadPropertyImages(long propertyId, MultipartFile file, User user, String bucketName) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                ()->new ImageException("Property not found with id: "+propertyId)
        );

        // Extract the file name from the MultipartFile
        String fileName = file.getOriginalFilename();

        // Check if the image already exists for the property, user, and file name (partial match)
        boolean imageExists = imageRepository.findByPropertyAndImageUrlAndUser(property, fileName, user)
                .isPresent();

        // If the image already exists, throw an exception or return an error
        if (imageExists) {
            throw new ImageException("Image with the same file name already exists for this property.");
        }
        Image image=new Image();
        image.setProperty(property);
        image.setUser(user);
        String imageUrl = bucketService.uploadFile(file, bucketName);
        image.setImageUrl(imageUrl);

        Image save = imageRepository.save(image);
        return entityToDto(save);

    }

    @Override
    public ImageDto uploadRoomImages(long roomId, MultipartFile file, String bucketName, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ImageException("Room not found with id: " + roomId)
        );

        // Get the associated property from the room
        Property property = room.getProperty();

        // Extract the file name from the MultipartFile
        String fileName = file.getOriginalFilename();

        // Check if the image already exists for the room, user, and file name (partial match)
        boolean imageExists = imageRepository.findByRoomAndImageUrlAndUser(room, fileName, user)
                .isPresent();

        // If the image already exists, throw an exception or return an error
        if (imageExists) {
            throw new ImageException("Image with the same file name already exists for this room.");
        }

        Image image=new Image();
        image.setRoom(room);
        image.setUser(user);
        image.setProperty(property);
        String imageUrl = bucketService.uploadFile(file, bucketName);
        image.setImageUrl(imageUrl);

        Image save = imageRepository.save(image);
          return  entityToDto(save);
    }


    @Override
    public String deleteRoomImage(long roomId, String bucketName, String fileName, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ImageException("Room not found with id: " + roomId)
        );

        // Fetch the image by room, fileName, and user
        Image image = imageRepository.findByRoomAndImageUrlAndUser(room, fileName, user).orElseThrow(
                () -> new ImageException("Image not found for the given room and file name")
        );

        // Delete the file from the bucket
        String deletedFile = bucketService.deleteFile(bucketName, fileName);

        // Remove the image record from the database
        imageRepository.delete(image);
        return deletedFile;
    }

    @Override
    public String deletePropertyImage(long propertyId, String bucketName, String fileName, User user) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ImageException("Property not found with id: " + propertyId)
        );

        // Fetch the image by room, fileName, and user
        Image image = imageRepository.findByPropertyAndImageUrlAndUser(property, fileName, user).orElseThrow(
                () -> new ImageException("Image not found for the given property and file name")
        );

        // Delete the file from the bucket
        String deletedFile = bucketService.deleteFile(bucketName, fileName);

        // Remove the image record from the database
        imageRepository.delete(image);
        return deletedFile;
    }

    @Override
    public ImageDto updatePropertyImage(long propertyId, MultipartFile file, String bucketName, String fileName, User user) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ImageException("Property not found with id: " + propertyId)
        );

        Image image = imageRepository.findByPropertyAndImageUrlAndUser(property, fileName, user).orElseThrow(
                () -> new ImageException("Image not found for the given property and file name")
        );
        //update the image URL
        String updatedImage = bucketService.updateFile(file, bucketName, fileName);
        image.setImageUrl(updatedImage);
        image.setUser(user);

        Image save = imageRepository.save(image);
        return entityToDto(save);
    }

    @Override
    public ImageDto updateRoomImage(long roomId, MultipartFile file, String bucketName, String fileName, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ImageException("Room not found with id: " + roomId)
        );
        Image image = imageRepository.findByRoomAndImageUrlAndUser(room, fileName, user).orElseThrow(
                () -> new ImageException("Image not found for the given room and file name")
        );

        String updatedImage = bucketService.updateFile(file, bucketName, fileName);
        image.setImageUrl(updatedImage);
        image.setUser(user);

        Image save = imageRepository.save(image);
        return entityToDto(save);
    }

    //entity to dto
    private ImageDto entityToDto(Image image){
        ImageDto dto=new ImageDto();
        dto.setId(image.getId());
        dto.setProperty(image.getProperty());
        dto.setImageUrl(image.getImageUrl());
        dto.setRoom(image.getRoom());
        dto.setUser(image.getUser());
        return dto;
    }
}
