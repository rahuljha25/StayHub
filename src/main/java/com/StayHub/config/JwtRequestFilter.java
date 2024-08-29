package com.StayHub.config;

import com.StayHub.entity.User;
import com.StayHub.exception.ResourceNotFoundException;
import com.StayHub.repository.UserRepository;
import com.StayHub.service.Impl.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private UserRepository userRepository;

    public JwtRequestFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader!=null && tokenHeader.startsWith("Bearer ")){
           try {
               String token = tokenHeader.substring(8, tokenHeader.length() - 1);
               String username = jwtService.getUsername(token);
               Optional<User> byEmail = userRepository.findByEmail(username);
               if (byEmail.isPresent()){
                   User user = byEmail.get();
                   UsernamePasswordAuthenticationToken authenticationToken=
                           new UsernamePasswordAuthenticationToken(user,null, Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
                   authenticationToken.setDetails(new WebAuthenticationDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               }
               else {
                   throw new ResourceNotFoundException("User not present with username: "+username);
               }
           }catch (Exception e){
               throw new BadCredentialsException("Invalid Token received..");
           }
        }
        filterChain.doFilter(request,response);
    }
}
