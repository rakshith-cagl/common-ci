using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel.Appointments;
namespace Appzillon.Plugins
{
    class Calendar
    {
        private string id;
#region Singleton Pattern
        private static Calendar instance;
        private Calendar()
        {
        }
        public static Calendar Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Calendar();
                }
                return instance;
            }
        }
#endregion
        public async void saveAppointment(JObject json)
        {
            id = json[JsonKey.ID].ToString();
                bool addEvent = false;
                try
                {
                    addEvent = await AddAppointment(json);
                    if (addEvent)
                    {
                        JObject success = new JObject();
                        success[JsonKey.SUCCESS_MESSAGE] = "Event Created Successfully";
                        Response.Success(id,success);
                    }

                    else if (!addEvent)
                    {
                       // JObject failure = new JObject();
                      //  failure["errorDescription"] = "Unable to create event";
                        Response.Fail(id,ErrorCode.UNABLE_TO_CREATE_EVENT);
                     Log.Error("Unable To Create Calendar Evevnt");
                    }
                }
                catch (Exception e)
                {
                    //    JObject failure = new JObject();
                    //failure["errorDescription"] = "Unable to create event";
                    Response.Fail(id, ErrorCode.UNABLE_TO_CREATE_EVENT);
                Log.Error(e.Message);
            }

        }

        private async Task<bool> AddAppointment(JObject json)
        {
         //   JObject json = new JObject();
         //   json = JObject.Parse(calendarstring);
            string title = json[JsonKey.TITLE].ToString();
            string alarm = json[JsonKey.ALARM].ToString();
            string eventStart = json[JsonKey.START_DATE].ToString();
            string eventEnd = json[JsonKey.END_DATE].ToString();
            string tStart = json[JsonKey.START_TIME].ToString();
            string tEnd = json[JsonKey.END_TIME].ToString();
            string priority = json[JsonKey.PRIORITY].ToString();
            string summary = json[JsonKey.SUMMERY].ToString();
            string recurrence = json[JsonKey.RECCURENCE].ToString();
            string recurEndDate = (json[JsonKey.RECCURENCE_END_DATE] == null) ? "null" : json[JsonKey.RECCURENCE_END_DATE].ToString();
            string loc = json[JsonKey.VENUE].ToString();
            bool recur = false;

            TimeSpan alarmFreq = TimeSpan.FromMinutes(0);
            AppointmentRecurrence rec = new AppointmentRecurrence();


            if (alarm == "5M")
                alarmFreq = TimeSpan.FromMinutes(5);
            else if (alarm == "15M")
                alarmFreq = TimeSpan.FromMinutes(15);
            else if (alarm == "1H")
                alarmFreq = TimeSpan.FromMinutes(60);
            else if (alarm == "1D")
                alarmFreq = TimeSpan.FromMinutes(24 * 60);
            else if (alarm == "none")
                alarmFreq = TimeSpan.FromMinutes(0);


            string eventStartDate = eventStart + " " + tStart;
            DateTime d = DateTime.Parse(eventStartDate, System.Globalization.CultureInfo.InvariantCulture);
            string eventEndDate = eventEnd + " " + tEnd;
            DateTime d1 = DateTime.Parse(eventEndDate, System.Globalization.CultureInfo.InvariantCulture);

            //Set Recurrence End Date
            recurEndDate = recurEndDate + " " + "00:00:00";
            DateTime recurrenceEndDate = DateTime.Parse(recurEndDate, System.Globalization.CultureInfo.InvariantCulture);

            TimeSpan duration = d1 - d;

            if (recurrence != null && recurrence != "")
            {
                recur = true;
                if (recurrence == "Daily")
                    rec.Unit = AppointmentRecurrenceUnit.Daily;
                else if (recurrence == "Weekly")
                    rec.Unit = AppointmentRecurrenceUnit.Weekly;
                else if (recurrence == "Monthly")
                    rec.Unit = AppointmentRecurrenceUnit.Monthly;
                else
                    recur = false;
            }

            try
            {
                AppointmentStore store = await AppointmentManager.RequestStoreAsync(AppointmentStoreAccessType.AppCalendarsReadWrite);
                var lists = await store.FindAppointmentCalendarsAsync();
                //Find list
                var list = lists.FirstOrDefault((x) => x.DisplayName == "myCalendar");
                if (list == null)
                {
                    list = await store.CreateAppointmentCalendarAsync("myCalendar");
                    list.OtherAppReadAccess = AppointmentCalendarOtherAppReadAccess.Full;
                    list.OtherAppWriteAccess = AppointmentCalendarOtherAppWriteAccess.SystemOnly;
                    await list.SaveAsync();
                }


                Appointment newEvent = new Appointment();
                newEvent.Details = summary;
                newEvent.Location = loc;
                newEvent.StartTime = d.ToUniversalTime();
                newEvent.Subject = title;
                newEvent.Duration = duration;
                newEvent.Reminder = alarmFreq;
                if (recur == true)
                    newEvent.Recurrence = rec;
                //newEvent.Recurrence.Until = recurrenceEndDate;
                await list.SaveAppointmentAsync(newEvent);
                return true;


            }

            catch (Exception)
            {
                return false;
            }
        }

        public async void DeleteAppointment(JObject json)
        {
            id = json[JsonKey.ID].ToString();
            //JObject json = new JObject();
           // json = JObject.Parse(jsonstring);
            bool deleted = false;
            deleted = await DeleteEvent(json);
            if (deleted == true)
            {
                JObject success = new JObject();
                 success[JsonKey.SUCCESS_MESSAGE] = "Event Deleted Successfully";
              
                Response.Success(id,success);
            }

            else if (!deleted)
            {
                Response.Fail(id,ErrorCode.UNABLE_TO_DELETE_EVENT);
                Log.Error("Calendar Event Delete Fail");
            }
        }

        private async Task<bool> DeleteEvent(JObject json)
        {
          //  JObject json = new JObject();
         //  var js = json.ToString();
            string title = json[JsonKey.TITLE].ToString();
            string eventStart = json[JsonKey.START_DATE].ToString();
            string eventEnd = json[JsonKey.END_DATE].ToString();
            string tStart = json[JsonKey.START_TIME].ToString();
            string tEnd = json[JsonKey.END_TIME].ToString();
            int tlength = tStart.Length;
          string  tStart1 = tStart.Substring(0, tlength - 2) + "00";
            try
            {
                string eventStartDate = eventStart + " " + tStart;
                string eventStartDate1 = eventStart + " " + tStart1;
                string eventEndDate = eventEnd + " " + tEnd;
                DateTime startDate = DateTime.Parse(eventStartDate, System.Globalization.CultureInfo.InvariantCulture);
                DateTime forComp = DateTime.Parse(eventStartDate1, System.Globalization.CultureInfo.InvariantCulture);
                DateTime endDate = DateTime.Parse(eventEndDate, System.Globalization.CultureInfo.InvariantCulture);
                DateTime startDate1 = startDate.ToUniversalTime();
                DateTime endDate1 = endDate.ToUniversalTime();

                TimeSpan t = endDate1 - startDate1;

                string future = "31-Dec-2020";
                string past = "01-Jan-2000";
                DateTime d = DateTime.Parse(future, System.Globalization.CultureInfo.InvariantCulture);
                DateTime d1 = DateTime.Parse(past, System.Globalization.CultureInfo.InvariantCulture);

                TimeSpan duration = d - d1;
                AppointmentStore allAccessStore = await AppointmentManager.RequestStoreAsync(AppointmentStoreAccessType.AppCalendarsReadWrite);

              FindAppointmentsOptions options = new FindAppointmentsOptions();
                options.FetchProperties.Add(AppointmentProperties.Subject);
                options.FetchProperties.Add(AppointmentProperties.Details);
                options.FetchProperties.Add(AppointmentProperties.Duration);
                options.FetchProperties.Add(AppointmentProperties.Location);
                options.FetchProperties.Add(AppointmentProperties.StartTime);
               // options.FetchProperties.Add();
                var appointments = await allAccessStore.FindAppointmentsAsync(d1, duration);

                var target = from Appointment sEvent in appointments
                             where sEvent.Subject.Contains(title)
                             && (sEvent.StartTime.DateTime == forComp)
                             && (sEvent.Duration == t)
                             select sEvent;


                if (target.Count() > 0)
                {
                    var l = await allAccessStore.FindAppointmentCalendarsAsync();
                    var count = target.ElementAt(0);
                    var id = count.LocalId;
                    AppointmentCalendar list = l.FirstOrDefault((x) => x.DisplayName == "myCalendar");
                    await list.DeleteAppointmentAsync(id);
                    return true;
                }
                else
                    return false;
            }

            catch (Exception e)
            {
                Log.Error(e.Message);
                return false;
            }
        }

        /*public static async void SearchAppointment(string jsonstring, MainPage lobject)
        {
            JObject json = new JObject();
            json = JObject.Parse(jsonstring);
            string title = json["title"].ToString();
            string eventStart = json["startDate"].ToString();
            string eventEnd = json["endDate"].ToString();
            string tStart = json["startTime"].ToString();
            string tEnd = json["endTime"].ToString();

            try
            {
                string eventStartDate = eventStart + " " + tStart;
                string eventEndDate = eventEnd + " " + tEnd;
                DateTime startDate = DateTime.Parse(eventStartDate, System.Globalization.CultureInfo.InvariantCulture);
                DateTime endDate = DateTime.Parse(eventEndDate, System.Globalization.CultureInfo.InvariantCulture);
                DateTime startDate1 = startDate.ToUniversalTime();
                DateTime endDate1 = endDate.ToUniversalTime();

                TimeSpan t = endDate1 - startDate1;

                string future = "31-Dec-2020";
                string past = "01-Jan-2000";
                DateTime d = DateTime.Parse(future, System.Globalization.CultureInfo.InvariantCulture);
                DateTime d1 = DateTime.Parse(past, System.Globalization.CultureInfo.InvariantCulture);

                TimeSpan duration = d - d1;
                AppointmentStore allAccessStore = await AppointmentManager.RequestStoreAsync(AppointmentStoreAccessType.AppCalendarsReadWrite);

                FindAppointmentsOptions options = new FindAppointmentsOptions();
                options.FetchProperties.Add(AppointmentProperties.Subject);
                options.FetchProperties.Add(AppointmentProperties.Details);
                options.FetchProperties.Add(AppointmentProperties.Duration);
                options.FetchProperties.Add(AppointmentProperties.Location);
                options.FetchProperties.Add(AppointmentProperties.StartTime);

                var appointments = await allAccessStore.FindAppointmentsAsync(d1, duration, options);

                var target = from Appointment sEvent in appointments
                             where sEvent.Subject.Contains(title)
                             && (sEvent.StartTime.DateTime == startDate)
                             && (sEvent.Duration == t)
                             select sEvent;

                string result = string.Empty;
                result = "{\"searchResult\":[";

                if (target.Count() > 0)
                {
                    var count = target.ElementAt(0);
                    foreach(var item in target)
                    {
                        result += "{\"Title\":\"" + item.Details + "\",";
                        result += "\"Address\":\"" + item.
                    }
                }*/

        public async void EditAppointment(JObject o)
        {
            id = o[JsonKey.ID].ToString();
            JObject deletejson = new JObject();
            deletejson[JsonKey.TITLE] = o[JsonKey.TITLE];
            deletejson[JsonKey.START_DATE] = o[JsonKey.START_DATE];
            deletejson[JsonKey.END_DATE] = o[JsonKey.END_DATE];
            deletejson[JsonKey.START_TIME] = o[JsonKey.START_TIME];
            deletejson[JsonKey.END_TIME] = o[JsonKey.END_TIME];

            JObject addjson = new JObject();
            addjson[JsonKey.TITLE] = o[JsonKey.TITLE];
            addjson[JsonKey.ALARM] = o[JsonKey.ALARM];
            addjson[JsonKey.START_DATE] = o["newStartDate"];
            addjson[JsonKey.END_DATE] = o["newEndDate"];
            addjson[JsonKey.START_TIME] = o["newStartTime"];
            addjson[JsonKey.END_TIME] = o["newEndTime"];
            addjson[JsonKey.PRIORITY] = o[JsonKey.PRIORITY];
            addjson[JsonKey.SUMMERY] = o[JsonKey.SUMMERY];
            addjson[JsonKey.RECCURENCE] = o[JsonKey.RECCURENCE];
            addjson[JsonKey.RECCURENCE_END_DATE] = o[JsonKey.RECCURENCE_END_DATE];
            addjson[JsonKey.VENUE] = o[JsonKey.VENUE];

            bool deleted = await DeleteEvent(deletejson);
            bool added = false;
            if (deleted)
            {
                added = await AddAppointment(addjson);
            }

            if (!deleted)
            {
                Response.Fail(id,ErrorCode.UNABLE_TO_EDIT_EVENT);
            }

            if (added)
            {
                JObject success = new JObject();
                success[JsonKey.SUCCESS_MESSAGE] = "Event Edited Successfully";
                Response.Success(id,success);
            }

            else if (!added)
            {
                //JObject failure = new JObject();
                //failure["errorDescription"] = "Unable to create event";
                Response.Fail(id,ErrorCode.UNABLE_TO_EDIT_EVENT);
                Log.Error("Unable To Create Calendar Event");
            }
        }
    }
}
