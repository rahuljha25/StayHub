package com.StayHub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "room_no", nullable = false)
    private Integer roomNo;

    @Column(name = "price_per_night", nullable = false, precision = 19, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "no_bedrooms", nullable = false)
    private Integer noBedrooms;

    @Column(name = "no_bathrooms", nullable = false)
    private Integer noBathrooms;

    @Column(name = "sleeps", nullable = false)
    private Integer sleeps;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @ElementCollection
    @Column(name = "bed_types", nullable = false)
    private List<String> bedTypes;

    @Column(name = "status", nullable = false)
    private Boolean status; //true means available or false means unavailable

    @ElementCollection
    @Column(name = "room_amenities",length = 2000)
    private List<String> roomAmenities;

    @Column(name = "currency", nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;

    @ElementCollection
    @MapKeyColumn(name = "available_date") // Name for the LocalDate key column
    @Column(name = "is_available")         // Name for the Boolean value column
    private Map<LocalDate, Boolean> availability = new HashMap<>(); // Tracks availability per date


}