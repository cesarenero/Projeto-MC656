import Foundation

// This request structure should align with what VehicleRequest DTO in backend expects.
// Backend VehicleRequest: make, model, yearManufacture, licensePlate, type, latitude, longitude
// iOS Vehicle model has: name, photo, isAvailable, description, pickupLocation, accessories, requiresId, depositAmount
// There's a mismatch.
// For now, I'll assume VehicleEditService's createVehicle signature is what we intend to send,
// and CreateVehicleRequest will be structured accordingly.
// The current VehicleEditService.createVehicle signature matches VehicleRequest in backend more closely if 'name' maps to 'make' and 'description' to 'model'.

struct CreateVehicleRequest: Codable { // This is the DTO for the backend
    let make: String
    let model: String
    let yearManufacture: Int
    let licensePlate: String
    let type: String // Backend expects VehicleType enum string
    let latitude: Double? // Made optional to match backend better
    let longitude: Double? // Made optional to match backend better
    // 'accessories', 'requiresId', 'depositAmount' are not in backend VehicleRequest DTO.
    // These might be part of a different model or different endpoint if they are vehicle *settings* vs properties.
    // For now, they are removed from CreateVehicleRequest to match backend.
}


protocol VehicleEditServiceProtocol {
    func createVehicle(
        name: String, // maps to 'make'
        description: String?, // maps to 'model'
        pickupLocation: String?, // Not in backend VehicleRequest, maybe store differently or ignore for now
        type: String, // VehicleType.rawValue
        yearManufacture: Int?,
        licensePlate: String?, // If null, backend might auto-generate or reject
        latitude: Double?,
        longitude: Double?,
        accessories: [String]?, // Not in backend VehicleRequest
        requiresId: Bool?,      // Not in backend VehicleRequest
        depositAmount: Double?  // Not in backend VehicleRequest
    ) async throws
    // func updateVehicle(...) async throws
    // func deleteVehicle(id: UUID) async throws
}


class VehicleEditService: VehicleEditServiceProtocol {
    
    private let client = ApiClient()
    // Consider injecting ApiClient for better testability
    
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
        
        // Construct the DTO that matches the backend's VehicleRequest
        let body = CreateVehicleRequest(
            make: name, // name from form maps to 'make'
            model: description ?? "", // description from form maps to 'model'
            yearManufacture: yearManufacture ?? Calendar.current.component(.year, from: Date()), // Default to current year
            licensePlate: licensePlate ?? "AUTO-\(UUID().uuidString.prefix(8))", // Auto-generate if not provided
            type: type, // This should be the string representation of VehicleType enum from backend
            latitude: latitude,
            longitude: longitude
        )
        
        // Assuming ApiEndpoints.Vehicles.list is the POST endpoint for creating vehicles.
        // The current ApiClient.post methods add an Auth token. This is correct for creating vehicles.
        try await client.post(url: ApiEndpoints.Vehicles.list, requestBody: body)
    }

    // Implement updateVehicle and deleteVehicle if/when those functionalities are added
}
