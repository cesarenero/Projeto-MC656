//
//  Vehicle.swift
//  Projeto-MC656
//
//  Created by Gab on 19/06/25.
//

import Foundation

struct Vehicle: Identifiable, Codable {
    let id: UUID
    let name: String
    let photo: String
    let isAvailable: Bool

    let description: String?
    let pickupLocation: String?
    let accessories: [String]?
    let requiresId: Bool?
    let depositAmount: Double?

    enum CodingKeys: String, CodingKey {
        case id, name, photo, description
        case isAvailable = "is_available"
        case pickupLocation = "pickup_location"
        case accessories
        case requiresId = "requires_id"
        case depositAmount = "deposit_amount"
    }
}
