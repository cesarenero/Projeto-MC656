//
//  VehicleListService.swift
//  Projeto-MC656
//
//  Created by Gab on 19/06/25.
//

import Foundation

class VehicleListService {
    
    private let client = ApiClient()
    
    func fetchVehicles() async throws -> [Vehicle] {
        return try await client.get(url: ApiEndpoints.Vehicles.listAvailable, responseType: [Vehicle].self)
    }

    func getVehicleDetails(vehicleId: Int) async throws -> Vehicle {
        return try await client.get(url: ApiEndpoints.Vehicles.get(vehicleId: vehicleId), responseType: Vehicle.self)
    }
}
