// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

protocol ImageViewDelegate: AnyObject {
    func cancelImageView(_ interfaceOrientation: UIInterfaceOrientation)
    func imageViewCallback(_ imagePath: String, status: Bool)
}
 class ImageViewController: UIViewController, CropperViewControllerDelegate {
    var customCancelButtonrequired: Bool = false
    var imagePath: String
    var jsonDict: [AnyHashable: Any]?
    var imageView: UIImageView?
    var delegate: ImageViewDelegate?
    // MARK: Initialize Methods
    public init(imagePath: String) {
        self.imagePath = imagePath
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        if customCancelButtonrequired {
            navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel,
                                                               target: self,
                                                               action: #selector(cancelButton))
        }
        if let requestJson = jsonDict,
           (requestJson[StringConstants.Generic.crop] as? String)?.uppercased() == StringConstants.Generic.yes,
           let image = UIImage(contentsOfFile: imagePath) {
            navigationItem.leftBarButtonItem = nil
            setupCropController(image: image)
        } else {
            setupImageView()
        }
    }
     func setupImage(customNavigationItem isCustomNavigationItemNeeded: Bool,
                     withJson jsonDict: [AnyHashable: Any]?) {
        customCancelButtonrequired = isCustomNavigationItemNeeded
        self.jsonDict = jsonDict
    }
    fileprivate func setupCropController(image: UIImage) {
        let isFlip: Bool = jsonDict?[StringConstants.Camera.flipRequired] as? String == StringConstants.Generic.yes
        let isRotate: Bool =
            jsonDict?[StringConstants.Camera.rotationRequired] as? String == StringConstants.Generic.yes
        let isAspectRatio: Bool =
            jsonDict?[StringConstants.Camera.aspectRatioRequired] as? String == StringConstants.Generic.yes
        let isAnglurRule: Bool =
            jsonDict?[StringConstants.Camera.angularScaleRequired] as? String == StringConstants.Generic.yes
        let cropperViewController = CropperViewController(originalImage: image,
                                                          isAngleRulerRequired: isAnglurRule,
                                                          isflipRequired: isFlip,
                                                          isAspectratioRequired: isAspectRatio,
                                                          isRotationRequired: isRotate)
        cropperViewController.delegate = self
        self.present(cropperViewController, animated: true, completion: nil)
    }
    fileprivate func setupImageView() {
        imageView = UIImageView(image: UIImage(contentsOfFile: imagePath))
        imageView?.contentMode = .scaleAspectFit
        if view.frame.size.height < view.frame.size.width {
            imageView?.frame = CGRect(x: 100, y: 0, width: view.frame.size.width - 200, height: view.frame.size.height)
            imageView?.center = CGPoint(x: view.frame.size.width / 2, y: view.frame.size.height / 2)
        } else {
            imageView?.frame = CGRect(x: 0, y: 0, width: view.frame.size.width, height: view.frame.size.height)
        }
        if let imageView = imageView {
            view.addSubview(imageView)
        }
    }
    @objc func cancelButton() {
        navigationController?.dismiss(animated: true) { [self] in
            APZLogger.log(logLvl: "I", message: "ImageViewController Cancelled")
            self.delegate?.cancelImageView(WindowUtility.getUiInterfaceOrientation())
        }
    }
    func saveImage(_ image: UIImage) {
        if let image = image.jpegData(compressionQuality: 1.0) {
            let documentDirectoryPath = FileManagerUtility.documentDirectory()
            var imageFilePath = documentDirectoryPath.appendingPathComponent(StringConstants.Generic.photo)
            if !FileManager.default.fileExists(atPath: imageFilePath.path) {
                do {
                    try FileManager.default.createDirectory(atPath: imageFilePath.path,
                                                            withIntermediateDirectories: true,
                                                            attributes: nil)
                } catch {
                    APZLogger.log(logLvl: "E", message: "ImageViewController--could not create file")
                }
            }
            let timestamp = "\(Date().timeIntervalSince1970 * 1000)"
            imageFilePath = imageFilePath.appendingPathComponent("\(timestamp)\(StringConstants.FileExtensions.jpg)")
            let result = FileManagerUtility.write(image, toFile: imageFilePath)
            if result {
                self.navigationController?.dismiss(animated: true) { [weak self] in
                    self?.delegate?.imageViewCallback(imageFilePath.path, status: true)
                }
            } else {
                self.navigationController?.dismiss(animated: true) { [weak self] in
                    self?.delegate?.imageViewCallback(StringConstants.ImageViewController.imageViewFailed,
                                                      status: false)
                }
            }
        }
    }
    // MARK: Delegate Methods
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        if size.width > view.frame.size.width {
            imageView?.frame = CGRect(x: 100, y: 0, width: size.width - 200, height: size.height)
            imageView?.center = CGPoint(x: size.width / 2, y: size.height / 2)
        } else {
            imageView?.frame = CGRect(x: 0, y: 0, width: size.width, height: size.height)
        }
    }
    public func cropperDidConfirm(_ cropper: CropperViewController, state: CropperState?) {
        cropper.dismiss(animated: true) {
            if let state = state,
               let image = cropper.originalImage.cropped(withCropperState: state) {
                self.saveImage(image)
            } else {
                self.delegate?.imageViewCallback(StringConstants.ImageViewController.imageViewFailed, status: false)
            }
        }
 }
    func cropperDidCancel(_ cropper: CropperViewController) {
        cropper.dismiss(animated: true, completion: nil)
        navigationController?.dismiss(animated: true) { [self] in
            APZLogger.log(logLvl: "I", message: "ImageViewController Cancelled")
            self.delegate?.cancelImageView(WindowUtility.getUiInterfaceOrientation())
        }
    }
}
