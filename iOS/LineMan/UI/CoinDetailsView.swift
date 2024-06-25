//
//  CoinDetailsView.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/6/24.
//

import SwiftUI
import SDWebImageSwiftUI
import Combine

#Preview {
    CoinDetailsView(viewModel: CoinDetailsViewModel(uuid: ""))
}


struct CoinDetailsView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject var viewModel: CoinDetailsViewModel
    @State var thumbnail = ""
    @State var title: String = ""
    @State var description: String = ""
    @State var price: String = ""
    @State var coinrankingText: String = ""
    @State var uuid: String = ""
    @State var dataImage = UIImage()
    @ObservedObject var imageManager = ImageManager()
    
    @State var progressValue: Double = 0

    var body: some View {
        VStack(alignment: .leading) {
            HStack(alignment: .top) {
                Image(uiImage: dataImage)
                    .interpolation(.low)
                    .resizable()
                    .clipShape(Circle())
                    .aspectRatio(contentMode: .fit)
                    .frame(maxWidth: 60, maxHeight: 60)
                    .onAppear {
                        DispatchQueue.global(qos: .background).async {
                            self.imageManager.load(url: URL(string: thumbnail), options: [.continueInBackground])
                            self.imageManager
                                .setOnSuccess { image, data, error  in
                                    dataImage = UIImage(data: image.pngData() ?? Data()) ?? UIImage()
                                }
                        }
                    }
                    .frame(width: 60, height: 60)
                    .padding([.leading, .top], 15)
                VStack(alignment: .leading) {
                    Text(title)
                    Text(price)
                    Text(description)
                }
                .frame(maxWidth: .infinity, maxHeight: 60, alignment: .leading)
                .padding([.leading, .top], 15)
                
                Image("x")
                    .onTapGesture {
                        dismiss()
                    }
                    .font(.title)
                    .frame(height: 60)
                    .padding(.trailing, 15)
            }
            .frame(maxWidth: .infinity, maxHeight: 60)
            
            Spacer(minLength: 15)
            
            ProgressView(value: progressValue)
                .onAppear {
                    progressValue = 1
                }
                .progressViewStyle(GaugeProgressStyle())
                .frame(maxWidth: .infinity, maxHeight: viewModel.description.isEmpty ? 40 : 0)
                .padding(.vertical, viewModel.description.isEmpty ? 30 : 0)
                .contentShape(Rectangle())
                .rotationEffect(.radians(progressValue * 4))
                .animation(.easeInOut.speed(0.2) .repeatForever(autoreverses: true), value: progressValue)
                .isHidden(viewModel.description.isEmpty ? false : true)
            
            Text(self.viewModel.description)
                .lineSmallText()
                .foregroundStyle(.gray)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
                .padding(.all, 15)



            Spacer()
            
            Link("GO TO WEBSITE", destination: (URL(string: coinrankingText) ?? URL(string: ""))!)
                .padding([.bottom, .horizontal], 15)
                .frame(maxWidth: .infinity ,alignment: .center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.all, 15)
        .onAppear {
            viewModel.fetch()
        }

    }
    
}
