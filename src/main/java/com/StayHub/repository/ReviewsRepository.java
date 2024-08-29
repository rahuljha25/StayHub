package com.StayHub.repository;

import com.StayHub.entity.Property;
import com.StayHub.entity.Reviews;
import com.StayHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
   @Query("select r from Reviews r where r.user=:user and r.property=:property")
   Optional<Reviews> findReviewByUser(@Param("user") User user, @Param("property") Property property);

   @Query("select r from Reviews r where r.user=:user")
   List<Reviews>findByUserReview(@Param("user") User user);

   @Query("select r from Reviews r where r.property=:property")
   List<Reviews> findByProperty(@Param("property") Property property);

}