import Foundation

struct CreateVehicleRequest: Codable {
    let make: String
    let model: String
    let yearManufacture: Int
    let licensePlate: String
    let type: String?
    let latitude: Double
    let longitude: Double
    let accessories: [String]?
    let requiresId: Bool?
    let depositAmount: Double?
}

class VehicleEditService {
    
    private let client = ApiClient()
    
    func createVehicle(
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
        
        let body = CreateVehicleRequest(
            make: name,
            model: description ?? "",
            yearManufacture: yearManufacture ?? 2024,
            licensePlate: licensePlate ?? UUID().uuidString,
            type: type,
            latitude: latitude ?? 0.0,
            longitude: longitude ?? 0.0,
            accessories: accessories,
            requiresId: requiresId,
            depositAmount: depositAmount
        )
        
        try await client.post(url: ApiEndpoints.Vehicles.list, requestBody: body)
    }
}
