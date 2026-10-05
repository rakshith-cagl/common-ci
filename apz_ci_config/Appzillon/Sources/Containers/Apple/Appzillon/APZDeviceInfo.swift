// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

extension AppzillonViewController {
     func getDeviceInfo(result: [AnyHashable: Any]) {
        let screenProperties = self.setScreenProperty()
        var orientation: String?
        var lockRotation: Bool = true
        let pluginId = result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if self.appOrientation == StringConstants.Generic.any {
            if UIApplication.shared.windows.first?.windowScene?.interfaceOrientation.isLandscape == true {
                orientation = StringConstants.Generic.landScape
            } else if UIApplication.shared.windows.first?.windowScene?.interfaceOrientation.isPortrait == true {
                orientation = StringConstants.Generic.portrait
            }
            lockRotation = false
        } else {
            orientation = self.appOrientation
        }
        let resultKeys = [StringConstants.DeviceInfo.deviceId,
                          StringConstants.DeviceInfo.hashKey1,
                          StringConstants.DeviceInfo.hashKey2,
                          StringConstants.DeviceInfo.deviceGroup,
                          StringConstants.DeviceInfo.deviceOs,
                          StringConstants.DeviceInfo.screenPpi,
                          StringConstants.DeviceInfo.screenSize,
                          StringConstants.DeviceInfo.deviceType,
                          StringConstants.DeviceInfo.otaRequired,
                          StringConstants.DeviceInfo.orientation,
                          StringConstants.DeviceInfo.lockRotation,
                          StringConstants.Generic.pluginId,
                          StringConstants.Generic.deviceName]
        let appDelg = APZAppDelegateUtility.shared.appDelegate
        let resultvalues = [self.uniqueID as Any, self.uniqueID as Any,
                            StringConstants.Generic.emptyString, deviceGroup as Any,
                            StringConstants.DeviceInfo.iOS,
                            screenProperties[0],
                            screenProperties[1],
                            StringConstants.DeviceInfo.deviceTypeValue,
                            appDelg?.containerPropsDictionary[StringConstants.Generic.otaRequired] ?? "",
                            orientation as Any,
                            NSNumber(value: lockRotation), pluginId,
                            UIDevice.current.name]
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: true,
                                                                     keepAlive: false,
                                                                     responseKeys: resultKeys,
                                                                     responseValues: resultvalues)
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
    }
    func setScreenProperty() -> [AnyHashable] {
        let systemResolution: String?
        let resolutionSize = UIScreen.main.bounds.size
        if UIApplication.shared.windows.first?.windowScene?.interfaceOrientation.isLandscape == true {
            systemResolution = String(format: "%iX%i",
                                      Int(ceil(resolutionSize.height)), Int(ceil(resolutionSize.width)))
        } else {
            systemResolution = String(format: "%iX%i",
                                      Int(ceil(resolutionSize.width)),
                                      Int(ceil(resolutionSize.height)))
        }
        let scale = UIScreen.main.scale
        var scrppi: CGFloat
        if UIDevice.current.userInterfaceIdiom == .pad {
            scrppi = 132 * scale
        } else if UIDevice.current.userInterfaceIdiom == .phone {
            scrppi = 163 * scale
        } else {
            scrppi = 160 * scale
        }
        let screenProperties = [String(format: "%f", scrppi), systemResolution]
        return screenProperties
    }
}
