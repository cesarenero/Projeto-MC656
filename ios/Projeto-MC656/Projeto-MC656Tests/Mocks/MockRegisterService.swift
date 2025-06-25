//
//  MockRegisterService.swift
//  Projeto-MC656Tests
//
//  Created by Jules on 27/07/2024.
//

import Foundation
@testable import Projeto_MC656

class MockRegisterService: RegisterService {
    var callRegisterResult: Error? // nil for success, or an Error for failure
    var callRegisterCalled: Bool = false
    var nameArg: String?
    var socialNameArg: String?
    var emailArg: String?
    var phoneArg: String?
    var cpfArg: String?
    var passwordArg: String?

    override func callRegister(name: String, socialName: String?, email: String, phone: String, cpf: String, password: String) async throws {
        callRegisterCalled = true
        nameArg = name
        socialNameArg = socialName
        emailArg = email
        phoneArg = phone
        cpfArg = cpf
        passwordArg = password

        if let error = callRegisterResult {
            throw error
        }
        // If no error, simulate success (completes normally)
    }
}
