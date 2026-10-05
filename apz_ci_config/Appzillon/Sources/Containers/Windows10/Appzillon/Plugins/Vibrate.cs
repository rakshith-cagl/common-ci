using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Phone.Devices.Notification;

namespace Appzillon.Plugins
{
    class Vibrate
    {

        public static bool stop = false;   //exposed so that it can be set to true
        public static void VibrateDevice(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                VibrationDevice vib = VibrationDevice.GetDefault();
                if (vib != null)
                {
                    double time = Double.Parse(obj[JsonKey.TIME].ToString());
                    if (time > 5000)
                    {
                        time = 5000;
                       Log.Debug("max supported time is 5 seconds");
                    }
                    vib.Vibrate(TimeSpan.FromMilliseconds(time));
                }
                // set stop to true if u want to stop vibration
                if (stop)
                {
                    vib.Cancel();
                }
                Response.Success(id,false);
            }
            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.VIBRATION_REQUEST_FAIL);
                Log.Error(e.Message);
            }

        }
    }
}
