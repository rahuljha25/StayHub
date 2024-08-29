package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.AddressDto;
import com.StayHub.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/addAddress")
    public ResponseEntity<AddressDto> addAddress(@RequestBody AddressDto dto,
                                                 @AuthenticationPrincipal User user){
        AddressDto savedAddress = addressService.addAddress(dto, user);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }
    @PutMapping("/update/address/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable long addressId,
                                                    @AuthenticationPrincipal User user,
                                                    @RequestBody AddressDto dto){
        AddressDto updatedAddress = addressService.updateAddress(addressId, user, dto);
        return new ResponseEntity<>(updatedAddress,HttpStatus.OK);
    }
}
