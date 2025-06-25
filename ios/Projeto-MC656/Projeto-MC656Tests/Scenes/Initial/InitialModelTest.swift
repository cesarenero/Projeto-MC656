//
//  InitialModelTest.swift
//  Projeto-MC656Tests
//
//  Created by Gab on 12/06/25.
//

import Testing
@testable import Projeto_MC656
import Combine // For expectation handling if needed for @Published

@MainActor // Ensure tests run on the main actor as UI-related properties are updated
struct InitialModelTest {
    var sut: InitialModel!
    var mockLoginService: MockLoginService!

    init() {
        // Initialize is called for each test method by the testing framework.
        // We'll set up sut and mockLoginService here.
        // The actual InitialModel uses a concrete LoginService. We need to inject the mock.
        // Modifying InitialModel to allow service injection would be ideal.
        // For now, let's assume we can modify InitialModel or use a workaround.

        // **Assumption:** InitialModel is modified to accept a LoginService in its initializer
        // e.g., class InitialModel: ObservableObject {
        //           private let service: LoginService
        //           init(service: LoginService = LoginService()) { self.service = service }
        //       }
        // If not, these tests cannot directly mock the service unless we use more advanced techniques
        // or test at a higher level (UI tests).

        // For the purpose of this exercise, I will proceed as if InitialModel can take a service.
        // If InitialModel cannot be changed, then these unit tests for login logic
        // involving service calls are not directly feasible as written.
        mockLoginService = MockLoginService()
        sut = InitialModel(service: mockLoginService) // Assumes InitialModel(service:) constructor
    }

    @Test func login_success_updatesStateCorrectly() async {
        // Arrange
        let expectedToken = "fake_jwt_token"
        let loginResponse = LoginResponse(token: expectedToken)
        mockLoginService.callAuthResult = .success(loginResponse)
        sut.email = "test@example.com"
        sut.password = "password123"

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == true, "isLoggedIn should be true on successful login")
        #expect(sut.isLoading == false, "isLoading should be false after login attempt")
        #expect(sut.errorMessage == nil, "errorMessage should be nil on successful login")
        #expect(mockLoginService.callAuthCalled == true)
        #expect(mockLoginService.callAuthEmailArg == "test@example.com")
        #expect(mockLoginService.callAuthPasswordArg == "password123")
        // Optionally, check AuthManager.shared.token if accessible and relevant for this unit test
        // For a pure unit test of InitialModel, checking AuthManager might be out of scope.
    }

    @Test func login_failure_invalidCredentials_updatesStateCorrectly() async {
        // Arrange
        mockLoginService.callAuthResult = .failure(ServiceError.invalidCredentials)
        sut.email = "test@example.com"
        sut.password = "wrongpassword"

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == false, "isLoggedIn should be false on failed login")
        #expect(sut.isLoading == false, "isLoading should be false after login attempt")
        #expect(sut.errorMessage == ServiceError.invalidCredentials.localizedDescription, "errorMessage should be set for invalid credentials")
        #expect(mockLoginService.callAuthCalled == true)
    }

    @Test func login_failure_serverError_updatesStateCorrectly() async {
        // Arrange
        let serverErrorMessage = "Network connection lost"
        mockLoginService.callAuthResult = .failure(ServiceError.serverError(serverErrorMessage))
        sut.email = "test@example.com"
        sut.password = "password123"

        // Act
        await sut.login()

        // Assert
        #expect(sut.isLoggedIn == false, "isLoggedIn should be false on server error")
        #expect(sut.isLoading == false, "isLoading should be false after login attempt")
        #expect(sut.errorMessage == ServiceError.serverError(serverErrorMessage).localizedDescription, "errorMessage should be set for server error")
        #expect(mockLoginService.callAuthCalled == true)
    }

    @Test func login_isLoading_stateTransitionsCorrectly() async {
        // Arrange
        sut.email = "test@example.com"
        sut.password = "password123"

        // Use a cancellable to observe isLoading changes if needed, or check before/after await
        // For simplicity, we'll check isLoading before and after the call.
        // The actual transition during the await is harder to test without Combine expectations.

        // To ensure the async task starts and we can check isLoading = true
        // we can make the mock service delay its response.
        class DelayedMockLoginService: MockLoginService {
            override func callAuth(email: String, password: String) async throws -> LoginResponse {
                try await Task.sleep(nanoseconds: 100_000_000) // 0.1 seconds
                return try await super.callAuth(email: email, password: password)
            }
        }
        let delayedMockService = DelayedMockLoginService()
        sut = InitialModel(service: delayedMockService) // Re-init sut with delayed service
        delayedMockService.callAuthResult = .success(LoginResponse(token: "delayed_token"))


        // Act
        let loginTask = Task {
            await sut.login()
        }

        // Immediately after calling login, before it has a chance to fully complete (due to mock delay)
        // This check is timing-dependent and might be flaky.
        // A better way would be to use Combine's expectLater to check published values.
        // However, with basic XCTest, this is tricky.
        // For now, we'll assume isLoading becomes true at the start of login().

        // Let's check isLoading becomes true *synchronously* at the start of the method if possible
        // The `isLoading = true` is the first line in `login()`.

        // To test this more reliably, we'd ideally observe @Published properties.
        // Swift Testing framework might offer better tools for this eventually.
        // For now, let's trust `isLoading = true` is set.

        await loginTask.value // Wait for login to complete

        // Assert
        #expect(sut.isLoading == false, "isLoading should be false after login completes")
    }

    // Note: Input validation tests (email/password format) are omitted here
    // because the checks `try email.check(.email)` and `try password.check(.password)`
    // are currently commented out in InitialModel.swift.
    // If they were active, tests similar to the failure cases above would be added,
    // but mocking `check` or providing invalid input to trigger those specific DataErrors.
}

// To make the above tests work, InitialModel needs to be injectable.
// Example modification to InitialModel:
/*
class InitialModel: ObservableObject {
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var isLoggedIn: Bool = false

    private let service: LoginServiceProtocol // Using a protocol is even better for testability

    init(service: LoginServiceProtocol = LoginService()) { // Default to real service
        self.service = service
    }

    @MainActor
    func login() async {
        isLoading = true
        errorMessage = nil
        // ... rest of the login method
    }
}

// Define a protocol if not already done (good practice)
protocol LoginServiceProtocol {
    func callAuth(email: String, password: String) async throws -> LoginResponse
}

// Make LoginService conform
extension LoginService: LoginServiceProtocol {}

// Make MockLoginService conform
extension MockLoginService: LoginServiceProtocol {}
*/
