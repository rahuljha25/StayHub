package com.StayHub.service.Impl;

import com.StayHub.entity.User;
import com.StayHub.exception.AdminException;
import com.StayHub.exception.UserException;
import com.StayHub.payload.UserDto;
import com.StayHub.repository.UserRepository;
import com.StayHub.service.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto registerUser(UserDto dto) throws UserException {
        User user = dtoToEntity(dto);
        user.setRole("ROLE_USER");
        Optional<User> existEmail = userRepository.findByEmail(user.getEmail());
        if (existEmail.isPresent()){
            throw new UserException("Already email exist");
        }
        User save = userRepository.save(user);
        return entityToDto(save);

    }

    @Override
    public UserDto registerPropertyOwner(UserDto dto) throws UserException {
        Optional<User> existEmail = userRepository.findByEmail(dto.getEmail());
        if (existEmail.isPresent()){
            throw new UserException("Already registered with this email");
        }
        User user = dtoToEntity(dto);
        user.setRole("ROLE_OWNER");
        User save = userRepository.save(user);
        return entityToDto(save);
    }

    @Override
    public UserDto registerPropertyManager(UserDto dto) throws UserException{
        Optional<User> existEmail = userRepository.findByEmail(dto.getEmail());
        if (existEmail.isPresent()){
            throw new UserException("Already registered with this email");
        }
        User user = dtoToEntity(dto);
        user.setRole("ROLE_MANAGER");
        User save = userRepository.save(user);
        return entityToDto(save);
    }

    @Override
    public UserDto registerAdmin(UserDto dto) throws AdminException {
        Optional<User> existEmail = userRepository.findByEmail(dto.getEmail());
        if (existEmail.isPresent()){
            throw new AdminException("Already registered with this email ");
        }
        User user = dtoToEntity(dto);
        user.setRole("ROLE_ADMIN");
        User save = userRepository.save(user);
        return entityToDto(save);
    }

    @Override
    public UserDto getUserDetailsById(User user) {
        Long userId = user.getId();
        User user1 = userRepository.findById(userId).orElseThrow(
                () -> new UserException("User not found with id: " + userId)
        );
        return entityToDto(user1);
    }

    @Override
    public void deleteUser(User user) {
        Long userId = user.getId();
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto updateUser(User user, UserDto dto) {
        Long userId = user.getId();
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new UserException("User not found with id: " + userId)
        );

        User updatedUser = dtoToEntity(dto);
        updatedUser.setRole(existingUser.getRole(
        ));
        updatedUser.setId(existingUser.getId());

        User save = userRepository.save(updatedUser);
        return entityToDto(save);
    }

    //dto to entity
   private User dtoToEntity(UserDto dto){
        User user=new User();
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(BCrypt.hashpw(dto.getPassword(),BCrypt.gensalt(10)));
        return user;
    }
    //entity to Dto
   private UserDto entityToDto(User user){
        UserDto dto=new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
   }

}
