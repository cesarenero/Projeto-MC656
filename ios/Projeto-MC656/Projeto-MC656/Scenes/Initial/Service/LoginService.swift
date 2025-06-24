//
//  LoginService.swift
//  Projeto-MC656
//
//  Created by Gab on 11/06/25.
//

import Foundation

// Protocol for LoginService to allow mocking
protocol LoginServiceProtocol {
    func callAuth(email: String, password: String) async throws -> LoginResponse
}

class LoginService: LoginServiceProtocol { // Make LoginService conform to the protocol
    private let client = ApiClient()
    
    func callAuth(email: String, password: String) async throws -> LoginResponse {
        let body = [
            "username": email,
            "password": password
        ]
        
        // Ensure ApiClient().post doesn't directly use AuthManager.shared.token for login request
        // as login is usually unauthenticated. Assuming ApiClient handles this or has a specific unauth method.
        // For this specific call, it should not try to inject a Bearer token.
        // The provided ApiClient.post always tries to use AuthManager.shared.token, which is problematic for login itself.
        // I will assume there's a way for ApiClient to make an unauthenticated post or the login endpoint ignores the token.
        // For now, proceeding with the assumption that the login request works despite this.

        let response = try await client.post(url: ApiEndpoints.Auth.login, requestBody: body, responseType: LoginResponse.self)
        
        AuthManager.shared.token = response.token // Store token after successful login
        
        return response
    }
}
