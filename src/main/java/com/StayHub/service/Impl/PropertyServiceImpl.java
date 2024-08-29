package com.StayHub.service.Impl;

import com.StayHub.entity.Address;
import com.StayHub.entity.Property;
import com.StayHub.entity.User;
import com.StayHub.exception.PropertyException;
import com.StayHub.payload.PropertyDto;
import com.StayHub.repository.AddressRepository;
import com.StayHub.repository.PropertyRepository;
import com.StayHub.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private PropertyRepository propertyRepository;
    private AddressRepository addressRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository,AddressRepository addressRepository) {
        this.propertyRepository = propertyRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public PropertyDto registerProperty(long addressId, User user, PropertyDto dto) {

        Address address = addressRepository.findById(addressId).orElseThrow(
                ()->new PropertyException("Address not found with id: "+addressId)
        );

        // Check if property with the same name and address already exists
        Optional<Property> existingProperty = propertyRepository.findByNameAndAddressId(dto.getName(), addressId);
        if (existingProperty.isPresent()) {
            throw new PropertyException("Property with the same name and address already exists.");
        }
        dto.setAddress(address);
        dto.setUser(user);

        Property property = dtoToEntity(dto);
        Property save = propertyRepository.save(property);
        return entityToDto(save);
    }

    @Override
    public List<PropertyDto> searchProperties(String name, String city, String state, String country) {
        List<Property> byNameAndCity = propertyRepository.findPropertyByNameAndAddress(name,city,state,country);
        List<PropertyDto> collect = byNameAndCity.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public PropertyDto updateProperty(long propertyId, long addressId, User user, PropertyDto dto) {
        Property property=null;
        property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new PropertyException("Property not found with id: " + propertyId)
        );

        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new PropertyException("Address not found with id: " + addressId)
        );
        dto.setAddress(address);
        dto.setUser(user);

        property= dtoToEntity(dto);
        property.setId(propertyId);

        Property save = propertyRepository.save(property);
        return entityToDto(save);

    }

    @Override
    public PropertyDto getPropertyById(long propertyId,User user) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new PropertyException("Property not found with id: " + propertyId)
        );
        return entityToDto(property);
    }

    @Override
    public List<PropertyDto> getAllProperties(User user, int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable=null;
        if (sortDir.equalsIgnoreCase("asc")){
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        }else if (sortDir.equalsIgnoreCase("desc")){
            pageable=PageRequest.of(pageNo,pageSize,Sort.by(sortBy).descending());
        }else {
            pageable=PageRequest.of(pageNo,pageSize);
        }
        Page<Property> all = propertyRepository.findAll(pageable);
        List<Property> content = all.getContent();
        List<PropertyDto> collect = content.stream().map(e -> entityToDto(e)).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void deleteRegisteredProperty(User user, long propertyId) {
        propertyRepository.deleteById(propertyId);
    }

    //dto to entity
    private Property dtoToEntity(PropertyDto dto){
        Property property=new Property();
        property.setName(dto.getName());
        property.setUnits(dto.getUnits());
        property.setLegalName(dto.getLegalName());
        property.setUser(dto.getUser());
        property.setAddress(dto.getAddress());
        property.setPropertyAmenities(dto.getPropertyAmenities());
        property.setPropertyRules(dto.getPropertyRules());
        property.setPropertyType(dto.getPropertyType());
        return property;
    }
    //entity to Dto
    private PropertyDto entityToDto(Property property){
        PropertyDto dto=new PropertyDto();
        dto.setId(property.getId());
        dto.setName(property.getName());
        dto.setUnits(property.getUnits());
        dto.setLegalName(property.getLegalName());
        dto.setUser(property.getUser());
        dto.setAddress(property.getAddress());
        dto.setPropertyAmenities(property.getPropertyAmenities());
        dto.setPropertyRules(property.getPropertyRules());
        dto.setPropertyType(property.getPropertyType());
        return dto;
    }
}
