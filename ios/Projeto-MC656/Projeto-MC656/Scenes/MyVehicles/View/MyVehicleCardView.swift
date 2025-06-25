//
//  MyVehicleCardView.swift
//  Projeto-MC656
//
//  Created by Gab on 21/06/25.
//

import SwiftUI

struct MyVehicleCardView: View {
    let vehicle: Vehicle

    var body: some View {
        HStack(spacing: 16) {
            Image(systemName: vehicle.photo)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 80, height: 80)
                .cornerRadius(8)

            VStack(alignment: .leading, spacing: 8) {
                Text(vehicle.name)
                    .font(.headline)

                Text(vehicle.isAvailable ? "Disponível" : "Indisponível")
                    .font(.subheadline)
                    .foregroundColor(vehicle.isAvailable ? .green : .red)
            }
            Spacer()
            NavigationLink(destination: VehicleEditView(vehicle: vehicle)) {
                Text("Editar")
            }
            .buttonStyle(.bordered)
            .tint(.accentColor)
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
    }
}
