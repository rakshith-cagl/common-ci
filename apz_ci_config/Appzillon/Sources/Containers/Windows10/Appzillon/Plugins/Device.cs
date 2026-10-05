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
    class Device
    {
        public static void GetStatus(JObject device)
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

        public static void GetDeviceInfo(JObject obj)
        {
            JObject setting = new JObject();
            setting[JsonKey.DEVICE] = Utils.GetDeviceID();
            setting["deviceOs"] = new EasClientDeviceInformation().OperatingSystem;
            setting[JsonKey.SCREEN_PPI] = DisplayInformation.GetForCurrentView().LogicalDpi;
            var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
            setting[JsonKey.SCREEN_SIZE] = (bounds.Width) + "X" + (bounds.Height);

            setting[JsonKey.DEV_TYPE] = "WIN10";
            JObject json = DeviceGroup();
            setting[JsonKey.DEVICE_GROUP] = json[JsonKey.NAME];

            if (DisplayInformation.AutoRotationPreferences != DisplayOrientations.None)
            {
                setting[JsonKey.LOCK_ROTATION] = true;
                setting[JsonKey.ORIENTATION] = json[JsonKey.ORIENTATION];
            }
            else
            {
                setting[JsonKey.LOCK_ROTATION] = false;
                var orientation = DisplayInformation.GetForCurrentView().CurrentOrientation;
                if (orientation == Windows.Graphics.Display.DisplayOrientations.Landscape || orientation == Windows.Graphics.Display.DisplayOrientations.LandscapeFlipped)
                    setting[JsonKey.ORIENTATION] = "LANDSCAPE";
                else if (orientation == Windows.Graphics.Display.DisplayOrientations.Portrait || orientation == Windows.Graphics.Display.DisplayOrientations.PortraitFlipped)
                    setting[JsonKey.ORIENTATION] = "PORTRAIT";
            }
            setting[JsonKey.OTA_REQUIRED] =AppSettings.ContainerProperties[JsonKey.OTA_REQUIRED];
            setting[JsonKey.HASHKEY_1] = setting[JsonKey.DEVICE];
            setting[JsonKey.HASHKEY_2]= "";
            Response.Success(obj[JsonKey.ID].ToString(),setting);
        }
        internal static JObject DeviceGroup()
        {
            string fp;         
            fp = AppSettings.GetSettingPath("DeviceGroups.json",MainPage.CurrentAppId);
            JObject fileval = JObject.Parse(File.ReadAllText(fp));
            var devgrparray = fileval["deviceGroups"].ToArray();
            var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
            int devheight = (int)bounds.Height;
            int devwidth = (int)bounds.Width;
            int hmin = 10000;
            int wmin = 10000;
            int psum = 10000;
            int optimum = 0;
            for (int i = 0; i < devgrparray.Length; i++)
            {
                var temp = JObject.Parse(devgrparray[i].ToString());
                int cdh = int.Parse(temp[JsonKey.HEIGHT].ToString()) - devheight;
                int cdw = int.Parse(temp[JsonKey.WIDTH].ToString()) - devwidth;
                int csum = Math.Abs(cdw) + Math.Abs(cdh);
                if (i == 0)
                {
                    optimum = i;
                    psum = csum;
                    hmin = cdh;
                    wmin = cdw;
                }
                else
                {
                    if (psum > csum)
                    {
                        optimum = i;
                        psum = csum;
                        hmin = cdh;
                        wmin = cdw;
                    }
                    else if (psum == csum)
                    {
                        if (hmin < 0 && wmin < 0)
                        {
                            optimum = i;
                            psum = csum;
                            hmin = cdh;
                            wmin = cdw;
                        }
                    }
                }
            }
            var tem = JObject.Parse(devgrparray[optimum].ToString());
            //  string devicegroup = tem[JsonKey.NAME].ToString();
            return tem;
        }

        #region IP
        internal static async void GetIP(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            ConnectionProfile conType = NetworkInformation.GetInternetConnectionProfile();
            ConnectionProfileFilter fil = new ConnectionProfileFilter();
            var hnames = NetworkInformation.GetHostNames();
            fil.IsConnected = true;
            var nhosts = await NetworkInformation.FindConnectionProfilesAsync(fil);
            int i = hnames.Count;
            string IP = "";
            if (nhosts.Count > 0)
            {
                for (int j = 0; j < i; j++)
                {
                    if (hnames[j].IPInformation != null)
                    {
                        if (hnames[j].IPInformation.NetworkAdapter.NetworkAdapterId == nhosts[0].NetworkAdapter.NetworkAdapterId)
                        {
                            IP = hnames[j].DisplayName;
                            j = i + 6;
                        }
                    }
                }
                var json = new JObject();
                json[JsonKey.IP] = IP;
                Response.Success(id, json);
            }
            else
            {
                Response.Fail(id, ErrorCode.NOT_CONNECTED_TO_INTERNET);
            }
        }
        #endregion
    }
}


//needs to be updated