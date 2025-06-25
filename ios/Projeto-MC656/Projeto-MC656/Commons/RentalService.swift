import Foundation

struct StartRentalRequest: Codable {
    let vehicleId: Int
}

struct StartRentalResponse: Codable {
    let id: Int
    let vehicleId: Int
    let userId: Int
    let startTime: String
    // Adicione outros campos conforme resposta do backend
}

struct RentalHistoryItem: Codable {
    let id: Int
    let vehicleId: Int
    let userId: Int
    let startTime: String
    let endTime: String?
    // Adicione outros campos conforme resposta do backend
}

class RentalService {
    static let shared = RentalService()
    private let client = ApiClient()

    func startRental(vehicleId: Int) async throws -> StartRentalResponse {
        return try await client.post(url: ApiEndpoints.Rentals.start, requestBody: StartRentalRequest(vehicleId: vehicleId), responseType: StartRentalResponse.self)
    }

    func endRental(rentalId: Int) async throws {
        return try await client.post(url: ApiEndpoints.Rentals.end(rentalId: rentalId))
    }

    func getUserRentalHistory() async throws  -> [RentalHistoryItem] {
        return try await client.get(url: ApiEndpoints.Users.history, responseType: [RentalHistoryItem].self)
    }
}
