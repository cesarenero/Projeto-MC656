package com.MC_656.mobility.repository;

import com.MC_656.mobility.model.Rental;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserOrderByStartTimeDesc(User user);
    List<Rental> findByVehicleOrderByStartTimeDesc(Vehicle vehicle);

    // Find active rental for a user (endTime is null)
    Optional<Rental> findByUserAndEndTimeIsNull(User user);

    // Find active rental for a vehicle (endTime is null)
    Optional<Rental> findByVehicleAndEndTimeIsNull(Vehicle vehicle);

    // Find rentals that overlap with a given period for a specific vehicle (useful for checking availability, though status check is primary)
    List<Rental> findByVehicleAndStartTimeBeforeAndEndTimeAfter(Vehicle vehicle, LocalDateTime potentialEndTime, LocalDateTime potentialStartTime);
}
