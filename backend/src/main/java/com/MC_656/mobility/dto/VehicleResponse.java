package com.MC_656.mobility.dto;

import com.MC_656.mobility.model.Vehicle;
import com.MC_656.mobility.model.VehicleStatus;
import com.MC_656.mobility.model.VehicleType;

public class VehicleResponse {
    private Long id;
    private String make;
    private String model;
    private Integer yearManufacture;
    private String licensePlate;
    private VehicleType type;
    private VehicleStatus status;
    private Double latitude;
    private Double longitude;
    private String ownerUsername; // Or ownerId

    public VehicleResponse(Long id, String make, String model, Integer yearManufacture, String licensePlate,
                           VehicleType type, VehicleStatus status, Double latitude, Double longitude, String ownerUsername) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.yearManufacture = yearManufacture;
        this.licensePlate = licensePlate;
        this.type = type;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerUsername = ownerUsername;
    }

    public static VehicleResponse fromVehicle(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYearManufacture(),
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getStatus(),
                vehicle.getLatitude(),
                vehicle.getLongitude(),
                vehicle.getOwner() != null ? vehicle.getOwner().getUsername() : null
        );
    }

    // Getters
    public Long getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public Integer getYearManufacture() { return yearManufacture; }
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
    public VehicleStatus getStatus() { return status; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getOwnerUsername() { return ownerUsername; }

    // Setters if needed, but typically response DTOs are immutable or populated via constructor/factory
}
