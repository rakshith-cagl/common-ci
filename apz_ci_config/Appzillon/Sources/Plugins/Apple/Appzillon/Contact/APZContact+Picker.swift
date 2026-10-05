//
//  APZContact+Picker.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 07/10/21.
//

import Foundation
import ContactsUI
import Contacts

extension APZContact: CNContactPickerDelegate {
    func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
        let resultkeys: [String]? = nil
        let result: [Any]? = nil
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys ?? [],
                                                                    responseValues: result ?? [])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        viewController?.dismiss(animated: true)
        print("APZContact--Cancelled")
        cleanPlugin()
    }
    public func contactPicker(_ picker: CNContactPickerViewController, didSelect contact: CNContact) {
        var birthday: String = StringConstants.Generic.emptyString
        let nameField = contact.givenName + StringConstants.Generic.whiteSpace + contact.familyName
        var phoneNumberArray = [String]()
        if contact.phoneNumbers != [] {
            let userPhoneNumbers: [CNLabeledValue<CNPhoneNumber>] = contact.phoneNumbers
            for phoneNumber in userPhoneNumbers {
                let number: CNPhoneNumber = phoneNumber.value
                let numberString: String = number.stringValue
                phoneNumberArray.append(numberString)
            }
        }
        var emailArray = [String]()
        if contact.emailAddresses != [] {
            let userEmails: [CNLabeledValue<NSString>] = contact.emailAddresses
            for email in userEmails {
                let mail: NSString = email.value
                emailArray.append(mail as String)
            }
        }
        if let birthdate = contact.birthday, birthdate.isValidDate,
           let date = NSCalendar.current.date(from: birthdate) {
            var birthdayString: String
            let dateFormate = DateFormatter()
            dateFormate.dateFormat = contactHelper.getUserDateFormat(appString: self.viewController?.appString,
                                                                     sandboxPath: self.viewController?.sandboxPath)
            birthdayString = dateFormate.string(from: date)
            birthday = birthdayString
        }
        let photo = contact.imageData
        var base64Image: String?
        base64Image = photo?.base64EncodedString()
        let filteredPhoneArray = phoneNumberArray.filter { !$0.isEmpty }
        let selectedContact: [String: Any] = [StringConstants.Generic.name: nameField,
                                              StringConstants.Contact.phoneNo: filteredPhoneArray,
                                              StringConstants.Contact.email: emailArray,
                                              StringConstants.Contact.birthday: birthday,
                                              StringConstants.Contact.encodeImage: base64Image ?? StringConstants.Generic.emptyString]
        self.selectedContact(withDetails: selectedContact)
    }
    func selectedContact(withDetails details: [String: Any]) {
        let resultkeys = [StringConstants.Generic.name,
                          StringConstants.Contact.phoneNo,
                          StringConstants.Contact.email,
                          StringConstants.Contact.birthday,
                          StringConstants.Contact.encodeImage]
        let result = [details[StringConstants.Generic.name],
                      details[StringConstants.Contact.phoneNo],
                      details[StringConstants.Contact.email],
                      details[StringConstants.Contact.birthday],
                      details[StringConstants.Contact.encodeImage]]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result as [Any])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        print("APZContact--Contact Fetched SuccessFully")
        cleanPlugin()
    }
}
