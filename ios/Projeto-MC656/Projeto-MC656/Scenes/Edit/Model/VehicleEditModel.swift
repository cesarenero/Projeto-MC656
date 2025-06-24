//
//  VehicleEditModel.swift
//  Projeto-MC656
//
//  Created by Gab on 21/06/25.
//

import Foundation

// This enum should ideally match or map to the backend's VehicleType enum
enum IOSVehicleType: String, CaseIterable, Identifiable {
    case bicicleta = "Bicicleta" // Maps to BICYCLE or E_BIKE
    case patinete = "Patinete"   // Maps to SCOOTER or E_SCOOTER
    case patins = "Patins"       // No direct map, maybe needs "OTHER" or similar in backend
    case skate = "Skate"         // No direct map
    case outros = "Outros"       // General fallback

    var id: Self { self }

    // Helper to map to backend string values if they differ significantly
    // For now, assuming rawValue is close enough or backend handles these strings.
    // Example: backend expects "E_BIKE", iOS uses "Bicicleta" for type.
    // This mapping needs to be robust.
    // For VehicleEditService, we pass vehicleType.rawValue.
    // The backend VehicleType enum includes: BICYCLE, E_BIKE, SCOOTER, E_SCOOTER, ELECTRIC_CAR, CARGO_BIKE
    // We need to ensure these rawValues align or are mapped.
    // For now, let's use a simplified mapping for the test.
    var backendTypeString: String {
        switch self {
        case .bicicleta: return "BICYCLE" // Or could be E_BIKE depending on other properties
        case .patinete: return "SCOOTER"  // Or E_SCOOTER
        default: return self.rawValue.uppercased() // Fallback, might not match backend
        }
    }
}


@MainActor
class VehicleEditModel: ObservableObject {
    @Published var vehicleType: IOSVehicleType = .bicicleta // Use the iOS specific enum
    // @Published var vehicleImage: Data? // Foto?
    @Published var name: String = "" // This will map to 'make' in the backend
    @Published var description: String = "" // This will map to 'model'
    @Published var pickupLocation: String = "" // Not directly in backend VehicleRequest DTO

    @Published var includesHelmet: Bool = false // Not in backend VehicleRequest DTO
    @Published var includesLock: Bool = false   // Not in backend VehicleRequest DTO
    @Published var includesLight: Bool = false  // Not in backend VehicleRequest DTO
    @Published var includesPump: Bool = false   // Not in backend VehicleRequest DTO
    @Published var otherAccessories: String = "" // Not in backend VehicleRequest DTO

    @Published var requiresId: Bool = false     // Not in backend VehicleRequest DTO
    @Published var requiresDeposit: Bool = false
    @Published var depositAmount: String = ""   // Not in backend VehicleRequest DTO

    // Fields that map more directly to backend VehicleRequest
    @Published var yearManufacture: String = "\(Calendar.current.component(.year, from: Date()))"
    @Published var licensePlate: String = "" // Optional, can be auto-generated
    @Published var latitude: String = ""
    @Published var longitude: String = ""


    var isEditing: Bool
    private var vehicleId: UUID? // This is from the iOS Vehicle struct, backend uses Long

    @Published var errorMessage: String? // For displaying errors
    @Published var isLoading: Bool = false


    var navigationTitle: String {
        isEditing ? "Editar Veículo" : "Cadastrar Veículo"
    }

    private let service: VehicleEditServiceProtocol

    init(service: VehicleEditServiceProtocol = VehicleEditService(), vehicle: Vehicle? = nil) {
        self.service = service
        if let vehicle = vehicle {
            self.isEditing = true
            self.vehicleId = vehicle.id
            self.name = vehicle.name // Maps to 'make'
            // self.description = vehicle.description ?? "" // Maps to 'model', ensure Vehicle struct has it
            // ... populate other fields from 'vehicle' object ...
            // Example: self.vehicleType = IOSVehicleType(rawValue: vehicle.typeStringFromBackend) ?? .outros
        } else {
            self.isEditing = false
        }
    }

    func saveVehicle() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        // Basic client-side validation (can be expanded)
        guard !name.isEmpty else {
            errorMessage = "Nome do veículo é obrigatório."
            return
        }

        let year = Int(yearManufacture)
        let lat = Double(latitude)
        let lon = Double(longitude)
        let deposit = Double(depositAmount) // Convert string to double

        if isEditing {
            print("Salvando (editando) veículo: \(name)")
            // TODO: Implementar lógica de edição chamando um método update no serviço
            // try await service.updateVehicle(...)
        } else {
            print("Cadastrando novo veículo: \(name)")
            do {
                try await service.createVehicle(
                    name: name,
                    description: description,
                    pickupLocation: pickupLocation, // Not sent to backend via CreateVehicleRequest
                    type: vehicleType.backendTypeString, // Use mapping
                    yearManufacture: year,
                    licensePlate: licensePlate.isEmpty ? nil : licensePlate,
                    latitude: lat,
                    longitude: lon,
                    accessories: collectAccessories(), // Not sent to backend
                    requiresId: requiresId,           // Not sent to backend
                    depositAmount: requiresDeposit ? deposit : nil // Not sent to backend
                )
                print("Veículo cadastrado com sucesso!")
                // TODO: Add navigation or success message handling
            } catch {
                print("Erro ao cadastrar veículo: \(error.localizedDescription)")
                errorMessage = error.localizedDescription
            }
        }
    }

    private func collectAccessories() -> [String]? {
        var acc: [String] = []
        if includesHelmet { acc.append("Capacete") }
        if includesLock { acc.append("Cadeado") }
        if includesLight { acc.append("Farol") }
        if includesPump { acc.append("Bomba de Ar") }
        if !otherAccessories.isEmpty { acc.append(contentsOf: otherAccessories.split(separator: ",").map({ $0.trimmingCharacters(in: .whitespacesAndNewlines) })) }
        return acc.isEmpty ? nil : acc
    }


    func cancel() {
        print("Ação de cancelar no VehicleEditModel")
        // Typically handled by @Environment(\.dismiss) in the View
    }

    func deleteVehicle() {
        guard let idToDelete = vehicleId, isEditing else {
            print("Não é possível deletar: ID do veículo não encontrado ou não está em modo de edição.")
            return
        }
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        print("Tentando apagar veículo com ID (iOS UUID): \(idToDelete.uuidString)")
        // TODO: Implementar lógica de deleção chamando um método delete no serviço
        // Ex: try await service.deleteVehicle(id: idToDelete) -> backend expects Long for ID
        // This requires service.deleteVehicle and mapping UUID to Long if necessary, or backend adapting.
        // For now, just logging. If service had delete, it would be:
        // do {
        //     try await service.deleteVehicle(id: idToDelete) // (assuming service method exists)
        //     print("Veículo apagado com sucesso!")
        // } catch {
        //     print("Erro ao apagar veículo: \(error.localizedDescription)")
        //     errorMessage = error.localizedDescription
        // }
    }
}
