import Combine
import Foundation

protocol CoinsRequestProtocol: AnyObject {
    var networkManager: NetworkManagerProtocol { get }

    func getCoins(offset: Int) -> AnyPublisher<CoinsResponseModel, Error>
    func getCoins(textSearch: String) -> AnyPublisher<CoinsResponseModel, Error>
    func getCoinDetails(uuid: String) -> AnyPublisher<CoinResponseModel, Error>
}

final class CoinsRequestManager: CoinsRequestProtocol {
    let networkManager: NetworkManagerProtocol
    
    init(networkManager: NetworkManagerProtocol) {
        self.networkManager = networkManager
    }
    
    func getCoins(offset: Int) -> AnyPublisher<CoinsResponseModel, Error> {
        var endpoint = Endpoint.coins
        endpoint.queryItems.append(URLQueryItem(name: "offset", value: offset.description))
        endpoint.queryItems.append(URLQueryItem(name: "limit", value: loadLimit.description))
        return networkManager.get(type: CoinsResponseModel.self,
                                     url: endpoint.url,
                                     headers: endpoint.headers)
    }
    
    func getCoins(textSearch: String) -> AnyPublisher<CoinsResponseModel, Error> {
        var endpoint = Endpoint.coins
        endpoint.queryItems.append(URLQueryItem(name: "search", value: textSearch))
        return networkManager.get(type: CoinsResponseModel.self,
                                  url: endpoint.url,
                                  headers: endpoint.headers)
    }
        
    func getCoinDetails(uuid: String) -> AnyPublisher<CoinResponseModel, Error> {
        let endpoint = Endpoint.coinDetails(uuid: uuid)
    
        return networkManager.get(type: CoinResponseModel.self,
                                  url: endpoint.url,
                                  headers: endpoint.headers)
    }
}

extension Endpoint {
    static var coins: Self {
        return Endpoint(path: "coins/")
    }
    
    static func coins(count: Int) -> Self {
        return Endpoint(path: "coins/",
                        queryItems: [
                            URLQueryItem(name: "limit",
                                         value: "\(count)")
            ]
        )
    }
    
    static func coinDetails(uuid: String) -> Self {
        return Endpoint(path: "coin/\(uuid)/")
    }
}

import SwiftUI

#Preview {
    MainView()
}
