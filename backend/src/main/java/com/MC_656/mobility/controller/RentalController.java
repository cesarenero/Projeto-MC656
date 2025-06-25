package com.MC_656.mobility.controller;

import com.MC_656.mobility.dto.RentalResponse;
import com.MC_656.mobility.dto.StartRentalRequest;
import com.MC_656.mobility.model.Rental;
import com.MC_656.mobility.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rentals")
@PreAuthorize("isAuthenticated()") // All rental operations require authentication
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping("/start")
    public ResponseEntity<RentalResponse> startRental(@Valid @RequestBody StartRentalRequest startRentalRequest) {
        Rental rental = rentalService.startRental(startRentalRequest);
        return new ResponseEntity<>(RentalResponse.fromRental(rental), HttpStatus.CREATED);
    }

    @PostMapping("/{rentalId}/end")
    public ResponseEntity<RentalResponse> endRental(@PathVariable Long rentalId) {
        Rental rental = rentalService.endRental(rentalId);
        return ResponseEntity.ok(RentalResponse.fromRental(rental));
    }

    @GetMapping("/me/active")
    public ResponseEntity<RentalResponse> getCurrentUserActiveRental() {
        Rental activeRental = rentalService.getActiveRentalForCurrentUser();
        if (activeRental == null) {
            return ResponseEntity.noContent().build(); // Or NotFound, depending on preference
        }
        return ResponseEntity.ok(RentalResponse.fromRental(activeRental));
    }
}
