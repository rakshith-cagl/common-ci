//
//  SignaturePadHandler.swift
//  Appzillon
//
//  Created by Bhavya V on 07/07/21.
//

import Foundation

@objc public protocol SignaturePadHandler: AnyObject {
    @objc func saveCallBack(base64String: String, interfaceOrientation: UIInterfaceOrientation)
    @objc func cancelCallBack(interfaceOrientation: UIInterfaceOrientation)
}
