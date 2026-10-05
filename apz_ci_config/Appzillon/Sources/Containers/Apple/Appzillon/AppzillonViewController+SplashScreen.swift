// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

var currentHeightPortrait: Int = 0
var currentWidthPortrait: Int = 0
var currentHeightLandscape: Int = 0
var currentWidthLandscape: Int = 0
var orientation: UIInterfaceOrientation = WindowUtility.getUiInterfaceOrientation()
var portraitImageName: String = StringConstants.Generic.emptyString
var landscapeImageName: String = StringConstants.Generic.emptyString
extension AppzillonViewController {
    // MARK: Show splash screen
     func showSplashScreen() {
        self.isSplashScreenLaunched = false
        setCurrentHeightWidth()
        if UIDevice.current.userInterfaceIdiom == .pad {
            setImageNameForPad()
        }
        if UIDevice.current.userInterfaceIdiom == .phone {
            setImageNameForPhone()
        }
        self.splashViewL = UIImageView.init(image: UIImage.init(named: landscapeImageName))
        self.splashViewP = UIImageView.init(image: UIImage.init(named: portraitImageName))
        setImageViewProperty(imageView: self.splashViewP,
                             currentWidth: currentWidthPortrait,
                             currentHeigth: currentHeightPortrait)
        setImageViewProperty(imageView: self.splashViewL,
                             currentWidth: currentWidthLandscape,
                             currentHeigth: currentHeightLandscape)
         if isSplashImageRequired() {
             self.splashViewP.image = UIImage.gif(name: "AppzillonSplashScreenGif")
         }
        if orientation.isLandscape {
            if self.splashViewL != nil {
                self.view.addSubview(splashViewL)
            }
        } else {
            self.view.addSubview(splashViewP)
        }
        if !self.isSplashLaunchedBefore {
            self.isSplashLaunchedBefore.toggle()
        }
    }
    // MARK: Utility methods for show splash screen
    fileprivate func isSplashImageRequired() -> Bool {
        let containerPropDict = APZAppDelegateUtility.shared.getContainerPropsDict()
        if let appID = containerPropDict[StringConstants.Generic.mainAppId] as? String {
            let appPropertiesPath = AppzillonMainUtility.shared.getAppPropertiesPath(appID: appID)
            if let appPropsDictionary = NSDictionary(contentsOfFile: appPropertiesPath) as? [AnyHashable: Any],
               let needSplashAnimation = appPropsDictionary["animatedSplashScreen"] as? String,
               CryptoSwiftManager.shared.decryptSingleValue(value: needSplashAnimation) == StringConstants.Generic.yes {
                return true
            }
        }
        return false
    }
    func setImageViewProperty(imageView: UIImageView, currentWidth: Int, currentHeigth: Int) {
        imageView.backgroundColor = .white
        imageView.isHidden = false
        imageView.frame = CGRect(x: 0, y: 0, width: currentWidth, height: currentHeigth)
    }
    func setCurrentHeightWidth() {
        if UIDevice.current.userInterfaceIdiom == .pad {
            if orientation == .landscapeLeft || orientation == .landscapeRight {
                currentHeightLandscape = Int(UIScreen.main.bounds.size.height)
                currentWidthLandscape = Int(UIScreen.main.bounds.size.width)
                currentHeightPortrait = currentWidthLandscape
                currentWidthPortrait = currentHeightLandscape
            } else {
                currentHeightLandscape = Int(UIScreen.main.bounds.size.width)
                currentWidthLandscape = Int(UIScreen.main.bounds.size.height)
                currentWidthPortrait = currentHeightLandscape
                currentHeightPortrait = currentWidthLandscape
            }
        } else {
            currentHeightLandscape = Int(UIScreen.main.bounds.size.width)
            currentWidthLandscape = Int(UIScreen.main.bounds.size.height)
            currentHeightPortrait = Int(UIScreen.main.bounds.size.height)
            currentWidthPortrait = Int(UIScreen.main.bounds.size.width)
        }
    }
    func setImageNameForPad() {
        let scaleFactor: CGFloat = UIScreen.main.scale
        if scaleFactor == 1.0 {
            portraitImageName = StringConstants.SplashScreen.portraitScale1
            landscapeImageName = StringConstants.SplashScreen.landscapeScale1
        } else if scaleFactor == 2.0 {
            portraitImageName = StringConstants.SplashScreen.portraitScale2
            landscapeImageName = StringConstants.SplashScreen.landscapeScale2
        }
    }
    func setImageNameForPhone() {
        let screenHeight: CGFloat = UIScreen.main.bounds.size.height
        if screenHeight == 736.0 {
            portraitImageName = StringConstants.SplashScreen.portrait736
            landscapeImageName = StringConstants.SplashScreen.landscape736
        } else if screenHeight == 812.0 {
            portraitImageName = StringConstants.SplashScreen.portrait812
            landscapeImageName = StringConstants.SplashScreen.landscape812
        } else if screenHeight == 667.0 {
            portraitImageName = StringConstants.SplashScreen.portrait667
            landscapeImageName = StringConstants.SplashScreen.landscape667
        } else if screenHeight == 568.0 {
            portraitImageName = StringConstants.SplashScreen.portrait568
            landscapeImageName = StringConstants.SplashScreen.landscape568
        } else if screenHeight == 480.0 {
            let scaleFactor: CGFloat = UIScreen.main.scale
            if scaleFactor == 2.0 {
                portraitImageName = StringConstants.SplashScreen.portraitScale2
                landscapeImageName = StringConstants.SplashScreen.landscape480Scale2
            } else if scaleFactor == 1.0 {
                portraitImageName = StringConstants.SplashScreen.portrait480Scale1
                landscapeImageName = StringConstants.SplashScreen.landscape480Scale1
            }
        }
    }
    // MARK: Show splash screen with rotation
     func showSplashScreenWithRotation() {
        if orientation.isLandscape {
            self.splashViewP.removeFromSuperview()
            self.view.addSubview(splashViewL)
        } else {
            self.splashViewL.removeFromSuperview()
            self.view.addSubview(splashViewP)
        }
    }
    // MARK: Hide splash screen
     func hideSplashScreen() {
        if self.splashScreenAuto {
            self.splashScreenAuto.toggle()
        }
        if self.splashScreenManual {
            self.splashScreenManual.toggle()
        }
        if !self.isSplashScreenLaunched {
            self.splashViewP.removeFromSuperview()
            self.splashViewL.removeFromSuperview()
            self.splashViewP = nil
            self.splashViewL = nil
            self.isSplashScreenLaunched = true
        }
    }
}
