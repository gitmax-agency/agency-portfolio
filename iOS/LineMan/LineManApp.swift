//
//  LineManApp.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/5/24.
//

import SwiftUI
import SDWebImageSwiftUI
import SDWebImageSVGCoder

@main
struct LineManApp: App {
    init() {
        self.setUpDependencies()        
    }
    
    var body: some Scene {
        WindowGroup {
            MainView()
        }
    }
}

extension LineManApp {
    func setUpDependencies() {
        SDImageCodersManager.shared.addCoder(SDImageSVGCoder.shared)
    }
}
