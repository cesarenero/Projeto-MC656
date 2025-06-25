package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.RegisterRequest;
import com.MC_656.mobility.exception.EmailAlreadyInUseException;
import com.MC_656.mobility.exception.UsernameAlreadyExistsException;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Error: Username '" + registerRequest.getUsername() + "' is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyInUseException("Error: Email '" + registerRequest.getEmail() + "' is already in use!");
        }

        // Create new user's account
        User user = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(),
                registerRequest.getName(),
                registerRequest.getSocialName(),
                registerRequest.getPhoneNumber(),
                registerRequest.getCpf()
        );

        return userRepository.save(user);
    }
}
