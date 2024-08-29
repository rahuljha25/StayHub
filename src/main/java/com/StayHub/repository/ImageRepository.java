package com.StayHub.repository;

import com.StayHub.entity.Image;
import com.StayHub.entity.Property;
import com.StayHub.entity.Room;
import com.StayHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.room = :room AND i.imageUrl LIKE %:fileName% AND i.user = :user")
    Optional<Image> findByRoomAndImageUrlAndUser(@Param("room")Room room, @Param("fileName")String imageUrl, @Param("user")User user);

    @Query("SELECT i FROM Image i WHERE i.property = :property AND i.imageUrl LIKE %:fileName% AND i.user = :user")
    Optional<Image> findByPropertyAndImageUrlAndUser(@Param("property") Property property, @Param("fileName")String imageUrl, @Param("user")User user);


}