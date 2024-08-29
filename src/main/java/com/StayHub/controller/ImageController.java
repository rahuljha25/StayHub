package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.ImageDto;
import com.StayHub.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    @PostMapping(path = "/upload/file/{bucketName}/property/{propertyId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageDto> uploadPropertyImages(@PathVariable long propertyId,
                                                 @RequestParam MultipartFile file,
                                                 @AuthenticationPrincipal User user,
                                                 @PathVariable String bucketName){
        ImageDto uploadedImages = imageService.uploadPropertyImages(propertyId, file, user, bucketName);
        return new ResponseEntity<>(uploadedImages, HttpStatus.CREATED);
    }

    @PostMapping(path = "/upload/file/{bucketName}/room/{roomId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageDto> uploadRoomImages(
            @PathVariable long roomId,
            @RequestParam MultipartFile file,
            @PathVariable String bucketName,
            @AuthenticationPrincipal User user
    ){
        ImageDto uploadedRoomImages = imageService.uploadRoomImages(roomId, file, bucketName, user);
        return new ResponseEntity<>(uploadedRoomImages,HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/delete/roomImage/{bucketName}/{fileName}/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteRoomImage(@PathVariable long roomId,
                                                  @PathVariable String bucketName,
                                                  @PathVariable String fileName,
                                                  @AuthenticationPrincipal User user
                                                  ){
        String deletedRoomImage = imageService.deleteRoomImage(roomId, bucketName, fileName, user);
        return new ResponseEntity<>(deletedRoomImage,HttpStatus.OK);
    }
    @DeleteMapping(path = "/delete/propertyImage/{bucketName}/{fileName}/{propertyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deletePropertyImage(@PathVariable long propertyId,
                                                  @PathVariable String bucketName,
                                                  @PathVariable String fileName,
                                                  @AuthenticationPrincipal User user
    ){
        String deletedPropertyImage = imageService.deletePropertyImage(propertyId, bucketName, fileName, user);
        return new ResponseEntity<>(deletedPropertyImage,HttpStatus.OK);
    }

    @PutMapping(path = "/update/propertyImage/{bucketName}/{fileName}/{propertyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageDto> updatePropertyImage(
            @PathVariable long propertyId,
            @RequestParam MultipartFile file,
            @PathVariable String bucketName,
            @PathVariable String fileName,
            @AuthenticationPrincipal User user

    ){
        ImageDto updated = imageService.updatePropertyImage(propertyId, file, bucketName, fileName, user);
        return new ResponseEntity<>(updated,HttpStatus.OK);
    }

    @PutMapping(path = "/update/roomImage/{bucketName}/{fileName}/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageDto> updateRoomImage(
            @PathVariable long roomId,
            @RequestParam MultipartFile file,
            @PathVariable String bucketName,
            @PathVariable String fileName,
            @AuthenticationPrincipal User user

    ){
        ImageDto updated = imageService.updateRoomImage(roomId, file, bucketName, fileName, user);
       return new ResponseEntity<>(updated,HttpStatus.OK);
    }

}
