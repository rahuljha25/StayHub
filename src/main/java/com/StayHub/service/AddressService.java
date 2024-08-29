package com.StayHub.service;

import com.StayHub.entity.User;
import com.StayHub.payload.AddressDto;

public interface AddressService {
    AddressDto addAddress(AddressDto dto, User user);

    AddressDto updateAddress(long addressId, User user, AddressDto dto);
}
