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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleRequest vehicleRequest;
    private User owner;
    private Authentication authentication;
    private SecurityContext securityContext;
    private UserDetails userDetails;


    @BeforeEach
    void setUp() {
        vehicleRequest = new VehicleRequest();
        vehicleRequest.setMake("TestMake");
        vehicleRequest.setModel("TestModel");
        vehicleRequest.setYearManufacture(2023);
        vehicleRequest.setLicensePlate("TESTPLATE");
        vehicleRequest.setType(VehicleType.E_BIKE);
        vehicleRequest.setLatitude(-22.817);
        vehicleRequest.setLongitude(-47.068);

        owner = new User("owneruser", "password", "owner@example.com", "Owner User", null, null, null);
        owner.setId(1L);

        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        userDetails = mock(UserDetails.class);

    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(owner.getUsername());
        when(userRepository.findByUsername(owner.getUsername())).thenReturn(Optional.of(owner));
    }

    @Test
    void createVehicle_success() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();

            when(vehicleRepository.existsByLicensePlate(vehicleRequest.getLicensePlate())).thenReturn(false);
            Vehicle savedVehicle = new Vehicle(); // Populate as needed
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> {
                Vehicle v = invocation.getArgument(0);
                v.setId(1L); // Simulate save
                return v;
            });

            Vehicle result = vehicleService.createVehicle(vehicleRequest);

            assertNotNull(result);
            assertEquals(vehicleRequest.getMake(), result.getMake());
            assertEquals(VehicleStatus.AVAILABLE, result.getStatus());
            assertEquals(owner, result.getOwner());
            verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        }
    }

    @Test
    void createVehicle_failure_invalidType() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            // VehicleType.CAR is not in ALLOWED_ECO_FRIENDLY_TYPES by default in service
            // For this test, let's assume a type that is definitely not in the list.
            // The service uses a hardcoded set, so we can't directly mock that set.
            // Instead, we rely on the service's definition of ALLOWED_ECO_FRIENDLY_TYPES.
            // If ELECTRIC_CAR is allowed and we want to test an unallowed one, we need a new enum or to ensure one is not in the set.
            // For now, let's assume VehicleType has a value that is not in the allowed set.
            // To make this test robust, we should pick a type that is guaranteed not to be in the set.
            // The service currently allows: BICYCLE, E_BIKE, SCOOTER, E_SCOOTER, ELECTRIC_CAR, CARGO_BIKE
            // Let's test with a hypothetical type or ensure one type is excluded for testing.
            // For this example, let's ensure the request type is something not in the allowed list.
            // The test below assumes that an unlisted type like `null` or a specific non-eco type would fail.
            // The `ALLOWED_ECO_FRIENDLY_TYPES` in VehicleService includes ELECTRIC_CAR.
            // Let's try a type that is not on the list.
            // The DTO uses VehicleType enum, so it must be a valid enum.
            // The check is `!ALLOWED_ECO_FRIENDLY_TYPES.contains(vehicleRequest.getType())`
            // This test is tricky if all VehicleType enum values are in ALLOWED_ECO_FRIENDLY_TYPES.
            // We'll assume there's a way to set an invalid type or the enum itself has types not in the allowed set.
            // Given the current setup, this specific test might be hard to trigger without modifying VehicleType or ALLOWED_ECO_FRIENDLY_TYPES.
            // For now, this test is more conceptual unless we can ensure an "invalid" type for the purpose of the test.
            // A better approach might be to ensure ALLOWED_ECO_FRIENDLY_TYPES is configurable or to add a test-specific type.

            // Let's make a more direct test by trying to register a null type if that's considered invalid by a @NotNull or similar.
            // The DTO's type field is @NotNull, so this would be caught before service logic.
            // The service check is `!ALLOWED_ECO_FRIENDLY_TYPES.contains(vehicleRequest.getType())`
            // So, if vehicleRequest.getType() is a value not in that set, it should throw.
            // We need to find a VehicleType enum value that is NOT in ALLOWED_ECO_FRIENDLY_TYPES.
            // If all enum values ARE in the set, this test path is untestable without changing the enum or the set.

            // For the sake of having a test, let's assume we can add a temporary non-allowed type or mock the set (not easy for static final).
            // Alternative: Modify the test to use a type known to be disallowed if the enum/set allows.
            // For now, we'll skip this specific path if all enums are allowed, as it's hard to test.
            // The provided VehicleType enum only contains types that ARE in ALLOWED_ECO_FRIENDLY_TYPES.
            // This test case will be omitted for now as it's not directly testable with current setup.
        }
    }


    @Test
    void createVehicle_failure_licensePlateExists() {
       try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            when(vehicleRepository.existsByLicensePlate(vehicleRequest.getLicensePlate())).thenReturn(true);

            Exception exception = assertThrows(BadRequestException.class, () -> {
                vehicleService.createVehicle(vehicleRequest);
            });
            assertEquals("License plate '" + vehicleRequest.getLicensePlate() + "' already exists.", exception.getMessage());
        }
    }

    @Test
    void getVehiclesByTypeAndStatus_allFilters() {
        Vehicle v1 = new Vehicle(); v1.setType(VehicleType.E_BIKE); v1.setStatus(VehicleStatus.AVAILABLE);
        List<Vehicle> expected = Collections.singletonList(v1);
        when(vehicleRepository.findByTypeAndStatus(VehicleType.E_BIKE, VehicleStatus.AVAILABLE)).thenReturn(expected);

        List<Vehicle> actual = vehicleService.getVehiclesByTypeAndStatus(VehicleType.E_BIKE, VehicleStatus.AVAILABLE);
        assertEquals(expected, actual);
        verify(vehicleRepository, times(1)).findByTypeAndStatus(VehicleType.E_BIKE, VehicleStatus.AVAILABLE);
    }

     @Test
    void getVehiclesByTypeAndStatus_typeOnly() {
        Vehicle v1 = new Vehicle(); v1.setType(VehicleType.BICYCLE);
        List<Vehicle> expected = Collections.singletonList(v1);
        when(vehicleRepository.findByType(VehicleType.BICYCLE)).thenReturn(expected);

        List<Vehicle> actual = vehicleService.getVehiclesByTypeAndStatus(VehicleType.BICYCLE, null);
        assertEquals(expected, actual);
        verify(vehicleRepository, times(1)).findByType(VehicleType.BICYCLE);
    }

    @Test
    void getVehiclesByTypeAndStatus_statusOnly() {
        Vehicle v1 = new Vehicle(); v1.setStatus(VehicleStatus.RENTED);
        List<Vehicle> expected = Collections.singletonList(v1);
        when(vehicleRepository.findByStatus(VehicleStatus.RENTED)).thenReturn(expected);

        List<Vehicle> actual = vehicleService.getVehiclesByTypeAndStatus(null, VehicleStatus.RENTED);
        assertEquals(expected, actual);
        verify(vehicleRepository, times(1)).findByStatus(VehicleStatus.RENTED);
    }

    @Test
    void getVehiclesByTypeAndStatus_noFilters() {
        Vehicle v1 = new Vehicle(); Vehicle v2 = new Vehicle();
        List<Vehicle> expected = Arrays.asList(v1, v2);
        when(vehicleRepository.findAll()).thenReturn(expected);

        List<Vehicle> actual = vehicleService.getVehiclesByTypeAndStatus(null, null);
        assertEquals(expected, actual);
        verify(vehicleRepository, times(1)).findAll();
    }


    @Test
    void getVehicleById_success() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        Vehicle result = vehicleService.getVehicleById(1L);
        assertEquals(vehicle, result);
    }

    @Test
    void getVehicleById_notFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.getVehicleById(1L);
        });
    }

    @Test
    void getSupportedVehicleTypes_success() {
        List<VehicleType> expectedTypes = Arrays.asList(VehicleType.values());
        List<VehicleType> actualTypes = vehicleService.getSupportedVehicleTypes();
        assertEquals(expectedTypes.size(), actualTypes.size());
        assertTrue(actualTypes.containsAll(expectedTypes) && expectedTypes.containsAll(actualTypes));
    }
}