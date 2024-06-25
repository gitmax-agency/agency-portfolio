//
//  Extends.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/6/24.
//

import Foundation
import SwiftUI

let loadLimit = 10

extension Text {
    func lineTitleText() -> Text {
        self.lineFont(16, .bold)
    }
    
    func lineSmallText() -> Text {
        self.lineFont(12, .regular)
    }
    
    func lineFont(_ size: CGFloat, _ weight: Font.Weight = .regular) -> Text {
        switch weight {
        case .bold:
            return self.font(.custom("Roboto-Bold", size: size))
        default:
            return font(.custom("Roboto-Regular", size: size))
        }
    }
}

extension View {
    func isHidden(_ shouldHide: Bool) -> some View {
        opacity(shouldHide ? 0 : 1)
    }
}

extension Array where Element: Equatable {
    func removeDuplicates() -> [Element] {
        var result = [Element]()

        for value in self {
            if result.contains(value) == false {
                result.append(value)
            }
        }

        return result
    }
}
