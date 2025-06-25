package com.MC_656.mobility.service;
import com.MC_656.mobility.model.VehicleStatus;
import java.util.Optional;
import com.MC_656.mobility.model.Rental;
import com.MC_656.mobility.model.RentalPlan;
import com.MC_656.mobility.model.User;
import com.MC_656.mobility.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.MC_656.mobility.dto.StartRentalRequest;
import com.MC_656.mobility.exception.BadRequestException;
import com.MC_656.mobility.exception.ResourceNotFoundException;
import com.MC_656.mobility.repository.RentalRepository;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @InjectMocks // Using InjectMocks to test the calculateCost method directly, which is private.
                 // For a public method, I'd mock dependencies if any were used by it.
                 // Since calculateCost is private, I'll use a public wrapper or test through endRental.
                 // For simplicity here, I'll make calculateCost package-private or use reflection if needed,
                 // or test it via its public calling method (endRental).
                 // Let's assume calculateCost is refactored to be testable or tested via endRental.
                 // For this example, I'll test a conceptual public version of calculateCost or make it package-private.
                 // To make it directly testable without Spring context, I'll call it directly.
    private RentalService rentalService;

    // For the actual private method, this direct test is not possible without reflection or making it package-private.
    // Let's adjust RentalService to make calculateCost package-private for testing.
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private UserRepository userRepository;

    // Mocks for SecurityContext
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;


    private User testUser;
    private Vehicle testVehicle;
    private StartRentalRequest startRentalRequest;

    @BeforeEach
    void setUp() {
        // No longer new RentalService() directly, rely on @InjectMocks and @Mock for dependencies
        testUser = new User("testuser", "ValidPassword1!", "test@example.com", "Test User", null, " (11) 99999-9999", "123.456.789-00");
        testUser.setId(1L); // Important for comparisons
        testVehicle = new Vehicle("TestMake", "TestModel", 2023, "TESTPLATE", com.MC_656.mobility.model.VehicleType.E_BIKE, testUser);
        testVehicle.setId(100L);
        testVehicle.setStatus(VehicleStatus.AVAILABLE);

        startRentalRequest = new StartRentalRequest();
        startRentalRequest.setVehicleId(testVehicle.getId());
        startRentalRequest.setRentalPlan(RentalPlan.DAILY);

        // Setup for SecurityContext if RentalService methods use it.
        // Assuming rentalService uses SecurityContextHolder.getContext().getAuthentication().getName()
        // This needs to be done within each test method that triggers security context access, using try-with-resources for MockedStatic
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext); // For static access
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    }


    private Rental createRentalForCostTest(LocalDateTime startTime, LocalDateTime endTime, RentalPlan plan) {
        Rental rental = new Rental(testUser, testVehicle, startTime, plan);
        rental.setEndTime(endTime);
        // No need to set ID or save for this specific cost calculation test
        return rental;
    }

    // Test cases for DAILY plan (Cost Calculation)
    @Test
    void testCalculateCost_Daily_LessThanThreshold_ShouldBeZeroOrMin() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 10, 30, 0); // 30 minutes
        Rental rental = createRentalForCostTest(start, end, RentalPlan.DAILY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("0.00"), cost, "Cost for 30 mins daily should be 0.00 or a minimum fee");
    }

    @Test
    void testCalculateCost_Daily_JustOverThreshold_ShouldBeOneDay() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 11, 5, 0); // 1 hour 5 minutes
        Rental rental = createRentalForCostTest(start, end, RentalPlan.DAILY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("20.00"), cost, "Cost for 1hr 5mins daily should be 1 day rate");
    }

    @Test
    void testCalculateCost_Daily_LessThanOneDay_ShouldBeOneDayRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 15, 0, 0); // 5 hours
        Rental rental = createRentalForCostTest(start, end, RentalPlan.DAILY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("20.00"), cost, "Cost for 5 hours daily should be 1 day rate");
    }

    @Test
    void testCalculateCost_Daily_ExactlyOneDay_ShouldBeOneDayRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 10, 0, 0); // 24 hours
        Rental rental = createRentalForCostTest(start, end, RentalPlan.DAILY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("20.00"), cost, "Cost for 1 day daily should be 1 day rate");
    }

    @Test
    void testCalculateCost_Daily_OneDayAndAHalf_ShouldBeTwoDaysRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 22, 0, 0); // 1 day and 12 hours
        Rental rental = createRentalForCostTest(start, end, RentalPlan.DAILY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("40.00"), cost, "Cost for 1.5 days daily should be 2 days rate");
    }

    // Test cases for WEEKLY plan
    @Test
    void testCalculateCost_Weekly_LessThanOneWeek_ShouldBeOneWeekRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 5, 10, 0, 0); // 4 days
        Rental rental = createRentalForCostTest(start, end, RentalPlan.WEEKLY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("100.00"), cost, "Cost for 4 days weekly should be 1 week rate");
    }

    @Test
    void testCalculateCost_Weekly_ExactlyOneWeek_ShouldBeOneWeekRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 8, 10, 0, 0); // 7 days
        Rental rental = createRentalForCostTest(start, end, RentalPlan.WEEKLY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("100.00"), cost, "Cost for 1 week weekly should be 1 week rate");
    }

    @Test
    void testCalculateCost_Weekly_OneWeekAndPartial_ShouldBeTwoWeeksRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 10, 10, 0, 0); // 9 days
        Rental rental = createRentalForCostTest(start, end, RentalPlan.WEEKLY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("200.00"), cost, "Cost for 9 days weekly should be 2 weeks rate");
    }

    // Test cases for MONTHLY plan
    @Test
    void testCalculateCost_Monthly_LessThanOneMonth_ShouldBeOneMonthRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 15, 10, 0, 0); // 14 days
        Rental rental = createRentalForCostTest(start, end, RentalPlan.MONTHLY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("350.00"), cost, "Cost for 14 days monthly should be 1 month rate");
    }

    @Test
    void testCalculateCost_Monthly_ExactlyThirtyDays_ShouldBeOneMonthRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 31, 10, 0, 0); // 30 days
        Rental rental = createRentalForCostTest(start, end, RentalPlan.MONTHLY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("350.00"), cost, "Cost for 30 days monthly should be 1 month rate");
    }

    @Test
    void testCalculateCost_Monthly_OneMonthAndPartial_ShouldBeTwoMonthsRate() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 2, 15, 10, 0, 0); // Approx 45 days
        Rental rental = createRentalForCostTest(start, end, RentalPlan.MONTHLY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("700.00"), cost, "Cost for ~45 days monthly should be 2 months rate");
    }

    @Test
    void testCalculateCost_Daily_ZeroDuration_ShouldBeZero() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 10, 0, 0); // 0 minutes
        Rental rental = createRentalForCostTest(start, end, RentalPlan.DAILY);
        BigDecimal cost = rentalService.calculateCost(rental);
        assertEquals(new BigDecimal("0.00"), cost, "Cost for 0 mins daily should be 0.00");
    }

    // Tests for startRental
    @Test
    void startRental_success() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();

            when(vehicleRepository.findById(testVehicle.getId())).thenReturn(Optional.of(testVehicle));
            when(rentalRepository.findByUserAndEndTimeIsNull(testUser)).thenReturn(Optional.empty());
            when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));


            Rental result = rentalService.startRental(startRentalRequest);

            assertNotNull(result);
            assertEquals(testUser, result.getUser());
            assertEquals(testVehicle, result.getVehicle());
            assertEquals(VehicleStatus.RENTED, testVehicle.getStatus());
            assertNotNull(result.getStartTime());
            assertEquals(startRentalRequest.getRentalPlan(), result.getRentalPlan());

            verify(vehicleRepository, times(1)).save(testVehicle);
            verify(rentalRepository, times(1)).save(any(Rental.class));
        }
    }

    @Test
    void startRental_failure_vehicleNotFound() {
         try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            when(vehicleRepository.findById(testVehicle.getId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> rentalService.startRental(startRentalRequest));
        }
    }

    @Test
    void startRental_failure_vehicleNotAvailable() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            testVehicle.setStatus(VehicleStatus.RENTED);
            when(vehicleRepository.findById(testVehicle.getId())).thenReturn(Optional.of(testVehicle));

            assertThrows(BadRequestException.class, () -> rentalService.startRental(startRentalRequest));
        }
    }

    @Test
    void startRental_failure_userHasActiveRental() {
       try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            when(vehicleRepository.findById(testVehicle.getId())).thenReturn(Optional.of(testVehicle));
            when(rentalRepository.findByUserAndEndTimeIsNull(testUser)).thenReturn(Optional.of(new Rental())); // Active rental exists

            assertThrows(BadRequestException.class, () -> rentalService.startRental(startRentalRequest));
        }
    }

    // Tests for endRental
    @Test
    void endRental_success() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();

            Rental activeRental = new Rental(testUser, testVehicle, LocalDateTime.now().minusHours(2), RentalPlan.DAILY);
            activeRental.setId(1L); // Important for it to be "found"
            // Ensure the vehicle in the rental is the same instance as testVehicle for status update checks
            activeRental.setVehicle(testVehicle);


            when(rentalRepository.findById(activeRental.getId())).thenReturn(Optional.of(activeRental));
            when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));


            Rental result = rentalService.endRental(activeRental.getId());

            assertNotNull(result.getEndTime());
            assertNotNull(result.getTotalCost());
            assertEquals(VehicleStatus.AVAILABLE, testVehicle.getStatus()); // Vehicle status should be updated
            assertEquals(new BigDecimal("20.00"), result.getTotalCost()); // Example cost for 2 hours on daily plan

            verify(rentalRepository, times(1)).save(activeRental);
            verify(vehicleRepository, times(1)).save(testVehicle);
        }
    }

    @Test
    void endRental_failure_rentalNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            when(rentalRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> rentalService.endRental(1L));
        }
    }

    @Test
    void endRental_failure_alreadyEnded() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            Rental endedRental = new Rental(testUser, testVehicle, LocalDateTime.now().minusDays(1), RentalPlan.DAILY);
            endedRental.setId(1L);
            endedRental.setEndTime(LocalDateTime.now().minusHours(1)); // Already ended
            when(rentalRepository.findById(1L)).thenReturn(Optional.of(endedRental));

            assertThrows(BadRequestException.class, () -> rentalService.endRental(1L));
        }
    }

    @Test
    void endRental_failure_userNotAuthorized() {
       try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            // Current user is testUser (ID 1L)
            mockSecurityContext();


            User anotherUser = new User("anotheruser", "Pass!123", "another@example.com", "Another", null, null, null);
            anotherUser.setId(2L); // Different user ID

            Rental rentalByAnotherUser = new Rental(anotherUser, testVehicle, LocalDateTime.now().minusHours(1), RentalPlan.DAILY);
            rentalByAnotherUser.setId(1L);

            when(rentalRepository.findById(1L)).thenReturn(Optional.of(rentalByAnotherUser));

            assertThrows(BadRequestException.class, () -> rentalService.endRental(1L));
        }
    }

    // Tests for getRentalHistoryForCurrentUser
    @Test
    void getRentalHistoryForCurrentUser_success() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();

            List<Rental> history = Arrays.asList(new Rental(), new Rental());
            when(rentalRepository.findByUserOrderByStartTimeDesc(testUser)).thenReturn(history);

            List<Rental> result = rentalService.getRentalHistoryForCurrentUser();
            assertEquals(history, result);
            verify(rentalRepository, times(1)).findByUserOrderByStartTimeDesc(testUser);
        }
    }

    // Tests for getActiveRentalForCurrentUser
    @Test
    void getActiveRentalForCurrentUser_success_hasActive() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            Rental activeRental = new Rental();
            when(rentalRepository.findByUserAndEndTimeIsNull(testUser)).thenReturn(Optional.of(activeRental));

            Rental result = rentalService.getActiveRentalForCurrentUser();
            assertEquals(activeRental, result);
        }
    }

    @Test
    void getActiveRentalForCurrentUser_success_noActive() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            mockSecurityContext();
            when(rentalRepository.findByUserAndEndTimeIsNull(testUser)).thenReturn(Optional.empty());

            Rental result = rentalService.getActiveRentalForCurrentUser();
            assertNull(result);
        }
    }
}
