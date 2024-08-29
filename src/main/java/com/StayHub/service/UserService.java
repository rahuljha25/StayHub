package com.StayHub.service;

import com.StayHub.entity.User;
import com.StayHub.exception.AdminException;
import com.StayHub.exception.UserException;
import com.StayHub.payload.UserDto;

public interface UserService {
    UserDto registerUser(UserDto dto) throws UserException;

    UserDto registerPropertyOwner(UserDto dto) throws UserException;

    UserDto registerPropertyManager(UserDto dto) throws UserException;

    UserDto registerAdmin(UserDto dto) throws AdminException;

    UserDto getUserDetailsById(User user);

    void deleteUser(User user);

    UserDto updateUser(User user, UserDto dto);
}
