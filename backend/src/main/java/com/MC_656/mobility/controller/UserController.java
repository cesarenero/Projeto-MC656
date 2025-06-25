package com.MC_656.mobility.controller;

import com.MC_656.mobility.dto.RegisterRequest;
import com.MC_656.mobility.dto.MessageResponse;
import com.MC_656.mobility.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping; // Added
import org.springframework.security.access.prepost.PreAuthorize; // Added
import java.util.List; // Added
import java.util.stream.Collectors; // Added for stream operations

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Exceptions like UsernameAlreadyExistsException, EmailAlreadyInUseException
        // will be handled by GlobalExceptionHandler
        userService.registerUser(registerRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @Autowired
    private com.MC_656.mobility.service.RentalService rentalService; // Added import for RentalService

    @GetMapping("/me/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<com.MC_656.mobility.dto.RentalResponse>> getUserRentalHistory() {
        List<com.MC_656.mobility.model.Rental> history = rentalService.getRentalHistoryForCurrentUser();
        List<com.MC_656.mobility.dto.RentalResponse> historyResponse = history.stream()
                .map(com.MC_656.mobility.dto.RentalResponse::fromRental)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(historyResponse);
    }
}
