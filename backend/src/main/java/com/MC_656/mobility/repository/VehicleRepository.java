package com.MC_656.mobility.repository;

import com.MC_656.mobility.model.Vehicle;
import com.MC_656.mobility.model.VehicleStatus;
import com.MC_656.mobility.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    Boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByType(VehicleType type);
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByTypeAndStatus(VehicleType type, VehicleStatus status);
}
