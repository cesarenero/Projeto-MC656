//
//  MockVehicleListService.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 27/07/2024.
//

import Foundation
@testable import Projeto_MC656

class MockVehicleListService: VehicleListService {
    var fetchVehiclesResult: Result<[Vehicle], Error>?
    var fetchVehiclesCalled: Bool = false

    var getVehicleDetailsResult: Result<Vehicle, Error>?
    var getVehicleDetailsCalled: Bool = false
    var getVehicleDetailsIdArg: Int?

    override func fetchVehicles() async throws -> [Vehicle] {
        fetchVehiclesCalled = true
        guard let result = fetchVehiclesResult else {
            throw MockServiceError.resultSetNotProvided("fetchVehiclesResult in MockVehicleListService not set")
        }
        switch result {
        case .success(let vehicles):
            return vehicles
        case .failure(let error):
            throw error
        }
    }

    override func getVehicleDetails(vehicleId: Int) async throws -> Vehicle {
        getVehicleDetailsCalled = true
        getVehicleDetailsIdArg = vehicleId
        guard let result = getVehicleDetailsResult else {
            throw MockServiceError.resultSetNotProvided("getVehicleDetailsResult in MockVehicleListService not set")
        }
        switch result {
        case .success(let vehicle):
            return vehicle
        case .failure(let error):
            throw error
        }
    }
}
