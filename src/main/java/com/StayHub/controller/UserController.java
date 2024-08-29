package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.UserDto;
import com.StayHub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto dto,
                                                BindingResult result){
        if (result.hasErrors()){
            return new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.OK);
        }
        UserDto registered = userService.registerUser(dto);
        return new ResponseEntity<>(registered, HttpStatus.CREATED);
    }

    @PostMapping("/registerPropertyOwner")
    public ResponseEntity<?> registerPropertyOwner(@Valid@RequestBody UserDto dto,
                                                         BindingResult result){

        if (result.hasErrors()){
            return new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.OK);
        }
        UserDto ownerRegistered = userService.registerPropertyOwner(dto);
        return new ResponseEntity<>(ownerRegistered,HttpStatus.CREATED);
    }
    @PostMapping("/registerPropertyManager")
    public ResponseEntity<?> registerPropertyManager(@Valid@RequestBody UserDto dto,
                                                     BindingResult result){
        if (result.hasErrors()){
            return new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.OK);
        }
        UserDto registeredManager = userService.registerPropertyManager(dto);
        return new ResponseEntity<>(registeredManager,HttpStatus.CREATED);
    }
    @GetMapping("/get/by-id")
    public ResponseEntity<UserDto> getUserDetailsById(@AuthenticationPrincipal User user){
        UserDto userDetailsById = userService.getUserDetailsById(user);
        return new ResponseEntity<>(userDetailsById,HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user){
        userService.deleteUser(user);
        return new ResponseEntity<>("Record is deleted!!",HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal User user,
                                              @RequestBody UserDto dto){
        UserDto updatedUser = userService.updateUser(user, dto);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }
}
