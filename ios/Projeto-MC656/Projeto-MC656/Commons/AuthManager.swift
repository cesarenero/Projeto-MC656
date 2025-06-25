import Foundation

class AuthManager {
    static let shared = AuthManager()
    private init() {}
    
    var token: String?
}
