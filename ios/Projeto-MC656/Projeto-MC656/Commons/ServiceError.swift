//
//  ServiceError.swift
//  Projeto-MC656
//
//  Created by Gab on 11/06/25.
//

import Foundation

enum ServiceError: Error, LocalizedError, Equatable {
    case invalidCredentials
    case serverError(String)
    case missingToken

    var errorDescription: String? {
        switch self {
        case .invalidCredentials:
            return "E-mail ou senha incorretos."
        case .serverError(let message):
            return message
        case .missingToken:
            return "Usuário não autenticado."
        }
    }
}

enum DataError: Error, LocalizedError, Equatable {
    case invalidName
    case invalidSocialName
    case invalidEmail
    case invalidPhoneNumber
    case invalidCPF
    case invalidPassword
    case diferentPasswords

    var errorDescription: String? {
        switch self {
        case .invalidName:
            return "Nome inválido (apenas letras com nome e sobrenome)"
        case .invalidSocialName:
            return "Nome social inválido (apenas letras com nome e sobrenome)"
        case .invalidEmail:
            return "E-mail inválido."
        case .invalidPhoneNumber:
            return "Número de telefone inválido."
        case .invalidCPF:
            return "CPF inválido."
        case .invalidPassword:
            return "A senha precisa ter pelo menos:\n - uma letra maiúscula\n - uma letra minúscula\n - um caracter especial\n - 6 caracteres"
        case .diferentPasswords:
            return "As senhas não estão iguais."
        }
    }
}

