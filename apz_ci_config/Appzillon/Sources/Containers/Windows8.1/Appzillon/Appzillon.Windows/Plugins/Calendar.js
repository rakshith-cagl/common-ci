//----Calendar plugin---------
(function () {
    var editJson = {};
    var editSuccessCallback = null;
    var id;
    function getCalendarTime(date, time) {
        var utc = new Date().toTimeString().slice(12);
        var d = encodeURIComponent(date);
        var t = encodeURIComponent(apz.dateFormat);
        date = Date.parseExact(encodeURIComponent(date), encodeURIComponent(apz.dateFormat));
        date = date.toString("yyyy-MMM-dd");
        time = Date.parseExact(time, apz.timeFormat);
        time = time.toString("HH:mm:ss");
        var startParts = date.split('-');
        var timeparts = time.split(':');
        var x = new Date(startParts[0] + " " + startParts[1] + " " + startParts[2] + " " + time + " " + utc);
        return x;
    }
    WinContainer.plugin.calendar = function (json) {
        function createEvent(json) {
            var title = json.title,
             alarm = json.alarm,
             eventStartDate = json.startDate,
             eventEndDate = json.endDate,
             startTime = json.startTime,
             endTime = json.endTime,
             priority = json.priority,
             summary = json.summary,
             recurrence = json.recurrence,
             loc = json.location,
             rec = "false",
             alarmFreq = 0;
            id = json.id;
            if (recurrence != null && recurrence != "")
                rec = "true";
            if (alarm == "5M")
                alarmFreq = 5;
            else if (alarm == "15M")
                alarmFreq = 15;
            else if (alarm == "1H")
                alarmFreq = 60;
            else if (alarm == "1D")
                alarmFreq = 24 * 60;
            else if (alarm == "none")
                alarmFreq = 0;


            var d = new Date();
            sd = d.toTimeString();
            var utc = sd.slice(12, sd.length);
            try {
                var stTime = getCalendarTime(eventStartDate, startTime);//(new Date(startParts[0] + " " + startParts[1] + " " + startParts[2] + " " + startTime + " " + utc)).toISOString();
                var edTime = getCalendarTime(eventEndDate, endTime);//(new Date(endParts[0] + " " + endParts[1] + " " + endParts[2] + " " + endTime + " " + utc)).toISOString();
                WinContainer.Log.info("Calendar Start");
            }
            catch (e) {
                WinContainer.failureCallback(id, "APZ-CNT-018");
                WinContainer.Log.error(e.description);
                return;
            }

            WL.login({ scope: "wl.events_create" }).then(
                       function (responseL) {
                           WL.api({
                               path: "/me/events",
                               method: "POST",
                               body: {
                                   name: title,
                                   description: summary,
                                   start_time: stTime,//2011-08-05T19:41:04Z+0000
                                   end_time: edTime,
                                   location: loc,
                                   is_all_day_event: "false",
                                   availability: "busy",
                                   visibility: "public",
                                   reminder_time: alarmFreq,
                                   is_recurrent: rec,
                                   recurrence: ""
                               }
                           }).then(
                               function (res) {
                                   WL.logout();
                                   var data = JSON.stringify({ successMessage: "Event Created Successfully" }, null, " ");
                                   var json = JSON.parse(data);
                                   json.id = id;
                                   WinContainer.successCallback(json);
                               },
                               function (res) {
                                   WinContainer.Log.info("Calendar End");
                                   WL.logout();
                                   WinContainer.failureCallback(id, "APZ-CNT-077");
                               });
                       },
                       function (e) {
                           WinContainer.Log.info("Calendar End");
                           WL.logout();
                           WinContainer.failureCallback(id, "APZ-CNT-035");
                           WinContainer.Log.error(e.error_description);
                       });
        }
       
        WinContainer.plugin.eventEditSuccess = function (json) {
            var jsonObj = editJson;
            jsonObj.startDate = editJson.newStartDate;
            jsonObj.endDate = editJson.newEndDate;
            jsonObj.startTime = editJson.newStartTime;
            jsonObj.endTime = editJson.newEndTime;
            createEvent(jsonObj);
        }
        WinContainer.plugin.eventCreateSuccess = function (json) {
            editJson = {};
            var data = JSON.stringify({ successMessage: "Event edited successfully" }, null, " ");
            var json = JSON.parse(data);
            json.id = id;
            WinContainer.successCallback(json);
        }
        try {
          //  var failureCallback = json.failureCallback;
            WL.init();
                createEvent(json);
        }
        catch (err) {
            WinContainer.failureCallback(id, "APZ-CNT-082");
            WinContainer.Log.error(err.description);
        }
    }
    WinContainer.plugin.deleteCalendarEvent = function (jsonObj) {
        var utc = new Date().toTimeString().slice(12);
        var eventStartDate = jsonObj.startDate,
            eventEndDate = jsonObj.endDate,
            startTime = jsonObj.startTime,
            endTime = jsonObj.endTime,
            eventName = jsonObj.title,
          id = jsonObj.id;
        try {
            startTime = getCalendarTime(eventStartDate, startTime);//(new Date(startParts[0] + " " + startParts[1] + " " + startParts[2] + " " + startTime + " " + utc)).toISOString();

            endTime = getCalendarTime(eventEndDate, endTime); //(new Date(endParts[0] + " " + endParts[1] + " " + endParts[2] + " " + endTime + " " + utc)).toISOString();
            eventName = eventName + startTime + endTime;
        } catch (e) {
            WinContainer.failureCallback(id, "APZ-CNT-077");
            WinContainer.Log.error(e.description);
        }

        function deleteItem(eventID) {
            WL.login({
                scope: "wl.calendars_update"
            }).then(
            function (responseL) {
                WL.api({
                    path: eventID,
                    method: "DELETE",
                }).then(
                function (s) {
                    var data = JSON.stringify({ successMessage: "Event deleted successfully" }, null, " ");
                    var json = JSON.parse(data);
                    json.id = id;
                    WinContainer.successCallback(json);
                }, function (e) {
                    //var data = JSON.stringify({ errorCode: "APZ-CNT-038" }, null, " ");
                    //var json = JSON.parse(data);
                    WinContainer.failureCallback(id, "APZ-CNT-038");
                });
            }, function (e) {
                //var data = JSON.stringify({ errorCode: "APZ-CNT-035" }, null, " ");
                //var json = JSON.parse(data);
                WinContainer.failureCallback(id, "APZ-CNT-035");
            });
        }
        WL.init();
        WL.login({ scope: "wl.calendars" }).then(
            function (responseL) {
                WL.api({
                    path: "/me/events",
                    method: "GET",
                }).then(
                    function (res) {
                        var res = res.data;
                        var checkDelete = false;
                        for (var i = 0; i < res.length; i++) {
                            if (eventName == (res[i].name + Date.parse(res[i].start_time).toISOString() + Date.parse(res[i].end_time).toISOString())) {
                                deleteItem(res[i].id);
                                checkDelete = true;
                                break;
                            }
                        }
                        if (!checkDelete) {
                            WinContainer.failureCallback(id, "APZ-CNT-039");
                        }
                    });
            }, function (e) {
                WinContainer.failureCallback(id, "APZ-CNT-035");
                WinContainer.Log.error(e.error_description);
            });
    }
})();