package com.StayHub.repository;

import com.StayHub.entity.Booking;
import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomAndStatus(Room room, String status);
     List<Booking> findByProperty(Property property);
     List<Booking> findByUser(User user);

}