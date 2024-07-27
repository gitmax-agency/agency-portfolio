//
//  MainViewModel.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/5/24.
//

import Foundation
import Combine

class MainViewModel: ObservableObject {
    var networkManager: CoinsRequestManager = CoinsRequestManager(networkManager: NetworkManager())
    @Published var coinsToView: [Coin] = []
    @Published var topCoins: [Coin] = []
    @Published var subscriptions = Set<AnyCancellable>()
    @Published var offset = -loadLimit {
        didSet {
            fetch()
        }
    }
    
    @Published var isLoading: Bool = false
    @Published var isLoadingFail: Bool = false
    
    var indexFriend = 5
    
    func loadMore() {
        self.isLoadingFail = false 
        self.offset = self.offset + loadLimit
    }
    
    func reset() {
        coinsToView = []
        topCoins = []
        offset = -loadLimit
        indexFriend = 5
    }
    
    func fetch(_ textSearch: String? = nil) {
        guard !isLoading else {
            return
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            if textSearch != nil {
                self.getCoins(textSearch: textSearch ?? "")
            } else {
                self.getCoins(offset: self.offset)
            }
        }
    }
    
    func getCoins(offset: Int) {
        networkManager.getCoins(offset: self.offset)
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { (response) in
                self.isLoading = false
                
                switch response {
                case let .failure(error):
                    debugPrint("Error: \(error)")
                case .finished:
                    debugPrint("Finished: ", response)
                }
                
            }) { data in
                self.coinsToView.append(contentsOf: data.data?.coins ?? [])
                if self.topCoins.isEmpty, self.coinsToView.count > 2 {
                    self.topCoins.append(contentsOf: self.coinsToView[0...2])
                }
                
                if data.status == "fail" {
                    self.isLoadingFail = true
                    debugPrint(data.status)
                    
                    // TODO: Uncomment to load every 2 sec
//                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
//                        self.fetch()
//                    }
                } else if data.status == "success"  {
                    self.isLoadingFail = false
                    debugPrint(data.status)
                }
            }
            .store(in: &subscriptions)
    }
    
    func getCoins(textSearch: String) {
        networkManager.getCoins(textSearch: textSearch)
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { (response) in
                self.isLoading = false

                switch response {
                case let .failure(error):
                    debugPrint("Error: \(error)")
                case .finished:
                    debugPrint("Finished: ", response)
                }
                
            }) { data in
                self.coinsToView.append(contentsOf: data.data?.coins ?? [])
                if self.topCoins.isEmpty {
                    self.topCoins.append(contentsOf: self.coinsToView[0...2])
                }
                
                if data.status == "fail", !self.isLoading {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                        self.fetch()
                    }
                }
            }
            .store(in: &subscriptions)
    }
}

import SwiftUI
    
#Preview {
    MainView()
}
