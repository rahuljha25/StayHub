package com.StayHub.repository;

import com.StayHub.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT p FROM Property p JOIN p.address a WHERE p.name LIKE %:name% OR a.city LIKE %:city% OR a.state LIKE %:state% OR a.country LIKE %:country%")
    List<Property> findPropertyByNameAndAddress(
            @Param("name") String name,
            @Param("city") String city,
            @Param("state") String state,
            @Param("country") String country
    );

    @Query("SELECT p FROM Property p JOIN p.address a WHERE p.name = :name AND a.id = :addressId")
    Optional<Property> findByNameAndAddressId(@Param("name") String name, @Param("addressId") Long addressId);


}