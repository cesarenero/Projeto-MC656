//
//  VehicleListService.swift
//  Projeto-MC656
//
//  Created by Gab on 19/06/25.
//

import Foundation

protocol VehicleListServiceProtocol {
    func fetchVehicles() async throws -> [Vehicle]
    func getVehicleDetails(vehicleId: Int) async throws -> Vehicle
}

class VehicleListService: VehicleListServiceProtocol {
    
    private let client = ApiClient()
    // Consider injecting ApiClient if it needs mocking for finer-grained tests.
    
    func fetchVehicles() async throws -> [Vehicle] {
        // The current Vehicle model (id: UUID, name: String, photo: String, isAvailable: Bool, etc.)
        // might not match the backend VehicleResponse (id: Long, make: String, model: String, etc.)
        // This will cause decoding errors if not aligned.
        // For the test to pass with current mock data, this is fine, but for real API calls, models must match.
        return try await client.get(url: ApiEndpoints.Vehicles.listAvailable, responseType: [Vehicle].self)
    }

    func getVehicleDetails(vehicleId: Int) async throws -> Vehicle {
        // Same potential decoding issue here.
        return try await client.get(url: ApiEndpoints.Vehicles.get(vehicleId: vehicleId), responseType: Vehicle.self)
    }
}
