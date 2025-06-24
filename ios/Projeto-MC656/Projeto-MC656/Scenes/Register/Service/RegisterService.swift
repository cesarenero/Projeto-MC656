//
//  RegisterService.swift
//  Projeto-MC656
//
//  Created by José Carlos Cieni Júnior on 24/06/25.
//


import Foundation

protocol RegisterServiceProtocol {
    func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws
}

class RegisterService: RegisterServiceProtocol {
    private let client = ApiClient()
    // It's better to inject ApiClient if it also needs mocking for more granular tests,
    // but for now, we'll keep it simple as per current structure.
    
    func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws {
        // Step 1: Register the user
        let registerBody: [String: String?] = [
            "username": email,
            "password": password,
            "name": name,
            "email": email,
            "socialName": socialName, // Add socialName if not empty
            "phoneNumber": phone,    // Add phone
            "cpf": cpf               // Add CPF
        ]
        
        // Assuming ApiEndpoints.Users.register is an unauthenticated endpoint
        // and ApiClient has a way to handle posts without forcing a token.
        try await client.post(url: ApiEndpoints.Users.register, requestBody: registerBody.compactMapValues { $0 }) // compactMapValues to remove nil optionals if any
        
        // Step 2: Log the user in to get a token
        let loginBody = [
            "username": email,
            "password": password
        ]
        
        let response = try await client.post(url: ApiEndpoints.Auth.login, requestBody: loginBody, responseType: LoginResponse.self)
        
        AuthManager.shared.token = response.token
    }
}
