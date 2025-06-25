package com.MC_656.mobility.controller;

import com.MC_656.mobility.dto.JwtResponse;
import com.MC_656.mobility.dto.LoginRequest;
import com.MC_656.mobility.dto.MessageResponse;
import com.MC_656.mobility.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // AuthenticationException (e.g., BadCredentialsException) will be handled by GlobalExceptionHandler
        // Other RuntimeExceptions from authService (if any) will also be handled globally.
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
