import Foundation
import SwiftUI

struct CoinsResponseModel: Codable {
    var status: String = ""
    var data: CoinsData?
}

struct CoinResponseModel: Codable {
    var status: String = ""
    var data: CoinData?
}

struct CoinsData: Codable {
    var coins: [Coin] = []
}

struct CoinData: Codable  {
    var coin: Coin?
}

struct Coin: Codable, Identifiable, Hashable {
    let id: UUID = UUID()
    
    static func == (lhs: Coin, rhs: Coin) -> Bool {
        return lhs.uuid == rhs.uuid
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(uuid)
    }
    
    let uuid, symbol, name: String
    let description: String?
    let color: String?
    let iconUrl: String
    let marketCap: String?
    let price: String?
    let listedAt: Int?
    let tier: Int?
    let change: String?
    let rank: Int
    var sparkline: [String?]
    let lowVolume: Bool
    let btcPrice: String?
    var coinrankingUrl: String?
    var iconURL: String?
    var websiteURL: String?
    var numberOfMarkets, numberOfExchanges: Int?
    var priceAt: Int?
    var coinrankingURL: String?
    var hasContent: Bool?
}

struct Stats: Codable {
    let total, totalCoins, totalMarkets, totalExchanges: Int
    let totalMarketCap: String

    enum CodingKeys: String, CodingKey {
        case total, totalCoins, totalMarkets, totalExchanges, totalMarketCap
    }
}

/*

enum RowType {
    case learn
    case notification
    case hh
}

struct NotificationFeedContent: Identifiable, Hashable {
    var id = UUID()
    var title: String = ""
    var time: String = "11:11"
    
    static func == (lhs: NotificationFeedContent, rhs: NotificationFeedContent) -> Bool {
        return lhs.id == rhs.id
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(title)
    }
}

struct Category: Identifiable, Codable, CustomStringConvertible {
    var id = UUID()
    
    var title: String = ""
    var isLast: Bool = false
    var isSelected: Bool = false
    var categories: [Category] = []
    
    init(title: String, categories: [Category] = []) {
        self.title = title
        self.categories = categories
        self.isLast = categories.isEmpty ? true : false
    }

    enum CodingKeys: String, CodingKey {
        case title
    }
}
*/
