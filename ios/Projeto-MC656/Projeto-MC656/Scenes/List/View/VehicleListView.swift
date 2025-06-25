//
//  VehicleListView.swift
//  Projeto-MC656
//
//  Created by Gab on 19/06/25.
//

import SwiftUI

struct VehicleListView: View {
    @StateObject private var model = VehicleListModel()

    var body: some View {
        NavigationView {
            VStack {
                if model.isLoading {
                    ProgressView("Carregando veículos...")
                } else if let errorMessage = model.errorMessage {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .padding()
                } else {
                    List(model.vehicles) { vehicle in
                        NavigationLink(destination: VehicleDetailView(vehicle: vehicle)) {
                            VehicleCardView(vehicle: vehicle)
                        }
                    }
                }
            }
            .navigationTitle("Veículos Anunciados")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    NavigationLink(destination: MyVehiclesView()) {
                        Text("Meus Veículos")
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
}

#Preview {
    VehicleListView()
}
