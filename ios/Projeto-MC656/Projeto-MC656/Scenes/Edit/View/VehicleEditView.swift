//
//  VehicleEditView.swift
//  Projeto-MC656
//
//  Created by Gab on 21/06/25.
//

import SwiftUI

struct VehicleEditView: View {
    @StateObject private var model: VehicleEditModel
    @Environment(\.dismiss) private var dismiss
    
    init(vehicle: Vehicle?) {
        _model = StateObject(wrappedValue: VehicleEditModel(vehicle: vehicle))
    }
    
    var body: some View {
        Form {
            Section(header: Text("Informações Básicas")) {
                VStack {
                    Image(systemName: "camera.fill")
                        .font(.largeTitle)
                        .padding()
                        .background(Color(.secondarySystemBackground))
                        .clipShape(Circle())
                    Text("Toque para adicionar foto")
                        .font(.caption)
                }
                .frame(maxWidth: .infinity)
                
                Picker("Tipo de Veículo", selection: $model.vehicleType) {
                    ForEach(VehicleType.allCases) { type in
                        Text(type.rawValue).tag(type)
                    }
                }
                
                TextField("Nome do Veículo", text: $model.name)
                TextField("Descrição", text: $model.description, axis: .vertical)
                TextField("Local de Retirada", text: $model.pickupLocation)
            }
            
            Section(header: Text("Acessórios Inclusos")) {
                Toggle("Capacete", isOn: $model.includesHelmet)
                Toggle("Cadeado", isOn: $model.includesLock)
                Toggle("Farol", isOn: $model.includesLight)
                Toggle("Bomba de Ar", isOn: $model.includesPump)
                TextField("Outros Acessórios", text: $model.otherAccessories)
            }
            
            Section(header: Text("Requisitos do Locatário")) {
                Toggle("Exigir documento de identificação", isOn: $model.requiresId)
                Toggle("Exigir depósito caução", isOn: $model.requiresDeposit)
                
                if model.requiresDeposit {
                    HStack {
                        Text("R$")
                        TextField("Valor do Depósito", text: $model.depositAmount)
                            .keyboardType(.decimalPad)
                    }
                }
            }
            
            Section {
                Button(model.isEditing ? "Salvar Alterações" : "Cadastrar") {
                    Task {
                        await model.saveVehicle()
                        dismiss()
                    }
                }
                .tint(.accentColor)
                
                if model.isEditing {
                    Button("Apagar Veículo", role: .destructive) {
                        model.deleteVehicle()
                        dismiss()
                    }
                } else {
                    Button("Cancelar", role: .cancel) {
                        dismiss()
                    }
                    .tint(.red)
                }
            }
        }
        .navigationTitle(model.navigationTitle)
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview("Modo Cadastro") {
    NavigationStack {
        VehicleEditView(vehicle: nil)
    }
}

#Preview("Modo Edição") {
    NavigationStack {
        VehicleEditView(
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
