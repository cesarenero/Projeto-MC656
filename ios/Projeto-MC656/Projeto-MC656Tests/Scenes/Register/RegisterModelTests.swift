//
//  RegisterModelTests.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 01/08/2024.
//

import Testing
@testable import Projeto_MC656

@MainActor
struct RegisterModelTests {
    var sut: RegisterModel!
    var mockRegisterService: MockRegisterService!

    init() {
        mockRegisterService = MockRegisterService()
        sut = RegisterModel(service: mockRegisterService) // Assuming RegisterModel takes service via DI
    }

    @Test func testRegisterSuccess() async {
        // Arrange
        sut.fullName = "Test User"
        sut.socialName = "" // Optional
        sut.email = "test@example.com"
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"

        mockRegisterService.registerShouldSucceed = true
        mockRegisterService.loginResponse = LoginResponse(token: "new-fake-token")


        // Act
        await sut.register()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.showError == false)
        #expect(sut.errorMessage == nil)
        // #expect(AuthManager.shared.token == "new-fake-token") // Depends if register logs in immediately
                                                            // The current RegisterService does try to log in.
    }

    @Test func testRegisterFailure_PasswordsDoNotMatch() async {
        // Arrange
        sut.fullName = "Test User"
        sut.email = "test@example.com"
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@321" // Mismatch

        // Act
        await sut.register()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.showError == true)
        #expect(sut.errorMessage == DataError.diferentPasswords.localizedDescription)
    }

    @Test func testRegisterFailure_InvalidEmail() async {
        // Arrange
        sut.fullName = "Test User"
        sut.email = "invalid" // Invalid email
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"

        // Act
        await sut.register()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.showError == true)
        #expect(sut.errorMessage == DataError.invalidEmail.localizedDescription)
    }

    @Test func testRegisterFailure_InvalidName() async {
        sut.fullName = "" // Invalid name
        sut.email = "test@example.com"
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"
        await sut.register()
        #expect(sut.errorMessage == DataError.invalidName.localizedDescription)
    }

    @Test func testRegisterFailure_InvalidPhoneNumber() async {
        sut.fullName = "Test User"
        sut.email = "test@example.com"
        sut.phone = "111" // Invalid phone
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"
        await sut.register()
        #expect(sut.errorMessage == DataError.invalidPhoneNumber.localizedDescription)
    }

    @Test func testRegisterFailure_InvalidCPF() async {
        sut.fullName = "Test User"
        sut.email = "test@example.com"
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123" // Invalid CPF
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"
        await sut.register()
        #expect(sut.errorMessage == DataError.invalidCPF.localizedDescription) // String+Checks uses .invalidPhoneNumber for CPF error, this should be fixed.
    }


    @Test func testRegisterFailure_ServerError() async {
        // Arrange
        sut.fullName = "Test User"
        sut.socialName = ""
        sut.email = "test@example.com"
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"

        mockRegisterService.registerShouldSucceed = false
        mockRegisterService.serviceError = .serverError("Email already exists")

        // Act
        await sut.register()

        // Assert
        #expect(sut.isLoading == false)
        #expect(sut.showError == true)
        #expect(sut.errorMessage == ServiceError.serverError("Email already exists").localizedDescription)
    }

    @Test func testIsLoadingStateDuringRegistration() async {
        // Arrange
        sut.fullName = "Test User"
        sut.email = "test@example.com"
        sut.phone = "(11) 99999-9999"
        sut.cpf = "123.456.789-00"
        sut.password = "Password@123"
        sut.passwordConfirmation = "Password@123"
        mockRegisterService.registerShouldSucceed = true
        mockRegisterService.delay = 0.1

        // Act
        let registerTask = Task { await sut.register() }

        try! await Task.sleep(nanoseconds: UInt64(0.01 * 1_000_000_000))
        #expect(sut.isLoading == true)

        await registerTask.value

        #expect(sut.isLoading == false)
    }
}

// MockRegisterService
class MockRegisterService: RegisterServiceProtocol {
    var registerShouldSucceed = true
    var serviceError: ServiceError?
    var loginResponse: LoginResponse? // If registration also logs in
    var delay: TimeInterval = 0

    func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws {
        if delay > 0 {
            try await Task.sleep(nanoseconds: UInt64(delay * 1_000_000_000))
        }

        if registerShouldSucceed {
            // If registration also logs in and sets a token:
            if let tokenResponse = loginResponse {
                 AuthManager.shared.token = tokenResponse.token
            }
            return
        } else {
            guard let error = serviceError else {
                fatalError("ServiceError not set for failed mock registration")
            }
            throw error
        }
    }
}

// Protocol for RegisterService (if not already defined)
protocol RegisterServiceProtocol {
    func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws
}

// Make real RegisterService conform
// extension RegisterService: RegisterServiceProtocol {}
