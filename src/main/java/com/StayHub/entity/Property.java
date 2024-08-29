package com.StayHub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "units", nullable = false)
    private Integer units;

    @Column(name = "legal_name", nullable = false, length = 150)
    private String legalName;
    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @ElementCollection
    @Column(name = "property_amenities", nullable = false)
    private List<String> propertyAmenities;

    @ElementCollection
    @Column(name = "property_rules", nullable = false)
    private List<String> propertyRules;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Room> rooms;

    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Reviews> reviews;

    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Image> images;

}