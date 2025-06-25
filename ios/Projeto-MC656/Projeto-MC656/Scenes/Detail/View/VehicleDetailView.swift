//
//  VehicleDetailView.swift
//  Projeto-MC656
//
//  Created by Gab on 21/06/25.
//

import SwiftUI

struct VehicleDetailView: View {
    let vehicle: Vehicle

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Image(systemName: vehicle.photo)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(height: 250)
                    .frame(maxWidth: .infinity)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                    .padding(.horizontal)
                
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        Text(vehicle.name)
                            .font(.largeTitle)
                            .fontWeight(.bold)
                        Spacer()
                        Text(vehicle.isAvailable ? "Disponível" : "Indisponível")
                            .font(.headline)
                            .foregroundColor(vehicle.isAvailable ? .green : .orange)
                            .padding(8)
                            .background(vehicle.isAvailable ? .green.opacity(0.1) : .orange.opacity(0.1))
                            .cornerRadius(8)
                    }
                    Text("Descrição")
                        .font(.title2)
                        .fontWeight(.semibold)
                    Text("Esta é uma descrição detalhada do veículo. Ele é ótimo para passeios no parque e super confortável. Possui marchas e freio a disco.")
                        .foregroundColor(.secondary)
                    Text("Acessórios Inclusos")
                        .font(.title2)
                        .fontWeight(.semibold)
                    HStack {
                        Label("Capacete", systemImage: "checkmark.circle.fill").foregroundColor(.green)
                        Label("Cadeado", systemImage: "checkmark.circle.fill").foregroundColor(.green)
                    }
                    Text("Local de Retirada")
                        .font(.title2)
                        .fontWeight(.semibold)
                    Text("Rua dos Bobos, nº 0 - Campinas, SP")
                        .foregroundColor(.secondary)
                    Text("Requisitos")
                        .font(.title2)
                        .fontWeight(.semibold)
                    Label("Exige documento de identificação", systemImage: "checkmark.shield.fill").foregroundColor(.blue)
                    Label("Exige caução de R$ 50,00", systemImage: "checkmark.shield.fill").foregroundColor(.blue)
                    
                }
                .padding(.horizontal)
                Spacer()
                Button(action: {
                    print("Solicitando empréstimo para o veículo: \(vehicle.name)")
                }) {
                    Text("Pedir Empréstimo")
                        .font(.headline)
                        .fontWeight(.bold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(vehicle.isAvailable ? Color.accentColor : Color.gray)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                }
                .disabled(!vehicle.isAvailable)
                .padding()
            }
        }
        .navigationTitle("Detalhes do Veículo")
        .navigationBarTitleDisplayMode(.inline)
    }
}


#Preview {
    NavigationStack {
        VehicleDetailView(
            vehicle: Vehicle(
                id: UUID(),
                name: "Bicicleta Caloi",
                photo: "bicycle",
                isAvailable: true,
                description: "",
                pickupLocation: "",
                accessories: nil,
                requiresId: false,
                depositAmount: 0
            )
        )
    }
}
