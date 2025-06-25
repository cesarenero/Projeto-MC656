package com.MC_656.mobility.dto;

import com.MC_656.mobility.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public class VehicleRequest {

    @NotBlank(message = "Make cannot be blank")
    @Size(max = 50, message = "Make must be less than 50 characters")
    private String make;

    @NotBlank(message = "Model cannot be blank")
    @Size(max = 50, message = "Model must be less than 50 characters")
    private String model;

    @NotNull(message = "Year of manufacture cannot be null")
    @Min(value = 1900, message = "Year must be 1900 or later") // Basic validation for year
    private Integer yearManufacture;

    @NotBlank(message = "License plate cannot be blank")
    @Size(max = 20, message = "License plate must be less than 20 characters")
    private String licensePlate;

    @NotNull(message = "Vehicle type cannot be null")
    private VehicleType type; // Will be validated by enum conversion

    private Double latitude;
    private Double longitude;

    // Getters and Setters
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYearManufacture() {
        return yearManufacture;
    }

    public void setYearManufacture(Integer yearManufacture) {
        this.yearManufacture = yearManufacture;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
