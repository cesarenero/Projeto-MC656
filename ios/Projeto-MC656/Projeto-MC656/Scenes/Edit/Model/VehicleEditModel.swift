//
//  VehicleEditModel.swift
//  Projeto-MC656
//
//  Created by Gab on 21/06/25.
//

import Foundation

// Define a protocol for the VehicleEditService for better testability
protocol VehicleEditServiceProtocol {
    func createVehicle(
        name: String,
        description: String?,
        pickupLocation: String?, // This param is in the model but not directly used in service.createVehicle call
        type: String,
        yearManufacture: Int?,
        licensePlate: String?,
        latitude: Double?,
        longitude: Double?,
        accessories: [String]?, // This param is in the model but not directly used in service.createVehicle call
        requiresId: Bool?,
        depositAmount: Double?
    ) async throws
    // Add updateVehicle and deleteVehicle if they become part of this service
}

// Make the original VehicleEditService conform to this protocol
extension VehicleEditService: VehicleEditServiceProtocol {}


enum VehicleType: String, CaseIterable, Identifiable {
    case bicicleta = "Bicicleta"
    case patinete = "Patinete"
    case patins = "Patins"
    case skate = "Skate"
    case outros = "Outros"
    var id: Self { self }
}

@MainActor
class VehicleEditModel: ObservableObject {
    @Published var vehicleType: VehicleType = .bicicleta
    // @Published var vehicleImage: Data? // Foto?
    @Published var name: String = ""
    @Published var description: String = ""
    @Published var pickupLocation: String = ""

    @Published var includesHelmet: Bool = false
    @Published var includesLock: Bool = false
    @Published var includesLight: Bool = false
    @Published var includesPump: Bool = false
    @Published var otherAccessories: String = "" // This should probably be an array or structured if passed to service

    @Published var requiresId: Bool = false
    @Published var requiresDeposit: Bool = false
    @Published var depositAmount: String = ""

    @Published var isLoading: Bool = false
    @Published var errorMessage: String? // For displaying errors to the user

    var isEditing: Bool
    private var vehicleId: UUID?

    private let service: VehicleEditServiceProtocol

    var navigationTitle: String {
        isEditing ? "Editar Veículo" : "Cadastrar Veículo"
    }

    init(vehicle: Vehicle? = nil, service: VehicleEditServiceProtocol = VehicleEditService()) {
        self.service = service
        if let vehicle = vehicle {
            self.isEditing = true
            self.vehicleId = vehicle.id
            self.name = vehicle.name
            self.description = vehicle.description ?? ""
            self.pickupLocation = vehicle.pickupLocation ?? ""

            if let vType = VehicleType(rawValue: vehicle.photo) { // Assuming vehicle.photo stores type string for now
                self.vehicleType = vType
            }
            // TODO: Populate other fields like accessories, requiresId, depositAmount from 'vehicle'
            self.requiresId = vehicle.requiresId ?? false
            if let deposit = vehicle.depositAmount, deposit > 0 {
                self.requiresDeposit = true
                self.depositAmount = String(format: "%.2f", deposit)
            }

        } else {
            self.isEditing = false
        }
    }

    func saveVehicle() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        // Consolidate accessories from toggles and text field
        var currentAccessories: [String] = []
        if includesHelmet { currentAccessories.append("Capacete") }
        if includesLock { currentAccessories.append("Cadeado") }
        if includesLight { currentAccessories.append("Farol") }
        if includesPump { currentAccessories.append("Bomba de Ar") }
        if !otherAccessories.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            currentAccessories.append(contentsOf: otherAccessories.split(separator: ",").map { $0.trimmingCharacters(in: .whitespacesAndNewlines) })
        }


        if isEditing {
            // TODO: Implementar edição (chamada ao serviço de update)
            print("Salvando (editando) veículo: \(name)")
            // Example:
            // do {
            //     try await service.updateVehicle(id: vehicleId!, name: name, ...)
            //     print("Veículo atualizado com sucesso!")
            // } catch {
            //     print("Erro ao atualizar veículo: \(error.localizedDescription)")
            //     self.errorMessage = error.localizedDescription
            // }
        } else {
            // Creating a new vehicle
            print("Cadastrando novo veículo: \(name)")
            do {
                // The service's createVehicle method expects 'name' as 'make' and 'description' as 'model'.
                // LicensePlate, latitude, longitude are not currently collected in VehicleEditModel UI.
                // yearManufacture is also not collected.
                // Passing nil or defaults for these.
                try await service.createVehicle(
                    name: name, // This is 'make' in CreateVehicleRequest
                    description: description, // This is 'model'
                    pickupLocation: pickupLocation, // Not directly in CreateVehicleRequest, but could be part of logic
                    type: vehicleType.rawValue,
                    yearManufacture: 2024, // Default/Placeholder
                    licensePlate: UUID().uuidString, // Default/Placeholder - backend might generate this or require uniqueness
                    latitude: nil, // Placeholder
                    longitude: nil, // Placeholder
                    accessories: currentAccessories.isEmpty ? nil : currentAccessories,
                    requiresId: requiresId,
                    depositAmount: requiresDeposit ? Double(depositAmount) : nil
                )
                print("Veículo cadastrado com sucesso!")
            } catch {
                print("Erro ao cadastrar veículo: \(error.localizedDescription)")
                self.errorMessage = error.localizedDescription
            }
        }
    }

    func cancel() {
        // Typically handled by the View (e.g., dismissing the sheet/view)
        print("Ação de cancelar no modelo.")
    }

    func deleteVehicle() {
        guard isEditing, let id = vehicleId else {
            print("Não é possível apagar: veículo não está em modo de edição ou não tem ID.")
            return
        }
        // TODO: Implementar chamada ao serviço de delete
        print("Apagando veículo com ID: \(id.uuidString)")
        // Example:
        // Task {
        //     isLoading = true
        //     defer { isLoading = false }
        //     do {
        //         try await service.deleteVehicle(id: id)
        //         print("Veículo apagado com sucesso!")
        //         // Need a mechanism to signal success to the view for navigation/dismissal
        //     } catch {
        //         print("Erro ao apagar veículo: \(error.localizedDescription)")
        //         self.errorMessage = error.localizedDescription
        //     }
        // }
    }
}
