//
//  APZContact.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 18/02/21.
//
// swiftlint:disable all

import Foundation
import Contacts
import ContactsUI

class APZContact: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    var sortOrder: String?
    var contactStore: CNContactStore
    var contactHelper = ContactHelper()
    // MARK: - Plugin LifeCycle Methods
    public override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.contactStore = CNContactStore()
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    open override func execute(_ jsonDict: [AnyHashable: Any]) {
        print("APZContact--execute")
        if !jsonDict.isEmpty {
            self.requestJson = jsonDict
            self.pluginId = requestJson[StringConstants.Generic.pluginId]
                as? String ?? StringConstants.Generic.emptyString
            performOperation()
        }
    }
    fileprivate func performOperation() {
        var opnType = self.requestJson[StringConstants.Generic.opnType]
        as? String
        opnType = opnType?.lowercased()
        switch opnType {
        case StringConstants.Contact.add:
            if let details = self.requestJson[StringConstants.Contact.details] as? NSDictionary {
                self.addContacts(details: details)
            }
        case StringConstants.Generic.edit :
            if let details = self.requestJson[StringConstants.Contact.details]
                as? NSDictionary,
               let searchDetails = self.requestJson[StringConstants.Contact.searchCriteria] as? NSDictionary {
                self.editContact(editDetails: details, searchDetails: searchDetails)
            }
        case StringConstants.Generic.delete :
            if let details = self.requestJson[StringConstants.Contact.deleteCriteria] as? NSDictionary {
                self.deleteContact(details: details)
            }
        case StringConstants.Contact.search :
            if let details = self.requestJson[StringConstants.Contact.searchCriteria] as? NSDictionary {
                self.searchContact(searchDetails: details)
            }
        case  StringConstants.Contact.fetchAll :
            if let details = self.requestJson[StringConstants.Contact.details] as? NSDictionary {
                self.fetchAllContactList(details: details)
            }
        case StringConstants.Contact.fetch:
            self.fetchContacts()
        default: print("Opn Type does not match")
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: - Request for Contact
    func requestForAccess(completionHandler: @escaping (_ accessGranted: Bool) -> Void) {
        let authorizationStatus = CNContactStore.authorizationStatus(for: CNEntityType.contacts)
        switch authorizationStatus {
        case .authorized:
            completionHandler(true)
        case .denied, .notDetermined:
            self.contactStore.requestAccess(for: .contacts, completionHandler: {(access, _) -> Void in
                if access {
                    completionHandler(access)
                } else {
                    if authorizationStatus == CNAuthorizationStatus.denied ||
                        authorizationStatus == CNAuthorizationStatus.notDetermined {
                        DispatchQueue.main.async { [weak self] in
                            self?.permissionDeniedToAccessContact()
                        }
                    }
                }
            })
        default:
            completionHandler(false)
        }
    }
    // MARK: - All Contacts
    lazy var allFetchedContacts: [CNContact] = {
        var results: [CNContact] = []
        let keys = [CNContactGivenNameKey, CNContactFamilyNameKey,
                    CNContactEmailAddressesKey, CNContactPhoneNumbersKey,
                    CNContactBirthdayKey, CNContactUrlAddressesKey,
                    CNContactImageDataKey, CNContactPostalAddressesKey]
        let request = CNContactFetchRequest(keysToFetch: keys as [CNKeyDescriptor])
        request.sortOrder = contactHelper.getSortOrder(givenSortOrder: self.sortOrder)
        do {
            try self.contactStore.enumerateContacts(with: request, usingBlock: { (contact, _) in
                results.append(contact)
            })
        } catch let error {
            print("Failed to enumerate contact")
        }
        return results
    }()
    // MARK: - Add Contact
    func addContacts(details: NSDictionary) {
        self.requestForAccess { [self] (accessGranted) -> Void in
            if let vcont = self.viewController, accessGranted {
                let contactToAdd = contactHelper.createContactObject(details: details, viewController: vcont)
                self.saveContactRequest(contactToAdd: contactToAdd)
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
    }
    func saveContactRequest(contactToAdd: CNMutableContact) {
        let saveRequest = CNSaveRequest()
        saveRequest.add(contactToAdd, toContainerWithIdentifier: nil)
        do {
            try contactStore.execute(saveRequest)
            self.contactEventCallBack(status: true, message: StringConstants.Contact.addSuccessful, errorCode: nil)
        } catch {
            self.contactEventCallBack(status: false,
                                      message: StringConstants.Contact.addFailure,
                                      errorCode: StringConstants.Contact.contactCreationFailCode)
        }
    }
    // MARK: - Fetch Contact
    func fetchContacts() {
        self.requestForAccess { [self] (accessGranted) -> Void in
            if accessGranted {
                DispatchQueue.main.async {
                    let contactPickerViewController = CNContactPickerViewController()
                    contactPickerViewController.modalPresentationCapturesStatusBarAppearance = true
                    contactPickerViewController.modalPresentationStyle = .fullScreen
                    contactPickerViewController.delegate = self
                    self.viewController?.present(contactPickerViewController, animated: true)
                }
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
    }
    func fetchAllContactList(details: NSDictionary) {
        self.requestForAccess { [self] (accessGranted) -> Void in
            if let vcont = self.viewController, accessGranted {
                self.sortOrder = details[StringConstants.Contact.sortOrder] as? String
                let allConatcts = contactHelper.prepareContactArray(contacts: allFetchedContacts, viewController: vcont)
                self.fetchedAllContactList(withContacts: allConatcts)
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
    }
    // MARK: - Search
    func searchContact(searchDetails: NSDictionary) {
        self.requestForAccess { [self] (accessGranted) -> Void in
            if let vcont = self.viewController, accessGranted {
                let contacts = self.getSearchedContactsFromContactStore(searchDetails: searchDetails)
                let contactDictArray = contactHelper.prepareContactArray(contacts: contacts, viewController: vcont)
                if contactDictArray.count == 0 {
                    self.contactEventCallBack(status: false,
                                              message: StringConstants.Contact.noContactFound,
                                              errorCode: StringConstants.Contact.contactNotFoundCode)
                } else {
                    self.searchedContactList(withContacts: contactDictArray)
                }
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
    }
    func getSearchedContactsFromContactStore(searchDetails: NSDictionary) -> [CNContact] {
        var searchedContacts = [CNContact]()
        self.requestForAccess { [self] (accessGranted) -> Void in
            if accessGranted {
                let searchFirstName = searchDetails.value(forKey: StringConstants.Contact.firstName)
                    as? String ?? StringConstants.Generic.emptyString
                let searchLastName = searchDetails.value(forKey: StringConstants.Contact.lastName)
                    as? String ?? StringConstants.Generic.emptyString
                let searchPhoneHome = searchDetails.value(forKey: StringConstants.Contact.phoneHome)
                    as? String ?? StringConstants.Generic.emptyString
                let searchPhoneMobile = searchDetails.value(forKey: StringConstants.Contact.phoneMobile)
                    as? String ?? StringConstants.Generic.emptyString
                let searchPhoneWork = searchDetails.value(forKey: StringConstants.Contact.phoneWork)
                    as? String ?? StringConstants.Generic.emptyString
                if searchFirstName.isEmpty && searchLastName.isEmpty && searchPhoneHome.isEmpty &&
                    searchPhoneMobile.isEmpty && searchPhoneWork.isEmpty {
                    searchedContacts = self.allFetchedContacts
                    return
                }
                let phoneArray = [searchPhoneHome, searchPhoneMobile, searchPhoneWork]
                searchedContacts = filterSearchedContacts(phoneArray: phoneArray,
                                                          searchFirstName: searchFirstName,
                                                          searchLastName: searchLastName)
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
        return searchedContacts
    }
    fileprivate func filterSearchedContacts(phoneArray: [String], searchFirstName: String, searchLastName: String) -> [CNContact] {
        var searchedContacts = [CNContact]()
        let filteredPhoneArray = phoneArray.filter { !$0.isEmpty }
        if !searchFirstName.isEmpty && searchLastName.isEmpty {
            let fpredicate = CNContact.predicateForContacts(matchingName: searchFirstName)
            searchedContacts = self.searchWithPredicate(predicate: fpredicate,
                                                        searchedPhoneArray: filteredPhoneArray)
        } else  if searchFirstName.isEmpty && !searchLastName.isEmpty {
            let lpredicate = CNContact.predicateForContacts(matchingName: searchLastName)
            searchedContacts = self.searchWithPredicate(predicate: lpredicate,
                                                        searchedPhoneArray: filteredPhoneArray)
        } else if !searchFirstName.isEmpty && !searchLastName.isEmpty {
            let cpredicate = CNContact.predicateForContacts(matchingName: searchFirstName +
                                                        StringConstants.Generic.whiteSpace + searchLastName)
            searchedContacts = self.searchWithPredicate(predicate: cpredicate,
                                                        searchedPhoneArray: filteredPhoneArray)
        } else if searchFirstName.isEmpty && searchLastName.isEmpty {
            let fetchedContacts = self.allFetchedContacts
            searchedContacts =  contactHelper.searchContactWithPhone(searchedPhoneArray: filteredPhoneArray,
                                                                     availableContactArray: fetchedContacts)
        }
        return searchedContacts
    }
    func searchWithPredicate(predicate: NSPredicate, searchedPhoneArray: [String]) -> [CNContact] {
        var finalSearchedContact = [CNContact]()
        let keys = [CNContactGivenNameKey, CNContactFamilyNameKey,
                    CNContactEmailAddressesKey, CNContactPhoneNumbersKey,
                    CNContactBirthdayKey, CNContactUrlAddressesKey,
                    CNContactImageDataKey, CNContactPostalAddressesKey]
        var contacts: [CNContact] = []
        do {
            contacts = try contactStore.unifiedContacts(matching: predicate, keysToFetch: keys as [CNKeyDescriptor])
            if contacts.count > 0 && searchedPhoneArray.count > 0 {
                finalSearchedContact =  contactHelper.searchContactWithPhone(searchedPhoneArray: searchedPhoneArray,
                                                                             availableContactArray: contacts)
            } else {
                finalSearchedContact = contacts
            }
        } catch {
            self.contactEventCallBack(status: false,
                                      message: StringConstants.Contact.noContactFound,
                                      errorCode: StringConstants.Contact.contactNotFoundCode)
        }
        return finalSearchedContact
    }
    // MARK: - Delete
    func deleteContact(details: NSDictionary) {
        self.requestForAccess { [self] (accessGranted) -> Void in
            if accessGranted {
                let contacts = self.getSearchedContactsFromContactStore(searchDetails: details)
                guard let contact = contacts.first else {
                    self.contactEventCallBack(status: false,
                                              message: StringConstants.Contact.noContactForDelete,
                                              errorCode: StringConstants.Contact.contactNotFoundCode)
                    return
                }
                if contacts.count > 1 {
                    self.contactEventCallBack(status: false,
                                              message: StringConstants.Contact.multipleContacts,
                                              errorCode: StringConstants.Contact.multipleContactFailCode)
                    return
                }
                let req = CNSaveRequest()
                if let mutableContact = contact.mutableCopy() as? CNMutableContact {
                    req.delete(mutableContact)
                }
                do {
                    try contactStore.execute(req)
                    self.contactEventCallBack(status: true,
                                              message: StringConstants.Contact.deleteSuccessful,
                                              errorCode: nil)
                } catch {
                    self.contactEventCallBack(status: false,
                                              message: StringConstants.Contact.deleteFailure,
                                              errorCode: StringConstants.Contact.contactDeleteFailCode)
                }
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
    }
    // MARK: - Edit
    func editContact(editDetails: NSDictionary, searchDetails: NSDictionary) {
        self.requestForAccess { [self] (accessGranted) -> Void in
            if accessGranted {
                let searchedContact = self.getSearchedContactsFromContactStore(searchDetails: searchDetails)
                if searchedContact.count > 1 {
                    self.contactEventCallBack(status: false,
                                              message: StringConstants.Contact.multipleContacts,
                                              errorCode: StringConstants.Contact.multipleContactFailCode)
                } else if searchedContact.count == 0 {
                    self.contactEventCallBack(status: false,
                                              message: StringConstants.Contact.noContactFound,
                                              errorCode: StringConstants.Contact.contactNotFoundCode)
                } else if searchedContact.count == 1 {
                    self.updateContactInContactBook(editedContact: searchedContact[0], editedDetails: editDetails)
                }
            } else {
                self.permissionDeniedToAccessContact()
            }
        }
    }
    fileprivate func mutateContact(_ editedDetails: NSDictionary, _ newEditedContact: CNMutableContact, _ editedContact: CNContact) {
        let newFirstName = editedDetails[StringConstants.Contact.firstName]
        as? String ?? StringConstants.Generic.emptyString
        let newLastName = editedDetails[StringConstants.Contact.lastName]
        as? String ?? StringConstants.Generic.emptyString
        let newPhoneMobile = editedDetails[StringConstants.Contact.phoneMobile]
        as? String ?? StringConstants.Generic.emptyString
        let newPhoneHome = editedDetails[StringConstants.Contact.phoneHome]
        as? String ?? StringConstants.Generic.emptyString
        let newPhoneWork = editedDetails[StringConstants.Contact.phoneWork]
        as? String ?? StringConstants.Generic.emptyString
        let newMailId = editedDetails[StringConstants.Contact.mail]
        as? String ?? StringConstants.Generic.emptyString
        let newWebsite = editedDetails[StringConstants.Contact.website]
        as? String ?? StringConstants.Generic.emptyString
        let newAddress = editedDetails[StringConstants.Generic.address]
        as? String ?? StringConstants.Generic.emptyString
        if !newFirstName.isEmpty {
            newEditedContact.givenName = newFirstName
        }
        if !newLastName.isEmpty {
            newEditedContact.familyName = newLastName
        }
        if !newMailId.isEmpty {
            for email in editedContact.emailAddresses where email.value as String != newMailId {
                newEditedContact.emailAddresses = [CNLabeledValue(label: CNLabelHome,
                                                                  value: newMailId as NSString)]
            }
        }
        if !newAddress.isEmpty {
            let postalAddress = CNMutablePostalAddress()
            postalAddress.street = newAddress
            newEditedContact.postalAddresses = [CNLabeledValue(label: CNLabelWork,
                                                               value: postalAddress)]
        }
        newEditedContact.urlAddresses = [CNLabeledValue(label: CNLabelURLAddressHomePage,
                                                        value: newWebsite as NSString)]
        if !newPhoneHome.isEmpty {
            let phoneHomeNumber = CNPhoneNumber(stringValue: newPhoneHome)
            let phoneHomeNumberValue = CNLabeledValue(label: CNLabelHome, value: phoneHomeNumber)
            for number in editedContact.phoneNumbers where number.value.stringValue as String != newPhoneHome {
                newEditedContact.phoneNumbers.append(phoneHomeNumberValue)
            }
        }
        if !newPhoneWork.isEmpty {
            let workMobileNumber = CNPhoneNumber(stringValue: newPhoneWork)
            let workMobileValue = CNLabeledValue(label: CNLabelWork, value: workMobileNumber)
            for number in editedContact.phoneNumbers where number.value.stringValue as String != newPhoneWork {
                newEditedContact.phoneNumbers.append(workMobileValue)
            }
        }
        if !newPhoneMobile.isEmpty {
            let mobileNumber = CNPhoneNumber(stringValue: newPhoneMobile)
            let mobileValue = CNLabeledValue(label: CNLabelPhoneNumberMobile, value: mobileNumber)
            for number in editedContact.phoneNumbers where number.value.stringValue as String != newPhoneMobile {
                newEditedContact.phoneNumbers.append(mobileValue)
            }
        }
    }
    
    func updateContactInContactBook(editedContact: CNContact,
                                    editedDetails: NSDictionary) {
        if let newEditedContact = editedContact.mutableCopy() as? CNMutableContact {
            mutateContact(editedDetails, newEditedContact, editedContact)
            self.editContactRequest(newEditedContact: newEditedContact)
        }
    }
    func editContactRequest(newEditedContact: CNMutableContact) {
        do {
            let req = CNSaveRequest()
            req.update(newEditedContact)
            try contactStore.execute(req)
            self.contactEventCallBack(status: true, message: StringConstants.Contact.editSuccessful, errorCode: nil)
        } catch {
            self.contactEventCallBack(status: false,
                                      message: StringConstants.Contact.editFailure,
                                      errorCode: StringConstants.Contact.contactEditFailCode)
        }
    }
    // MARK: - Callback Methods
    func contactEventCallBack(status: Bool, message: String, errorCode: String?) {
        if status {
            let resultkeys = [StringConstants.Generic.message]
            let result = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        } else {
            let resultkeys = [StringConstants.Generic.errorCode, StringConstants.Generic.message]
            let result = [errorCode, message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result as [Any])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
        cleanPlugin()
    }
    func fetchedAllContactList(withContacts contacts: [[String: Any?]]) {
        let resultkeys = [StringConstants.Contact.contactString]
        let resultValue = [contacts]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    func searchedContactList(withContacts contacts: [[String: Any?]]) {
        var multiContactDict: [AnyHashable: Any] = [:]
        multiContactDict[StringConstants.Contact.searchJsonRoot] = contacts
        let resultkeys = [StringConstants.Contact.contactDetails]
        let resultValue = [multiContactDict]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    func permissionDeniedToAccessContact() {
        let resultkeys = [StringConstants.Generic.errorCode]
        let result = [StringConstants.Contact.contactAccessFailCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        print("APZContact--Permission denied to access Contacts")
        cleanPlugin()
    }
}
// swiftlint:enable all
