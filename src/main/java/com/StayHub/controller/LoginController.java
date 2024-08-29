package com.StayHub.controller;

import com.StayHub.entity.User;
import com.StayHub.payload.JwtTokenDto;
import com.StayHub.payload.LoginDto;
import com.StayHub.repository.UserRepository;
import com.StayHub.service.Impl.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    private UserRepository userRepository;
    private JwtService jwtService;

    public LoginController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> verifyUser(@RequestBody LoginDto loginDto){
        User user = userRepository.findByEmail(loginDto.getEmail()).get();
            if (BCrypt.checkpw(loginDto.getPassword(),user.getPassword())){
                String token = jwtService.generateToken(user);
                JwtTokenDto jwtTokenDto=new JwtTokenDto();
                jwtTokenDto.setType("Jwt Token");
                jwtTokenDto.setToken(token);
                if (jwtTokenDto!=null){
                    return new ResponseEntity<>(jwtTokenDto, HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);
                }
            }else {
                return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);

            }
    }

    @PostMapping("/owner")
    public ResponseEntity<?> verifyOwner(@RequestBody LoginDto loginDto){
        User user = userRepository.findByEmail(loginDto.getEmail()).get();
        if (BCrypt.checkpw(loginDto.getPassword(),user.getPassword())){
            String token = jwtService.generateToken(user);
            JwtTokenDto jwtTokenDto=new JwtTokenDto();
            jwtTokenDto.setType("Jwt Token");
            jwtTokenDto.setToken(token);
            if (jwtTokenDto!=null){
                return new ResponseEntity<>(jwtTokenDto, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("/manager")
    public ResponseEntity<?> verifyManager(@RequestBody LoginDto loginDto){
        User user = userRepository.findByEmail(loginDto.getEmail()).get();
        if (BCrypt.checkpw(loginDto.getPassword(),user.getPassword())){
            String token = jwtService.generateToken(user);
            JwtTokenDto jwtTokenDto=new JwtTokenDto();
            jwtTokenDto.setType("Jwt Token");
            jwtTokenDto.setToken(token);
            if (jwtTokenDto!=null){
                return new ResponseEntity<>(jwtTokenDto, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> verifyAdmin(@RequestBody LoginDto loginDto){
        User user = userRepository.findByEmail(loginDto.getEmail()).get();
        if (BCrypt.checkpw(loginDto.getPassword(),user.getPassword())){
            String token = jwtService.generateToken(user);
            JwtTokenDto jwtTokenDto=new JwtTokenDto();
            jwtTokenDto.setType("Jwt Token");
            jwtTokenDto.setToken(token);
            if (jwtTokenDto!=null){
                return new ResponseEntity<>(jwtTokenDto, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("Invalid username/password",HttpStatus.BAD_REQUEST);

        }
    }
}
