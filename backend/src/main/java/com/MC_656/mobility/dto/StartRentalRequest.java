package com.MC_656.mobility.dto;

import com.MC_656.mobility.model.RentalPlan;
import jakarta.validation.constraints.NotNull;

public class StartRentalRequest {

    @NotNull(message = "Vehicle ID cannot be null")
    private Long vehicleId;

    @NotNull(message = "Rental plan cannot be null")
    private RentalPlan rentalPlan;

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public RentalPlan getRentalPlan() {
        return rentalPlan;
    }

    public void setRentalPlan(RentalPlan rentalPlan) {
        this.rentalPlan = rentalPlan;
    }
}
