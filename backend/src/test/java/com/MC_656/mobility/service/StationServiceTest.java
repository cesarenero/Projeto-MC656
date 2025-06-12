package com.MC_656.mobility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(); // Initialize the service
    }

    @Test
    @DisplayName("Should successfully register a valid station (Valid Equivalence Class)")
    void registerValidStation() {
        String name = "Station Alpha";
        String address = "123 Main St, Anytown";
        int capacity = 20; // Valid capacity
        double latitude = -22.9071; // Valid latitude
        double longitude = -47.0632; // Valid longitude

        assertTrue(stationService.registerStation(name, address, capacity, latitude, longitude),
                "Registration should succeed for valid station data.");
    }

    @ParameterizedTest
    @CsvSource({
            ", 123 Main St, 10, -22.9071, -47.0632, Null name", // Invalid: Null name
            "'', 123 Main St, 10, -22.9071, -47.0632, Empty name", // Invalid: Empty name
            "A, 123 Main St, 10, -22.9071, -47.0632, Name too short (assuming min length > 1)", // Boundary: Potentially too short name
            "This station name is way too long and definitely should not be accepted by the system as it exceeds the maximum length allowed for station names in our database schema or business rules, 123 Main St, 10, -22.9071, -47.0632, Name too long" // Boundary: Name too long
    })
    @DisplayName("Should reject registration with invalid station names (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidNames(String name, String address, int capacity, double latitude, double longitude, String testCaseName) {
        assertFalse(stationService.registerStation(name, address, capacity, latitude, longitude),
                "Registration should fail for: " + testCaseName);
    }

    @Test
    @DisplayName("Should reject registration with null address")
    void rejectRegistrationWithNullAddress() {
        assertFalse(stationService.registerStation("Station Null Address", null, 10, -22.9071, -47.0632),
                "Registration should fail for null address.");
    }

    @Test
    @DisplayName("Should reject registration with empty address")
    void rejectRegistrationWithEmptyAddress() {
        assertFalse(stationService.registerStation("Station Empty Address", "", 10, -22.9071, -47.0632),
                "Registration should fail for empty address.");
    }


    @ParameterizedTest
    @ValueSource(ints = {
            -1, // Invalid Equivalence Class: Negative capacity
            0  // Boundary Value: Zero capacity (often invalid for physical capacity)
    })
    @DisplayName("Should reject registration with invalid station capacities (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidCapacities(int invalidCapacity) {
        String name = "Test Station Capacity";
        String address = "456 Capacity Rd";
        double latitude = -22.8000;
        double longitude = -47.0500;

        assertFalse(stationService.registerStation(name, address, invalidCapacity, latitude, longitude),
                "Registration should fail for capacity: " + invalidCapacity);
    }

    @Test
    @DisplayName("Should accept registration with minimum valid capacity (Boundary)")
    void acceptRegistrationWithMinValidCapacity() {
        assertTrue(stationService.registerStation("Station Min Cap", "789 MinCap Ave", 1, -22.9071, -47.0632),
                "Registration should succeed for minimum valid capacity (1).");
    }

    @ParameterizedTest
    @CsvSource({
            // Latitude boundaries and invalid classes
            "-90.1, -47.0632, Invalid latitude (too low, below -90)",
            "90.1, -47.0632, Invalid latitude (too high, above 90)",
            "-90.0, -47.0632, Valid latitude (boundary -90)",   // Valid Boundary
            "90.0, -47.0632, Valid latitude (boundary 90)",     // Valid Boundary
            // Longitude boundaries and invalid classes
            "-22.9071, -180.1, Invalid longitude (too low, below -180)",
            "-22.9071, 180.1, Invalid longitude (too high, above 180)",
            "-22.9071, -180.0, Valid longitude (boundary -180)", // Valid Boundary
            "-22.9071, 180.0, Valid longitude (boundary 180)"   // Valid Boundary
    })
    @DisplayName("Should handle registration with boundary and invalid coordinates (Equivalence Classes & Boundaries)")
    void handleRegistrationWithBoundaryAndInvalidCoordinates(double latitude, double longitude, String testCaseDesc) {
        String name = "Test Station Coordinates";
        String address = "321 Geo St";
        int capacity = 15;

        // The stub StationService.registerStation has basic checks: lat < -90 || lat > 90 || lon < -180 || lon > 180 is false
        // So, values exactly on -90, 90, -180, 180 should be true (valid)
        // Values outside these ranges should be false (invalid)
        if (testCaseDesc.startsWith("Invalid")) {
            assertFalse(stationService.registerStation(name, address, capacity, latitude, longitude),
                    "Registration should fail for: " + testCaseDesc);
        } else {
            assertTrue(stationService.registerStation(name, address, capacity, latitude, longitude),
                    "Registration should succeed for: " + testCaseDesc);
        }
    }

    @Test
    @DisplayName("Should successfully update station capacity with valid new capacity (Valid Equivalence Class)")
    void updateStationCapacityValid() {
        String stationId = "STA001_VALID_CAP_UPDATE";
        int newCapacity = 15; // Valid new capacity

        assertTrue(stationService.updateStationCapacity(stationId, newCapacity),
                "Capacity update should succeed for valid new capacity.");
    }

    @Test
    @DisplayName("Should successfully update station capacity to minimum valid (Boundary)")
    void updateStationCapacityToMinimum() {
        String stationId = "STA001_MIN_CAP_UPDATE";
        int newCapacity = 0; // Assuming 0 is a valid "empty" capacity post-creation

        assertTrue(stationService.updateStationCapacity(stationId, newCapacity),
                "Capacity update should succeed for minimum valid capacity (0).");
    }


    @ParameterizedTest
    @ValueSource(ints = {
            -1,  // Invalid Equivalence Class: Negative capacity
            -10  // Invalid Equivalence Class: Another negative capacity
    })
    @DisplayName("Should reject invalid capacity updates (Invalid Equivalence Class & Boundary)")
    void rejectInvalidCapacityUpdates(int invalidCapacity) {
        String stationId = "STA002_INVALID_CAP_UPDATE";

        assertFalse(stationService.updateStationCapacity(stationId, invalidCapacity),
                "Capacity update should fail for invalid capacity: " + invalidCapacity);
    }

    @Test
    @DisplayName("Should reject capacity update for null station ID")
    void rejectCapacityUpdateForNullStationId() {
        assertFalse(stationService.updateStationCapacity(null, 10),
                "Capacity update should fail for null station ID.");
    }

    @Test
    @DisplayName("Should reject capacity update for empty station ID")
    void rejectCapacityUpdateForEmptyStationId() {
        assertFalse(stationService.updateStationCapacity("", 10),
                "Capacity update should fail for empty station ID.");
    }

    @Test
    @DisplayName("Should successfully check station availability for known station (Valid Equivalence Class)")
    void checkStationAvailabilityKnownStation() {
        String stationId = "STA003_KNOWN";
        // Assuming the stub returns "AVAILABLE" for known, non-empty IDs
        assertEquals("AVAILABLE", stationService.checkStationAvailability(stationId),
                "Should return 'AVAILABLE' for a known station ID.");
    }

    @Test
    @DisplayName("Should handle station availability check for null ID (Invalid Equivalence Class)")
    void checkStationAvailabilityNullId() {
        assertEquals("UNKNOWN", stationService.checkStationAvailability(null),
                "Should return 'UNKNOWN' for null station ID.");
    }

    @Test
    @DisplayName("Should handle station availability check for empty ID (Invalid Equivalence Class)")
    void checkStationAvailabilityEmptyId() {
        assertEquals("UNKNOWN", stationService.checkStationAvailability(""),
                "Should return 'UNKNOWN' for empty station ID.");
    }

    // Tests for reportStationStatus
    @ParameterizedTest
    @CsvSource({
            "STA001, 5, 10, AVAILABLE, Valid: Space available",
            "STA002, 10, 10, FULL, Valid: Station is full (Boundary)",
            "STA003, 0, 10, AVAILABLE, Valid: Station is empty (Boundary)",
            "STA004, 7, 7, FULL, Valid: Station is full (Boundary, current=capacity)"
    })
    @DisplayName("Should correctly report station status (Valid Equivalence Classes & Boundaries)")
    void reportStationStatusValid(String stationId, int currentVehicles, int capacity, String expectedStatus, String testCaseDesc) {
        assertEquals(expectedStatus, stationService.reportStationStatus(stationId, currentVehicles, capacity),
                "Status should be '" + expectedStatus + "' for: " + testCaseDesc);
    }

    @ParameterizedTest
    @CsvSource({
            ", 5, 10, INVALID_DATA, Invalid: Null station ID",
            "'', 5, 10, INVALID_DATA, Invalid: Empty station ID",
            "STA005, -1, 10, INVALID_DATA, Invalid: Negative current vehicles",
            "STA006, 5, 0, INVALID_DATA, Invalid: Zero capacity (Boundary, invalid for division/logic)",
            "STA007, 5, -5, INVALID_DATA, Invalid: Negative capacity",
            "STA008, 11, 10, INVALID_DATA, Invalid: Current vehicles exceed capacity"
    })
    @DisplayName("Should report INVALID_DATA for invalid inputs to reportStationStatus (Invalid Equivalence Classes)")
    void reportStationStatusInvalidInputs(String stationId, int currentVehicles, int capacity, String expectedStatus, String testCaseDesc) {
        assertEquals(expectedStatus, stationService.reportStationStatus(stationId, currentVehicles, capacity),
                "Status should be '" + expectedStatus + "' for: " + testCaseDesc);
    }
}
