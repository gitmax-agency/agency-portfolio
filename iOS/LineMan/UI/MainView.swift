//
//  ContentView.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/5/24.
//

import SwiftUI
import Combine
import SDWebImageSwiftUI

@MainActor
struct MainView: View {
    @StateObject var viewModel: MainViewModel = MainViewModel()

    @State var searchText = String("")
    @State private var showingSheet = false
    @State var selectedCoin: Coin?
    @State var progressValue: Double = 0

    var searchResults: [Coin] {
        var tempSearch: [Coin] = []
        
        tempSearch = viewModel.coinsToView.filter { isIncluded in
            isIncluded.name.contains(searchText)
        }
        
        viewModel.fetch(searchText)
        return tempSearch
    }

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView {
                Spacer(minLength: 60)
                LineSearchTextField(placeholder: "Search", text: $searchText)
                    .padding(15)
                    .shadow(color: lineGray.opacity(0.3) , radius: 1, y: 3)
                if !viewModel.topCoins.isEmpty, searchText.isEmpty {
                    VStack(alignment: .leading) {
                        HStack(spacing: 5) {
                            Text("Best")
                                .lineTitleText()
                            Text("\(viewModel.topCoins.count)")
                                .lineTitleText()
                                .foregroundStyle(.red)
                            Text("coins")
                                .lineTitleText()
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        HStack {
                            ForEach(viewModel.topCoins) { coin in
                                SmallRow(thumbnail: coin.iconUrl, title: coin.symbol, description: coin.symbol, price: coin.price ?? "", change: coin.change ?? "")
                            }
                            .shadow(color: lineGray.opacity(0.3) , radius: 1, y: 3)
                            .frame(height: 140)
                        }
                    }
                    .padding(.horizontal, 15)
                }
                Spacer(minLength: 15)
                HStack(spacing: 15) {
                    Text("Buy sell and hold crypto")
                        .lineTitleText()
                        .isHidden(viewModel.isLoading)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 15)
                LazyVStack {
                    ForEach(searchText.isEmpty ? viewModel.coinsToView : searchResults, id: \.self) { coin in
                        if viewModel.indexFriend == viewModel.coinsToView.firstIndex(of: coin), searchText.count == 0 {
                            LargeRow(type: .friend)
                                .shadow(color: lineGray.opacity(0.3) , radius: 1, y: 3)
                                .padding(.horizontal)
                                .onAppear {
                                    debugPrint("Invite friend index", self.viewModel.indexFriend)
                                    self.viewModel.indexFriend = self.viewModel.indexFriend * 2
                                }
                        }
                        
                        LargeRow(thumbnail: coin.iconUrl, title: coin.name, description: coin.symbol, price: coin.price ?? "", change: coin.change ?? "")
                                .shadow(color: lineGray.opacity(0.3) , radius: 1, y: 3)
                                .padding(.horizontal)
                                .id(coin)
                                .onTapGesture {
                                    selectedCoin = coin
                                    showingSheet = true
                                }
                        
                    }
                    .sheet(item: $selectedCoin) { coin in
                        CoinDetailsView(viewModel: CoinDetailsViewModel(uuid: coin.uuid), thumbnail: coin.iconUrl,  title: coin.name, description: coin.symbol, price: coin.price ?? "", coinrankingText: coin.coinrankingUrl ?? "")
                    }
                    

                    if !viewModel.isLoading {
                        ProgressView(value: progressValue)
                            .onAppear {
                                viewModel.loadMore()
                                progressValue = 1
                            }
                            .progressViewStyle(GaugeProgressStyle())
                            .frame(maxWidth: .infinity, maxHeight: viewModel.isLoadingFail ? 0 : 40)
                            .padding(.vertical, viewModel.isLoadingFail ? 0 : 15)
                            .contentShape(Rectangle())
                            .rotationEffect(.radians(progressValue * 4))
                            .animation(.easeInOut.speed(0.2) .repeatForever(autoreverses: true), value: progressValue)
                            .isHidden(viewModel.isLoadingFail)
                    }
                }
                if viewModel.isLoadingFail {
                    VStack(alignment: .center) {
                        HStack {
                            Text("Could not load data")
                                .lineTitleText()
                        }
                        HStack {
                            Button("Try again", action: {
                                viewModel.reset()
                                viewModel.fetch()
                            })
                            
                        }
                    }
                    .frame(maxHeight: 60)
                    .padding(.vertical, 15)
                }
            }
            .refreshable {
                viewModel.reset()
            }
        }
        .ignoresSafeArea(.all)

    }
}




#Preview {
    MainView()
}
