//
//  MockLoginService.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 27/07/2024.
//

import Foundation
@testable import Projeto_MC656

class MockLoginService: LoginService {
    var callAuthResult: Result<LoginResponse, Error>?
    var callAuthCalled: Bool = false
    var callAuthEmailArg: String?
    var callAuthPasswordArg: String?

    override func callAuth(email: String, password: String) async throws -> LoginResponse {
        callAuthCalled = true
        callAuthEmailArg = email
        callAuthPasswordArg = password

        guard let result = callAuthResult else {
            // To make it easier to debug tests, let's throw a specific error if the result isn't set,
            // rather than fatalError, so tests can catch this if needed.
            throw MockServiceError.resultSetNotProvided("callAuthResult in MockLoginService not set")
        }

        switch result {
        case .success(let response):
            return response
        case .failure(let error):
            throw error
        }
    }
}

// Helper error for mock services
enum MockServiceError: Error, LocalizedError {
    case resultSetNotProvided(String)

    var errorDescription: String? {
        switch self {
        case .resultSetNotProvided(let message):
            return message
        }
    }
}
