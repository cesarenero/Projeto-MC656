//
//  MockVehicleEditService.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 27/07/2024.
//

import Foundation
@testable import Projeto_MC656

class MockVehicleEditService: VehicleEditService {
    var createVehicleResult: Error? // nil for success
    var createVehicleCalled: Bool = false
    var createVehicleRequestArg: CreateVehicleRequest?

    // If you add update/delete methods to VehicleEditService, add mocks here too
    // var updateVehicleResult: Error?
    // var deleteVehicleResult: Error?

    override func createVehicle(
        name: String,
        description: String?,
        pickupLocation: String?,
        type: String,
        yearManufacture: Int?,
        licensePlate: String?,
        latitude: Double?,
        longitude: Double?,
        accessories: [String]?,
        requiresId: Bool?,
        depositAmount: Double?
    ) async throws {
        createVehicleCalled = true
        // Capture the effective arguments by reconstructing the request
        // This is a bit indirect; ideally, the service method would take the Request object.
        // For now, we'll just mark it as called and assume arguments are passed through.
        // If precise argument checking is needed, the service method should accept the DTO.

        // Reconstruct the CreateVehicleRequest to capture arguments for assertion
        // This makes the mock a bit more complex but allows verifying what was passed.
        self.createVehicleRequestArg = CreateVehicleRequest(
            make: name, // In VehicleEditModel, 'name' maps to 'make'
            model: description ?? "", // 'description' maps to 'model'
            yearManufacture: yearManufacture ?? 2024, // Default from VehicleEditModel
            licensePlate: licensePlate ?? "", // Default from VehicleEditModel (though it uses UUID().uuidString)
            type: type,
            latitude: latitude ?? 0.0,
            longitude: longitude ?? 0.0,
            accessories: accessories,
            requiresId: requiresId,
            depositAmount: depositAmount
        )


        if let error = createVehicleResult {
            throw error
        }
    }
}
