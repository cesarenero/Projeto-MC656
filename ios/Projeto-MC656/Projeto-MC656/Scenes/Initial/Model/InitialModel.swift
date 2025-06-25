//
//  InitialModel.swift
//  Projeto-MC656
//
//  Created by Gab on 19/05/25.
//

import SwiftUI

// Define a protocol for the LoginService for better testability
protocol LoginServiceProtocol {
    func callAuth(email: String, password: String) async throws -> LoginResponse
}

// Make the original LoginService conform to this protocol
extension LoginService: LoginServiceProtocol {}


class InitialModel: ObservableObject {
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var isLoggedIn: Bool = false

    private let service: LoginServiceProtocol // Use the protocol type

    // Initialize with a service that conforms to the protocol
    // Provide a default instance of the real service for normal app operation
    init(service: LoginServiceProtocol = LoginService()) {
        self.service = service
    }

    @MainActor
    func login() async {
        isLoading = true
        errorMessage = nil

        do {
//            try email.check(.email) // Assuming these checks might be re-enabled later
//            try password.check(.password)
            let _ = try await service.callAuth(email: email, password: password) // response not directly used here other than for success
            AuthManager.shared.token = nil // Clear previous token first
            // The token is set by LoginService itself. If callAuth is successful, token is set in AuthManager by the service.
            // We might want to retrieve the token from response if needed by the model itself.
            // For now, successful call implies token is set by the service.
            isLoggedIn = true
        } catch {
            errorMessage = error.localizedDescription
            isLoggedIn = false // Ensure isLoggedIn is false on error
        }

        isLoading = false
    }
}

extension InitialModel: CustomButtonDelegate {
    
}
