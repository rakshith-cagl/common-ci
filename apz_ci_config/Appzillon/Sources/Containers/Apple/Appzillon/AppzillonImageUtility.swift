// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class AppzillonImageUtility: NSObject {
    @objc static let shared = AppzillonImageUtility()
    private override init() {
        //do nothing
    }
     @objc func resizeImage(image: UIImage, targetWidth: Double, targetHeight: Double) -> UIImage? {
        let newSize = CGSize(width: targetWidth, height: targetHeight)
        var scaledImageRect = CGRect.zero
        scaledImageRect.size.width = CGFloat(targetWidth)
        scaledImageRect.size.height = CGFloat(targetHeight)
        scaledImageRect.origin.x = 0.0
        scaledImageRect.origin.y = 0.0
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: scaledImageRect)
        let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return scaledImage
    }
    func grayScaleImage(image: UIImage) -> UIImage {
        if let cgImage = image.cgImage {
            let beginImage = CIImage(cgImage: cgImage)
            let blackAndWhite = CIFilter(name: "CIColorControls")
            blackAndWhite?.setValuesForKeys([kCIInputImageKey: beginImage,
                                             "inputBrightness": NSNumber(value: 0.0),
                                             "inputContrast": NSNumber(value: 1.1),
                                             "inputSaturation": NSNumber(value: 0.0)])
            let bwFilterOutput = (blackAndWhite?.outputImage)
            let output = CIFilter(name: "CIExposureAdjust")
            output?.setValuesForKeys([kCIInputImageKey: bwFilterOutput as Any, "inputEV": NSNumber(value: 0.7)])
            let exposureFilterOutput = (output?.outputImage)
            let context = CIContext(options: nil)
            let cgiimage = context.createCGImage(exposureFilterOutput!, from: exposureFilterOutput!.extent)
            let newImage = UIImage(cgImage: cgiimage!, scale: 1.0, orientation: image.imageOrientation)
            return newImage
        }
        return UIImage()
    }
    func convertToBlackAndWhiteImage(image: UIImage, threshold: String) -> UIImage? {
        // Threshold not Consumed
        if let cgImage = image.cgImage {
            let beginImage = CIImage(cgImage: cgImage)
            let output = CIFilter(name: "CIColorMonochrome",
                                  parameters: [kCIInputImageKey: beginImage,
                                               "inputIntensity": NSNumber(value: 1.0),
                                               "inputColor": CIColor(color: UIColor.white)])?.outputImage
            let context = CIContext(options: nil)
            let cgiimage = context.createCGImage(output!, from: output!.extent)
            var bwImage: UIImage?
            if let cgiimage = cgiimage {
                bwImage = UIImage(cgImage: cgiimage, scale: image.scale, orientation: image.imageOrientation)
            }
            return bwImage
        }
        return UIImage()
    }
}
