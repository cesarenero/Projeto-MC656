//
//  ApiClient.swift
//  Projeto-MC656
//
//  Created by José Carlos Cieni Júnior on 23/06/25.
//

import Foundation

struct ApiEndpoints {

    static let baseURL = "http://localhost:8080"

    struct Rentals {
        static let start = URL(string: "\(ApiEndpoints.baseURL)/api/rentals/start")!
        
        static func end(rentalId: Int) -> URL {
            return URL(string: "\(ApiEndpoints.baseURL)/api/rentals/\(rentalId)/end")!
        }
    }

    struct Users {
        static let history = URL(string: "\(ApiEndpoints.baseURL)/api/users/me/history")!
        
        static let register = URL(string: "\(ApiEndpoints.baseURL)/api/users/register")!
    }

    struct Vehicles {
        static let list = URL(string: "\(ApiEndpoints.baseURL)/api/vehicles")!
        
        static let listAvailable = URL(string: "\(ApiEndpoints.baseURL)/api/vehicles?status=AVAILABLE")!
        
        static let types = URL(string: "\(ApiEndpoints.baseURL)/api/vehicles/types")!
        
        static func get(vehicleId: Int) -> URL {
            return URL(string: "\(ApiEndpoints.baseURL)/api/vehicles/\(vehicleId)")!
        }
    }
    
    struct Auth {
        static let login = URL(string: "\(ApiEndpoints.baseURL)/api/auth/login")!
    }
}

struct ApiClient {

    func get<Response: Decodable>(url: URL, responseType: Response.Type) async throws -> Response {
        guard let token = AuthManager.shared.token else {
            throw ServiceError.missingToken
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        return try await doRequest(request, responseType: responseType)
    }

    func post<Request : Encodable, Response: Decodable>(url: URL, requestBody: Request, responseType: Response.Type) async throws -> Response {
        guard let token = AuthManager.shared.token else {
            throw ServiceError.missingToken
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        do {
            request.httpBody = try JSONEncoder().encode(requestBody)
        } catch {
            throw ServiceError.serverError("Erro ao serializar request")
        }
        
        return try await doRequest(request, responseType: responseType)
    }
    
    func post<Request : Encodable>(url: URL, requestBody: Request) async throws {
        guard let token = AuthManager.shared.token else {
            throw ServiceError.missingToken
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        do {
            request.httpBody = try JSONEncoder().encode(requestBody)
        } catch {
            throw ServiceError.serverError("Erro ao serializar request")
        }
        
        return try await doRequest(request)
    }
    
    func post<Response: Decodable>(url: URL, responseType: Response.Type) async throws -> Response {
        guard let token = AuthManager.shared.token else {
            throw ServiceError.missingToken
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        return try await doRequest(request, responseType: responseType)
    }
    
    func post(url: URL) async throws {
        guard let token = AuthManager.shared.token else {
            throw ServiceError.missingToken
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        return try await doRequest(request)
    }
    
    private func doRequest(_ request: URLRequest) async throws {
        let (_, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw ServiceError.serverError("Erro ao processar a resposta")
        }
                
        if httpResponse.statusCode == 401 {
            throw ServiceError.invalidCredentials
        }

        guard 200..<300 ~= httpResponse.statusCode else {
            throw ServiceError.serverError("Erro no request: status \(httpResponse.statusCode)")
        }
    }
    
    private func doRequest<Response : Decodable>(_ request: URLRequest, responseType: Response.Type) async throws -> Response {
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw ServiceError.serverError("Erro ao processar a resposta")
        }
                
        if httpResponse.statusCode == 401 {
            throw ServiceError.invalidCredentials
        }

        guard 200..<300 ~= httpResponse.statusCode else {
            throw ServiceError.serverError("Erro no request: status \(httpResponse.statusCode)")
        }
                
        do {
            return try JSONDecoder().decode(responseType, from: data)
        } catch {
            throw ServiceError.serverError("Erro ao deserializar resposta")
        }
    }
}
