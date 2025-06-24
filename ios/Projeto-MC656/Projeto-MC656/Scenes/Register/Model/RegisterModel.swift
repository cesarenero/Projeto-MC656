//
//  RegisterModel.swift
//  Projeto-MC656
//
//  Created by Gab on 13/06/25.
//

import Foundation

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
    
    private let service: RegisterServiceProtocol // Use the protocol

    init(service: RegisterServiceProtocol = RegisterService()) { // Dependency Injection
        self.service = service
    }

    @MainActor
    func register() async {
        isLoading = true
        showError = false // Reset error state
        errorMessage = nil
        defer { isLoading = false }

        do {
            try fullName.check(.name)
            // Social name can be empty, but if not, it should be valid.
            // The current regex ^[a-zA-Z\\s]{0,20}$ allows empty.
            try socialName.check(.socialName)
            try email.check(.email)
            try phone.check(.phoneNumber)
            try cpf.check(.cpf) // This uses .invalidPhoneNumber error in String+Checks.swift, which is a bug.
            try password.check(.password)
            try password.comparePassword(with: passwordConfirmation)

            // Use socialName if not empty, otherwise backend might expect null or not expect the field
            let effectiveSocialName = socialName.isEmpty ? nil : socialName
            try await service.callRegister(name: fullName, socialName: effectiveSocialName, email: email, phone: phone, cpf: cpf, password: password)

        } catch {
            showError = true
            errorMessage = error.localizedDescription
        }
    }
}
