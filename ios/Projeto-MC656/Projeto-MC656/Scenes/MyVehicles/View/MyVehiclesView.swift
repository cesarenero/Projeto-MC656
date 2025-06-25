//
//  MyVehiclesView.swift
//  Projeto-MC656
//
//  Created by Gab on 21/06/25.
//

import SwiftUI

struct MyVehiclesView: View {
    @StateObject private var model = VehicleListModel()

    var body: some View {
        VStack {
            if model.isLoading {
                ProgressView("Carregando seus veículos...")
            } else if let errorMessage = model.errorMessage {
                Text(errorMessage)
                    .foregroundColor(.red)
                    .padding()
            } else {
                List(model.vehicles) { vehicle in
                    MyVehicleCardView(vehicle: vehicle)
                }
            }
        }
        .navigationTitle("Meus Veículos")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                NavigationLink(destination: VehicleEditView(vehicle: nil)) {
                    Image(systemName: "plus")
                }
            }
        }
        .onAppear {
            Task {
                await model.loadVehicles()
            }
        }
    }
}

#Preview {
    NavigationStack {
        MyVehiclesView()
    }
}
