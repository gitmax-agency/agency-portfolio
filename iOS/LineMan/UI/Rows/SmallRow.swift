//
//  SmallCrypto.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/5/24.
//

import Foundation
import SwiftUI
import SDWebImageSwiftUI

struct SmallRow: View {
    var type: RowType = .small
    @State var thumbnail = ""
    @State var title: String = ""
    @State var description: String = ""
    @State var price: String = ""
    @State var change: String = ""
    @ObservedObject var imageManager = ImageManager()
    @State var dataImage = UIImage()

    
    var body: some View {
        VStack(alignment: .center) {
            VStack(alignment: .center) {
                Image(uiImage: dataImage)
                    .resizable()
                    .clipShape(Circle())
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 40, height: 40)
                    .onAppear { 
                        DispatchQueue.global(qos: .background).async {
                            self.imageManager.load(url: URL(string: thumbnail), options: [.fromCacheOnly])
                            self.imageManager
                                .setOnSuccess { image, data, error  in
                                    dataImage = UIImage(data: image.pngData() ?? Data()) ?? UIImage()
                                }
                        }
                    }
                    .padding(.bottom, 5)

                VStack(alignment: .center) {
                    Text(title)
                        .lineTitleText()
                        .foregroundColor(lineBlack)
                        .multilineTextAlignment(.center)
                        .lineLimit(1)
                    Spacer()
                    Text(price.prefix(7))
                        .lineSmallText()
                        .foregroundColor(lineBlack)
                        .multilineTextAlignment(.trailing)
                        .lineLimit(1)
                }
                Spacer()
                VStack(alignment: .center) {
                    Text((change.prefix(1) == "-") ? ("↓" + change) : ("↑" + change))
                        .lineSmallText()
                        .foregroundColor((change.prefix(1) == "-") ? .red :lineGreen)
                        .multilineTextAlignment(.trailing)
                        .lineLimit(1)
                }
            }
            .frame(height: 40)
            .padding(.horizontal, 15)
        }
        .frame(maxWidth: 110, maxHeight: 140
               , alignment: .center)
        .padding([.horizontal], 15)
        .background(lineBackgroundGray)
        .cornerRadius(19)
    }
}

#Preview {
    MainView()
}
