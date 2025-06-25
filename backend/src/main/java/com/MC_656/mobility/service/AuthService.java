package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.JwtResponse;
import com.MC_656.mobility.dto.LoginRequest;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.repository.UserRepository;
import com.MC_656.mobility.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
            () -> new RuntimeException("User not found after authentication") // Should not happen
        );

        return new JwtResponse(jwt,
                               user.getId(),
                               user.getUsername(),
                               user.getEmail(),
                               user.getName(),
                               user.getSocialName(),
                               user.getPhoneNumber(),
                               user.getCpf());
    }
}
