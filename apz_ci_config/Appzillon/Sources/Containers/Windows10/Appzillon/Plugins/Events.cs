using System;
using Newtonsoft.Json.Linq;
using Windows.Devices.Power;
using Windows.System.Power;
using Windows.System.Profile;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.Media.Devices;
using Appzillon.Constants;
using Appzillon.Native;

namespace Appzillon.Plugins
{
    public class Events
    {
        // Activation and deactivation event
        #region Singleton Pattern
        private static Events instance;
        private Events()
        {
        }
        public static Events Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Events();
                }
                return instance;
            }
        }
        #endregion
        private String eve_id = null;
        public void UpdateEvents(JObject eventjson)
        {
            eve_id = eventjson[JsonKey.ID].ToString();
            string all = "off";
            try
            {
             all = eventjson[JsonKey.ALL_EVENTS].ToString();
            } catch (Exception)
            { }
            if (all == "on")
            {
                try
                {
                    if (eventjson[JsonKey.BACK_BUTTON_EVENT].ToString() == JsonKey.ON)
                    {
                        if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
                        {
                            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += onbackpressed_Evt;
                        }
                    }
                    if (eventjson[JsonKey.APP_PAUSED_EVENT].ToString() == JsonKey.ON)
                    {
                        Application.Current.Suspending += Current_Suspending;

                    }
                    if (eventjson[JsonKey.APP_RESUMED_EVENT].ToString() == JsonKey.ON)
                    {

                        Application.Current.Resuming += Current_Resuming;
                    }
                    var obj = new JObject();
                    obj[JsonKey.EVENT] = "started";
                    Response.Success(eve_id, true, obj);
                }
                catch (Exception e)
                {
                    Response.Fail(eve_id,ErrorCode.REGISTER_TO_EVT_FAIL);
                    Log.Error("Event Registration Fail, "+e.Message);
                }
            }
            else
            {
                try
                {
                    if(eventjson["backButtonEvent"].ToString() == "on")
                    Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += onbackpressed_Evt;
                }
                catch (Exception)
                { }
                try
                {
                    if (eventjson["appPausedEvent"].ToString() == "on")
                        Application.Current.Suspending += Current_Suspending;
                }
                catch (Exception)
                {
                }
                try
                {
                    if (eventjson["appResumedEvent"].ToString() == "on")
                        Application.Current.Resuming += Current_Resuming;
                }
                catch (Exception)
                { }
                var obj = new JObject();
                obj[JsonKey.EVENT] = "started";
                Response.Success(eve_id, true, obj);
            }
        }
        private void Current_Resuming(object sender, object e)
        {
            //ect.InvokeScript("applicationPaused", "");
            var obj =new JObject();
            obj[JsonKey.EVENT] = "appResumed";
            Response.Success(eve_id,true,obj);
        }

        private void Current_Suspending(object sender, Windows.ApplicationModel.SuspendingEventArgs e)
        {
            var obj =new JObject();
            obj[JsonKey.EVENT] = "appPaused";
            Response.Success(eve_id,true,obj);
        }

        private void onbackpressed_Evt(object sender, BackRequestedEventArgs e)
        {
            var obj =new JObject();
            obj[JsonKey.EVENT] = "backButton";
            Response.Success(eve_id,true,obj);
        }

    }
}
