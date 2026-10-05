import UIKit

@objc protocol delegateHookToAPZVision {
    func deliverScannedImage(scannedImage: UIImage, firebaseResult: [String: Any])
    func cancelledOperation()
    func failedDueToCameraPermissionDenied()
    func captureSessionTimeout()
    func captureSessionFirebaseSetupFailed()
}

@objc open class HookClassFromImageScanner: NSObject {
    var viewController: UIViewController
    @objc var scannedImage: UIImage?
    @objc var delegate: delegateHookToAPZVision?
    var requestJson = [String: Any]()
    @objc init(viewController: UIViewController) {
        self.viewController = viewController
     }

    @objc  func scanImage(viewController: UIViewController) {
        let scannerViewController = ImageScannerController(delegate: self)
        scannerViewController.modalPresentationStyle = .fullScreen
        if #available(iOS 13.0, *) {
            scannerViewController.navigationBar.tintColor = .label
        } else {
            scannerViewController.navigationBar.tintColor = .black
        }
        self.viewController.present(scannerViewController, animated: true)
    }
    @objc  func scanImage(viewController: UIViewController, requestJson: [String: Any]) {
           self.requestJson = requestJson
//        print("requestJson = \(self.requestJson)")
        let scannerViewController = ImageScannerController(delegate: self, requestJson: self.requestJson)
           scannerViewController.modalPresentationStyle = .fullScreen
           if #available(iOS 13.0, *) {
               scannerViewController.navigationBar.tintColor = .label
           } else {
               scannerViewController.navigationBar.tintColor = .black
           }
           self.viewController.present(scannerViewController, animated: true)
       }
  @objc   func selectImage(viewController: UIViewController) {
        let imagePicker = UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.sourceType = .photoLibrary
        self.viewController.present(imagePicker, animated: true)
    }
}
extension HookClassFromImageScanner: ImageScannerControllerDelegate {
    public func imageScannerController(_ scanner: ImageScannerController, didFailWithError error: Error) {
        print("error = \(error), error.localizedDescription = \(error.localizedDescription)")
        if error.localizedDescription == "Failed to get the user's authorization for camera." {
            scanner.dismiss(animated: true, completion: {
                self.delegate?.failedDueToCameraPermissionDenied()
            })
        }
    }
    public func imageScannerControllerTimeout(_ scanner: ImageScannerController) {
        scanner.dismiss(animated: true, completion: {
            self.delegate?.captureSessionTimeout()
        })
    }
    public func imageScannerControllerFirebaseSetupFailed(_ scanner: ImageScannerController) {
        scanner.dismiss(animated: true, completion: {
            self.delegate?.captureSessionFirebaseSetupFailed()
        })
    }
    public func imageScannerController(_ scanner: ImageScannerController,
                                       didFinishScanningWithResults finalResults: [String: Any]) {
        if let results: ImageScannerResults = finalResults["imageResult"] as? ImageScannerResults,
           let firebaseResult = finalResults["firebaseResult"] as? [String: Any] {
            scanner.dismiss(animated: true, completion: {
                self.scannedImage = results.croppedScan.image
                self.delegate?.deliverScannedImage(scannedImage: results.croppedScan.image,
                                                   firebaseResult: firebaseResult)
            })
        }
    }
    public func imageScannerControllerDidCancel(_ scanner: ImageScannerController) {
        scanner.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation()
        })
    }
}
extension HookClassFromImageScanner: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation()
        })
    }
    public func imagePickerController(_ picker: UIImagePickerController,
                                      didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        picker.dismiss(animated: true)
        guard let image = info[.originalImage] as? UIImage else { return }
        let scannerViewController = ImageScannerController(image: image, delegate: self)
        self.viewController.present(scannerViewController, animated: true)
    }
}
