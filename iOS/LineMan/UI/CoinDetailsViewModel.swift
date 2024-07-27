//
//  CoinDetailsViewModel.swift
//  Igor Olnev Portfolio
//
//  Created by Igor Olnev ~ mailto:igor.olnev@gmail.com on 4/7/24.
//

import SwiftUI
import SDWebImageSwiftUI
import Combine

class CoinDetailsViewModel: ObservableObject {
    var networkManager: CoinsRequestManager = CoinsRequestManager(networkManager: NetworkManager())

    @Published var subscriptions = Set<AnyCancellable>()
    @Published var isLoading = false
    @Published var isLoadingFail = false
    
    @Published var thumbnail = ""
    @Published var title: String = ""
    @Published var description: String = ""
    @Published var price: String = ""
    @Published var coinrankingText: String = ""
    @Published var uuid: String = ""
    @Published var coin: Coin?
        
    init(uuid: String) {
        self.uuid = uuid
        fetch()
    }
    
    func fetch() {
        guard !isLoading, !isLoadingFail else {
            return
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.networkManager.getCoinDetails(uuid: self.uuid)
                .receive(on: DispatchQueue.main)
                .sink(receiveCompletion: { (response) in
                    self.isLoading = false
                    self.isLoadingFail = true
                    switch response {
                    case let .failure(error):
                        debugPrint("Error: \(error)")
                    case .finished:
                        debugPrint("Finished: ", response)
                    }
                    
                }) { data in
                    if data.status == "fail" {
                        self.isLoadingFail = true
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                            self.fetch()
                        }
                    } else if data.status == "success"  {
                        self.isLoadingFail = true
                        
                        self.coin = data.data?.coin
                        self.description = data.data?.coin?.description ?? ""
                        self.title = self.coin?.name ?? ""
                        self.price = self.coin?.price ?? ""
                        debugPrint(self.description)
                    }
                }
                .store(in: &self.subscriptions)
        }
    }
}
