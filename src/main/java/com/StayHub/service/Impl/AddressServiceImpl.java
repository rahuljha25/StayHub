package com.StayHub.service.Impl;

import com.StayHub.entity.Address;
import com.StayHub.entity.User;
import com.StayHub.exception.AddressException;
import com.StayHub.payload.AddressDto;
import com.StayHub.repository.AddressRepository;
import com.StayHub.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    private AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public AddressDto addAddress(AddressDto dto, User user) {
        // Check if address already exists for the user
        Optional<Address> existingAddress = addressRepository.findByAreaAndCityAndCountryAndStateAndPostalCode(
                dto.getArea(), dto.getCity(), dto.getCountry(), dto.getState(), dto.getPostalCode());

        if (existingAddress.isPresent()) {
            throw new AddressException("Address already exists with the same details.");
        }
        dto.setUser(user);

        Address address = dtoToEntity(dto);
        Address save = addressRepository.save(address);
        return entityToDto(save);
    }

    @Override
    public AddressDto updateAddress(long addressId, User user, AddressDto dto) {
        Address address=null;
        address = addressRepository.findById(addressId).orElseThrow(
                () -> new AddressException("Address not found with id: " + addressId)
        );
        dto.setUser(user);
        address = dtoToEntity(dto);
        address.setId(addressId);
        Address save = addressRepository.save(address);
        return entityToDto(save);
    }

    //dto to entity
    private Address dtoToEntity(AddressDto dto){
        Address address=new Address();
        address.setArea(dto.getArea());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setUser(dto.getUser());
        return address;
    }
    //entity to DTo
    private AddressDto entityToDto(Address address){
        AddressDto dto=new AddressDto();
        dto.setId(address.getId());
        dto.setArea(address.getArea());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setUser(address.getUser());
        return dto;

    }
}
