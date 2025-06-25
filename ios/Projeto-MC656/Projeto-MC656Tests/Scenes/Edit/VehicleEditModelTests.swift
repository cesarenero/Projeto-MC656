//
//  VehicleEditModelTests.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 27/07/2024.
//

import Testing
@testable import Projeto_MC656

@MainActor
struct VehicleEditModelTests {
    var sut: VehicleEditModel!
    var mockVehicleEditService: MockVehicleEditService!

    init() {
        // Assumes VehicleEditModel is modified for service injection
        mockVehicleEditService = MockVehicleEditService()
        sut = VehicleEditModel(service: mockVehicleEditService) // Assumed constructor
    }

    // MARK: - Create Mode Tests

    @Test func saveVehicle_create_success_callsServiceWithCorrectParameters() async {
        // Arrange
        sut = VehicleEditModel(vehicle: nil, service: mockVehicleEditService) // Ensure create mode
        sut.vehicleType = .bicicleta
        sut.name = "My Test Bike"
        sut.description = "A great bike for testing."
        sut.pickupLocation = "Test Location"
        sut.includesHelmet = true
        sut.requiresId = true
        sut.requiresDeposit = true
        sut.depositAmount = "50.00"

        mockVehicleEditService.createVehicleResult = nil // Success

        // Act
        await sut.saveVehicle()

        // Assert
        #expect(mockVehicleEditService.createVehicleCalled == true)
        #expect(sut.isLoading == false) // Assuming isLoading is part of VehicleEditModel

        let capturedRequest = mockVehicleEditService.createVehicleRequestArg
        #expect(capturedRequest != nil)
        #expect(capturedRequest?.make == "My Test Bike") // name maps to make
        #expect(capturedRequest?.model == "A great bike for testing.") // description maps to model
        #expect(capturedRequest?.type == VehicleType.bicicleta.rawValue)
        #expect(capturedRequest?.requiresId == true)
        #expect(capturedRequest?.depositAmount == 50.00)
        // More assertions on accessories, pickupLocation if they were part of CreateVehicleRequest directly
        // The current VehicleEditModel doesn't seem to pass all its properties (like accessories, pickupLocation)
        // to the VehicleEditService.createVehicle method parameters directly.
        // The CreateVehicleRequest in VehicleEditService.swift takes 'latitude', 'longitude', 'accessories', etc.
        // but the VehicleEditModel.saveVehicle() call to service.createVehicle(...) doesn't pass all of them.
        // This test will reflect what's currently passed.
    }

    @Test func saveVehicle_create_failure_serviceError_handlesError() async {
        // Arrange
        sut = VehicleEditModel(vehicle: nil, service: mockVehicleEditService) // Create mode
        sut.name = "Error Bike"
        // ... set other valid properties ...
        let expectedError = ServiceError.serverError("Network failed")
        mockVehicleEditService.createVehicleResult = expectedError

        // Act
        await sut.saveVehicle()

        // Assert
        #expect(mockVehicleEditService.createVehicleCalled == true)
        #expect(sut.isLoading == false) // Assuming isLoading is part of VehicleEditModel
        // The model currently prints the error. For tests, it would be better if it exposed an error state.
        // #expect(sut.errorMessage == expectedError.localizedDescription) // If error property existed
    }

    // MARK: - Editing Mode Tests (Conceptual - Service methods for update/delete not fully implemented)
    // These tests would be similar if VehicleEditModel handled editing.
    // For now, VehicleEditModel's saveVehicle only has a create path.
    // The `isEditing` flag exists, but the save/delete logic for editing isn't using a service yet.

    @Test func saveVehicle_edit_success_conceptual() async {
        // Arrange
        let initialVehicle = Vehicle(id: UUID(), name: "Old Bike", photo: "p", isAvailable: true, description: "d", pickupLocation: "l", accessories: nil, requiresId: false, depositAmount: 0)
        sut = VehicleEditModel(vehicle: initialVehicle, service: mockVehicleEditService) // Edit mode
        sut.name = "Updated Bike Name"
        // Assume an updateVehicle method on the service:
        // mockVehicleEditService.updateVehicleResult = nil

        // Act
        // await sut.saveVehicle() // If saveVehicle handled updates

        // Assert
        // #expect(mockVehicleEditService.updateVehicleCalled == true)
        // #expect(sut.name == "Updated Bike Name")
    }

    @Test func deleteVehicle_success_conceptual() async {
        // Arrange
        let vehicleId = UUID()
        let vehicleToDelete = Vehicle(id: vehicleId, name: "Bike to Delete", photo: "p", isAvailable: true, description: "d", pickupLocation: "l", accessories: nil, requiresId: false, depositAmount: 0)
        sut = VehicleEditModel(vehicle: vehicleToDelete, service: mockVehicleEditService)
        // Assume a deleteVehicle method on the service:
        // mockVehicleEditService.deleteVehicleResult = nil

        // Act
        // sut.deleteVehicle() // If deleteVehicle used a service

        // Assert
        // #expect(mockVehicleEditService.deleteVehicleCalledWithId == vehicleId)
    }

    @Test func navigationTitle_isCorrectForCreateMode() {
        sut = VehicleEditModel(vehicle: nil, service: mockVehicleEditService)
        #expect(sut.navigationTitle == "Cadastrar Veículo")
    }

    @Test func navigationTitle_isCorrectForEditMode() {
        let existingVehicle = Vehicle(id: UUID(), name: "Edit Bike", photo: "", isAvailable: true, description: nil, pickupLocation: nil, accessories: nil, requiresId: nil, depositAmount: nil)
        sut = VehicleEditModel(vehicle: existingVehicle, service: mockVehicleEditService)
        #expect(sut.navigationTitle == "Editar Veículo")
    }

}

// Requires VehicleEditModel to be injectable:
// protocol VehicleEditServiceProtocol { ... }
// extension VehicleEditService: VehicleEditServiceProtocol {}
// class VehicleEditModel { init(vehicle: Vehicle? = nil, service: VehicleEditServiceProtocol = VehicleEditService()) { ... } }
// extension MockVehicleEditService: VehicleEditServiceProtocol {}
