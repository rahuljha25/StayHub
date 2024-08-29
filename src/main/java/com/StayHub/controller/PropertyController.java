package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.PropertyDto;
import com.StayHub.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    private PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/registerProperty")
    public ResponseEntity<PropertyDto> registerProperty(@RequestParam long addressId,
                                                   @AuthenticationPrincipal User user,
                                                   @RequestBody PropertyDto dto){
        PropertyDto addedProperty = propertyService.registerProperty(addressId, user, dto);
        return new ResponseEntity<>(addedProperty, HttpStatus.CREATED);
    }
    @GetMapping("/search/properties")
    public ResponseEntity<List<PropertyDto>> searchProperties(String name, String city, String state, String country){
        List<PropertyDto> getProperties = propertyService.searchProperties(name,city,state,country);
        return new ResponseEntity<>(getProperties,HttpStatus.OK);
    }
    //for updating
    @PutMapping("/update/property/{propertyId}")
    public ResponseEntity<PropertyDto> updateRegisteredProperty(@PathVariable long propertyId,
                                                      @RequestParam long addressId,
                                                      @AuthenticationPrincipal User user,
                                                      @RequestBody PropertyDto dto){
        PropertyDto updatedProperty = propertyService.updateProperty(propertyId, addressId, user, dto);
        return new ResponseEntity<>(updatedProperty,HttpStatus.OK);
    }

    @GetMapping("/getById")
    public ResponseEntity<PropertyDto> getRegisteredPropertyDetailsById(@RequestParam long propertyId,
                                                              @AuthenticationPrincipal User user){
        PropertyDto propertyById = propertyService.getPropertyById(propertyId,user);
        return new ResponseEntity<>(propertyById,HttpStatus.OK);
    }

    @GetMapping("/getAllProperties")
    public ResponseEntity<List<PropertyDto>> getAllRegisteredProperties(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "pageNo",defaultValue = "0",required = false)int pageNo,
            @RequestParam(value = "pageSize",defaultValue = "5",required = false)int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "id",required = false)String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "id",required = false)String  sortDir){

        List<PropertyDto> allProperties = propertyService.getAllProperties(user, pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(allProperties,HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRegisteredProperty(@AuthenticationPrincipal User user,
                                                                @RequestParam long propertyId){
        propertyService.deleteRegisteredProperty(user,propertyId);
        return new ResponseEntity<>("Property is deleted!!",HttpStatus.OK);
    }


}
