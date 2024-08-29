package com.StayHub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {

    private JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf().disable().cors().disable();
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
        http.authorizeHttpRequests()
                .requestMatchers("/api/v1/login/**").permitAll()
                .requestMatchers("/api/v1/user/registerUser","/api/v1/user/registerPropertyOwner").permitAll()
                .requestMatchers("/api/v1/user/registerPropertyManager").hasRole("ADMIN")
                .requestMatchers("/api/v1/room/addRoom").hasAnyRole("MANAGER","ADMIN")
                .requestMatchers("/api/v1/room/getAvailable/{propertyId}").permitAll()
                .requestMatchers("/api/v1/room/getById").permitAll()
                .requestMatchers("/api/v1/room/delete").hasRole("ADMIN")
                .requestMatchers("/api/v1/room/getAllRooms").permitAll()
                .requestMatchers("/api/v1/room/update-room-status").hasAnyRole("MANAGER","ADMIN")
                .requestMatchers("/api/v1/property/registerProperty").hasAnyRole("OWNER","MANAGER","ADMIN")
                .requestMatchers("/api/v1/property/search/properties").permitAll()
                .requestMatchers("/api/v1/property/update/property/{propertyId}").hasAnyRole("MANAGER","ADMIN")
                .requestMatchers("/api/v1/property/getById").permitAll()
                .requestMatchers("/api/v1/property/getAllProperties").hasRole("ADMIN")
                .requestMatchers("/api/v1/property/delete").hasRole("ADMIN")
                .requestMatchers("/api/v1/image/upload/file/{bucketName}/property/{propertyId}").hasAnyRole("OWNER","MANAGER","ADMIN")
                .requestMatchers("/api/v1/image/upload/file/{bucketName}/room/{roomId}").hasAnyRole("OWNER","MANAGER","ADMIN")
                .requestMatchers("/api/v1/image/delete/roomImage/{bucketName}/{fileName}/{roomId}").hasAnyRole("ADMIN","MANAGER")
                .requestMatchers("/api/v1/image/delete/propertyImage/{bucketName}/{fileName}/{propertyId}").hasAnyRole("ADMIN","MANAGER")
                .requestMatchers("/api/v1/favourite/**").permitAll()
                .requestMatchers("/api/v1/booking/create/booking").permitAll()
                .requestMatchers("/api/v1/booking/cancel/{bookingId}").permitAll()
                .requestMatchers("/api/v1/booking/get/bookings-property").hasAnyRole("MANAGER","ADMIN")
                .requestMatchers("/api/v1/booking/get/allBookings").hasAnyRole("MANAGER","ADMIN")
                .requestMatchers("/api/v1/booking/get/bookings-user").permitAll()
                .requestMatchers("/api/v1/booking/get/{bookingId}").permitAll()
                .requestMatchers("/api/v1/address/addAddress").hasAnyRole("MANAGER","OWNER","ADMIN")
                .requestMatchers("/api/v1/address/update/address/{addressId}").hasAnyRole("MANAGER","OWNER","ADMIN")
                .requestMatchers("/api/v1/user/get/by-id").permitAll()
                .requestMatchers("/api/v1/user/delete").hasAnyRole("USER","ADMIN","MANAGER")
                .requestMatchers("/api/v1/user/update").permitAll()
                .requestMatchers("/api/v1/room/update/{roomId}").hasAnyRole("ADMIN","MANAGER")
                .requestMatchers("/api/v1/reviews/addReviews").permitAll()
                .requestMatchers("/api/v1/reviews/getReviewByUser").permitAll()
                .requestMatchers("/api/v1/reviews/delete").hasAnyRole("ADMIN","MANAGER")
                .requestMatchers("/api/v1/reviews/get-allReview").permitAll()
                .anyRequest()
                .authenticated();
        return http.build();
    }
}
