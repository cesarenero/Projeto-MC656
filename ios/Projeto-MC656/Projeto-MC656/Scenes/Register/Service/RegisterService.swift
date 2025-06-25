//
//  RegisterService.swift
//  Projeto-MC656
//
//  Created by José Carlos Cieni Júnior on 24/06/25.
//


import Foundation

class RegisterService {
    private let client = ApiClient()
    
    func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws {
        var body = [
            "username": email,
            "password": password,
            "name": name,
            "email": email,
        ]
        
        try await client.post(url: ApiEndpoints.Users.register, requestBody: body)
        
        body.removeValue(forKey: "name")
        body.removeValue(forKey: "email")
        
        let response = try await client.post(url: ApiEndpoints.Auth.login, requestBody: body, responseType: LoginResponse.self)
        
        AuthManager.shared.token = response.token
    }
}
