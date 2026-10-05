using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Power;
using Windows.UI.Core;
using Windows.System.Power;
using Windows.UI.Xaml;
using Appzillon.Native;
using Appzillon.Constants;

namespace Appzillon.Plugins
{
    class Battery
    {
        private static int time;
        private static int threshold;
        private static string level;
        private static string state;
        private static Windows.Devices.Power.Battery agrbattery;
        private static BatteryStatus status;
        private static int? lvl;
        private static DateTimeOffset lastTime;
        private static DateTimeOffset startTime;
        private static DispatcherTimer dispatcherTimer;
        private static bool keepAlive = false;
        private static string id;

        public static void BatteryMonitorStart(JObject js)
        {
            try
            {
                keepAlive = true;
                id = js[JsonKey.ID].ToString();
                state = js[JsonKey.STATE].ToString();
                level = js[JsonKey.LEVEL].ToString();
                try
                {
                    threshold = int.Parse(js[JsonKey.THRESHOLD].ToString());
                }
                catch (Exception) { }
                try
                {
                    time = int.Parse(js[JsonKey.TIME].ToString());
                }
                catch (Exception)
                {
                    time = 0;
                }
                agrbattery = Windows.Devices.Power.Battery.AggregateBattery;
                var report = agrbattery.GetReport();
                status = report.Status;
                lvl = (report.RemainingCapacityInMilliwattHours * 100) / report.FullChargeCapacityInMilliwattHours;
                agrbattery.ReportUpdated += Evt_ReportUpdated;
                if (time > 0)
                {
                    dispatcherTimer = new DispatcherTimer();
                    dispatcherTimer.Tick += Timed_Signal;
                    dispatcherTimer.Interval = new TimeSpan(0, 0, 0, time);
                    startTime = DateTimeOffset.Now;
                    lastTime = startTime;
                    dispatcherTimer.Start();
                }
                SendReport("started", agrbattery.GetReport());
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.REGISTERING_TO_TIMED_MONITORING);
                Log.Error(e.Message);
            }
        }

        private static void Timed_Signal(object sender, object e)
        {
            DateTimeOffset ti = DateTimeOffset.Now;
            TimeSpan span = ti - lastTime;
            lastTime = ti;
            SendReport(JsonKey.TIME, agrbattery.GetReport());
        }

        private static async void Evt_ReportUpdated(Windows.Devices.Power.Battery sender, object args)
        {
            BatteryReport btryreport;
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {

                if (state == "Y")
                {
                    BatteryStatus prevstatus = status;
                    btryreport = agrbattery.GetReport();
                    status = btryreport.Status;
                    if (!prevstatus.Equals(status))
                        SendReport("state", btryreport);
                }
                if (level == "Y")
                {
                    var prevlvl = lvl;
                    btryreport = agrbattery.GetReport();
                    lvl = (btryreport.RemainingCapacityInMilliwattHours * 100) / btryreport.FullChargeCapacityInMilliwattHours;
                    if (!prevlvl.Equals(lvl))
                    {
                        SendReport(JsonKey.LEVEL, btryreport);
                    }
                }
                if (threshold >= 1 && threshold <= 100)
                {
                    btryreport = agrbattery.GetReport();

                    if ((btryreport.RemainingCapacityInMilliwattHours * 100) / btryreport.FullChargeCapacityInMilliwattHours == threshold)
                        SendReport(JsonKey.THRESHOLD, btryreport);
                }
            });
        }

        private static void SendReport(string v, BatteryReport btryreport)
        {

            JObject body = new JObject();
            body[JsonKey.STATE] = btryreport.Status.ToString();
            body[JsonKey.LEVEL] = (btryreport.RemainingCapacityInMilliwattHours * 100) / btryreport.FullChargeCapacityInMilliwattHours;
            body[JsonKey.EVENT] = v;
            Response.Success(id, true, keepAlive, body);
        }
        public static void StopBatteryMonitor(JObject js)
        {
            string lid = null;
            try
            {
                lid = js[JsonKey.ID].ToString();
                threshold = 0;
                time = 0;
                try
                {
                    dispatcherTimer.Stop();
                    dispatcherTimer = null;
                }
                catch (Exception) { }
                keepAlive = false;
                try
                {
                    agrbattery.ReportUpdated -= Evt_ReportUpdated;
                }
                catch (Exception) { }
                Response.Success(lid, true, false, new JObject());
            }
            catch (Exception e)
            {
                Response.Fail(lid, ErrorCode.UNREGISTERING_MONITORING);
                Log.Error(e.Message);
            }
        }
    }
}
