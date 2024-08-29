package com.StayHub.repository;


import com.StayHub.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNoAndPropertyId(Integer roomNo, Long propertyId);
}