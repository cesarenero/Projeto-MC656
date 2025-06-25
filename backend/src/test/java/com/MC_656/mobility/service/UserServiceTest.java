package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.RegisterRequest;
import com.MC_656.mobility.exception.EmailAlreadyInUseException;
import com.MC_656.mobility.exception.UsernameAlreadyExistsException;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
        registerRequest.setPassword("ValidPass1!");
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setName("Test User");
        registerRequest.setSocialName("Test Social");
        registerRequest.setPhoneNumber("(11) 98765-4321");
        registerRequest.setCpf("123.456.789-00");
    }

    @Test
    void registerUser_success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        User expectedUser = new User(
                registerRequest.getUsername(),
                "encodedPassword",
                registerRequest.getEmail(),
                registerRequest.getName(),
                registerRequest.getSocialName(),
                registerRequest.getPhoneNumber(),
                registerRequest.getCpf()
        );
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User actualUser = userService.registerUser(registerRequest);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals("encodedPassword", actualUser.getPassword());
        verify(userRepository, times(1)).existsByUsername(registerRequest.getUsername());
        verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_usernameAlreadyExists_throwsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        Exception exception = assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("Error: Username '" + registerRequest.getUsername() + "' is already taken!", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_emailAlreadyInUse_throwsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("Error: Email '" + registerRequest.getEmail() + "' is already in use!", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(registerRequest.getUsername());
        verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}