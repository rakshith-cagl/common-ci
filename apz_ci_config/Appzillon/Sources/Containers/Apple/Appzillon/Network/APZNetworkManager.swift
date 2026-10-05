// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import Alamofire

class APZNetworkManager: NSObject {
    @objc static let shared = APZNetworkManager()
    private override init() {
        //do nothing
    }
    private let yes = StringConstants.Generic.yes
    private var session: Session?
    
    @objc public func callServerWithRequest(serverIP: String,
                                      request: URLRequest,
                                      appPropertiesDictionary: NSMutableDictionary,
                                      appID: String,
                                      completionHandler: @escaping (URLResponse?, Any?, Error?, Data?) -> Void) {
        let sslPinning = appPropertiesDictionary["sslPinning"] as? String ?? StringConstants.Generic.emptyString
        let certArray = APZNetworkUtility.shared.loadCertificates(appID: appID)
        let certificates = [ "consumerbaseapp.appzillon.com": PinnedCertificatesTrustEvaluator(certificates: certArray,
                                                                                 acceptSelfSignedCertificates: true,
                                                                                 performDefaultValidation: true,
                                                                                 validateHost: true)]
        let serverTrustPolicy = ServerTrustManager(allHostsMustBeEvaluated: true, evaluators: certificates)
        let trustManager = (CryptoSwiftManager.shared.decryptSingleValue(value: sslPinning) == yes) ? serverTrustPolicy : nil
        let rootQueue = DispatchQueue(label: "org.alamofire.customQueue")
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1
        queue.underlyingQueue = rootQueue
        let delegate = SessionDelegate()
        let urlSession =  URLSession(configuration: APZNetworkUtility.shared.loadConfiguration(),
                                     delegate: delegate,
                                     delegateQueue: queue)
        
        if session == nil {
            session = Session.init(session:urlSession,
                                   delegate: delegate,
                                   rootQueue: rootQueue,
                                   serverTrustManager: trustManager)
        }
        
        
        //Alamofire Request
        session?.request(request).response(completionHandler: { responseData in
            switch responseData.result {
            case .success(let jsonData):
                if let data = jsonData, let json = try? JSONSerialization.jsonObject(with: data) {
                    completionHandler(responseData.response, json, nil, data)
                }
                
            case .failure(let error):
                print("Alamofire Error: \(error.localizedDescription)")
                completionHandler(responseData.response, nil, error, nil)
            }
        })
    }


    @objc public func callServerForUpload(serverIP: String,
                                    appPropertiesDictionary: NSMutableDictionary,
                                    appID: String,
                                    path: String,
                                    fileName: String,
                                    data: NSData,
                                    completionHandler: @escaping (URLResponse?, Any?, Error?) -> Void) {
        if let request = try? URLRequest(url: serverIP, method: .post) {
            let sslPinning = appPropertiesDictionary["sslPinning"] as? String ?? StringConstants.Generic.emptyString
            let certArray = APZNetworkUtility.shared.loadCertificates(appID: appID)
            let certificates = [ "www.iexceed.com": PinnedCertificatesTrustEvaluator(certificates: certArray,
                                                                                     acceptSelfSignedCertificates: false,
                                                                                     performDefaultValidation: true,
                                                                                     validateHost: true)]
            let serverTrustPolicy = ServerTrustManager(allHostsMustBeEvaluated: true, evaluators: certificates)
            let trustManager = (CryptoSwiftManager.shared.decryptSingleValue(value: sslPinning) == yes) ? serverTrustPolicy : nil
            let rootQueue = DispatchQueue(label: "org.alamofire.customQueue")
            let queue = OperationQueue()
            queue.maxConcurrentOperationCount = 1
            queue.underlyingQueue = rootQueue
            let delegate = SessionDelegate()
            let urlSession =  URLSession(configuration: APZNetworkUtility.shared.loadConfiguration(),
                                         delegate: delegate,
                                         delegateQueue: queue)
            
            if session == nil {
                session = Session.init(session:urlSession,
                                       delegate: delegate,
                                       rootQueue: rootQueue,
                                       serverTrustManager: trustManager)
            }
            
            session?.upload(multipartFormData: { (multipartFormData) in
                multipartFormData.append(URL(fileURLWithPath: path),
                                         withName: fileName,
                                         fileName: fileName,
                                         mimeType: APZNetworkUtility.shared.mimeTypeForFile(path: path))
                multipartFormData.append(Data(referencing: data), withName: "appzillonRequest")
            }, with: request).response { (response) in
                switch response.result {
                case .success(let jsonData):
                    print("Response JSON: \(String(describing: jsonData))")
                    print(String(decoding: jsonData!, as: UTF8.self))
                    completionHandler(response.response, jsonData, nil)
                case .failure(let error):
                    print("Alamofire Upload Error: \(error.localizedDescription)")
                    completionHandler(response.response, response, error)
                }
            }.uploadProgress { progress in
                print("File Upload Progress: \(progress.fractionCompleted*100.0)%")
            }
        }
    }

    @objc public func callServerFromInfra(serverURL: URL,
                                          request: URLRequest,
                                          appPropertiesDictionary: NSMutableDictionary,
                                          appID: String,
                                          completionHandler: @escaping (URLResponse?, Any?, Error?, Data?) -> Void) {
        let sslPinning = appPropertiesDictionary["sslPinning"] as? String ?? StringConstants.Generic.emptyString
        let certArray = APZNetworkUtility.shared.loadCertificates(appID: appID)
        let certificates = [ "www.iexceed.com": PinnedCertificatesTrustEvaluator(certificates: certArray,
                                                                                 acceptSelfSignedCertificates: false,
                                                                                 performDefaultValidation: true,
                                                                                 validateHost: true)]
        let serverTrustPolicy = ServerTrustManager(allHostsMustBeEvaluated: true, evaluators: certificates)
        let trustManager = (CryptoSwiftManager.shared.decryptSingleValue(value: sslPinning) == yes) ? serverTrustPolicy : nil
        let rootQueue = DispatchQueue(label: "org.alamofire.customQueue")
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1
        queue.underlyingQueue = rootQueue
        let delegate = SessionDelegate()
        let urlSession =  URLSession(configuration: APZNetworkUtility.shared.loadConfiguration(),
                                     delegate: delegate,
                                     delegateQueue: queue)
        
        if session == nil {
            session = Session.init(session:urlSession,
                                   delegate: delegate,
                                   rootQueue: rootQueue,
                                   serverTrustManager: trustManager)
        }
        
        //Alamofire Request
        session?.request(request).response(completionHandler: { responseData in
            switch responseData.result {
            case .success(let jsonData):
                if let data = jsonData, let json = try? JSONSerialization.jsonObject(with: data) {
                    completionHandler(responseData.response, json, nil, jsonData)
                }
                
            case .failure(let error):
                print("Alamofire Error: \(error.localizedDescription)")
                completionHandler(responseData.response, nil, error, nil)
            }
        })
    }

    @objc public func performNonAppzillonRequest(requestDictionary: [String: Any], webView: WKWebView, appID: String) {
        let pluginId = requestDictionary[StringConstants.Generic.pluginId]
        as? String ?? StringConstants.Generic.emptyString
        if APZNetworkUtility.shared.isConnectedToNetwork() {
            if let httpHeaders = requestDictionary["httpHeaders"] as? [String: Any],
               let serverIP = requestDictionary["url"] as? String,
               let requestStr = requestDictionary["request"] as? String,
               let serverURL = URL(string: serverIP) {
                var request = URLRequest(url: serverURL)
                request.httpMethod = "POST"
                for key in httpHeaders.keys {
                    let value = httpHeaders[key] as? String ?? StringConstants.Generic.emptyString
                    request.addValue(value, forHTTPHeaderField: key)
                }
                let requestString = APZNetworkUtility.shared.getStringObject(content: requestStr)
                request.httpBody = requestString.data(using: .utf8)
                let appPropertyPath = APZNetworkUtility.shared.getAppPropertiesDictionaryPath(appID: appID)
                if let appPropertiesDictionary = NSMutableDictionary(contentsOfFile: appPropertyPath),
                   let sslPinning = appPropertiesDictionary["sslPinning"] as? String {
                    let certArray = APZNetworkUtility.shared.loadCertificates(appID: appID)
                    let certificates = [ "www.iexceed.com": PinnedCertificatesTrustEvaluator(certificates: certArray,
                                                                                             acceptSelfSignedCertificates: false,
                                                                                             performDefaultValidation: true,
                                                                                             validateHost: true)]
                    let serverTrustPolicy = ServerTrustManager(allHostsMustBeEvaluated: true, evaluators: certificates)
                    let trustManager = (CryptoSwiftManager.shared.decryptSingleValue(value: sslPinning) == yes) ? serverTrustPolicy : nil
                    let rootQueue = DispatchQueue(label: "org.alamofire.customQueue")
                    let queue = OperationQueue()
                    queue.maxConcurrentOperationCount = 1
                    queue.underlyingQueue = rootQueue
                    let delegate = SessionDelegate()
                    let urlSession =  URLSession(configuration: APZNetworkUtility.shared.loadConfiguration(),
                                                 delegate: delegate,
                                                 delegateQueue: queue)
                    
                    if session == nil {
                        session = Session.init(session:urlSession,
                                               delegate: delegate,
                                               rootQueue: rootQueue,
                                               serverTrustManager: trustManager)
                    }
                    
                    //Alamofire Request
                    session?.request(request).response(completionHandler: { responseData in
                        var status = false
                        var resultKeys: [String] = []
                        var resultValues: [Any] = []
                        
                        switch responseData.result {
                        case .success(let jsonData):
                            status = true
                            if let data = jsonData, let json = try? JSONSerialization.jsonObject(with: data) {
                                resultKeys = ["httpCode", "httpHeaders", "response"]
                                resultValues = [NSNumber(integerLiteral: responseData.response?.statusCode ?? 0),
                                                responseData.response?.headers as Any,
                                                json]
                            }
                            
                        case .failure(let error):
                            print("Alamofire NonAppzillonError: \(error.localizedDescription)")
                            resultKeys = ["httpCode", "httpHeaders"]
                            resultValues = [NSNumber(integerLiteral: responseData.response?.statusCode ?? 0),
                                            responseData.response?.headers as Any]
                        }
                        let jscallBackMethod = StringConstants.Generic.jscallBackMethod
                        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                                    status: status,
                                                                                    keepAlive: false,
                                                                                    responseKeys: resultKeys,
                                                                                    responseValues: resultValues)
                        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                               jsFunctionName: jscallBackMethod,
                                                               parameter: params)
                    })
                }
            }
        } else {
            let resultkeys = [StringConstants.Generic.errorCode]
            let resultValues = ["APZ-CNT-233"]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: resultValues)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
    }
}
