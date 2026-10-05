using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Geolocation;
using Windows.System;
using Windows.UI.Core;
using Windows.UI.Xaml;

namespace Appzillon.Plugins
{
    class Gps
    {
        private Geolocator geolocator = null;
        private bool gpsRunning;
        private DispatcherTimer dispatcherTimer;
        private DateTimeOffset startTime;
        private DateTimeOffset lastTime;
        private DateTimeOffset stopTime;
        private int sec = 0;
        private int ms = 0;
        private string id = "";

        #region Singleton Pattern
        private static Gps instance;
        private static bool keepAlive = false;

        private Gps()
        {
        }
        public static Gps Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Gps();
                }
                return instance;
            }
        }
        #endregion

        public async void startGPS(JObject obj)
        {
            string periodicity = obj[JsonKey.PERIODICITY].ToString();
            var accessStatus = await Geolocator.RequestAccessAsync().AsTask();
            id = obj[JsonKey.ID].ToString();
            if (geolocator != null && gpsRunning)
            {
                Response.Fail(id, ErrorCode.GPS_ALREADY_RUNNING);
                return;
            }
            if (!gpsRunning)
            {
                gpsRunning = true;
                try
                {
                    geolocator = new Geolocator();
                    geolocator.DesiredAccuracy = PositionAccuracy.High;
                    if (periodicity == JsonKey.NONE)
                    {
                        keepAlive = false;
                        Geoposition pos = await geolocator.GetGeopositionAsync();
                        SendLocation(pos);
                        gpsRunning = false;
                        geolocator = null;                        
                    }
                    else if (periodicity == JsonKey.ON_CHANGE)
                    {
                        keepAlive = true;
                        string distanceInterval = obj["distanceInterval"].ToString();
                        if (distanceInterval != "")
                        {
                            double distancegap = Double.Parse(distanceInterval);
                            geolocator.MovementThreshold = (uint)distancegap;

                        }
                        else
                        {
                            geolocator.MovementThreshold = 100;
                        }
                        geolocator.PositionChanged += OnPositionChanged;
                        geolocator.StatusChanged += OnStatusChanged;
                      
                    }
                    else if (periodicity == JsonKey.INTERVAL_BASED || periodicity == "timed")
                    {
                        keepAlive = true;
                        string timeInterval = obj[JsonKey.TIME_INTERVAL].GetString();
                        if(timeInterval == null)
                            timeInterval = obj[JsonKey.INTERVAL].GetString();
                        if (timeInterval != "" && timeInterval != null)
                        {
                            int timegap = Int32.Parse(timeInterval);
                            //geolocator.ReportInterval = (uint)timegap;
                            sec = timegap / 1000;
                            ms = timegap % 1000;

                        }
                        else
                        {
                            // geolocator.ReportInterval = 30; (does not work reliably). So We Have Implemented Dispatcher Timer for timeInterval
                            sec = 30;
                        }
                        geolocator = new Geolocator();
                        geolocator.DesiredAccuracy = PositionAccuracy.High;
                        dispatcherTimer = new DispatcherTimer();
                        dispatcherTimer.Tick += pos;
                        dispatcherTimer.Interval = new TimeSpan(0, 0, 0, sec, ms);
                        startTime = DateTimeOffset.Now;
                        lastTime = startTime;
                        dispatcherTimer.Start();
                     
                    }
                    
                }
                catch (Exception e)
                {
                    if (accessStatus != GeolocationAccessStatus.Allowed)
                    {
                        Response.Fail(id, ErrorCode.LOCATION_ACCESS_UNAVAILABLE);
                    }
                    else
                    {
                        Response.Fail(id, ErrorCode.GPS_FAIL);
                    }
                    Log.FatalError(e.Message);
                }
            }

        }



        private void OnStatusChanged(Geolocator sender, StatusChangedEventArgs status)
        {
            switch (status.Status)
            {
                case PositionStatus.Ready:
                    Log.Debug("Ready");
                    break;
                default:
                    Log.Debug("Location could not be obtained");
                    break;
            }
        }

        private async void OnPositionChanged(Geolocator sender, PositionChangedEventArgs pos)
        {
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                SendLocation(pos.Position);
            });
        }

        private void SendLocation(Geoposition pos)
        {
            if (gpsRunning)
            {
                JObject j = new JObject();
                j[JsonKey.LATITUDE] = pos.Coordinate.Point.Position.Latitude;
                j[JsonKey.LONGITUDE] = pos.Coordinate.Point.Position.Longitude;
                j[JsonKey.ALTITUDE] = pos.Coordinate.Point.Position.Altitude;
                j[JsonKey.ACCURACY] = pos.Coordinate.Accuracy;
                j[JsonKey.ALTITUDE_ACCURACY] = pos.Coordinate.AltitudeAccuracy;
                j[JsonKey.HEADING] = pos.Coordinate.Heading;
                j[JsonKey.SPEED] = pos.Coordinate.Speed;
                if (keepAlive)
                    gpsRunning = true;
                Response.Success(id, keepAlive,j);
            }
        }

        public void endGPS(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                if (geolocator != null && gpsRunning)
                {
                    gpsRunning = false;
                    geolocator.PositionChanged -= OnPositionChanged;
                    geolocator.StatusChanged -= OnStatusChanged;
                    geolocator.ReportInterval = 0;
                    geolocator = null;
                    try
                    {
                        dispatcherTimer.Stop();
                    }
                    catch (Exception e)
                    {
                        Log.Warning("GPS Not Running, " + e.Message);  //object not created.but its safely ends gps plugin.
                    }
                }
                JObject j = new JObject();
                j[JsonKey.GPS_RES] = "GPS stop success";
                Response.Success(id, j);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.GPS_STOP_FAIL);
                Log.Error(e.Message);
            }
        }
        private async void pos(object sender, object e)
        {
            DateTimeOffset time = DateTimeOffset.Now;
            TimeSpan span = time - lastTime;
            lastTime = time;
            Geoposition pos = await geolocator.GetGeopositionAsync();
            SendLocation(pos);

        }
    }
}
