//
//  LargeCrypto.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/5/24.
//

import Foundation
import SwiftUI
import Combine
import SDWebImageSwiftUI

enum RowType {
    case small
    case large
    case friend
}

struct LargeRow: View {
    var type: RowType = .large
    
    @State var thumbnail = ""
    @State var title: String = ""
    @State var description: String = ""
    @State var price: String = ""
    @State var change: String = ""
    @State var dataImage = UIImage()
    @ObservedObject var imageManager = ImageManager()

    var body: some View {
        VStack(alignment: .leading) {
            HStack(alignment: .center, spacing: 15) {
                if type == .friend {
                    HStack {
                        Image("gift")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 25, height: 25)
                    }
                    .frame(width: 40, height: 40)
                    .background(.white)
                    .clipShape(Circle())
                    Text("You can earn $10  when you invite a friend to buy crypto. Invite your friend")
                        .lineTitleText()
                        .foregroundColor(lineBlack)
                        .multilineTextAlignment(.leading)
                        .lineLimit(3)

                } else {
                    ZStack {
                        Image(uiImage: dataImage)
                            .interpolation(.low)
                            .resizable()
                            .clipShape(Circle())
                            .aspectRatio(contentMode: .fit)
                            .frame(maxWidth: 91, maxHeight: 91)
                            .onAppear {
                                DispatchQueue.main.async {
                                    self.imageManager.load(url: URL(string: thumbnail), options: [.continueInBackground])
                                    self.imageManager
                                        .setOnSuccess { image, data, error  in
                                            dataImage = UIImage(data: image.pngData() ?? Data()) ?? UIImage()
                                        }
                                }
                            }
                    }
                    VStack(alignment: .leading) {
                        Text(title)
                            .lineTitleText()
                            .foregroundColor(lineBlack)
                            .multilineTextAlignment(.leading)
                            .lineLimit(1)
                            .padding([.leading, .bottom], 15)
                        Text(description)
                            .lineTitleText()
                            .foregroundColor(lineGray)
                            .multilineTextAlignment(.leading)
                            .padding(.leading, 15)
                    }
                    Spacer()
                    VStack(alignment: .trailing) {
                        Text(price.prefix(10))
                            .lineSmallText()
                            .foregroundColor(lineBlack)
                            .multilineTextAlignment(.trailing)
                            .lineLimit(1)
                            .padding(.bottom, 15)
                        Text((change.prefix(1) == "-") ? ("↓" + change) : ("↑" + change))
                            .lineSmallText()
                            .foregroundColor((change.prefix(1) == "-") ? .red :lineGreen)
                            .multilineTextAlignment(.trailing)
                            .lineLimit(1)
                    }
                }
            }
            .frame(height: 91)
            .padding(.horizontal, 15)
        }
        .frame(maxWidth: .infinity, minHeight: 150
               , alignment: .leading)
        .padding([.horizontal], 15)
        .background((type == .friend) ? lineBackgroundBlue : lineBackgroundGray)
        .cornerRadius(19)
    }
}

#Preview {
    MainView()
}
