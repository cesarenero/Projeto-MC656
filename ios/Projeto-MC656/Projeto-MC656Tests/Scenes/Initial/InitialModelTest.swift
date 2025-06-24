//
//  InitialModelTest.swift
//  Projeto-MC656Tests
//
//  Created by Gab on 12/06/25.
//

import Testing
@testable import Projeto_MC656 // Ensure your main app target is importable

@MainActor // Models often interact with UI, so run tests on main actor
struct InitialModelTest {
    var sut: InitialModel!
    var mockLoginService: MockLoginService! // We'll create a mock for LoginService

    init() {
        mockLoginService = MockLoginService()
        sut = InitialModel(service: mockLoginService) // Assuming InitialModel can take a service for DI
    }

    @Test func testLoginSuccess() async {
        // Arrange
        sut.email = "test@example.com"
        sut.password = "Password@123"
        mockLoginService.loginShouldSucceed = true
        mockLoginService.loginResponse = LoginResponse(token: "fake-token")

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == true)
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == nil)
        #expect(AuthManager.shared.token == "fake-token")
    }

    @Test func testLoginFailure_InvalidCredentials() async {
        // Arrange
        sut.email = "test@example.com"
        sut.password = "wrongpassword"
        mockLoginService.loginShouldSucceed = false
        mockLoginService.serviceError = .invalidCredentials

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == false)
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == ServiceError.invalidCredentials.localizedDescription)
        #expect(AuthManager.shared.token == nil) // Token should not be set
    }

    @Test func testLoginFailure_ServerError() async {
        // Arrange
        sut.email = "test@example.com"
        sut.password = "Password@123"
        mockLoginService.loginShouldSucceed = false
        mockLoginService.serviceError = .serverError("Network failed")

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == false)
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == ServiceError.serverError("Network failed").localizedDescription)
    }

    @Test func testLogin_ClientSideEmailValidationFailure() async {
        // Arrange
        sut.email = "invalidemail" // Invalid email
        sut.password = "Password@123"
        // No need to set mockLoginService behavior as it shouldn't be called

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == false)
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == DataError.invalidEmail.localizedDescription)
    }

    @Test func testLogin_ClientSidePasswordValidationFailure() async {
        // Arrange
        sut.email = "test@example.com"
        sut.password = "short" // Invalid password
        // No need to set mockLoginService behavior as it shouldn't be called

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == false)
        #expect(sut.isLoading == false)
        #expect(sut.errorMessage == DataError.invalidPassword.localizedDescription)
    }


    @Test func testIsLoadingStateDuringLogin() async {
        // Arrange
        sut.email = "test@example.com"
        sut.password = "Password@123"
        mockLoginService.loginShouldSucceed = true
        mockLoginService.loginResponse = LoginResponse(token: "fake-token")

        // Make the service delay a bit to check isLoading
        mockLoginService.delay = 0.1

        // Act
        let loginTask = Task { await sut.login() }

        // Assert briefly after starting, before completion
        try! await Task.sleep(nanoseconds: UInt64(0.01 * 1_000_000_000)) // small delay
        #expect(sut.isLoading == true)

        await loginTask.value // Wait for login to complete

        #expect(sut.isLoading == false)
    }
}

// MockLoginService for testing InitialModel
class MockLoginService: LoginServiceProtocol {
    var loginShouldSucceed = true
    var serviceError: ServiceError?
    var loginResponse: LoginResponse?
    var delay: TimeInterval = 0 // To simulate network latency

    func callAuth(email: String, password: String) async throws -> LoginResponse {
        if delay > 0 {
            try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000))
        }

        if loginShouldSucceed {
            guard let response = loginResponse else {
                fatalError("LoginResponse not set for successful mock login")
            }
            AuthManager.shared.token = response.token // Simulate token storage
            return response
        } else {
            guard let error = serviceError else {
                fatalError("ServiceError not set for failed mock login")
            }
            AuthManager.shared.token = nil // Ensure token is nil on failure
            throw error
        }
    }
}

// Protocol for LoginService to allow mocking (if not already defined)
// If LoginService is a class, we can subclass and override.
// If it's a struct or final class, a protocol is better for DI.
protocol LoginServiceProtocol {
    func callAuth(email: String, password: String) async throws -> LoginResponse
}

// Make your real LoginService conform to this if you use this approach
// extension LoginService: LoginServiceProtocol {}
