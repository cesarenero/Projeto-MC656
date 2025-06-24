package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.RegisterRequest;
import com.MC_656.mobility.exception.EmailAlreadyInUseException;
import com.MC_656.mobility.exception.UsernameAlreadyExistsException;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest { // Renamed from UserRegistrationServiceTest to UserServiceTest

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setName("Test User");
        registerRequest.setSocialName("Testy");
        registerRequest.setPhoneNumber("(11) 98765-4321");
        registerRequest.setCpf("123.456.789-00");
    }

    @Test
    @DisplayName("Should successfully register user with valid data")
    void registerUser_WithValidData_ShouldSucceed() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(registerRequest.getUsername());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals(registerRequest.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UsernameAlreadyExistsException if username is taken")
    void registerUser_WhenUsernameExists_ShouldThrowException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        Exception exception = assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("Error: Username '" + registerRequest.getUsername() + "' is already taken!", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException if email is taken")
    void registerUser_WhenEmailExists_ShouldThrowException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("Error: Email '" + registerRequest.getEmail() + "' is already in use!", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    // Note: The detailed validation tests for fields like name, email format, CPF format, phone format
    // are primarily handled by @Valid annotations on the RegisterRequest DTO in the Controller layer,
    // and by constraints on the User entity.
    // Unit tests here focus on the service logic (username/email existence, password encoding, saving).
    // If UserService had more complex validation logic beyond what annotations provide, those would be tested here.
}