//
//  RegisterModel.swift
//  Projeto-MC656
//
//  Created by Gab on 13/06/25.
//

import Foundation

// Define a protocol for the RegisterService for better testability
protocol RegisterServiceProtocol {
    func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws
}

// Make the original RegisterService conform to this protocol
extension RegisterService: RegisterServiceProtocol {}


class RegisterModel: ObservableObject, CustomButtonDelegate {
    @Published var fullName: String = ""
    @Published var socialName: String = ""
    @Published var email: String = ""
    @Published var phone: String = ""
    @Published var password: String = ""
    @Published var passwordConfirmation: String = ""
    @Published var cpf: String = ""

    @Published var isLoading: Bool = false
    @Published var showError: Bool = false
    @Published var errorMessage: String?
    
    private let service: RegisterServiceProtocol // Use the protocol type

    // Initialize with a service that conforms to the protocol
    // Provide a default instance of the real service for normal app operation
    init(service: RegisterServiceProtocol = RegisterService()) {
        self.service = service
    }

    @MainActor
    func register() async {
        isLoading = true
        showError = false // Reset error state at the beginning
        errorMessage = nil

        defer { isLoading = false }

        do {
            try fullName.check(.name)
            // Allow socialName to be empty, but if not empty, validate it.
            if !socialName.isEmpty {
                try socialName.check(.socialName)
            } else {
                 // Ensure it's treated as nil or empty string if backend expects that for optional fields.
                 // The current check for socialName regex `^[a-zA-Z\\s]{0,20}$` allows empty string.
                 // So, an explicit check `!socialName.isEmpty` before `try socialName.check(.socialName)` might be redundant
                 // if the regex itself correctly handles empty strings as valid.
                 // The regex `^[a-zA-Z\\s]{0,20}$` does allow an empty string.
                 // So, `try socialName.check(.socialName)` can be called directly.
            }
            try socialName.check(.socialName) // Regex `^[a-zA-Z\\s]{0,20}$` handles empty.
            try email.check(.email)
            try phone.check(.phoneNumber)
            try cpf.check(.cpf)
            try password.check(.password)
            try password.comparePassword(with: passwordConfirmation)

            // Use socialName directly; if it's empty, it's fine.
            // If the backend expects nil for an empty optional field, adjustment might be needed here or in service.
            // For now, passing empty string as per current DTOs which are String?.
            let currentSocialName = socialName.isEmpty ? nil : socialName

            try await service.callRegister(name: fullName, socialName: currentSocialName, email: email, phone: phone, cpf: cpf, password: password)

        } catch {
            showError = true
            errorMessage = error.localizedDescription
        }
    }
}
