//
//  APZCalendar.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 17/09/21.
//
// swiftlint:disable all
import Foundation
import EventKit
import EventKitUI

class APZCalendar: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    let calendarHelper = CalendarHelper()
    var eventStore: EKEventStore
    var eventsArray: [[AnyHashable: Any]] = [[:]]
    var editEventsArray: [[AnyHashable: Any]] = [[:]]
    var deleteEventsArray: [[AnyHashable: Any]] = [[:]]
    var eventCreatedCount: Int = 0
    var duplicateEventsCount: Int = 0
    var eventEditedCount: Int = 0
    var eventNotFound: Int = 0
    var eventDeletedCount: Int = 0
    var noOfTimesCreateEventExcuted: Int = 0
    var isMultipleEventEdited: Bool = false
    var isMultipleEventDeleted: Bool = false
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.eventStore =  EKEventStore()
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, requestJson)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            self.requestJson = jsonDict
            self.pluginId = requestJson[StringConstants.Generic.pluginId] as? String ?? ""
            print("APZCalendar--execute")
            self.eventsCalendarManager(eventDetails: self.requestJson)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: - Access Request
    private func requestForCalendarAccess(completionHandler: @escaping (_ accessGranted: Bool) -> Void) {
        let authorizationStatus = EKEventStore.authorizationStatus(for: EKEntityType.event)
        switch authorizationStatus {
        case .authorized:
            completionHandler(true)
        case .denied, .notDetermined:
            self.eventStore.requestAccess(to: EKEntityType.event) { (accessGranted, _)
                -> Void in
                if accessGranted {
                    completionHandler(accessGranted)
                } else {
                    DispatchQueue.main.async { [weak self] in
                        self?.noCalendarAcces()
                    }
                }
            }
        default:
            completionHandler(false)
        }
    }
    // MARK: - Calendar Manager
    func eventsCalendarManager(eventDetails: [AnyHashable: Any]) {
        let actionType = eventDetails[StringConstants.Generic.action] as? String ?? StringConstants.Generic.emptyString
        switch actionType {
        case StringConstants.Generic.create:
            self.handleAddEvent(eventDetails: eventDetails)
        case StringConstants.Generic.edit:
            self.handleEditEvent(eventDetails: eventDetails)
        case StringConstants.Generic.delete:
            self.handleDeleteEvent(eventDetails: eventDetails)
        default: print("APZCalendar--No Calendar operation present for given action type")
        }
    }
    func handleAddEvent(eventDetails: [AnyHashable: Any]) {
        if let events = eventDetails[StringConstants.Calendar.events] as? [[AnyHashable: Any]] {
            self.eventsArray = events
            if self.eventsArray.count != 0 {
                eventCreatedCount = 0
                duplicateEventsCount = 0
                noOfTimesCreateEventExcuted = 0
                for eventsDict in self.eventsArray where eventsDict[StringConstants.Generic.title] != nil {
                        self.addEvent(event: eventsDict)
                }
            }
        } else {
            self.calendarEventCallback(status: false, message: StringConstants.Calendar.addErrorCode)
        }
    }
    func handleEditEvent(eventDetails: [AnyHashable: Any]) {
        let newStartDate = eventDetails[StringConstants.Calendar.newStartDate] as? String
        let newEndDate = eventDetails[StringConstants.Calendar.newEndDate] as? String
        let newStartTime = eventDetails[StringConstants.Calendar.newStartTime] as? String
        let newEndTime = eventDetails[StringConstants.Calendar.newEndTime] as? String
        let startDate = eventDetails[StringConstants.Calendar.startDate] as? String
        let startTime = eventDetails[StringConstants.Calendar.startTime] as? String
        let endDate = eventDetails[StringConstants.Calendar.endDate] as? String
        let endTime = eventDetails[StringConstants.Calendar.endTime] as? String
        if let events = eventDetails[StringConstants.Calendar.events] as? [[AnyHashable: Any]] {
            self.handleMultipleEditEvent(events: events)
        } else if calendarHelper.checkForTimeFormat(startDate: startDate,
                                                   startTime: startTime,
                                                   endDate: endDate,
                                                   endTime: endTime) &&
                    eventDetails[StringConstants.Generic.title] as? String != StringConstants.Generic.emptyString &&
                    calendarHelper.checkForTimeFormat(startDate: newStartDate,
                                                      startTime: newStartTime,
                                                      endDate: newEndDate,
                                                      endTime: newEndTime) {
            self.isMultipleEventEdited = false
            self.editEvent(event: eventDetails)
        } else {
            if eventDetails[StringConstants.Generic.title] as? String == StringConstants.Generic.emptyString {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.titleMissing)
            } else {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.invalidDateFormat)
            }
        }
    }
    func handleMultipleEditEvent(events: [[AnyHashable: Any]]) {
        self.editEventsArray = events
        if self.editEventsArray.count != 0 {
            self.eventEditedCount = 0
            self.eventNotFound = 0
            self.isMultipleEventEdited = true
            for eventDict in self.editEventsArray {
                let sDate = eventDict[StringConstants.Calendar.startDate] as? String
                let sTime = eventDict[StringConstants.Calendar.startTime] as? String
                let eDate = eventDict[StringConstants.Calendar.endDate] as? String
                let eTime = eventDict[StringConstants.Calendar.endTime] as? String
                let nsDate = eventDict[StringConstants.Calendar.newStartDate] as? String
                let neDate = eventDict[StringConstants.Calendar.newEndDate] as? String
                let nsTime = eventDict[StringConstants.Calendar.newStartTime] as? String
                let neTime = eventDict[StringConstants.Calendar.newEndTime] as? String
                if calendarHelper.checkForTimeFormat(startDate: sDate,
                                                     startTime: sTime,
                                                     endDate: eDate,
                                                     endTime: eTime) &&
                    eventDict[StringConstants.Generic.title] as? String != StringConstants.Generic.emptyString &&
                    calendarHelper.checkForTimeFormat(startDate: nsDate,
                                                      startTime: nsTime,
                                                      endDate: neDate,
                                                      endTime: neTime) {
                    self.editEvent(event: eventDict)
                } else {
                    self.calendarEventCallback(status: false, message: StringConstants.Calendar.invalidDateFormat)
                }
            }
        }
    }
    fileprivate func validateDeleteEvents() {
        for eventDict in self.deleteEventsArray {
            let sDate = eventDict[StringConstants.Calendar.startDate] as? String
            let sTime = eventDict[StringConstants.Calendar.startTime] as? String
            let eDate = eventDict[StringConstants.Calendar.endDate] as? String
            let eTime = eventDict[StringConstants.Calendar.endTime] as? String
            if calendarHelper.checkForTimeFormat(startDate: sDate,
                                                 startTime: sTime,
                                                 endDate: eDate,
                                                 endTime: eTime) &&
                eventDict[StringConstants.Generic.title] as? String != StringConstants.Generic.emptyString {
                self.deleteEvent(event: eventDict)
            } else {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.invalidDateFormat)
            }
        }
    }
    
    func handleDeleteEvent(eventDetails: [AnyHashable: Any]) {
        let startDate = eventDetails[StringConstants.Calendar.startDate] as? String
        let startTime = eventDetails[StringConstants.Calendar.startTime] as? String
        let endDate = eventDetails[StringConstants.Calendar.endDate] as? String
        let endTime = eventDetails[StringConstants.Calendar.endTime] as? String
        if let events = eventDetails[StringConstants.Calendar.events] as? [[AnyHashable: Any]] {
            self.deleteEventsArray = events
            if self.deleteEventsArray.count != 0 {
                self.eventDeletedCount = 0
                self.eventNotFound = 0
                self.isMultipleEventDeleted = true
                validateDeleteEvents()
            }
        } else if calendarHelper.checkForTimeFormat(startDate: startDate,
                                                    startTime: startTime,
                                                    endDate: endDate,
                                                    endTime: endTime) &&
                    (eventDetails[StringConstants.Generic.title] as? String != StringConstants.Generic.emptyString) {
            self.isMultipleEventDeleted = false
            self.deleteEvent(event: eventDetails)
        } else {
            let title = eventDetails[StringConstants.Generic.title] as? String ?? StringConstants.Generic.emptyString
            let message = title.isEmpty ? StringConstants.Calendar.titleMissing : StringConstants.Calendar.invalidDateFormat
            self.calendarEventCallback(status: false, message: message)
        }
    }
    // MARK: - Add Event
    func addEvent(event: [AnyHashable: Any]) {
        self.requestForCalendarAccess { accessGranted in
            if accessGranted {
                DispatchQueue.main.async {
                    self.generateEvent(eventDetails: event)
                }
            } else {
                self.noCalendarAcces()
            }
        }
    }
    func generateEvent(eventDetails: [AnyHashable: Any]) {
        if calendarHelper.checkForDuplicateEvent(eventDict: eventDetails,
                                                 eventStore: self.eventStore) {
            duplicateEventsCount += 1
        } else {
            self.noOfTimesCreateEventExcuted += 1
            let event = calendarHelper.createEvent(eventDetails: eventDetails, eventStore: eventStore)
            var result = false
            do {
                try self.eventStore.save(event, span: .thisEvent)
                result = true
            } catch let saveError {print(saveError)}
            if result {
                self.eventCreatedCount += 1
            }
        }
        if eventsArray.count == noOfTimesCreateEventExcuted {
            if eventCreatedCount != 0 {
                self.calendarEventCallback(status: true, message: "\(eventCreatedCount) events created")
            } else {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.addFailedCode)
            }
        } else if eventsArray.count == duplicateEventsCount {
            self.calendarEventCallback(status: false, message: StringConstants.Calendar.addFailedCode)
        } else if eventCreatedCount + duplicateEventsCount == eventsArray.count {
            self.calendarEventCallback(status: true,
                                       message: "\(eventCreatedCount) events created " +
                                        " and \(duplicateEventsCount) duplicate events")
        }
    }
    // MARK: - Edit Event
    func editEvent(event: [AnyHashable: Any]) {
        self.requestForCalendarAccess { accessGranted in
            if accessGranted {
                DispatchQueue.main.async {
                    self.prepareEditEvent(eventDetails: event)}
            } else {
                self.noCalendarAcces()
            }
        }
    }
    fileprivate func performEditEvent(_ event: EKEvent) {
        var result = false
        do {
            try eventStore.save(event, span: .futureEvents)
            self.eventEditedCount += 1
            result = true
        } catch {
            self.calendarEventCallback(status: false, message: StringConstants.Calendar.editFailedCode)
        }
        if !isMultipleEventEdited {
            if result {
                self.calendarEventCallback(status: true, message: StringConstants.Calendar.eventEditSucees)
            } else {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.editFailedCode)
            }
        }
    }
    
    fileprivate func callBackForMultipleEditEvents() {
        if isMultipleEventEdited {
            if self.editEventsArray.count == eventEditedCount {
                self.calendarEventCallback(status: true, message: "\(eventEditedCount) events edited")
            } else if self.editEventsArray.count == eventNotFound {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.eventNotFoundFailedCode)
            } else if self.editEventsArray.count == eventEditedCount + eventNotFound {
                self.calendarEventCallback(status: true,
                                           message: "\(eventEditedCount) event edited " +
                                           "and \(eventNotFound) event not found")
            }
        }
    }
    
    func prepareEditEvent(eventDetails: [AnyHashable: Any]) {
        let title = eventDetails[StringConstants.Generic.title] as? String ?? StringConstants.Generic.emptyString
        var startDate: String = StringConstants.Generic.emptyString
        if let calendarStartDate = eventDetails[StringConstants.Calendar.startDate],
           let calendarStartTime = eventDetails[StringConstants.Calendar.startTime] {
            startDate = "\(calendarStartDate) \(calendarStartTime)"
        }
        var endDate: String = StringConstants.Generic.emptyString
        if let calendarEndDate = eventDetails[StringConstants.Calendar.endDate],
           let calendarEndTime = eventDetails[StringConstants.Calendar.endTime] {
            endDate = "\(calendarEndDate) \(calendarEndTime)"
        }
        var sDate: Date?
        var eDate: Date?
        var fullDateTimeFormat = false
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.Calendar.fullDateFormat
        sDate = dateFormatter.date(from: startDate)
        eDate = dateFormatter.date(from: endDate)
        if sDate != nil && eDate != nil {
            fullDateTimeFormat = true
        } else {
            dateFormatter.dateFormat = StringConstants.Calendar.halfDateFormat
            sDate = dateFormatter.date(from: startDate)
            eDate = dateFormatter.date(from: endDate)
            fullDateTimeFormat = false
        }
        if let startDate = sDate, let endDate = eDate {
            if let event = calendarHelper.searchEvent(title, start: startDate,
                                                      end: endDate,
                                                      eventStore: self.eventStore) {
                let event = calendarHelper.createEditEvent(eventDetails: eventDetails,
                                                           eventStore: eventStore,
                                                           event: event,
                                                           fullDateTimeFormat: fullDateTimeFormat,
                                                           dateFormatter: dateFormatter)
                performEditEvent(event)
            } else {
                self.eventNotFound += 1
                if !isMultipleEventEdited {
                    self.calendarEventCallback(status: false, message: StringConstants.Calendar.eventNotFoundFailedCode)
                }
            }
            callBackForMultipleEditEvents()
        } else {
            self.calendarEventCallback(status: false, message: StringConstants.Calendar.eventNotFoundFailedCode)
        }
    }
    // MARK: - Delete Event
    func deleteEvent(event: [AnyHashable: Any]) {
        self.requestForCalendarAccess { accessGranted in
            if accessGranted {
                DispatchQueue.main.async {
                    self.prepareDeleteEvent(eventDetails: event)}
            } else {
                self.noCalendarAcces()
            }
        }
    }
    fileprivate func callBackForMultipleDeleteEvents() {
        if isMultipleEventDeleted {
            if self.deleteEventsArray.count == eventDeletedCount {
                self.calendarEventCallback(status: true, message: "\(eventDeletedCount) events deleted")
            } else if self.deleteEventsArray.count == eventNotFound {
                self.calendarEventCallback(status: false, message: StringConstants.Calendar.eventNotFoundFailedCode)
            } else if self.deleteEventsArray.count == eventDeletedCount + eventNotFound {
                self.calendarEventCallback(status: true,
                                           message: "\(eventDeletedCount) event deleted" +
                                           " and \(eventNotFound) event not found")
            }
        }
    }
    
    fileprivate func performDeleteEvent(_ eventDetails: [AnyHashable : Any], _ event: EKEvent) {
        var result = false
        let isFutureEvents = eventDetails[StringConstants.Calendar.futureEvents] as? String == StringConstants.Generic.kTrue
        let span: EKSpan = isFutureEvents ? .futureEvents : .thisEvent
        do {
            try eventStore.remove(event, span: span, commit: true)
            self.eventDeletedCount += 1
            result = true
        } catch {
            self.calendarEventCallback(status: false, message: StringConstants.Calendar.deleteFailedCode)
        }
        if !isMultipleEventDeleted {
            if result {
                self.calendarEventCallback(status: true, message: StringConstants.Calendar.eventDeleteSuccess)
            } else {
                self.calendarEventCallback(status: false,
                                           message: StringConstants.Calendar.eventNotFoundFailedCode)
            }
        }
    }
    
    func prepareDeleteEvent(eventDetails: [AnyHashable: Any]) {
        let deleteObject = calendarHelper.detilsForDeleteEvent(eventDetails: eventDetails)
        if let startDate = deleteObject.startDate, let endDate = deleteObject.endDate {
            if let event = calendarHelper.searchEvent(deleteObject.title,
                                                      start: startDate,
                                                      end: endDate,
                                                      eventStore: self.eventStore) {
                performDeleteEvent(eventDetails, event)
            } else {
                self.eventNotFound += 1
                if !self.isMultipleEventDeleted {
                    self.calendarEventCallback(status: false, message: StringConstants.Calendar.eventNotFoundFailedCode)
                }
            }
            callBackForMultipleDeleteEvents()
        } else {
            self.calendarEventCallback(status: false, message: StringConstants.Calendar.eventNotFoundFailedCode)
        }
    }
    // MARK: - Callback Methods
    func noCalendarAcces() {
        let resultkeys = [StringConstants.Generic.errorCode]
        let result = [StringConstants.Calendar.accessFailedCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        print("APZCalendar--Access Denied")
        self.cleanPlugin()
    }
    func calendarEventCallback(status: Bool, message: String) {
        if status {
            let resultkeys = [StringConstants.Generic.success]
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
            let resultkeys = [StringConstants.Generic.errorCode]
            let result = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
        self.cleanPlugin()
    }
}
// swiftlint:enable all
