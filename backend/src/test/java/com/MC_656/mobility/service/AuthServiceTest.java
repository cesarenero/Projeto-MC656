package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.JwtResponse;
import com.MC_656.mobility.dto.LoginRequest;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.repository.UserRepository;
import com.MC_656.mobility.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication; // Mocked Authentication object returned by AuthenticationManager

    @Mock
    private UserDetails userDetails; // Mocked UserDetails returned by Authentication object

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser@example.com");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser@example.com");
        user.setEmail("testuser@example.com");
        user.setName("Test User");
        user.setSocialName("Testy");
        user.setPhoneNumber("(11)99999-8888");
        user.setCpf("123.456.789-00");
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT response")
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() {
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(loginRequest.getUsername());
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked.jwt.token");

        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);

        assertNotNull(jwtResponse);
        assertEquals("mocked.jwt.token", jwtResponse.getToken());
        assertEquals(user.getId(), jwtResponse.getId());
        assertEquals(user.getUsername(), jwtResponse.getUsername());
        assertEquals(user.getEmail(), jwtResponse.getEmail());
        assertEquals(user.getName(), jwtResponse.getName());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid credentials")
    void authenticateUser_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException if user not found after authentication (edge case)")
    void authenticateUser_WhenUserNotFoundAfterAuth_ShouldThrowRuntimeException() {
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(loginRequest.getUsername());
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty()); // Simulate user not found

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("User not found after authentication", exception.getMessage());
    }
}
