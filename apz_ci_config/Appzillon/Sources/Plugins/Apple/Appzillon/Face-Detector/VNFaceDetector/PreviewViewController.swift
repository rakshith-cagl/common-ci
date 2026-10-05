//
//  PreviewViewController.swift
//  VisionFaceTrack
//
//  Created by Pradeep Kumar Tiwari on 10/02/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit

 protocol DelegatePreviewViewController {
    func userApprovesImage(capturedImage: UIImage)
}
class PreviewViewController: UIViewController {
    lazy var imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.clipsToBounds = true
        imageView.isOpaque = true
        imageView.backgroundColor = .black
        imageView.contentMode = .scaleAspectFill
        imageView.translatesAutoresizingMaskIntoConstraints = true
        return imageView
    }()
    var capturedImage: UIImage
    var backgroundColor: UIColor
    var textColor: UIColor
    var previewDelegate: DelegatePreviewViewController?
    override func viewDidLoad() {
        super.viewDidLoad()
         self.title = "Preview"
        // Do any additional setup after loading the view.
    }
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        self.navigationController?.navigationBar.barTintColor = self.backgroundColor
//        self.navigationController?.navigationBar.tintColor = self.textColor
        self.navigationController?.navigationBar.titleTextAttributes = [.foregroundColor: self.textColor]
        let btnCancel = UIButton(frame: CGRect(x: self.view.bounds.width*0.85,
                                               y: 0, width: self.view.bounds.width*0.15,
                                               height: 15))
        btnCancel.addTarget(self, action: #selector(PreviewViewController.userApprovesImage), for: .touchUpInside)
        btnCancel.setTitle("Done", for: .normal)
        btnCancel.setTitleColor(UIColor.systemBlue, for: .normal)
        let rightBarButton = UIBarButtonItem()
        rightBarButton.customView = btnCancel
        self.navigationItem.rightBarButtonItem = rightBarButton
        imageView.image = self.capturedImage
        imageView.frame = view.frame
        view.addSubview(imageView)
    }
    init(capturedImage: UIImage, bgColor: UIColor, txtColor: UIColor) {
          self.capturedImage = capturedImage
          self.backgroundColor = bgColor
          self.textColor = txtColor
          super.init(nibName: nil, bundle: nil)
       }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    @objc func userApprovesImage() {
        self.navigationController?.dismiss(animated: true, completion: {
//            print("store image ")
            self.previewDelegate?.userApprovesImage(capturedImage: self.capturedImage)
        });}
}
