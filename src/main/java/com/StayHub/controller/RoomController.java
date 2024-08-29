package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.DateDto;
import com.StayHub.payload.RoomDto;
import com.StayHub.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
public class RoomController {


    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/addRoom")
    public ResponseEntity<RoomDto> addRooms(@RequestParam long propertyId,
                                            @AuthenticationPrincipal User user,
                                            @RequestBody RoomDto dto){
        RoomDto addedRooms = roomService.addRooms(propertyId, user, dto);
        return new ResponseEntity<>(addedRooms, HttpStatus.CREATED);
    }
    @GetMapping("/getAvailable/{propertyId}")
    public ResponseEntity<List<RoomDto>> getAllAvailableRoomsByPropertyId(@PathVariable long propertyId,
                                                                    @RequestBody DateDto dto,
                                                                          @AuthenticationPrincipal User user){
        List<RoomDto> allAvailableRoomsByPropertyId = roomService.getAllAvailableRoomsByPropertyId(propertyId, dto,user);
        return new ResponseEntity<>(allAvailableRoomsByPropertyId,HttpStatus.OK);
    }

    @GetMapping("/getById")
    public ResponseEntity<RoomDto> getRoomById(@RequestParam long roomId,
                                               @AuthenticationPrincipal User user){
        RoomDto roomById = roomService.getRoomById(roomId, user);
        return new ResponseEntity<>(roomById,HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoom(@RequestParam long roomId,
                                              @AuthenticationPrincipal User user){
        roomService.deleteRoom(user,roomId);
        return new ResponseEntity<>("Room is deleted!!",HttpStatus.OK);
    }
    @GetMapping("/getAllRooms")
    public ResponseEntity<List<RoomDto>> getAllRooms(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "pageNo",defaultValue = "0",required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = "5",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "id",required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "id",required = false) String sortDir
    ){
        List<RoomDto> allRooms = roomService.getAllRooms(user, pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allRooms,HttpStatus.OK);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomDto>updateRoomDetails(@AuthenticationPrincipal User user,
                                                    @PathVariable long roomId,
                                                    @RequestParam long propertyId,
                                                    @RequestBody RoomDto dto){
        RoomDto updatedRoomDetails = roomService.updateRoomDetails(user,propertyId, roomId, dto);
        return new ResponseEntity<>(updatedRoomDetails,HttpStatus.OK);
    }

    //for making room available
    @PostMapping("/update-room-status")
    public ResponseEntity<String> updateRoomStatus(@RequestParam long roomId,
                                                   @RequestParam("checkIn") LocalDate checkIn,
                                                   @RequestParam("checkOut") LocalDate checkOut) {
        roomService.updateRoomStatusAfterBookingEnds(roomId,checkIn,checkOut);
        return new ResponseEntity<>("Successfully updated",HttpStatus.OK);
    }
}
