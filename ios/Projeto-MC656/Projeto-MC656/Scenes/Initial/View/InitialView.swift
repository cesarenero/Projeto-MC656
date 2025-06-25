//
//  InitialView.swift
//  Projeto-MC656
//
//  Created by Gab on 18/05/25.
//

import SwiftUI

struct InitialView: View {
    private var screenPadding: CGFloat = 24
    @EnvironmentObject private var model: InitialModel

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Spacer()
                logo
                Spacer()
            }
            textFields
            NavigationLink("Ainda não tenho conta", destination: RegisterView())
            CustomButton(title: "Entrar", delegate: model, action:  {
                Task {
                    await model.login()
                }
            })
        }
        .padding(
            EdgeInsets(
                top: screenPadding,
                leading: screenPadding,
                bottom: screenPadding,
                trailing: screenPadding
            )
        )
        if let errorMessage = model.errorMessage {
            Text(errorMessage)
                .foregroundColor(.red)
                .multilineTextAlignment(.center)
        }
    }

    var logo: some View {
        Image(systemName: "bicycle.circle")
            .imageScale(.large)
            .font(.system(size: 160))
            .frame(width: 160, height: 160, alignment: .center)
            .background(
                LinearGradient(
                    gradient: Gradient(
                        colors: [Color.blue,
                                 Color.purple]),
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing))
            .cornerRadius(80)
    }

    var textFields: some View {
        VStack {
            CustomTextField(
                placeHolder: "E-mail",
                text: $model.email,
                rightIcon: "person.fill"
            )
            CustomTextField(
                placeHolder: "Senha",
                text: $model.password,
                shouldBeSecure: true,
                rightIcon: "key.fill"
            )
        }
    }
}

#Preview {
    InitialView()
}
