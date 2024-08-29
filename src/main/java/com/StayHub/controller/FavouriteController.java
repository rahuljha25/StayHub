package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.FavouriteDto;
import com.StayHub.service.FavouriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favourite")
public class FavouriteController {

    private FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @PostMapping("/addFavourite")
    public ResponseEntity<FavouriteDto> addFavourite(@RequestParam long propertyId,
                                                     @AuthenticationPrincipal User user,
                                                     @RequestBody FavouriteDto favouriteDto){
        FavouriteDto addedFavourite = favouriteService.addFavourite(propertyId, user, favouriteDto);
        return new ResponseEntity<>(addedFavourite, HttpStatus.CREATED);
    }
    @PutMapping("/{favouriteId}")
    public ResponseEntity<FavouriteDto> updateFavourite(@PathVariable long favouriteId,
                                                        @RequestParam long propertyId,
                                                        @AuthenticationPrincipal User user,
                                                        @RequestBody FavouriteDto dto){
        FavouriteDto updatedFavourite = favouriteService.updateFavourite(favouriteId, propertyId, user, dto);
        return new ResponseEntity<>(updatedFavourite,HttpStatus.OK);
    }

    @GetMapping("/getFavouriteByUser")
    public ResponseEntity<List<FavouriteDto>> getFavouriteByUser(@AuthenticationPrincipal User user){
        List<FavouriteDto> favouriteByUser = favouriteService.getFavouriteByUser(user);
        return new ResponseEntity<>(favouriteByUser,HttpStatus.OK);
    }


}
