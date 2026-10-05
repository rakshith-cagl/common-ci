using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Sensors;
using Windows.UI.Core;

namespace Appzillon.Plugins
{
    class Compass
    {
        public bool compassRunning;
        public Windows.Devices.Sensors.Compass compass;
        public string id;
        public bool keepAlive=false;
#region Singleton Pattern
        private static Compass instance;
        private Compass()
        {
        }
        public static Compass Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Compass();
                }
                return instance;
            }
        }
#endregion

        public void CompassStart(JObject obj)
        {
            id = obj[JsonKey.ID].ToString();
            if (compass != null && compassRunning)
            {
                Response.Fail(id,ErrorCode.COMPASS_ALREADY_RUNNING);
                return;
            }
                if (!compassRunning)
            {
                compass = Windows.Devices.Sensors.Compass.GetDefault();
                if (compass != null)
                {
                    compassRunning = true;
                    string periodicity = obj[JsonKey.PERIODICITY].ToString();
                    switch (periodicity)
                    {
                        case JsonKey.NONE:
                            CompassReading reading = compass.GetCurrentReading();
                            SendNorth(reading);
                            compass = null;
                            compassRunning = false;
                            break;
                        case JsonKey.ON_CHANGE:
                            keepAlive = true;
                            compass.ReadingChanged += CompassReadingChanged;
                            break;
                        case JsonKey.TIMED:
                            keepAlive = true;
                            double t = compass.MinimumReportInterval;
                            string interval = obj[JsonKey.INTERVAL].ToString();
                            double time = Double.Parse(interval);
                            if (interval == "" || time < t)
                            {
                                compass.ReportInterval = (UInt32)t;
                            }
                            else
                            {
                                compass.ReportInterval = (UInt32)time;
                            }
                            compass.ReadingChanged += CompassReadingChanged;
                            break;
                    }

                }
                else
                {
                    // JObject j = new JObject();
                    /// j["error"] = "";
                    // j["errorDescription"] = "Compass not found";
                    Response.Fail(id,ErrorCode.COMPASS_NOT_FOUND);
                    Log.FatalError("No Compass Device Found");
                }

            }

        }

        private async void CompassReadingChanged(Windows.Devices.Sensors.Compass sender, CompassReadingChangedEventArgs args)
        {
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                SendNorth(args.Reading);
            });
        }

        public void SendNorth(CompassReading dir)
        {
            if (compassRunning)
            {
                JObject j = new JObject();
                j[JsonKey.MAGNETIC_NORTH] = dir.HeadingMagneticNorth;
                j[JsonKey.TRUE_NORTH] = dir.HeadingTrueNorth;
                if (keepAlive)
                    Response.Success(id, true, j);
                else
                    Response.Success(id, j);
            }
        }
        public  void CompassEnd(JObject obj)
        {
           string id = obj[JsonKey.ID].ToString(); 
            try
            {
                if (compass != null && compassRunning)
                {
                    compassRunning = false;
                    keepAlive = false;
                    compass.ReportInterval = 0;
                    compass.ReadingChanged -= CompassReadingChanged;
                    compass = null;
                    JObject j = new JObject();
                    j[JsonKey.COMPASS_RESULT] = "Success";
                    Response.Success(id,j);
                }
            }
            catch (Exception e)
            {
                // JObject j = new JObject();
                //  j["errorCode"] = "";
                // j["errorDescription"] = e.Message;
                Response.Fail(id,ErrorCode.FAILED_TO_STOP_COMPASS);
                Log.Error(e.Message);
            }
        }
    }
}
