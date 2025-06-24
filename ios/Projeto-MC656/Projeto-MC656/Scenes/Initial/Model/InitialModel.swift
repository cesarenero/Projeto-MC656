//
//  InitialModel.swift
//  Projeto-MC656
//
//  Created by Gab on 19/05/25.
//

import SwiftUI

class InitialModel: ObservableObject {
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var isLoggedIn: Bool = false

    private let service: LoginServiceProtocol // Use the protocol

    // Dependency injection for the service
    init(service: LoginServiceProtocol = LoginService()) { // Provide a default real service
        self.service = service
    }

    @MainActor
    func login() async {
        isLoading = true
        errorMessage = nil
        // Clear previous token before attempting login
        AuthManager.shared.token = nil


        do {
            // Client-side validation first
            try email.check(.email)
            try password.check(.password)

            let response = try await service.callAuth(email: email, password: password)
            // Token is set by the service upon successful auth
            isLoggedIn = true
        } catch {
            errorMessage = error.localizedDescription
        }

        isLoading = false
    }
}

extension InitialModel: CustomButtonDelegate {
    
}
