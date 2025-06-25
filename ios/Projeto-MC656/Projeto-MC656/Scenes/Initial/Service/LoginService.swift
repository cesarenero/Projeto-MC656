//
//  LoginService.swift
//  Projeto-MC656
//
//  Created by Gab on 11/06/25.
//

import Foundation

class LoginService {
    private let client = ApiClient()
    
    func callAuth(email: String, password: String) async throws -> LoginResponse {
        let body = [
            "username": email,
            "password": password
        ]
        
        let response = try await client.post(url: ApiEndpoints.Auth.login, requestBody: body, responseType: LoginResponse.self)
        
        AuthManager.shared.token = response.token
        
        return response
    }
}
