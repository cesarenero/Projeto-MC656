//
//  VehicleListModelTests.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 01/08/2024.
//

import Testing
@testable import Projeto_MC656

@MainActor
struct VehicleListModelTests {
    var sut: VehicleListModel!
    var mockVehicleListService: MockVehicleListService!

    init() {
        mockVehicleListService = MockVehicleListService()
        sut = VehicleListModel(service: mockVehicleListService) // DI for service
    }

    @Test func testLoadVehiclesSuccess() async {
        // Arrange
        let mockVehicles = [
            Vehicle(id: UUID(), name: "Bike 1", photo: "bicycle", isAvailable: true, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil),
            Vehicle(id: UUID(), name: "Scooter 1", photo: "scooter", isAvailable: false, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil)
        ]
        mockVehicleListService.fetchVehiclesShouldSucceed = true
        mockVehicleListService.vehiclesToReturn = mockVehicles

        // Act
        await sut.loadVehicles()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == nil)
        #expect(sut.vehicles.count == 2)
        #expect(sut.vehicles[0].name == "Bike 1")
    }

    @Test func testLoadVehiclesFailure() async {
        // Arrange
        mockVehicleListService.fetchVehiclesShouldSucceed = false
        mockVehicleListService.serviceError = .serverError("Failed to fetch")

        // Act
        await sut.loadVehicles()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == "Falha ao carregar os veículos. Tente novamente mais tarde.")
        #expect(sut.vehicles.isEmpty == true)
    }

    @Test func testLoadVehiclesEmpty() async {
        // Arrange
        mockVehicleListService.fetchVehiclesShouldSucceed = true
        mockVehicleListService.vehiclesToReturn = []

        // Act
        await sut.loadVehicles()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == nil)
        #expect(sut.vehicles.isEmpty == true)
    }

    @Test func testIsLoadingStateDuringLoadVehicles() async {
        // Arrange
        mockVehicleListService.fetchVehiclesShouldSucceed = true
        mockVehicleListService.vehiclesToReturn = []
        mockVehicleListService.delay = 0.1

        // Act
        let loadTask = Task { await sut.loadVehicles() }

        try! await Task.sleep(nanoseconds: UInt64(0.01 * 1_000_000_000))
        #expect(sut.isLoading == true)

        await loadTask.value

        #expect(sut.isLoading == false)
    }
}

// Mock VehicleListService
class MockVehicleListService: VehicleListServiceProtocol {
    var fetchVehiclesShouldSucceed = true
    var vehiclesToReturn: [Vehicle] = []
    var vehicleDetailToReturn: Vehicle?
    var serviceError: ServiceError?
    var delay: TimeInterval = 0

    func fetchVehicles() async throws -> [Vehicle] {
        if delay > 0 { try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000)) }
        if fetchVehiclesShouldSucceed { return vehiclesToReturn }
        throw serviceError ?? ServiceError.serverError("Unknown error")
    }

    func getVehicleDetails(vehicleId: Int) async throws -> Vehicle {
        if delay > 0 { try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000)) }
        if let vehicle = vehicleDetailToReturn { return vehicle }
        throw serviceError ?? ServiceError.serverError("Unknown error for details")
    }
}

// Protocol for VehicleListService
protocol VehicleListServiceProtocol {
    func fetchVehicles() async throws -> [Vehicle]
    func getVehicleDetails(vehicleId: Int) async throws -> Vehicle // Added as per service file
}

// Make real VehicleListService conform
// extension VehicleListService: VehicleListServiceProtocol {}
