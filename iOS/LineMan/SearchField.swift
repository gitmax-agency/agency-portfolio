//
//  LineManApp.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/5/24.
//

import SwiftUI

struct LineTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .aspectRatio(contentMode: .fit)
            .font(Font.custom("Robotot-Regular", size: 14))
            .foregroundColor(lineGray)
            .padding(.trailing, 15)
    }
}

struct LineSearchTextField: View {
    @State var placeholder: String = ""
    @State var text: Binding<String> {
        didSet {
                debugPrint(text)
        }
    }
    
    var body: some View {
        ZStack {
            HStack {
                Image(systemName: "magnifyingglass")
                    .frame(width: 22, height: 22).padding()
                    .foregroundColor(lineGray)
                TextField(placeholder, text: text)
                    .frame(maxWidth: .infinity, maxHeight: 48, alignment: .leading)
                    .textFieldStyle(LineTextFieldStyle())
                    .padding(.leading, -10)
            }
            .frame(maxWidth: .infinity, maxHeight: 48, alignment: .leading)
            .background(lineBackgroundField)
            .cornerRadius(19)
            .overlay(
                RoundedRectangle(cornerRadius: 19)
                    .stroke(lineBorderGray, lineWidth: 1)
            )
        }
    }
}
