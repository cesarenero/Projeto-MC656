//
//  Projeto_MC656App.swift
//  Projeto-MC656
//
//  Created by Gab on 18/05/25.
//

import SwiftUI

@main
struct Projeto_MC656App: App {
    @StateObject private var model = InitialModel()

    var body: some Scene {
        WindowGroup {
            NavigationStack {
                if model.isLoggedIn {
                    VehicleListView()
                } else {
                    InitialView()
                        .environmentObject(model)
                }
            }
        }
    }
}
