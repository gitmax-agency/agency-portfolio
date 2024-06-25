import Foundation
import Combine

protocol NetworkManagerProtocol {
    typealias Headers = [String: Any]
    
    func get<T>(type: T.Type,
                url: URL,
                headers: Headers
    ) -> AnyPublisher<T, Error> where T: Decodable
}

final class NetworkManager: NetworkManagerProtocol {
    func get<T: Decodable>(type: T.Type,
                           url: URL,
                           headers: Headers
    ) -> AnyPublisher<T, Error> {
        
        var urlRequest = URLRequest(url: url)
        
        headers.forEach { (key, value) in
            if let value = value as? String {
                urlRequest.setValue(value, forHTTPHeaderField: key)
            }
        }
        
        URLSession.shared.invalidateAndCancel()
        
        return URLSession.shared.dataTaskPublisher(for: urlRequest)
            .map(\.data)
            .decode(type: T.self, decoder: JSONDecoder())
            .eraseToAnyPublisher()
    }
    
}

struct Endpoint {
    var path: String
    var queryItems: [URLQueryItem] = []
}

extension Endpoint {
    var url: URL {
        var components = URLComponents()
        components.scheme = "https"
        components.host = "api.coinranking.com"
        components.path = "/v2/" + path
        components.queryItems = queryItems
        
        guard let url = components.url else {
            preconditionFailure("Invalid URL components: \(components)")
        }
        
        debugPrint("URL", url)
        return url
    }
    
    var headers: [String: Any] {
        return [
            "authorization": ""
        ]
    }
}
