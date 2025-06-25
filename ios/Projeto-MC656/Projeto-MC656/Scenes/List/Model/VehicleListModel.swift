//
//  VehicleListModel.swift
//  Projeto-MC656
//
//  Created by Gab on 19/06/25.
//

import Foundation

// Define a protocol for the VehicleListService for better testability
protocol VehicleListServiceProtocol {
    func fetchVehicles() async throws -> [Vehicle]
    func getVehicleDetails(vehicleId: Int) async throws -> Vehicle // Assuming this might be used by the model later
}

// Make the original VehicleListService conform to this protocol
extension VehicleListService: VehicleListServiceProtocol {}


@MainActor
class VehicleListModel: ObservableObject {
    @Published var vehicles: [Vehicle] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let service: VehicleListServiceProtocol // Use the protocol type

    // Initialize with a service that conforms to the protocol
    init(service: VehicleListServiceProtocol = VehicleListService()) {
        self.service = service
    }

    func loadVehicles() async {
        isLoading = true
        errorMessage = nil
        // It might be good practice to clear old vehicles before loading new ones,
        // or decide if appending/merging is ever a use case.
        // For now, direct replacement is fine.
        // self.vehicles = [] // Optional: clear previous results immediately

        do {
            vehicles = try await service.fetchVehicles()
        } catch {
            vehicles = [] // Ensure vehicles are cleared on error
            errorMessage = "Falha ao carregar os veículos. Tente novamente mais tarde."
        }

        isLoading = false
    }
}
