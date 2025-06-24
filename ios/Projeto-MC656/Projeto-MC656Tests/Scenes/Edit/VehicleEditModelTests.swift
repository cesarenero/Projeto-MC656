//
//  VehicleEditModelTests.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 01/08/2024.
//

import Testing
@testable import Projeto_MC656

@MainActor
struct VehicleEditModelTests {
    var sut: VehicleEditModel!
    var mockVehicleEditService: MockVehicleEditService!

    init() {
        mockVehicleEditService = MockVehicleEditService()
        // Initialize for new vehicle creation
        sut = VehicleEditModel(service: mockVehicleEditService, vehicle: nil)
    }

    // --- Test Cases for Creating a New Vehicle ---
    @Test func testSaveNewVehicleSuccess() async {
        // Arrange
        sut.name = "Nova Bicicleta"
        sut.vehicleType = .bicicleta
        sut.description = "Super bicicleta"
        sut.pickupLocation = "Parque Taquaral"
        sut.depositAmount = "50" // Assuming string input
        sut.requiresDeposit = true

        mockVehicleEditService.operationShouldSucceed = true

        // Act
        await sut.saveVehicle()

        // Assert
        #expect(mockVehicleEditService.createVehicleCalled == true)
        #expect(mockVehicleEditService.lastCreateRequest?.make == "Nova Bicicleta")
        #expect(mockVehicleEditService.lastCreateRequest?.type == VehicleType.bicicleta.rawValue)
        #expect(mockVehicleEditService.lastCreateRequest?.depositAmount == 50.0)
        // Add more assertions based on what `saveVehicle` does after successful call (e.g., state change, navigation)
    }

    @Test func testSaveNewVehicleFailure_ServiceError() async {
        // Arrange
        sut.name = "Bike Falha"
        sut.vehicleType = .patinete
        mockVehicleEditService.operationShouldSucceed = false
        mockVehicleEditService.serviceError = .serverError("Network Error")

        // Act
        await sut.saveVehicle()

        // Assert
        #expect(mockVehicleEditService.createVehicleCalled == true)
        // #expect(sut.errorMessage == ServiceError.serverError("Network Error").localizedDescription) // If model stores error
    }

    @Test func testSaveNewVehicle_NameValidation() async {
        // Arrange
        sut.name = "" // Invalid: Empty name
        sut.vehicleType = .bicicleta
        // No need to set mock service behavior if client-side validation fails first.
        // However, VehicleEditModel doesn't have explicit client-side validation yet.
        // These tests would become more relevant if client-side checks were added.

        // Act
        await sut.saveVehicle()

        // Assert
        // If client-side validation added: #expect(sut.errorMessage != nil)
        // If only service-side: #expect(mockVehicleEditService.createVehicleCalled == true) -> and service would reject
        // For now, we assume it calls the service.
        #expect(mockVehicleEditService.createVehicleCalled == true)
        // This test highlights that VehicleEditModel currently lacks client-side validation for fields like name.
    }


    // --- Test Cases for Editing an Existing Vehicle (Illustrative - if functionality is expanded) ---
    // To test editing, initialize `sut` with an existing vehicle
    // e.g., sut = VehicleEditModel(service: mockVehicleEditService, vehicle: existingVehicle)
    // Then create tests like testUpdateExistingVehicleSuccess, testUpdateExistingVehicleFailure etc.
    // This would require `VehicleEditService` to have an `updateVehicle` method.

    @Test func testNavigationTitle_NewVehicle() {
        // Arrange
        let newVehicleModel = VehicleEditModel(service: mockVehicleEditService, vehicle: nil)
        // Assert
        #expect(newVehicleModel.navigationTitle == "Cadastrar Veículo")
    }

    @Test func testNavigationTitle_EditingVehicle() {
        // Arrange
        let existingVehicle = Vehicle(id: UUID(), name: "Bike Antiga", photo: "photo", isAvailable: true, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil)
        let editingModel = VehicleEditModel(service: mockVehicleEditService, vehicle: existingVehicle)
        // Assert
        #expect(editingModel.navigationTitle == "Editar Veículo")
    }

    // Test for delete functionality (if it uses the service)
    @Test func testDeleteVehicle() async {
        // Arrange
        let vehicleId = UUID()
        let existingVehicle = Vehicle(id: vehicleId, name: "Bike para Deletar", photo: "p", isAvailable: true, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil)
        sut = VehicleEditModel(service: mockVehicleEditService, vehicle: existingVehicle)
        mockVehicleEditService.operationShouldSucceed = true // Assuming delete is a generic success

        // Act
        sut.deleteVehicle() // This is currently synchronous and just prints.
                            // If it becomes async and calls a service: await sut.deleteVehicle()

        // Assert
        // If deleteVehicle called a service method:
        // #expect(mockVehicleEditService.deleteVehicleCalledWithId == vehicleId)
        // For now, it just prints, so no service interaction to assert here.
        // This test would be more meaningful if delete involved a service call.
    }
}

// Mock VehicleEditService
class MockVehicleEditService: VehicleEditServiceProtocol {
    var operationShouldSucceed = true
    var serviceError: ServiceError?
    var delay: TimeInterval = 0

    var createVehicleCalled = false
    var lastCreateRequest: CreateVehicleRequest?
    // var updateVehicleCalled = false // For editing
    // var lastUpdateRequest: UpdateVehicleRequest? // For editing
    // var deleteVehicleCalledWithId: UUID? // For deleting

    func createVehicle(name: String, description: String?, pickupLocation: String?, type: String, yearManufacture: Int?, licensePlate: String?, latitude: Double?, longitude: Double?, accessories: [String]?, requiresId: Bool?, depositAmount: Double?) async throws {
        createVehicleCalled = true
        lastCreateRequest = CreateVehicleRequest(
            make: name, model: description ?? "", yearManufacture: yearManufacture ?? 0,
            licensePlate: licensePlate ?? "", type: type, latitude: latitude ?? 0, longitude: longitude ?? 0,
            accessories: accessories, requiresId: requiresId, depositAmount: depositAmount
        )
        if delay > 0 { try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000)) }
        if !operationShouldSucceed {
            throw serviceError ?? ServiceError.serverError("Mock create vehicle error")
        }
    }

    // func updateVehicle(...) async throws { /* ... */ }
    // func deleteVehicle(id: UUID) async throws { /* ... */ }
}

// Protocol for VehicleEditService
protocol VehicleEditServiceProtocol {
    func createVehicle(
        name: String,
        description: String?,
        pickupLocation: String?,
        type: String, // From VehicleType.rawValue
        yearManufacture: Int?,
        licensePlate: String?,
        latitude: Double?,
        longitude: Double?,
        accessories: [String]?,
        requiresId: Bool?,
        depositAmount: Double?
    ) async throws

    // Add methods for update and delete if they are implemented in the service
    // func updateVehicle(...) async throws
    // func deleteVehicle(id: UUID) async throws
}

// Make real VehicleEditService conform
// extension VehicleEditService: VehicleEditServiceProtocol {}
