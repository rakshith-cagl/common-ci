using Appzillon.Constants;
using Appzillon.Plugins;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Windows.Devices.Geolocation;
using Windows.Foundation;
using Windows.Foundation.Metadata;
using Windows.Graphics.Display;
using Windows.Networking.Connectivity;
using Windows.System;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;

namespace Appzillon.Native
{

      static class Utils
    {
        #region Utils Method and Variable

        public static bool isOrientationLocked = false;
        public static string lockedorientation = "ANY";
        public static bool server_req_free = true;
        #region Following Code Will Move To Utils
        public static string GetServerUrl()
        {
            return AppSettings.AppProperties["serverUrl"].ToString();
        }
       
       
        
        public static string GetDeviceName()
        {
            return Windows.Networking.Connectivity.NetworkInformation.GetHostNames()[0].DisplayName;
        }
        public static string GetOS()
        {
            return "WINDOWS10";
        }

        public static string GetScreenResolution()
        {
            var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
            var scaleFactor = DisplayInformation.GetForCurrentView().RawPixelsPerViewPixel;
            var size = new Size(bounds.Width * scaleFactor, bounds.Height * scaleFactor);
            return size.ToString();
        }
        public static string GetOSVersion()
        {
            return "10";
        }
        public static string GetMobile1()
        {
            return "";
        }
        public static string GetMobile2()
        {
            return "";
        }
        public static string GetModel()
        {
            return new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation().SystemProductName;
        }
        public static string GetMake()
        {
            return "";
        }
        public static string GetDeviceID()
        {
            var token = Windows.System.Profile.HardwareIdentification.GetPackageSpecificToken(null);
            return Windows.Security.Cryptography.CryptographicBuffer.EncodeToHexString(token.Id);
        }

        public static string GetTodayDate()
        {
            string _currentdate = DateTime.Now.ToString("yyyyMMdd");
            string todaydate = _currentdate.Substring(0, 4) + "-" + _currentdate.Substring(4, 2) + "-" + _currentdate.Substring(6, 2);


            return todaydate;
        }

        #endregion
        #region Genrate client nonce   Author Anand Kumar
        private static readonly DateTime Jan1st1970 = new DateTime
(1970, 1, 1, 0, 0, 0, DateTimeKind.Local);
        public static long CurrentTimeMillis()
        {
            return (long)(DateTime.Now - Jan1st1970).TotalMilliseconds;
        }
        #endregion
        internal static bool isPhone
        {
            get
            {
                return ApiInformation.IsTypePresent("Windows.Phone.UI.Input.HardwareButtons");
            }
        }
        internal static Type GetType(string className)
        {
            return Type.GetType("Appzillon.Container." + className);
        }
        internal static bool IsPluginPresent(string className)
        {
            Type t = Type.GetType("Appzillon.Container." + className);
            return t != null ? true : false;
        }

        public static bool IsAllcaps(this string s)
        {
            return s.ToUpper() == s;
        }
        public static string GetString(this JToken s)
        {
            return s == null ? null : s.ToString();
        }
        /// <summary>
        /// Get default value.
        /// </summary>
        /// <param name="match">Condition to match</param>
        /// <param name="defaultValue">Set to default</param>
        /// 

        public static string GetDefault(this string s, string match, string defaultValue)
        {
            return s == match ? match : defaultValue;
        }
        public static string GetPath(this string s)
        {
            return s.Replace("/", "\\");
        }
        #endregion

        #region Keyboard visibility
        internal static void SetKeyBoardVisibility(string action)
        {
            JObject js = new JObject();
            string evt = "";
            if (action == "SHOW")
            {
                InputPane.GetForCurrentView().TryShow();
                evt = "keyboardUp";
            }
            else if (action == "HIDE")
            {
                InputPane.GetForCurrentView().TryHide();
                evt = "keyboardUp";
            }
            js[JsonKey.EVENT] = evt;
            // Response.Success(keybrd_id,true,js);
        }
        #endregion

        #region Native orientation Support
        internal static void SetOrientation(JObject orti)
        {
            string ort = orti["orientation"].ToString();
            string neworientation = ort.Substring(0, 3).ToUpper();
            if (neworientation == "POR")
            {
                DisplayInformation.AutoRotationPreferences =
                   DisplayOrientations.Portrait;
            }
            else if (neworientation == "LAN")
            {
                DisplayInformation.AutoRotationPreferences =
                     DisplayOrientations.Landscape;
            }
            else
            {
                DisplayInformation.AutoRotationPreferences =
                DisplayOrientations.None;
            }
            orti[JsonKey.STATUS] = true;
            Response.Success(orti[JsonKey.ID].ToString(), orti);
        }
        #endregion

        #region Native orientation unlock Support
        internal static void UnlockRotation(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            isOrientationLocked = false;
            var orientation = Windows.Graphics.Display.DisplayInformation.GetForCurrentView().CurrentOrientation;
            Windows.Graphics.Display.DisplayInformation.AutoRotationPreferences =
                Windows.Graphics.Display.DisplayOrientations.None;
            JObject param = new JObject();
            param[JsonKey.SUCCESS_MESSAGE] = "Rotation unlocked";
            Response.Success(id, param);
        }
        #endregion

        #region Native orientation lock Support
        internal static void LockRotation(JObject jsonObj)
        {
            string id = jsonObj[JsonKey.ID].ToString();
            isOrientationLocked = true;
            try
            {
                var orientation = Windows.Graphics.Display.DisplayInformation.GetForCurrentView().CurrentOrientation;
                if (orientation == Windows.Graphics.Display.DisplayOrientations.Portrait)
                {
                    Windows.Graphics.Display.DisplayInformation.AutoRotationPreferences =
                        Windows.Graphics.Display.DisplayOrientations.Portrait;
                    lockedorientation = "PORTRAIT";
                    JObject param = new JObject();
                    param[JsonKey.CURRENT_ORIENTATION] = "portrait";
                    Response.Success(id, param);
                }
                else if (orientation == Windows.Graphics.Display.DisplayOrientations.PortraitFlipped)
                {

                    Windows.Graphics.Display.DisplayInformation.AutoRotationPreferences =
                        Windows.Graphics.Display.DisplayOrientations.PortraitFlipped;
                    lockedorientation = "PORTRAIT";
                    JObject param = new JObject();
                    param[JsonKey.CURRENT_ORIENTATION] = "portraitFlipped";
                    Response.Success(id, param);

                }
                else if (orientation == Windows.Graphics.Display.DisplayOrientations.Landscape)
                {
                    Windows.Graphics.Display.DisplayInformation.AutoRotationPreferences =
                        Windows.Graphics.Display.DisplayOrientations.Landscape;
                    lockedorientation = "LANDSCAPE";
                    JObject param = new JObject();
                    param[JsonKey.CURRENT_ORIENTATION] = "landscape";
                    Response.Success(id, param);
                }
                else if (orientation == Windows.Graphics.Display.DisplayOrientations.LandscapeFlipped)
                {
                    Windows.Graphics.Display.DisplayInformation.AutoRotationPreferences =
                        Windows.Graphics.Display.DisplayOrientations.LandscapeFlipped;
                    lockedorientation = "LANDSCAPE";
                    JObject param = new JObject();
                    param[JsonKey.CURRENT_ORIENTATION] = "landscapeFlipped";
                    Response.Success(id, param);

                }
            }
            catch (Exception)
            {
                //param["errorCode"] = "APZ-CNT-082";
                Response.Fail(id, ErrorCode.ORIENATATION_LOCK_FAIL);
            }
        }
        #endregion

        #region Listner Class
        internal static void SetOrientaionListener(JObject obj)
        {
            ort_id = obj[JsonKey.ID].ToString();
            JObject ort = new JObject();
            ort = obj;
            ort["event"] = "started";
            ort["keepAlive"] = true;
            var disp = Windows.Graphics.Display.DisplayInformation.GetForCurrentView();
            //orientation_change_id = obj[JsonKey.ID].ToString();
            disp.OrientationChanged += _OrientationChanged;
            try
            {
                string curortn = Windows.Graphics.Display.DisplayProperties.CurrentOrientation.ToString();
                if (curortn == "Portrait" || curortn == "PortraitFlipped")
                {
                    ort["orientation"] = "PORTRAIT";
                }
                if (curortn == "LandscapeFlipped" || curortn == "Landscape")
                {
                    ort["orientation"] = "LANDSCAPE";
                }

                Response.Success(ort_id, true, ort);
            }
            catch (Exception e)
            {
                Response.Fail(ort_id, e.Message);
            }
        }

        internal static void KeyBrdListnerStop(JObject obj)
        {
            try
            {
                var keybd = InputPane.GetForCurrentView();
                keybd.Hiding -= _KbdHiding;
                keybd.Showing -= _Kbdshowing;
                var js = new JObject();
                js["event"] = "stopped";
                Response.Success(keybrd_id, js);
            }
            catch (Exception)
            {
                Response.Fail(obj[JsonKey.ID].ToString(), "");
            }
        }

        internal static void KeyBrdListner(JObject reqObject)
        {
            keybrd_id = reqObject[JsonKey.ID].ToString();
            var keybd = InputPane.GetForCurrentView();
            keybd.Hiding += _KbdHiding;
            keybd.Showing += _Kbdshowing;
            var js = new JObject();
            js["event"] = "started";
            Response.Success(keybrd_id, true, js);
        }

        private static void _Kbdshowing(InputPane sender, InputPaneVisibilityEventArgs args)
        {
            var js = new JObject();
            js["event"] = "show";
            Response.Success(keybrd_id, true, js);
        }

        private static void _KbdHiding(InputPane sender, InputPaneVisibilityEventArgs args)
        {
            var js = new JObject();
            js["event"] = "hide";
            Response.Success(keybrd_id, true, js);

        }

        private static void _OrientationChanged(DisplayInformation sender, object args)
        {
            JObject js = new JObject();
            js[JsonKey.ID] = ort_id;
            js["status"] = true;
            js["keepAlive"] = true;
            var orientation = sender.CurrentOrientation;
            if (DisplayInformation.AutoRotationPreferences == DisplayOrientations.None)
            {
                if (orientation == DisplayOrientations.Landscape || orientation == DisplayOrientations.LandscapeFlipped)
                    js[JsonKey.ORIENTATION] = "LANDSCAPE";
                else if (orientation == DisplayOrientations.Portrait || orientation == DisplayOrientations.PortraitFlipped)
                    js[JsonKey.ORIENTATION] = "PORTRAIT";

                Response.Success(ort_id, true, js);
            }
        }

        #endregion

        #region Native Alert Support
        private static SemaphoreSlim slim = new SemaphoreSlim(1, 1);
        // private static string orientation_change_id = "";
        internal static string NotificationCB_id = "";
        private static string keybrd_id;
        private static string ort_id;

        //   private static string keybrd_id;

        async static public void Alert(string msg)
        {
            await slim.WaitAsync();
            try
            {
                var a = new Windows.UI.Popups.MessageDialog(msg);
                await a.ShowAsync();
            }
            catch (Exception ex)
            {
                Log.Debug("Alert UI = " + ex.Message);
            }
            finally
            {
                slim.Release();
            }
            // Windows.Security.Authentication.Web.WebAuthenticationBroker.
        }
        #endregion

        #region Terminate
        internal static void Terminate()
        {
            Application.Current.Exit();
        }
        #endregion

        #region wipeout
        internal static async void WipeOut(JObject json)
        {
            try
            {
                var applicationData = Windows.Storage.ApplicationData.Current;
                var localSettings = applicationData.LocalSettings;
                try
                {
                    await applicationData.ClearAsync();
                }
                catch (Exception)
                {
                    DispatcherTimer dispatcherTimer = new DispatcherTimer();
                    dispatcherTimer.Tick += (sender, e) =>
                    {
                        dispatcherTimer.Stop();
                        WipeOut(json);
                    };
                    dispatcherTimer.Interval = new TimeSpan(0, 0, 0, 0, 500);
                    dispatcherTimer.Start();
                }
                localSettings.Values["APPEXPIRED"] = "Y";
                localSettings.Values["APPZILLONWIPEOUT"] = true;
                Application.Current.Exit();
            }
            catch (Exception)
            {
                // var data = JSON.stringify({ errorCode: "APZ-CNT-082", errorDescription: "Wipeout Failed" }, null, " ");
                Response.Fail(json[JsonKey.ID].ToString(), "APZ-CNT-082");
            }
        }
        internal static void SetScreenMapping(JObject obj)
        {
            foreach (var x in obj)
            {
                MainPage.PersistanceData.Values[x.Key] = obj[x.Key];
            }
        }

        internal static void GetAppVersion(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                var appid = obj["appId"].ToString();
                if (appid == MainPage.CurrentAppId)
                {
                    JObject succes = new JObject();
                    succes["appVersion"] = MainPage.PersistanceData.Containers[MainPage.CurrentAppId + "appVersion"].Values["appVersion"].ToString();
                    Response.Success(id, succes);
                }
                else
                {
                    string settingFilePath = AppSettings.GetSettingPath("appprops.json", appid);
                    string fileout = "";
                    try
                    {
                        fileout = File.ReadAllText(settingFilePath);
                    }
                    catch (Exception)
                    {
                        Response.Fail(id, ErrorCode.FILE_DOES_NOT_EXISTS);
                    }
                    if (fileout == "" || fileout == null)
                    {
                        Response.Fail(id, ErrorCode.FILE_CONTENT_EMPTY_ERROR);
                    }
                    var props = JObject.Parse(fileout);
                    JObject success = new JObject();
                    success["appVersion"] = props.GetValue("appVersion");
                    Response.Success(id, success);
                }
            }
            catch (Exception)
            {
                Response.Fail(id, ErrorCode.CHILD_APP_VERSION_GET_FAIL);
            }
        }
        #endregion

        #region getlocation

        public static async void GetLoc(JObject obj)
        {
            JObject loc = await Location();
            Response.Success(obj[JsonKey.ID].ToString(), loc);
        }
        public static async Task<JObject> Location()
        {
            JObject js = new JObject();
            if (AppSettings.AppProperties["trackLocation"].ToString() == "Y")
            {
                var accessStatus = await Geolocator.RequestAccessAsync().AsTask();
                try
                {
                    Geolocator geolocator = new Geolocator();
                    geolocator.DesiredAccuracy = PositionAccuracy.High;
                    Geoposition pos = await geolocator.GetGeopositionAsync();
                    js["longitude"] = pos.Coordinate.Point.Position.Longitude.ToString();
                    js["latitude"] = pos.Coordinate.Point.Position.Latitude.ToString();
                }
                catch (Exception)
                {
                    Log.Warning("location access denied");
                    js["longitude"] = "";
                    js["latitude"] = "";
                }
            }
            else
            {
                js["longitude"] = "";
                js["latitude"] = "";

            }
            return js;
        }

        #endregion

        #region Fetch IP  Method
        public static string GetIP()
        {
            try
            {
                var icp = NetworkInformation.GetInternetConnectionProfile();

                if (icp?.NetworkAdapter == null) return null;
                var hostname =
                    NetworkInformation.GetHostNames()
                        .SingleOrDefault(
                            hn =>
                                hn.IPInformation?.NetworkAdapter != null && hn.IPInformation.NetworkAdapter.NetworkAdapterId
                                == icp.NetworkAdapter.NetworkAdapterId);

                // the ip address
                return hostname?.CanonicalName;
            }
            catch (Exception ex)
            {
                return null;
            }
        }
        //internal static async Task<string> GetIP()
        //{
        //    ConnectionProfile conType = NetworkInformation.GetInternetConnectionProfile();
        //    ConnectionProfileFilter fil = new ConnectionProfileFilter();
        //    var hnames = NetworkInformation.GetHostNames();
        //    fil.IsConnected = true;
        //    var nhosts = await NetworkInformation.FindConnectionProfilesAsync(fil);
        //    int i = hnames.Count;
        //    string IP = "";
        //    if (nhosts.Count > 0)
        //    {
        //        for (int j = 0; j < i; j++)
        //        {
        //            if (hnames[j].IPInformation != null)
        //            {
        //                if (hnames[j].IPInformation.NetworkAdapter.NetworkAdapterId == nhosts[0].NetworkAdapter.NetworkAdapterId)
        //                {
        //                    IP = hnames[j].DisplayName;
        //                    j = i + 6;
        //                }
        //            }
        //        }
        //        var json = new JObject();
        //        return IP;
        //    }
        //    else
        //    {
        //        return "";
        //    }
        //}
        #endregion

        public static Windows.Storage.ApplicationDataContainer localSettings = Windows.Storage.ApplicationData.Current.LocalSettings;


        //public static void RegisterNotification(JObject obj)
        //{
        //    NotificationCB_id = obj[JsonKey.ID].ToString();
        //    var js = new JObject();
        //    js["event"] = "started";
        //    Response.Success(NotificationCB_id, true, js);
        //}
        
         #region Server Token  decryption key
       	 public static string ServerTokenkey = "APPZILLONDECRYPT";
         #endregion
    }
}


