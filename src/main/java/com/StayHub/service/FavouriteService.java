package com.StayHub.service;

import com.StayHub.entity.User;
import com.StayHub.payload.FavouriteDto;

import java.util.List;

public interface FavouriteService {
    FavouriteDto addFavourite(long propertyId, User user, FavouriteDto favouriteDto);

    FavouriteDto updateFavourite(long favouriteId, long propertyId, User user, FavouriteDto dto);

    List<FavouriteDto> getFavouriteByUser(User user);
}
