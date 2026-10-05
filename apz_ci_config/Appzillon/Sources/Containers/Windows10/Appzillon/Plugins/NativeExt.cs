using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Power;
using Windows.Graphics.Display;
using Windows.Networking.Connectivity;
using Windows.Security.ExchangeActiveSyncProvisioning;
using Windows.Storage;
using Windows.System.Profile;
using Windows.UI.ViewManagement;

namespace Appzillon.Plugins
{
    class NativeExt
    {
        public void GetDevStat(JObject device)
        {
            string id = device[JsonKey.ID].ToString();
            try
            {
                #region Internal Logic for Device Info
                //Get OS Version
                string osVersion = "10";

                //Get Screen Resolution
                var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
                var scaleFactor = DisplayInformation.GetForCurrentView().RawPixelsPerViewPixel;
                string screenResolution = bounds.Height.ToString() + "X" + bounds.Width.ToString();

                var devType = "Windows 10";
                string batteryStatus;
                //Get Battery status
                var report = Windows.Devices.Power.Battery.AggregateBattery.GetReport();
                try
                {
                    float status = ((float)report.RemainingCapacityInMilliwattHours / (float)report.FullChargeCapacityInMilliwattHours) * 100;
                    double percent = Math.Round(status);
                    batteryStatus = percent.ToString() + "%";
                }
                catch (Exception e)
                {
                    batteryStatus = null;
                   Log.Warning("Battery Capacity Not Applicable, "+e.Message);
                }

                EasClientDeviceInformation deviceInfo = new EasClientDeviceInformation();
                string osName = deviceInfo.OperatingSystem;
                //Get Connection Profile
                ConnectionProfile conType = NetworkInformation.GetInternetConnectionProfile();
                bool isWifi = conType.IsWlanConnectionProfile;
                bool isMob = conType.IsWwanConnectionProfile;
                string ctype = conType.ProfileName;
                string connectionType = "No-Connection";
                string mobiledata = null;
                if (isWifi)
                    connectionType = "WiFi";
                else if (isMob)
                {
                    connectionType = "Mobile Data";
                    WwanDataClass mobilenet = new WwanDataClass();
                    switch (mobilenet)
                    {
                        case WwanDataClass.Edge:
                        case WwanDataClass.Gprs:
                            mobiledata = "2G";
                            break;
                        case WwanDataClass.Cdma1xEvdo:
                        case WwanDataClass.Cdma1xEvdoRevA:
                        case WwanDataClass.Cdma1xEvdoRevB:
                        case WwanDataClass.Cdma1xEvdv:
                        case WwanDataClass.Cdma1xRtt:
                        case WwanDataClass.Cdma3xRtt:
                        case WwanDataClass.CdmaUmb:
                        case WwanDataClass.Umts:
                        case WwanDataClass.Hsdpa:
                        case WwanDataClass.Hsupa:
                            mobiledata = "3G";
                            break;
                        case WwanDataClass.LteAdvanced:
                            mobiledata = "4G";
                            break;
                        default:
                            mobiledata = "Unknown";
                            break;
                    }
                    if (mobiledata != null && mobiledata != "Unknown")
                        connectionType = mobiledata;
                }
                else if (ctype == "Ethernet")
                    connectionType = ctype;
                #endregion

                JObject body = new JObject();
                body["simDetails"] = new JArray();
                body[JsonKey.OS_NAME] = osName;
                body[JsonKey.OS_VERSION] = osVersion;
                body[JsonKey.DEV_TYPE] = devType;
                body[JsonKey.SCREEN_RESOLUTION] = screenResolution.ToString();
                body[JsonKey.CONNECTION_TYPE] = connectionType;
                body[JsonKey.BATTERY_STATUS] = batteryStatus;

                Response.Success(id, body);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.COULD_NOT_OBTAIN_SYSTEM_STATUS);
                Log.FatalError("Unable To Obtain System Status , :"+e.Message);
            }
        }

        
    }
}


