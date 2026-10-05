//
//  MyPreviewController.swift
//  Appzillon
//
//  Created by Manu Gowda N R on 5/18/20.
//

import Foundation
import UIKit

 protocol delegateMyPreviewController {
    func deliverFirebaseResultsWithImage(finalResult: [String: Any])
}

final class MyPreviewController: UIViewController {
// delegate
    var delegate: delegateMyPreviewController?
    lazy private var imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.clipsToBounds = true
        imageView.isOpaque = true
        imageView.image = image
        imageView.backgroundColor = .black
        imageView.contentMode = .scaleAspectFit
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()
    // Bar button items
    lazy private var doneButton: UIBarButtonItem = {
        let title = "Done"
        let button = UIBarButtonItem(title: title, style: .plain, target: self, action: #selector(doneButtonPressed))
        button.tintColor = navigationController?.navigationBar.tintColor
        return button
    }()
    lazy private var backButton: UIBarButtonItem = {
        let title = "Retake"
        let button = UIBarButtonItem(title: title, style: .plain, target: self, action: #selector(backButtonPressed))
        button.tintColor = navigationController?.navigationBar.tintColor
        return button
    }()
    // Image and firebase result
    private let image: UIImage
    private var firebaseResult: [String: Any] = [:]
    init(image: UIImage, firebaseResult: [String: Any]?) {
        self.firebaseResult = firebaseResult!
        self.image = image
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        setupViews()
        setupConstraints()
        title = "Preview"
        navigationItem.rightBarButtonItem = doneButton
        navigationItem.leftBarButtonItem = backButton
    }
    private func setupViews() {
        view.addSubview(imageView)
    }
    private func setupConstraints() {
        let imageViewConstraints = [
            imageView.topAnchor.constraint(equalTo: view.topAnchor),
            imageView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            view.bottomAnchor.constraint(equalTo: imageView.bottomAnchor),
            view.leadingAnchor.constraint(equalTo: imageView.leadingAnchor)
        ]
        NSLayoutConstraint.activate(imageViewConstraints)
}
    @objc func doneButtonPressed() {
        var finalResults: [String: Any] = [:]
        finalResults["image"] = self.image
        finalResults["ocrWholeText"] = self.firebaseResult["ocrWholeText"]
        finalResults["ocrText"] = self.firebaseResult["ocrText"]
        self.delegate?.deliverFirebaseResultsWithImage(finalResult: finalResults)
    }
    @objc func backButtonPressed() {
        self.navigationController?.popViewController(animated: false)
    }
}
