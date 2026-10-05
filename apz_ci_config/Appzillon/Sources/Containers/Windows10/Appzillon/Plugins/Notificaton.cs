using System;
using Appzillon.Constants;
using Appzillon.Native;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using Newtonsoft.Json.Linq;
using Windows.Networking.PushNotifications;
using Windows.UI.Core;

namespace Appzillon.Plugins
{
    class Notification
    {
        public Notification()
        {
        }
        public async void RegisterNotificationUrl()
        {
            string url = Utils.GetServerUrl();
            string notiurl = "";
            try
            {
               notiurl = MainPage.PersistanceData.Values["NOTIFICATIONURL"].ToString();
            } catch (Exception)
            {
            }
            string notificationUrl = await GetNotificationUrlAsync();         
            if (notificationUrl != notiurl)
            {
                string val = GetValues(notificationUrl);
                SendNotificationRequest(url, val);
            }
        }
        private async void SendNotificationRequest(string url, string data)
        {
            string response = await GenericUtils.SendRequestAsync(url, data);
            NotificationResponse(response);
        }
        //private async void SendNotificationRequest(string url, string data, string comment)
        //{
        //    using (var client = new HttpClient())
        //    {
        //        try
        //        {
        //            HttpContent content = new StringContent(data, Encoding.UTF8);
        //            var response = await client.PostAsync(url, content);

        //            var responseString = await response.Content.ReadAsStringAsync();
        //            NotificationResponse(responseString);
        //        }
        //        catch (Exception ex)
        //        {
        //            Log.Error(ex.Message);
        //        }
        //    }
        //}
        private async Task<string> GetNotificationUrlAsync()
        {
            try
            {
                PushNotificationChannel p = await PushNotificationChannelManager.CreatePushNotificationChannelForApplicationAsync();
                p.PushNotificationReceived += P_PushNotificationReceived;
                MainPage.PersistanceData.Values.Remove("NOTIFICATIONURL");
                MainPage.PersistanceData.Values["NOTIFICATIONURL"] = p.Uri;
                return p.Uri;
            }
            catch (Exception ex)
            {
              Log.Error(ex.Message);
                return "";
            }
        }

        private async void P_PushNotificationReceived(PushNotificationChannel sender, PushNotificationReceivedEventArgs args)
        {
            string msg = "";
            try
            {
                msg = args.ToastNotification.Content.InnerText;
            }
            catch (Exception)
            { }
            Log.Debug("Notification Message recieved");
            JObject js = new JObject();
            if (Utils.NotificationCB_id != "")
            {
                await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                js["event"] = "notification";
                js["text"] = msg;
                Response.Success(Utils.NotificationCB_id, true, js);
            });
           
            }
            double timestamp = Math.Round(DateTime.Now.Subtract(DateTime.MinValue.AddYears(1969)).TotalSeconds);
            Storage.Instance.storeNotification(msg, timestamp, 'N');
        }

        private void NotificationResponse(string res)
        {
            try
            {
                JObject obj = JObject.Parse(res);
                Log.Debug("Notifaction Registration Response Recieved");
            }
            catch (Exception ex)
            {
                Log.Error("Notifaction Registration Response failed\n======" + ex.Message);
            }

        }

        private string GetValues(string channelUrl)
        {
            string appId = MainPage.CurrentAppId;
            string deviceId = Utils.GetDeviceID();
            string os = Utils.GetOS();
            string userId = JsonKey.WINDOWS_10;//"windows10";
            string interfaceId = JsonKey.APPZILLON_NOTIFICATION_REGISTRATION;//"appzillonNotificationRegistration";
            string currChannelURL = channelUrl;
            string deviceName = Utils.GetDeviceName();

            JObject data = new JObject();
            data[JsonKey.APPZILLON_HEADER] = new JObject();
            data[JsonKey.APPZILLON_HEADER][JsonKey.PRELOGIN] = "true";
            data[JsonKey.APPZILLON_HEADER][JsonKey.APP_ID] = appId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.USER_ID] = userId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.DEVICE] = deviceId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.INTERFACE_ID] = interfaceId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.SESSION_ID] = "";
            data[JsonKey.APPZILLON_HEADER][JsonKey.STATUS] = true;
            data[JsonKey.APPZILLON_HEADER][JsonKey.REQUEST_KEY] = JsonKey.NULL;
            data[JsonKey.APPZILLON_HEADER][JsonKey.SCREEN_ID] = JsonKey.LOGIN;

            data[JsonKey.APPZILLON_BODY] = new JObject();
            data[JsonKey.APPZILLON_BODY][JsonKey.APP_ID] = appId;
            data[JsonKey.APPZILLON_BODY][JsonKey.OS_ID] = os;
            data[JsonKey.APPZILLON_BODY][JsonKey.OS_VERSION] = "10";
            data[JsonKey.APPZILLON_BODY][JsonKey.DEVICE_NAME] = deviceName;
            data[JsonKey.APPZILLON_BODY][JsonKey.DEVICE] = deviceId;
            data[JsonKey.APPZILLON_BODY][JsonKey.REG_ID] = currChannelURL;
            return data.ToString();
        }

        internal void UnRegisterNotification(JObject reqObject)
        {
            Utils.NotificationCB_id = "";
            var js = new JObject();
            js["event"] = "stopped";
            Response.Success(reqObject[JsonKey.ID].ToString(),js);
        }
    }
}
