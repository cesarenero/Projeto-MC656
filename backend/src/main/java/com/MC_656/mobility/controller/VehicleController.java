package com.MC_656.mobility.controller;

import com.MC_656.mobility.dto.VehicleRequest;
import com.MC_656.mobility.dto.VehicleResponse;
import com.MC_656.mobility.model.Vehicle;
import com.MC_656.mobility.model.VehicleStatus;
import com.MC_656.mobility.model.VehicleType;
import com.MC_656.mobility.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()") // Any authenticated user can register a vehicle
    public ResponseEntity<VehicleResponse> registerVehicle(@Valid @RequestBody VehicleRequest vehicleRequest) {
        Vehicle vehicle = vehicleService.createVehicle(vehicleRequest);
        return new ResponseEntity<>(VehicleResponse.fromVehicle(vehicle), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> listVehicles(
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) VehicleStatus status) {

        List<Vehicle> vehicles;
        if (type == null && status == null) {
             // By default, list only available vehicles if no status is specified
            vehicles = vehicleService.getVehiclesByTypeAndStatus(null, VehicleStatus.AVAILABLE);
        } else {
            vehicles = vehicleService.getVehiclesByTypeAndStatus(type, status);
        }

        List<VehicleResponse> vehicleResponses = vehicles.stream()
                .map(VehicleResponse::fromVehicle)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehicleResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(VehicleResponse.fromVehicle(vehicle));
    }

    @GetMapping("/types")
    public ResponseEntity<List<VehicleType>> getSupportedVehicleTypes() {
        return ResponseEntity.ok(vehicleService.getSupportedVehicleTypes());
    }
}
