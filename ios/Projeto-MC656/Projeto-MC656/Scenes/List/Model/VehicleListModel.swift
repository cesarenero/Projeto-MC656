//
//  VehicleListModel.swift
//  Projeto-MC656
//
//  Created by Gab on 19/06/25.
//

import Foundation

@MainActor
class VehicleListModel: ObservableObject {
    @Published var vehicles: [Vehicle] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let service: VehicleListServiceProtocol // Use protocol

    init(service: VehicleListServiceProtocol = VehicleListService()) { // DI
        self.service = service
    }

    func loadVehicles() async {
        isLoading = true
        errorMessage = nil

        do {
            vehicles = try await service.fetchVehicles()
        } catch {
            // Consider logging the actual error `error.localizedDescription` for debugging
            // For user display, a generic message is often better.
            errorMessage = "Falha ao carregar os veículos. Tente novamente mais tarde."
        }

        isLoading = false
    }
}
