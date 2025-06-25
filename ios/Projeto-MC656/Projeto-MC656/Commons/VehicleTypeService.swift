import Foundation

class VehicleTypeService {
    static let shared = VehicleTypeService()
    
    private let client = ApiClient()
    
    func getVehicleTypes() async throws -> [String] {
        return try await client.get(url: ApiEndpoints.Vehicles.types, responseType: [String].self)
    }
}
