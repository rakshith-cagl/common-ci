// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

@objc
extension AppzillonViewController {
    // MARK: Set Bounce Method
     func setBounce(_ jsonDict: [AnyHashable: Any]) {
        let shouldDisable = jsonDict[StringConstants.AppzillonViewHandler.disable] as? String ??
        StringConstants.Generic.emptyString
        webView.scrollView.bounces = !(jsonDict.isEmpty || shouldDisable == StringConstants.Generic.yes)
    }
    // MARK: applicationDidEnterBackground utility method
     func hideScreenInBackGround() {
        let currentWidth = UIScreen.main.bounds.size.width
        let currentHeigth = UIScreen.main.bounds.size.height
        let imageToDraw = UIImage()
        self.imageViewInBackground = UIImageView(image: imageToDraw)
        self.imageViewInBackground.backgroundColor = getAppSnapshotColor()
        self.imageViewInBackground.frame = CGRect(x: 0, y: 0, width: currentWidth, height: currentHeigth)
        self.view.addSubview(imageViewInBackground)
    }
    private func getAppSnapshotColor() -> UIColor {
        let containerPropDict = APZAppDelegateUtility.shared.getContainerPropsDict()
        if let appID = containerPropDict[StringConstants.Generic.mainAppId] as? String {
            let appPropertiesPath = AppzillonMainUtility.shared.getAppPropertiesPath(appID: appID)
            if let appPropsDictionary = NSDictionary(contentsOfFile: appPropertiesPath) as? [AnyHashable: Any],
               let colorHexStringEncrypted = appPropsDictionary["appSnapshotColor"] as? String {
                let colorHexStringPlain = CryptoSwiftManager.shared.decryptSingleValue(value: colorHexStringEncrypted)
                return UIColor.colorFromHex(hex: colorHexStringPlain)
            }
            return UIColor.black
        }
        return UIColor.black
    }
    // MARK: AppExpiry method
     func appExpiryForOTA(appString: String) {
         let docDirectory = FileManagerUtility.documentDirectory()
         let appDirPath = docDirectory.appendingPathComponent("Assets/apps/\(appString)/plist/AppProperties.plist").path
        if let plistDict = NSDictionary.init(contentsOfFile: appDirPath) {
            if let appStatusEnc = plistDict["Appstatus"] as? String,
               CryptoSwiftManager.shared.decryptSingleValue(value: appStatusEnc) == "Expired" {
                let alert = UIAlertController(title: nil,
                                              message: StringConstants.Generic.appExpriedMsg,
                                              preferredStyle: UIAlertController.Style.alert)
               alert.addAction(UIAlertAction(title: StringConstants.Generic.ucOk,
                                             style: UIAlertAction.Style.default,
                                             handler: nil))
               DispatchQueue.main.async {
                   self.present(alert, animated: true, completion: nil)
               }
               self.isAppExpired = true
            } else {
                if let expDateEnc = plistDict["expiryDate"] as? String,
                    !CryptoSwiftManager.shared.decryptSingleValue(value: expDateEnc).isEmpty {
                    let todayDate = getFormattedDate(stringDate: nil)
                    let dateFromPlist = getFormattedDate(stringDate: CryptoSwiftManager.shared.decryptSingleValue(value: expDateEnc))
                    let alert = UIAlertController(title: nil,
                                                  message: StringConstants.Generic.appExpriedMsg,
                                                  preferredStyle: UIAlertController.Style.alert)
                    alert.addAction(UIAlertAction(title: StringConstants.Generic.ucOk,
                                                  style: UIAlertAction.Style.default, handler: nil))
                    if  dateFromPlist.compare(todayDate) == .orderedAscending {
                        self.isAppExpired = true
                        plistDict.setValue(CryptoSwiftManager.shared.encryptSingleValue(value: "Expired"), forKey: "Appstatus")
                        plistDict.write(toFile: appDirPath, atomically: true)
                        DispatchQueue.main.async {
                            self.present(alert, animated: true, completion: nil)
                        }
                    }
                }
            }
        }
    }
    // MARK: Utility method for app expiry
    fileprivate func getFormattedDate(stringDate: String?) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"
        dateFormatter.locale = Locale(identifier: "en_US")
        if let stringDate = stringDate {
            if let formattedDate = dateFormatter.date(from: stringDate) {
                return formattedDate
            }
        } else {
            let dateFormat = dateFormatter.string(from: Date())
            if let today = dateFormatter.date(from: dateFormat) {
                return today
            }
        }
        return Date()
    }
    // MARK: applicationWillEnterForeground utility method
     func revealTheScreen() {
        if let imageViewInBg = self.imageViewInBackground {
            imageViewInBg.removeFromSuperview()
            self.imageViewInBackground = nil
        }
    }
     func initSize() {
        var currentHeightP = 0
        var currentWidthP = 0
        var currentWidthL = 0
        var currentHeightL = 0
        var reduceyp = 0
        var reduceyl = 0
        var reduceHeightBy = 0
        let screenSizeWidth = UIScreen.main.bounds.size.width
        let screenSizeHeight = UIScreen.main.bounds.size.height
        let orientation = UIApplication.shared.statusBarOrientation
        if orientation.isLandscape {
            currentWidthL = Int(screenSizeWidth)
            currentHeightL = Int(screenSizeHeight)
            currentWidthP = Int(screenSizeHeight)
            currentHeightP = Int(screenSizeWidth)
        } else {
            currentWidthL = Int(screenSizeHeight)
            currentHeightL = Int(screenSizeWidth)
            currentWidthP = Int(screenSizeWidth)
            currentHeightP = Int(screenSizeHeight)
        }
        if let hexColorString = appPropertyDictionary["statusBarColor"] as? String {
            view.backgroundColor = UIColor.colorFromHex(hex: hexColorString)
        }
        if UIDevice.current.userInterfaceIdiom == .phone {
            let screenSize = UIScreen.main.bounds.size
            if screenSize.height == 812 {
                reduceHeightBy = 44
                if let hexColorString = appPropertyDictionary["statusBarColor"] as? String {
                    view.backgroundColor = UIColor.colorFromHex(hex: hexColorString)
                }
            } else {
                reduceHeightBy = 20
            }
        } else {
            reduceHeightBy = 20
        }
        currentHeightL -= reduceHeightBy
        currentHeightP -= reduceHeightBy
        reduceyp=reduceHeightBy
        reduceyl=reduceHeightBy
        if orientation.isLandscape {
            webView.frame = CGRect(x: 0, y: reduceyl, width: currentWidthL, height: currentHeightL)
        } else {
            webView.frame = CGRect(x: 0, y: reduceyp, width: currentWidthP, height: currentHeightP)
        }
    }
    // MARK: ViewController Life Cycle Methods
    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if self.isFirstAppLaunch {
            self.splashcount += 1
            self.splashViewL.removeFromSuperview()
            self.splashViewP.removeFromSuperview()
            self.showSplashScreen()
            self.initSize()
            self.isFirstAppLaunch = false
        }
    }
    func setStatusBarOrientation() {
        UIApplication.shared.setStatusBarOrientation(.landscapeLeft, animated: true)
        let currentWidthL = UIScreen.main.bounds.size.height
        let currentHeightL = UIScreen.main.bounds.size.width
        webView.frame = CGRect(x: 0, y: 0, width: currentWidthL, height: currentHeightL)
    }
    open override func viewDidLoad() {
        super.viewDidLoad()
        self.setupWebview()
        self.isFirstAppLaunch = true
        self.isAppExpired = false
        self.getDeviceMapping()
        self.splashcount = 0
        self.splashScreenAuto = true
        self.showSplashScreen()
        self.setOrientationValue()
        uniqueID = AppzillonMainUtility.shared.getUUID()
        ipAddress = AppzillonMainUtility.shared.getIPAddressCall()
        self.runTimeDictPath = String(format: "%@/Assets/apps/%@/plist/Container.plist", sandboxPath, appString)
        self.appPropertyDictionary = AppzillonMainUtility.shared.loadAppProperties(appString: appString)
        as? [AnyHashable: Any] ?? [:]
        self.appExpiryForOTA(appString: appString)
        if let trustAllCert = self.appPropertyDictionary["trustAllCertificates"] as? String,
           trustAllCert == StringConstants.Generic.yes {
            trustAllCetificates = true
        }
        self.isServerNonceReceived = StringConstants.Generic.no
        if let serverUrl = self.appPropertyDictionary["serverUrl"] as? String, !serverUrl.isEmpty {
            let serverUrlEncrypted = self.appPropertyDictionary["serverUrlEncReq"]
            as? String ?? StringConstants.Generic.emptyString
            if serverUrlEncrypted == StringConstants.Generic.yes {
                apzServerURL = CryptoSwiftManager.shared.getAESDecryptedStringForServerCalls(dataToDecrypt: serverUrl, keyStr: SERVERTOKENDECRYPTKEY)
            } else {
                apzServerURL = serverUrl
            }
        }
        if let serverEnabled = appPropertyDictionary["enableServer"] as? String,
            serverEnabled == StringConstants.Generic.yes,
           let mockServer = appPropertyDictionary["enableMockServer"] as? String,
           let mockServerNum = Int(mockServer), mockServerNum == 0 {
            // Not typecasting to NSNumber, hence the workaround.
            self.injectHtmlInWebview()
            self.createAppzillonRequest()
        } else {
            self.injectHtmlInWebview()
        }
        self.logArray = []
        self.logIndex = 0
        self.initEventMonitoring()
        self.setBounce([:])
        self.isForceOrientation = false
        self.keyBoardHandleInWebView()
    }
}
