//
//  ContactHelper.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 07/10/21.
//

import Foundation
import Contacts
import ContactsUI

class ContactHelper {
    func getUserDateFormat(appString: String?, sandboxPath: String?) -> String? {
        var path: String = StringConstants.Generic.emptyString
        if let appString = appString {
            path = (sandboxPath ?? StringConstants.Generic.emptyString) +
                "/Assets/apps/\(appString)/plist/UserSettings.plist"
        }
        let settingsDictionary = NSDictionary(contentsOfFile: path)
        var dataFormat = settingsDictionary?[StringConstants.Contact.dateFormat]
        if dataFormat == nil {
            dataFormat = StringConstants.Contact.standardDateFormat
        }
        return dataFormat as? String
    }
    func getSortOrder(givenSortOrder: String?) -> CNContactSortOrder {
        var sortOrder: CNContactSortOrder = .userDefault
        if givenSortOrder == StringConstants.Contact.firstName {
            sortOrder = .givenName
        } else if givenSortOrder == StringConstants.Contact.lastName {
            sortOrder = .familyName
        } else if givenSortOrder == StringConstants.Contact.sortOrder {
            sortOrder = .userDefault
        }
        return sortOrder
    }
    func prepareContactArray(contacts: [CNContact], viewController: AppzillonViewController) -> [[String: Any]] {
        var contactDict = [String: Any]()
        var contactDictArray = [[String: Any]]()
        contacts.forEach { (contact) in
            var birthday: String = StringConstants.Generic.emptyString
            contactDict[StringConstants.Contact.firstName] = contact.givenName
            contactDict[StringConstants.Contact.lastName] = contact.familyName
            let userPhoneNumbers: [CNLabeledValue<CNPhoneNumber>] = contact.phoneNumbers
            for phoneNumber in userPhoneNumbers {
                let label = CNLabeledValue<CNPhoneNumber>.localizedString(forLabel: phoneNumber.label ?? "")
                contactDict[label] = phoneNumber.value.stringValue
                    .components(separatedBy: CharacterSet(charactersIn: "/.()-+ ")).joined(separator: "")
            }
            var emailArray = [String]()
            let userEmails: [CNLabeledValue<NSString>] = contact.emailAddresses
            for email in userEmails {
                let mail: NSString = email.value
                emailArray.append(mail as String)
            }
            let email = emailArray.joined(separator: ",")
            contactDict[StringConstants.Contact.mail] = email
            let address = contact.postalAddresses.count > 0 ? "\(contact.postalAddresses[0].value.street)" : ""
            contactDict[StringConstants.Generic.address] = address
            let website = contact.urlAddresses.count > 0 ? "\(contact.urlAddresses[0].value)" : ""
            contactDict[StringConstants.Contact.website] = website
            if let birthdate = contact.birthday, birthdate.isValidDate,
               let date = NSCalendar.current.date(from: birthdate) {
                var birthdayString: String
                let dateFormate = DateFormatter()
                dateFormate.dateFormat = self.getUserDateFormat(appString: viewController.appString,
                                                                sandboxPath: viewController.sandboxPath)
                birthdayString = dateFormate.string(from: date)
                birthday = birthdayString
            }
            contactDict[StringConstants.Contact.birthday] = birthday
            let photo = contact.imageData
            var base64Image: String?
            base64Image = photo?.base64EncodedString()
            contactDict[StringConstants.Contact.encodeImage] = base64Image
            contactDictArray.append(contactDict)
        }
        return contactDictArray
    }
    func createContactObject(details: NSDictionary, viewController: AppzillonViewController) -> CNMutableContact {
        let contactToAdd = CNMutableContact()
        contactToAdd.givenName = details[StringConstants.Contact.firstName]
            as? String ?? StringConstants.Generic.emptyString
        contactToAdd.familyName = details[StringConstants.Contact.lastName]
            as? String ?? StringConstants.Generic.emptyString
        let phoneHomeNumber = CNPhoneNumber(stringValue: details[StringConstants.Contact.phoneHome]
                                                as? String ?? StringConstants.Generic.emptyString)
        let phoneHomeNumberValue = CNLabeledValue(label: CNLabelHome, value: phoneHomeNumber)
        let mobileNumber = CNPhoneNumber(stringValue: details[StringConstants.Contact.phoneMobile]
                                            as? String ?? StringConstants.Generic.emptyString)
        let mobileValue = CNLabeledValue(label: CNLabelPhoneNumberMobile, value: mobileNumber)
        let workMobileNumber = CNPhoneNumber(stringValue: details[StringConstants.Contact.phoneWork]
                                                as? String ?? StringConstants.Generic.emptyString)
        let workMobileValue = CNLabeledValue(label: CNLabelWork, value: workMobileNumber)
        contactToAdd.phoneNumbers = [mobileValue, workMobileValue, phoneHomeNumberValue]
        let mail = StringConstants.Contact.mail
        contactToAdd.emailAddresses = [CNLabeledValue(label: CNLabelHome,
                                                      value: details[mail] as? NSString ?? "" as NSString)]
        let postalAddress = CNMutablePostalAddress()
        postalAddress.street = details[StringConstants.Generic.address]
            as? String ?? StringConstants.Generic.emptyString
        contactToAdd.postalAddresses = [CNLabeledValue(label: CNLabelWork, value: postalAddress)]
        if let birthdayDate = details[StringConstants.Contact.birthday] as? String {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = self.getUserDateFormat(appString: viewController.appString,
                                                              sandboxPath: viewController.sandboxPath)
            if let date = dateFormatter.date(from: birthdayDate) {
                contactToAdd.birthday = Calendar.current.dateComponents([.day, .month, .year], from: date)
            }
        }
        let image = UIImage(contentsOfFile: details[StringConstants.Generic.imagePath]
                                as? String ?? StringConstants.Generic.emptyString)
        contactToAdd.imageData = image?.pngData()
        contactToAdd.nickname = details[StringConstants.Contact.nickName]
            as? String ?? StringConstants.Generic.emptyString
        let webSite = StringConstants.Contact.website
        contactToAdd.urlAddresses = [CNLabeledValue(label: CNLabelURLAddressHomePage,
                                                    value: details[webSite] as? NSString ?? "" as NSString )]
        return contactToAdd
    }
    func searchContactWithPhone(searchedPhoneArray: [String], availableContactArray: [CNContact]) -> [CNContact] {
        var finalContacts = [CNContact]()
        searchedPhoneArray.forEach { (phoneNumber) in
            availableContactArray.forEach { (contact) in
                finalContacts.append(contentsOf: buildContacts(contact: contact, phoneNumber: phoneNumber))
            }
        }
        return finalContacts
    }
    
    fileprivate func buildContacts(contact: CNContact, phoneNumber: String) -> [CNContact] {
        var finalContacts = [CNContact]()
        let splCharSet = "/.()-+ "
        contact.phoneNumbers.forEach { (number) in
            let fetchedNumber = number.value
            let phoneNumberValue = phoneNumber.components(separatedBy: CharacterSet(charactersIn: splCharSet))
                .joined(separator: "")
            let phoneBookNumber = fetchedNumber.stringValue
                .components(separatedBy: CharacterSet(charactersIn: splCharSet))
                .joined(separator: "")
            if phoneNumberValue == phoneBookNumber {
                finalContacts.append(contact)
            }
        }
        return finalContacts
    }
}
