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
    class AcceleroMeter
    {
        public static bool isDummyFile = false;
        
        private Accelerometer accelerometer;
        private bool accelerometerRunning=false;
        private string id = null;
        private bool keepAlive = false;

#region Singleton Pattern
        private static AcceleroMeter instance;
        private AcceleroMeter()
        {
        }
        public static AcceleroMeter Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new AcceleroMeter();
                }
                return instance;
            }
        }
#endregion
        public void Start(JObject obj)
        {
            id = obj[JsonKey.ID].ToString();
            if (accelerometer != null && accelerometerRunning)
            {
                Response.Fail(id,ErrorCode.ACCELEROMETER_ALREADY_RUNNING);
                return;
            }
           
            if (!accelerometerRunning)
            {
                accelerometer = Accelerometer.GetDefault();
                if (accelerometer != null)
                {
#region Internal Logic for AcceleroMeter
                    accelerometerRunning = true;
                    string periodicity = obj[JsonKey.PERIODICITY].GetString();
                    switch (periodicity)
                    {
                        case JsonKey.NONE:
                            AccelerometerReading reading = accelerometer.GetCurrentReading();
                            SendAcceleration(reading);
                            accelerometer = null;
                            accelerometerRunning = false;
                            break;
                        case JsonKey.ON_CHANGE:
                            keepAlive = true;
                            //  accelerometer.ReportInterval = (UInt32)30;
                            accelerometer.ReadingChanged += AccelerometerReadingChanged;
                            break;
                        case JsonKey.TIMED:
                            keepAlive = true;
                            double t = accelerometer.MinimumReportInterval;
                            string interval = obj[JsonKey.INTERVAL].ToString();
                            double time = Double.Parse(interval);
                            if (interval == "" || time < t)
                            {
                                accelerometer.ReportInterval = (UInt32)t;
                            }
                            else
                            {
                                accelerometer.ReportInterval = (UInt32)time;
                            }
                            accelerometer.ReadingChanged += AccelerometerReadingChanged;
                            break;
                        default:
                            Response.Fail(id, ErrorCode.INVALID_JSON_REQUEST);
                            break;
                    }
#endregion
                }
                else
                {
                   Response.Fail(id, ErrorCode.ACCELERO_DEVICE_NOT_FOUND);//Device does not support Accerlometer
                    Log.FatalError("No Accelerometer Device Found Or Access Denied by user");
                }
            }

        }

        private  void SendAcceleration(AccelerometerReading reading)
        {
            if (accelerometerRunning)
            {

                JObject body = new JObject();
                body[JsonKey.X_CORD] = reading.AccelerationX;
                body[JsonKey.Y_CORD] = reading.AccelerationY;
                body[JsonKey.Z_CORD] = reading.AccelerationZ;
                Response.Success(id, keepAlive, body);
            }
        }

        private  async void AccelerometerReadingChanged(Accelerometer sender, AccelerometerReadingChangedEventArgs args)
        {
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                SendAcceleration(args.Reading);
            });
        }

        public void Stop(JObject obj)
        {
            string lid;
            try
            {
                lid = obj[JsonKey.ID].ToString();
                if (accelerometer != null && accelerometerRunning)
                {
                    accelerometerRunning = false;
                    keepAlive = false;
                    accelerometer.ReportInterval = 0;
                    accelerometer.ReadingChanged -= AccelerometerReadingChanged;
                    accelerometer = null;                
                }
                Response.Success(lid, keepAlive, null);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.ACCELERO_RUNTIME_EXCEPTION);
                Log.FatalError(e.Message);
            }
        }
    }
}
