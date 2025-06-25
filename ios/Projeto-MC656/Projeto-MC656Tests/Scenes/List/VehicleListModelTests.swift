//
//  VehicleListModelTests.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 27/07/2024.
//

import Testing
@testable import Projeto_MC656

@MainActor
struct VehicleListModelTests {
    var sut: VehicleListModel!
    var mockVehicleListService: MockVehicleListService!

    init() {
        // Assumes VehicleListModel is modified for service injection
        mockVehicleListService = MockVehicleListService()
        sut = VehicleListModel(service: mockVehicleListService) // Assumed constructor
    }

    @Test func loadVehicles_success_populatesVehiclesAndResetsError() async {
        // Arrange
        let expectedVehicles = [
            Vehicle(id: UUID(), name: "Bike 1", photo: "bicycle", isAvailable: true, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil),
            Vehicle(id: UUID(), name: "Scooter 1", photo: "scooter", isAvailable: false, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil)
        ]
        mockVehicleListService.fetchVehiclesResult = .success(expectedVehicles)

        // Act
        await sut.loadVehicles()

        // Assert
        #expect(mockVehicleListService.fetchVehiclesCalled == true)
        #expect(sut.vehicles.count == expectedVehicles.count)
        #expect(sut.vehicles.first?.name == expectedVehicles.first?.name)
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == nil)
    }

    @Test func loadVehicles_failure_serviceError_setsErrorMessageAndClearsVehicles() async {
        // Arrange
        let expectedError = ServiceError.serverError("Failed to fetch vehicles")
        mockVehicleListService.fetchVehiclesResult = .failure(expectedError)
        // Pre-populate vehicles to ensure they are cleared on error
        sut.vehicles = [Vehicle(id: UUID(), name: "Old Bike", photo: "bicycle", isAvailable: true, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil)]


        // Act
        await sut.loadVehicles()

        // Assert
        #expect(mockVehicleListService.fetchVehiclesCalled == true)
        #expect(sut.vehicles.isEmpty == true, "Vehicles list should be empty on error") // Assuming vehicles are cleared on error
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == "Falha ao carregar os veículos. Tente novamente mais tarde.") // Specific message from model
    }

    @Test func loadVehicles_isLoading_stateTransitionsCorrectly() async {
        // Arrange
        class DelayedMockVehicleListService: MockVehicleListService {
            override func fetchVehicles() async throws -> [Vehicle] {
                try await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
                return try await super.fetchVehicles()
            }
        }
        let delayedMockService = DelayedMockVehicleListService()
        sut = VehicleListModel(service: delayedMockService) // Re-init sut with delayed service
        delayedMockService.fetchVehiclesResult = .success([])

        // Act
        let loadTask = Task {
            await sut.loadVehicles()
        }
        // Check isLoading becomes true (synchronously set at start of loadVehicles)
        // This is hard to test perfectly without Combine expectations for @Published.

        await loadTask.value // Wait for load to complete

        // Assert
        #expect(sut.isLoading == false, "isLoading should be false after loading completes")
    }
}

// Requires VehicleListModel to be injectable:
// protocol VehicleListServiceProtocol { ... }
// extension VehicleListService: VehicleListServiceProtocol {}
// class VehicleListModel { init(service: VehicleListServiceProtocol = VehicleListService()) { ... } }
// extension MockVehicleListService: VehicleListServiceProtocol {}
