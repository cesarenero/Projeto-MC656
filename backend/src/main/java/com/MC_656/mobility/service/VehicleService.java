package com.MC_656.mobility.service;

import com.MC_656.mobility.dto.VehicleRequest;
import com.MC_656.mobility.exception.BadRequestException;
import com.MC_656.mobility.exception.ResourceNotFoundException;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.model.Vehicle;
import com.MC_656.mobility.model.VehicleStatus;
import com.MC_656.mobility.model.VehicleType;
import com.MC_656.mobility.repository.UserRepository;
import com.MC_656.mobility.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    // Define allowed eco-friendly types here or load from config if more dynamic
    private static final Set<VehicleType> ALLOWED_ECO_FRIENDLY_TYPES = Set.of(
            VehicleType.BICYCLE,
            VehicleType.E_BIKE,
            VehicleType.SCOOTER,
            VehicleType.E_SCOOTER,
            VehicleType.ELECTRIC_CAR,
            VehicleType.CARGO_BIKE
    );

    @Transactional
    public Vehicle createVehicle(VehicleRequest vehicleRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User owner = userRepository.findByUsername(currentPrincipalName)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentPrincipalName));

        if (!ALLOWED_ECO_FRIENDLY_TYPES.contains(vehicleRequest.getType())) {
            throw new BadRequestException("Invalid vehicle type: " + vehicleRequest.getType() +
                                          ". Allowed types are: " + ALLOWED_ECO_FRIENDLY_TYPES);
        }

        if (vehicleRepository.existsByLicensePlate(vehicleRequest.getLicensePlate())) {
            throw new BadRequestException("License plate '" + vehicleRequest.getLicensePlate() + "' already exists.");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(vehicleRequest.getMake());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setYearManufacture(vehicleRequest.getYearManufacture());
        vehicle.setLicensePlate(vehicleRequest.getLicensePlate());
        vehicle.setType(vehicleRequest.getType());
        vehicle.setLatitude(vehicleRequest.getLatitude());
        vehicle.setLongitude(vehicleRequest.getLongitude());
        vehicle.setStatus(VehicleStatus.AVAILABLE); // Default status
        vehicle.setOwner(owner);

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
    }

    public List<Vehicle> getVehiclesByTypeAndStatus(VehicleType type, VehicleStatus status) {
        if (type != null && status != null) {
            return vehicleRepository.findByTypeAndStatus(type, status);
        } else if (type != null) {
            return vehicleRepository.findByType(type);
        } else if (status != null) {
            return vehicleRepository.findByStatus(status);
        } else {
            return vehicleRepository.findAll(); // Or throw error / return empty
        }
    }


    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }

    public List<VehicleType> getSupportedVehicleTypes() {
        return Arrays.asList(VehicleType.values());
    }
}
