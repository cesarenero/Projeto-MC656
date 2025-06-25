package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.JwtResponse;
import com.MC_656.mobility.dto.LoginRequest;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.repository.UserRepository;
import com.MC_656.mobility.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User user;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser@example.com");
        loginRequest.setPassword("password123");

        user = new User("testuser@example.com", "encodedPassword", "testuser@example.com", "Test User", "Social", "(11)12345-6789", "123.456.789-00");
        user.setId(1L);

        authentication = mock(Authentication.class);
        userDetails = mock(UserDetails.class);
    }

    @Test
    void authenticateUser_success() {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getUsername());
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("dummy.jwt.token");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);

        assertNotNull(jwtResponse);
        assertEquals("dummy.jwt.token", jwtResponse.getToken());
        assertEquals(user.getId(), jwtResponse.getId());
        assertEquals(user.getUsername(), jwtResponse.getUsername());
        assertEquals(user.getEmail(), jwtResponse.getEmail());
        assertEquals(user.getName(), jwtResponse.getName());

        verify(authenticationManager, times(1)).authenticate(argThat(token ->
                token.getName().equals(loginRequest.getUsername()) &&
                token.getCredentials().toString().equals(loginRequest.getPassword())
        ));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void authenticateUser_failure_badCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Bad credentials", exception.getMessage());
        verify(jwtUtils, never()).generateJwtToken(any());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void authenticateUser_failure_userNotFoundAfterAuth() {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("User not found after authentication", exception.getMessage());
        verify(jwtUtils, times(1)).generateJwtToken(authentication); // Token might be generated before this specific check
    }
}
