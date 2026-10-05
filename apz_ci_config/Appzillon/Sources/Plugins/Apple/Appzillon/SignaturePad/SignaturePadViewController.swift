//
//  SignaturePadViewController.swift
//  Appzillon
//
//  Created by Bhavya V on 06/07/21.
//

import UIKit

class SignaturePadViewController: UIViewController {

    @IBOutlet weak var signaturePadView: SignaturePad!
    @objc weak var delegate: SignaturePadHandler?
    override func viewDidLoad() {
        super.viewDidLoad()
        signaturePadView.delegate = self
        addResetSaveAndCancelButton()
    }
    private func addResetSaveAndCancelButton() {

        let resetBarBtnItem = UIBarButtonItem(barButtonSystemItem: .refresh,
                                              target: self,
                                              action: #selector(resetTapped))
            resetBarBtnItem.isEnabled = false
        let saveBarBtnItem = UIBarButtonItem(barButtonSystemItem: .save, target: self, action: #selector(saveTapped))
            saveBarBtnItem.isEnabled = false
        let cancelBarBtnItem = UIBarButtonItem(barButtonSystemItem: .cancel,
                                               target: self,
                                               action: #selector(cancelTapped))
            navigationItem.rightBarButtonItems = [cancelBarBtnItem, saveBarBtnItem, resetBarBtnItem]
        }
    @objc func resetTapped() {
        signaturePadView.clear()
        configureResetBtn(didReset: false)
        configureSaveBtn(didEnable: false)
    }
    @objc func saveTapped() {
        configureSaveBtn(didEnable: false)
        captureSignatureAndSendCallback()
        dismiss(animated: true, completion: nil)
    }
    @objc func cancelTapped() {
        delegate?.cancelCallBack(interfaceOrientation: WindowUtility.getUiInterfaceOrientation())
        dismiss(animated: true, completion: nil)
    }
    private func configureSaveBtn(didEnable: Bool) {
        if let savebarButton = self.navigationItem.rightBarButtonItems?[1] {
            savebarButton.isEnabled = didEnable
        }
    }
    private func configureResetBtn(didReset: Bool) {
        let resetbarButton = self.navigationItem.rightBarButtonItems?.last
        resetbarButton?.isEnabled = didReset
    }
    private func captureSignatureAndSendCallback() {
        let size = CGSize(width: signaturePadView.frame.size.width, height: signaturePadView.frame.size.height)
        UIGraphicsBeginImageContextWithOptions(size, true, 0)
        guard let context = UIGraphicsGetCurrentContext() else { return }
        view.layer.render(in: context)
        guard let image = UIGraphicsGetImageFromCurrentImageContext() else { return }
        UIGraphicsEndImageContext()
        if let pngData = image.pngData() {
            let pngBase64String = pngData.base64EncodedString(options: .init(rawValue: 0))
            delegate?.saveCallBack(base64String: pngBase64String,
                                   interfaceOrientation: WindowUtility.getUiInterfaceOrientation())
        }
    }
}

extension SignaturePadViewController: SignaturePadDelegate {
    func didStart() {
        // Enable reset and save
        configureResetBtn(didReset: true)
        configureSaveBtn(didEnable: true)
    }
    func didFinish() {
        print("FINISHED")
    }
}
