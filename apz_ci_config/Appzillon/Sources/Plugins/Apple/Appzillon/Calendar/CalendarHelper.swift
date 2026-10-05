//
//  CalendarHelper.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 07/10/21.
//

import Foundation
import EventKit
import EventKitUI

class DeleteEventObject {
    var title: String
    var startDate: Date?
    var endDate: Date?
    init(title: String, startDate: Date?, endDate: Date?) {
        self.title = title
        self.startDate = startDate
        self.endDate = endDate
    }
}

class CalendarHelper {
    func searchEvent(_ title: String, start sDate: Date, end eDate: Date, eventStore: EKEventStore) -> EKEvent? {
        var reqEvent: EKEvent?
        let predicate = eventStore.predicateForEvents(withStart: sDate, end: eDate, calendars: nil)
        let existingEvents = eventStore.events(matching: predicate)
        for event in existingEvents {
            if title == event.title && event.startDate == sDate && event.endDate == eDate {
                reqEvent = event
                break
            }
        }
        return reqEvent
    }
    func checkForTimeFormat(startDate: String?, startTime: String?, endDate: String?, endTime: String?) -> Bool {
        var finalStartDate: String = StringConstants.Generic.emptyString
        if let calendarStartDate = startDate, let calendarStartTime = startTime {
            finalStartDate = "\(calendarStartDate) \(calendarStartTime)"
        }
        var finalEndDate: String = StringConstants.Generic.emptyString
        if let calendarEndDate = endDate, let calendarEndTime = endTime {
            finalEndDate = "\(calendarEndDate) \(calendarEndTime)"
        }
        let dateFormatter1 = DateFormatter()
        dateFormatter1.dateFormat = StringConstants.Calendar.fullDateFormat
        let dateFormatter2 = DateFormatter()
        dateFormatter2.dateFormat = StringConstants.Calendar.halfDateFormat
        if dateFormatter1.date(from: finalStartDate) != nil && dateFormatter1.date(from: finalEndDate) != nil {
            return true
        } else if dateFormatter2.date(from: finalStartDate) != nil && dateFormatter2.date(from: finalEndDate) != nil {
            return true
        } else {
            return false
        }
    }
    func checkForDuplicateEvent(eventDict: [AnyHashable: Any], eventStore: EKEventStore) -> Bool {
        var duplicateEvent: Bool = false
        let title = eventDict[StringConstants.Generic.title] as? String ?? StringConstants.Generic.emptyString
        var startDate: String = StringConstants.Generic.emptyString
        if let calendarStartDate = eventDict[StringConstants.Calendar.startDate],
           let calendarStartTime = eventDict[StringConstants.Calendar.startTime] {
            startDate = "\(calendarStartDate) \(calendarStartTime)"
        }
        var endDate: String = StringConstants.Generic.emptyString
        if let calendarEndDate = eventDict[StringConstants.Calendar.endDate],
           let calendarEndTime = eventDict[StringConstants.Calendar.endTime] {
            endDate = "\(calendarEndDate) \(calendarEndTime)"
        }
        var sDate: Date?
        var eDate: Date?
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.Calendar.fullDateFormat
        sDate = dateFormatter.date(from: startDate)
        eDate = dateFormatter.date(from: endDate)
        if let startDate = sDate, let endDate = eDate {
            let event = self.searchEvent(title, start: startDate, end: endDate, eventStore: eventStore)
            if event != nil {
                duplicateEvent = true
            } else {
                duplicateEvent = false
            }
        }
        return duplicateEvent
    }
    func checkForReccurence(eventDetails: [AnyHashable: Any],
                            fullDateTimeFormat: Bool,
                            dateFormatter: DateFormatter) -> EKRecurrenceRule? {
        var recurringRule: EKRecurrenceRule?
        if let eventFrequency = eventDetails[StringConstants.Calendar.frequency] as? String {
            let frequency = eventFrequency.lowercased()
            let isRecurringOccurs = (frequency == StringConstants.Calendar.frequencyDaily) ||
            (frequency == StringConstants.Calendar.frequencyWeekly) ||
            (frequency == StringConstants.Calendar.frequencyMonthly)
            if isRecurringOccurs {
                var recurrenceEndDate: Date?
                if let object = eventDetails[StringConstants.Calendar.recurrenceDate] {
                    recurrenceEndDate = fullDateTimeFormat ?
                    dateFormatter.date(from: "\(object) 23:59:59") :
                    dateFormatter.date(from: "\(object) 23:59")
                }
                
                if let recurrenceEndDate =  recurrenceEndDate {
                    let recurringEnd = EKRecurrenceEnd(end: recurrenceEndDate)
                    recurringRule = EKRecurrenceRule(
                        recurrenceWith: getEventFrequency(frequency: frequency),
                        interval: 1,
                        end: recurringEnd)
                } else {
                    recurringRule = EKRecurrenceRule(
                        recurrenceWith: getEventFrequency(frequency: frequency),
                        interval: 1,
                        end: nil)
                }
                return recurringRule
            }
            print("No Recurrence present")
        }
        return recurringRule
    }
    fileprivate func getEventFrequency(frequency: String) -> EKRecurrenceFrequency {
        var eventFreq: EKRecurrenceFrequency
        if frequency == StringConstants.Calendar.frequencyDaily {
            eventFreq = .daily
        } else if frequency == StringConstants.Calendar.frequencyWeekly {
            eventFreq = .weekly
        } else if frequency == StringConstants.Calendar.frequencyMonthly {
            eventFreq = .monthly
        } else {
            eventFreq = .yearly
        }
        return eventFreq
    }
    func checkForAlarm(eventDetails: [AnyHashable: Any]) -> [EKAlarm]? {
        var eventAlarmas: [EKAlarm]? = []
        if eventDetails[StringConstants.Calendar.alarm] as? String == StringConstants.Calendar.alarm5Min {
            eventAlarmas = [EKAlarm(relativeOffset: 60.0 * -5.0)]
        } else if eventDetails[StringConstants.Calendar.alarm] as? String == StringConstants.Calendar.alarm15Min {
            eventAlarmas = [EKAlarm(relativeOffset: 60.0 * -15.0)]
        } else if eventDetails[StringConstants.Calendar.alarm] as? String == StringConstants.Calendar.alarm1Hour {
            eventAlarmas = [EKAlarm(relativeOffset: 60.0 * -60.0)]
        } else if eventDetails[StringConstants.Calendar.alarm] as? String == StringConstants.Calendar.alarm1Day {
            eventAlarmas = [EKAlarm(relativeOffset: 60.0 * -60.0 * 24.0)]
        } else {
            eventAlarmas = nil
        }
        return eventAlarmas
    }
    func createEvent(eventDetails: [AnyHashable: Any], eventStore: EKEventStore) -> EKEvent {
        let event = EKEvent(eventStore: eventStore)
        event.title = eventDetails[StringConstants.Generic.title] as? String
        event.notes = eventDetails[StringConstants.Calendar.summary] as? String
        var startDate: String? = StringConstants.Generic.emptyString
        if let calendarStartDate = eventDetails[StringConstants.Calendar.startDate],
           let calendarStartTime = eventDetails[StringConstants.Calendar.startTime] {
            startDate = "\(calendarStartDate) \(calendarStartTime)"
        }
        var endDate: String? = StringConstants.Generic.emptyString
        if let calendarEndDate = eventDetails[StringConstants.Calendar.endDate],
           let calendarEndTime = eventDetails[StringConstants.Calendar.endTime] {
            endDate = "\(calendarEndDate) \(calendarEndTime)"
        }
        var fullDateTimeFormat = false
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.Calendar.fullDateFormat
        if let sDate = startDate, let eDate = endDate {
            event.startDate = dateFormatter.date(from: sDate)
            event.endDate = dateFormatter.date(from: eDate)
            if event.startDate != nil && event.endDate != nil {
                fullDateTimeFormat = true
            } else {
                fullDateTimeFormat = false
                dateFormatter.dateFormat = StringConstants.Calendar.halfDateFormat
                event.startDate = dateFormatter.date(from: sDate)
                event.endDate = dateFormatter.date(from: eDate)
            }
            event.location = eventDetails[StringConstants.Generic.location] as? String
            event.calendar = eventStore.defaultCalendarForNewEvents
            // check for recurrence
            if let recurringRule = self.checkForReccurence(eventDetails: eventDetails,
                                                           fullDateTimeFormat: fullDateTimeFormat,
                                                           dateFormatter: dateFormatter) {
                event.recurrenceRules = [recurringRule].compactMap { $0 }
            }
            // Check for alarm
            event.alarms  = self.checkForAlarm(eventDetails: eventDetails)
        }
        return event
    }
    func createEditEvent(eventDetails: [AnyHashable: Any],
                         eventStore: EKEventStore,
                         event: EKEvent,
                         fullDateTimeFormat: Bool,
                         dateFormatter: DateFormatter) -> EKEvent {
        event.title = eventDetails[StringConstants.Generic.title] as? String ?? StringConstants.Generic.emptyString
        event.notes = eventDetails[StringConstants.Calendar.summary] as? String
        var newStartDate: String = StringConstants.Generic.emptyString
        if let object = eventDetails[StringConstants.Calendar.newStartDate],
           let object1 = eventDetails[StringConstants.Calendar.newStartTime] {
            newStartDate = "\(object) \(object1)"
        }
        var newEndDate: String = StringConstants.Generic.emptyString
        if let object = eventDetails[StringConstants.Calendar.newEndDate],
           let object1 = eventDetails[StringConstants.Calendar.newEndTime] {
            newEndDate = "\(object) \(object1)"
        }
        let sNewStartDate = dateFormatter.date(from: newStartDate)
        event.startDate = sNewStartDate
        let eNewEndDate = dateFormatter.date(from: newEndDate)
        event.endDate = eNewEndDate
        event.location = eventDetails[StringConstants.Generic.location] as? String
        event.calendar = eventStore.defaultCalendarForNewEvents
        if let recurringRule = self.checkForReccurence(eventDetails: eventDetails,
                                                       fullDateTimeFormat: fullDateTimeFormat,
                                                       dateFormatter: dateFormatter) {
            event.recurrenceRules = [recurringRule].compactMap { $0 }
        }
        event.alarms = self.checkForAlarm(eventDetails: eventDetails)
        return event
    }
    func detilsForDeleteEvent(eventDetails: [AnyHashable: Any]) -> DeleteEventObject {
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
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.Calendar.fullDateFormat
        sDate = dateFormatter.date(from: startDate)
        eDate = dateFormatter.date(from: endDate)
        if !(sDate != nil && eDate != nil) {
            dateFormatter.dateFormat = StringConstants.Calendar.halfDateFormat
            sDate = dateFormatter.date(from: startDate)
            eDate = dateFormatter.date(from: endDate)
        }
        let deleteObject = DeleteEventObject(title: title, startDate: sDate, endDate: eDate)
        return deleteObject
    }
}
