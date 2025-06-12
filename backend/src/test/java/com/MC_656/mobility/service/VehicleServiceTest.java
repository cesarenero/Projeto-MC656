package com.MC_656.mobility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService();
    }

    @Test
    @DisplayName("Should successfully register a valid vehicle (Valid Equivalence Class)")
    void registerValidVehicle() {
        String name = "Bike 001"; // Valid name
        String type = "BICYCLE"; // Valid type from ALLOWED_TYPES
        String location = "Station A1"; // Valid location (meets length)
        String status = "AVAILABLE"; // Valid status from ALLOWED_STATUSES

        assertTrue(vehicleService.registerVehicle(name, type, location, status),
                "Registration should succeed for valid vehicle data.");
    }

    @Test
    @DisplayName("Should reject registration if vehicle name already exists")
    void rejectRegistrationWithExistingName() {
        String name = "Bike 001_existing"; // Configured as duplicate in stub
        String type = "BICYCLE";
        String location = "Station B";
        String status = "AVAILABLE";

        assertFalse(vehicleService.registerVehicle(name, type, location, status),
                "Registration should fail if vehicle name already exists.");
    }

    @ParameterizedTest
    @CsvSource({
            "'', BICYCLE, Station A1, AVAILABLE, Empty name",
            "A, BICYCLE, Station A1, AVAILABLE, Name too short (A)",
            "This vehicle name is way too long and should not be accepted by the system as it exceeds the maximum length allowed for vehicle names in our database because it is simply too verbose, BICYCLE, Station A1, AVAILABLE, Name too long"
    })
    @DisplayName("Should reject registration with invalid vehicle names (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidNames(String name, String type, String location, String status, String testCase) {
        assertFalse(vehicleService.registerVehicle(name, type, location, status),
                "Registration should fail for: " + testCase);
    }

    @Test
    @DisplayName("Should reject registration with null vehicle name")
    void rejectRegistrationWithNullName() {
        assertFalse(vehicleService.registerVehicle(null, "BICYCLE", "Station A1", "AVAILABLE"),
                "Registration should fail for null vehicle name.");
    }


    @ParameterizedTest
    @CsvSource({
            "Bike 002, INVALID_TYPE, Station A1, AVAILABLE, Invalid vehicle type (not in ALLOWED_TYPES)",
            "Bike 003, CAR, Station A1, AVAILABLE, Not supported type (CAR)",
            "Bike 004, '', Station A1, AVAILABLE, Empty vehicle type",
            "Bike 005, null, Station A1, AVAILABLE, Null vehicle type"
    })
    @DisplayName("Should reject registration with invalid vehicle types (Equivalence Classes)")
    void rejectRegistrationWithInvalidTypes(String name, String type, String location, String status, String testCase) {
        if ("null".equals(type)) { // CsvSource converts null to "null" string
            assertFalse(vehicleService.registerVehicle(name, null, location, status),
                    "Registration should fail for null type.");
        } else {
            assertFalse(vehicleService.registerVehicle(name, type, location, status),
                    "Registration should fail for: " + testCase);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bike 006, BICYCLE, '', AVAILABLE, Empty location",
            "Bike 007, BICYCLE, A, AVAILABLE, Location too short (A)", // 'A' is explicitly invalid in stub
            "Bike 008, BICYCLE, null, AVAILABLE, Null location",
            "Bike 009, BICYCLE, This location name is way too long and should not be accepted by the system as it exceeds the maximum length allowed for location names in our database which is typically quite restrictive for identifiers, AVAILABLE, Location too long"
    })
    @DisplayName("Should reject registration with invalid locations (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidLocations(String name, String type, String location, String status, String testCase) {
        if ("null".equals(location)) {
            assertFalse(vehicleService.registerVehicle(name, type, null, status),
                    "Registration should fail for null location.");
        } else {
            assertFalse(vehicleService.registerVehicle(name, type, location, status),
                    "Registration should fail for: " + testCase);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bike 010, BICYCLE, Station B1, INVALID_STATUS, Invalid status (not in ALLOWED_STATUSES)",
            "Bike 011, BICYCLE, Station B1, '', Empty status",
            "Bike 012, BICYCLE, Station B1, null, Null status"
    })
    @DisplayName("Should reject registration with invalid status (Equivalence Classes)")
    void rejectRegistrationWithInvalidStatus(String name, String type, String location, String status, String testCase) {
        if ("null".equals(status)) {
            assertFalse(vehicleService.registerVehicle(name, type, location, null),
                    "Registration should fail for null status.");
        } else {
            assertFalse(vehicleService.registerVehicle(name, type, location, status),
                    "Registration should fail for: " + testCase);
        }
    }

    // Test status updates
    // The stub for updateVehicleStatus is very simple: it checks if vehicleName and newStatus are valid.
    // It does not model current status or transitions. So tests will reflect this simplicity.
    @ParameterizedTest
    @CsvSource({
            "BikeToUpdate1, AVAILABLE, Valid status update to AVAILABLE",
            "BikeToUpdate2, IN_USE, Valid status update to IN_USE",
            "BikeToUpdate3, MAINTENANCE, Valid status update to MAINTENANCE",
            "BikeToUpdate4, UNAVAILABLE, Valid status update to UNAVAILABLE"
    })
    @DisplayName("Should allow updates to valid statuses")
    void allowValidStatusUpdates(String vehicleName, String newStatus, String testCase) {
        assertTrue(vehicleService.updateVehicleStatus(vehicleName, newStatus),
                "Status update should succeed for: " + testCase);
    }

    @ParameterizedTest
    @CsvSource({
            "BikeToUpdate5, INVALID_S, Invalid status string",
            "BikeToUpdate6, '', Empty status string",
            "BikeToUpdate7, null, Null status string"
    })
    @DisplayName("Should reject updates to invalid statuses")
    void rejectInvalidStatusUpdates(String vehicleName, String newStatus, String testCase) {
        if ("null".equals(newStatus)) {
            assertFalse(vehicleService.updateVehicleStatus(vehicleName, null),
                    "Status update should fail for null status.");
        } else {
            assertFalse(vehicleService.updateVehicleStatus(vehicleName, newStatus),
                    "Status update should fail for: " + testCase);
        }
    }

    @Test
    @DisplayName("Should reject status update for null vehicle name")
    void rejectStatusUpdateForNullVehicleName() {
        assertFalse(vehicleService.updateVehicleStatus(null, "AVAILABLE"),
                "Status update should fail for null vehicle name.");
    }

    @Test
    @DisplayName("Should reject status update for empty vehicle name")
    void rejectStatusUpdateForEmptyVehicleName() {
        assertFalse(vehicleService.updateVehicleStatus("", "AVAILABLE"),
                "Status update should fail for empty vehicle name.");
    }

    @Test
    @DisplayName("Should reject status update for too short vehicle name ('A')")
    void rejectStatusUpdateForTooShortVehicleName() {
        assertFalse(vehicleService.updateVehicleStatus("A", "AVAILABLE"),
                "Status update should fail for too short vehicle name 'A'.");
    }

    // Test location updates
    @Test
    @DisplayName("Should successfully update vehicle location with valid new location")
    void updateVehicleLocationValid() {
        String vehicleName = "VEH001_LOC_UPDATE";
        String newLocation = "Station B2"; // Valid new location

        assertTrue(vehicleService.updateVehicleLocation(vehicleName, newLocation),
                "Location update should succeed for valid new location.");
    }

    @ParameterizedTest
    @CsvSource({
            "VEH002_LOC_INV, '', Empty location",
            "VEH003_LOC_INV, A, Location too short (A)", // 'A' is explicitly invalid in stub
            "VEH004_LOC_INV, null, Null location",
            "VEH005_LOC_INV, This location name is way too long and should not be accepted by the system as it exceeds the maximum length allowed for location names in our database which is typically quite restrictive for identifiers, Location too long"
    })
    @DisplayName("Should reject invalid location updates (Equivalence Classes & Boundaries)")
    void rejectInvalidLocationUpdates(String vehicleName, String invalidLocation, String testCase) {
        if ("null".equals(invalidLocation)) {
            assertFalse(vehicleService.updateVehicleLocation(vehicleName, null),
                    "Location update should fail for null location.");
        } else {
            assertFalse(vehicleService.updateVehicleLocation(vehicleName, invalidLocation),
                    "Location update should fail for: " + testCase);
        }
    }

    @Test
    @DisplayName("Should reject location update for null vehicle name")
    void rejectLocationUpdateForNullVehicleName() {
        assertFalse(vehicleService.updateVehicleLocation(null, "Station C"),
                "Location update should fail for null vehicle name.");
    }

    @Test
    @DisplayName("Should reject location update for empty vehicle name")
    void rejectLocationUpdateForEmptyVehicleName() {
        assertFalse(vehicleService.updateVehicleLocation("", "Station D"),
                "Location update should fail for empty vehicle name.");
    }

    @Test
    @DisplayName("Should reject location update for too short vehicle name ('A')")
    void rejectLocationUpdateForTooShortVehicleName() {
        assertFalse(vehicleService.updateVehicleLocation("A", "Station E"),
                "Location update should fail for too short vehicle name 'A'.");
    }

    // Test getVehicleDetails (simple test as stub is simple)
    @Test
    @DisplayName("Should get details for a known vehicle")
    void getDetailsForKnownVehicle() {
        String vehicleName = "Bike 001"; // Known in stub
        String expectedDetails = "Vehicle{name='Bike 001', type='BICYCLE', location='Station A', status='AVAILABLE'}";
        assertEquals(expectedDetails, vehicleService.getVehicleDetails(vehicleName));
    }

    @Test
    @DisplayName("Should return null for an unknown vehicle details request")
    void getDetailsForUnknownVehicle() {
        String vehicleName = "UnknownBike99";
        assertNull(vehicleService.getVehicleDetails(vehicleName));
    }

    @Test
    @DisplayName("Should return null for get details with invalid vehicle name")
    void getDetailsWithInvalidName() {
        assertNull(vehicleService.getVehicleDetails(""), "Should be null for empty name");
        assertNull(vehicleService.getVehicleDetails("A"), "Should be null for short name 'A'");
        assertNull(vehicleService.getVehicleDetails(null), "Should be null for null name");
    }
}
