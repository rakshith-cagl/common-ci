//--------Plugin files-----------
(function () {
    var applicationData = Windows.Storage.ApplicationData.current;
    var localSettings = applicationData.localSettings;
    function storeEvent(eventID, appointID) {

        if (localSettings.containers.hasKey("AppzillonCalendar")) {
            localSettings.containers.lookup("AppzillonCalendar").values[eventID] = appointID;
        } else {
            localSettings.createContainer("AppzillonCalendar", Windows.Storage.ApplicationDataCreateDisposition.Always);
            localSettings.containers.lookup("AppzillonCalendar").values[eventID] = appointID;
        }
    }
    function getAppointmentID(eventID) {
        var hasContainer = localSettings.containers.hasKey("AppzillonCalendar");
        if (hasContainer) {
            if (localSettings.containers.lookup("AppzillonCalendar").values.hasKey(eventID)) {
                return localSettings.containers.lookup("AppzillonCalendar").values[eventID];
            }
        }
        //localSettings.deleteContainer("exampleContainer");
    }
    function removeAppointmentID(eventID) {
        try {
            localSettings.containers.lookup("AppzillonCalendar").values.remove(eventID);
        } catch (e) {
        }
    }
    function getCalendarTime(date, time) {
        var utc = new Date().toTimeString().slice(12);
        var d = encodeURIComponent(date);
        var t =encodeURIComponent(apz.dateFormat);
        date = Date.parseExact(encodeURIComponent(date), encodeURIComponent(apz.dateFormat));
        date = date.toString("yyyy-MMM-dd");
        time = Date.parseExact(time, apz.timeFormat);
        time = time.toString("HH:mm:ss");
        var startParts = date.split('-');
        var timeparts = time.split(':');
        var x = new Date(startParts[0] + " " + startParts[1] + " " + startParts[2] + " " + time + " " + utc);
        return x;
    }

    function createAppointmentEvent(json) {
        var title = json.title,
         alarm = json.alarm,
         eventStartDate = json.startDate,
         eventEndDate = json.endDate,
         startTime = json.startTime,
         endTime = json.endTime,
         priority = json.priority,
         summary = json.summary,
         recurrence = json.recurrence,
         loc = json.location;

        var alarmFreq = null;
        if (alarm == "5M")
            alarmFreq = (5 * 60 * 1000000000) / 100;
        else if (alarm == "15M")
            alarmFreq = (15 * 60 * 1000000000) / 100;
        else if (alarm == "1H")
            alarmFreq = (60 * 60 * 1000000000) / 100;
        else if (alarm == "1D")
            alarmFreq = (24 * 60 * 60 * 1000000000) / 100;

        try {
            var stTime = getCalendarTime(eventStartDate, startTime);
            var edTime = getCalendarTime(eventEndDate, endTime);
            WinContainer.Log.info("Calendar Start")
        }
        catch (e) {
            WinContainer.failureCallback(json.id, "APZ-CNT-018");
            WinContainer.Log.error(e.description);
            return;
        }

        var appoint = Windows.ApplicationModel.Appointments.Appointment();
        appoint.localId = title;
        appoint.details = summary;
        appoint.startTime = stTime;
        appoint.location = loc;
        appoint.duration = edTime - stTime;
        appoint.reminder = alarmFreq;
        appoint.subject = title;
        var rect = {
            x: 10,
            y: 10,
            width: 100,
            height: 100
        };
        var eventID = title + stTime + appoint.duration;
        Windows.ApplicationModel.Appointments.AppointmentManager.showAddAppointmentAsync(appoint, rect)
            .then(function (appointmentID) {
                WinContainer.Log.info("Calendar End")
                if (appointmentID) {
                    storeEvent(eventID, appointmentID);
                    var data = JSON.stringify({ successMessage: "Event Created Successfully" }, null, " ");
                    var js = JSON.parse(data);
                    js.id = json.id;
                    WinContainer.successCallback(js);
                } else {
                    WinContainer.failureCallback(json.id,"APZ-CNT-082");
                }
            }, function (e) {           
                WinContainer.failureCallback(json.id, "APZ-CNT-082");
                WinContainer.Log.error(e.description);
            });
    }
    WinContainer.plugin.calendar = function (json) {
        try {
                createAppointmentEvent(json);
        }
        catch (err) {
            WinContainer.Log.error(err.description)
            WinContainer.failureCallback(json.id, "APZ-CNT-082");
        }
    }
    WinContainer.plugin.deleteCalendarEvent = function (jsonObj) {
        try {
            var stTime = getCalendarTime(jsonObj.startDate, jsonObj.startTime);
            var edTime = getCalendarTime(jsonObj.endDate, jsonObj.endTime);
            var eventID = jsonObj.title + stTime + (edTime-stTime);
        } catch (e) {
            WinContainer.Log.error(e.description)
            WinContainer.failureCallback(jsonObj.id, "APZ-CNT-018");
            return;
        }
        var appointmentID = getAppointmentID(eventID);
        var rect = {
            x: 0,
            y: 0,
            width: 100,
            height: 100
        };
        Windows.ApplicationModel.Appointments.AppointmentManager.showRemoveAppointmentAsync(appointmentID, rect)
            .then(function (s) {
                if (s) {
                    removeAppointmentID(eventID);
                    var data = JSON.stringify({ successMessage: "Event deleted Successfully" }, null, " ");
                    var json = JSON.parse(data);
                    json.id = jsonObj.id;
                    WinContainer.successCallback(json);
                }
                else {
                    WinContainer.failureCallback(jsonObj.id, "APZ-CNT-018");
                    return;
                }
            });
    }
})();