package com.MC_656.mobility.dto;

import com.MC_656.mobility.model.Rental;
import com.MC_656.mobility.model.RentalPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RentalPlan rentalPlan;
    private BigDecimal totalCost;
    private String vehicleMakeModel; // Convenience field

    public RentalResponse(Long id, Long userId, String username, Long vehicleId, String vehicleLicensePlate, String vehicleMakeModel,
                          LocalDateTime startTime, LocalDateTime endTime, RentalPlan rentalPlan, BigDecimal totalCost) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.vehicleId = vehicleId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.vehicleMakeModel = vehicleMakeModel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rentalPlan = rentalPlan;
        this.totalCost = totalCost;
    }

    public static RentalResponse fromRental(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getUser().getId(),
                rental.getUser().getUsername(),
                rental.getVehicle().getId(),
                rental.getVehicle().getLicensePlate(),
                rental.getVehicle().getMake() + " " + rental.getVehicle().getModel(),
                rental.getStartTime(),
                rental.getEndTime(),
                rental.getRentalPlan(),
                rental.getTotalCost()
        );
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Long getVehicleId() { return vehicleId; }
    public String getVehicleLicensePlate() { return vehicleLicensePlate; }
    public String getVehicleMakeModel() { return vehicleMakeModel; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public RentalPlan getRentalPlan() { return rentalPlan; }
    public BigDecimal getTotalCost() { return totalCost; }
}
