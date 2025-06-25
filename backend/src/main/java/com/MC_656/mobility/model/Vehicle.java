package com.MC_656.mobility.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

// VehicleType and VehicleStatus enums are now in their own files.
// Imports for them are not strictly needed if they are in the same package,
// but would be if they were in different packages.
// For clarity, if they were in a sub-package like com.MC_656.mobility.model.enums,
// you'd add:
// import com.MC_656.mobility.model.enums.VehicleType;
// import com.MC_656.mobility.model.enums.VehicleStatus;
// Since they are in com.MC_656.mobility.model directly, no import is needed here.

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String make; // e.g., Honda, Tesla

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String model; // e.g., Civic, Model S

    @NotNull
    @Column(nullable = false)
    private Integer yearManufacture; // Year of manufacture

    @NotBlank
    @Size(max = 20)
    @Column(unique = true, nullable = false)
    private String licensePlate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    // For geolocation
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Constructors
    public Vehicle() {
    }

    public Vehicle(String make, String model, Integer yearManufacture, String licensePlate, VehicleType type, User owner) {
        this.make = make;
        this.model = model;
        this.yearManufacture = yearManufacture;
        this.licensePlate = licensePlate;
        this.type = type;
        this.owner = owner;
        this.status = VehicleStatus.AVAILABLE; // Default status
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id) &&
               Objects.equals(licensePlate, vehicle.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, licensePlate);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
               "id=" + id +
               ", make='" + make + '\'' +
               ", model='" + model + '\'' +
               ", yearManufacture=" + yearManufacture +
               ", licensePlate='" + licensePlate + '\'' +
               ", type=" + type +
               ", status=" + status +
               ", owner=" + (owner != null ? owner.getUsername() : "null") +
               '}';
    }
}
