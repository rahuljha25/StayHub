package com.StayHub.service;

import com.StayHub.entity.User;
import com.StayHub.payload.DateDto;
import com.StayHub.payload.RoomDto;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    RoomDto addRooms(long propertyId, User user, RoomDto dto);

    List<RoomDto> getAllAvailableRoomsByPropertyId(long propertyId, DateDto dto,User user);

    RoomDto getRoomById(long roomId, User user);

    void deleteRoom(User user, long roomId);

    List<RoomDto> getAllRooms(User user, int pageNo, int pageSize, String sortBy, String sortDir);

    RoomDto updateRoomDetails(User user,long propertyId, long roomId, RoomDto dto);

    void updateRoomStatusAfterBookingEnds(long roomId, LocalDate checkIn, LocalDate checkOut);
}
