//
//  String+Checks.swift
//  Projeto-MC656
//
//  Created by Gab on 13/06/25.
//

import Foundation

extension String {
    enum Regex: String {
        case name = "^[a-zA-Z\\s]{2,20}$"
        case socialName = "^[a-zA-Z\\s]{0,20}$"
        case email = "^(?!.*[\\.\\+\\_\\-\\@]{2})(?![0-9]+@)[\\w\\_\\-\\.\\+]+@([\\w-]+\\.)+(?![0-9]+$)[\\w-]{2,}$"
        case phoneNumber = "^(\\(?[0-9]{2}\\)?) ?([0-9]{4,5})-?([0-9]{4})$"
        case cpf = "([0-9]{3})(.?)([0-9]{3})(.?)([0-9]{3})(-?)([0-9]{2})"
        case password = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$"

        var error: Error {
            switch self {
            case .name:
                DataError.invalidName
            case .socialName:
                DataError.invalidSocialName
            case .email:
                DataError.invalidEmail
            case .phoneNumber:
                DataError.invalidPhoneNumber
            case .cpf:
                DataError.invalidCPF // Corrected error type
            case .password:
                DataError.invalidPassword
            }
        }
    }

    func check(_ regex: Regex) throws {
        guard evalute(with: regex) else {
            throw regex.error
        }
    }

    private func evalute(with regex: Regex) -> Bool {
        NSPredicate(format: "SELF MATCHES %@", regex.rawValue).evaluate(with: self)
    }

    func comparePassword(with text: String) throws {
        guard self == text else {
            throw DataError.diferentPasswords
        }
    }
}
