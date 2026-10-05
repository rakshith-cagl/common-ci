using System;
using System.IO;
using Windows.Storage;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using Windows.Data.Json;
using Appzillon.Plugins;
using Appzillon.CommonClass;

namespace Appzillon.Native
{
    static class AppSettings
    {
        public static JObject ContainerProperties;
        public static JObject AppProperties;

        public static void InitializeSettings()
        {
            AppProperties = new JObject();
            string settingFilePath = GetSettingPath("appprops.json", MainPage.CurrentAppId);
            var fileout = File.ReadAllText(settingFilePath);
            AppProperties = JObject.Parse(fileout);
            string container = MainPage.CurrentAppId + "appVersion";
            if (!MainPage.PersistanceData.Containers.ContainsKey(container))
            {
                MainPage.PersistanceData.CreateContainer(container, ApplicationDataCreateDisposition.Always);
                MainPage.PersistanceData.Containers[container].Values.Add("appVersion", AppProperties.GetValue("appVersion").ToString());
            }
            try
            {
                string debuglvl = AppProperties["logLevel"].ToString();
                switch (debuglvl)
                {
                    case "D":
                        Log.level = 4;
                        break;
                    case "I":
                        Log.level = 3;
                        break;
                    case "W":
                        Log.level = 2;
                        break;
                    case "E":
                        Log.level = 1;
                        break;
                    case "F":
                        Log.level = 0;
                        break;
                    default:
                        Log.level = -1;
                        break;
                }
                
            }
            catch (Exception)
            {
                Log.FatalError("error in setting debug level");
            }
        }

        internal static void SetContainerProps()
        {
            ContainerProperties = new JObject();
            var json = File.ReadAllText(@"containerprops.json");
            ContainerProperties = JObject.Parse(json);
            MainPage.MainAppId = ContainerProperties["MAINAPPID"].ToString();
            MainPage.CurrentAppId = MainPage.MainAppId;
        }
        public static string GetSettingPath(string jsonfile, string appid)
        {
            var fp = "";
            string flag = AppSettings.ContainerProperties["OTAREQUIRED"].ToString();
            if (flag == "N")
            {
                StorageFolder installedlocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
                fp = installedlocation.Path + @"\appzillonapps\apps\" + appid + @"\screens\config\" + jsonfile;
            }
            else
            {
                StorageFolder local = ApplicationData.Current.LocalFolder;
                fp = local.Path + @"\appzillonapps\apps\" + appid + @"\screens\config\" + jsonfile;
            }
            return fp;
        }
        public static void SetSetting(JObject js)
        {
            string appid = MainPage.CurrentAppId;
            string container = appid + "userprefs";
            string key = js["key"].ToString();
            string value = js["value"].ToString();
            ApplicationDataContainer settings = MainPage.PersistanceData.CreateContainer(container, ApplicationDataCreateDisposition.Always);
            try
            {
                settings.Values.Add(key, value);
            }
            catch (Exception e)
            {
                if (e.HResult.ToString() == "-2147024809")
                {
                    try
                    {
                        settings.Values.Remove(key);
                        settings.Values.Add(key, value);
                    }
                    catch (Exception)
                    {
                    }
                }
                Log.Error(e.Message);
            }
            Response.Success(js["id"].ToString(), false);
        }
        public static void SetSettings(JObject settingsjson)
        {
            string appid = MainPage.CurrentAppId;
            string container = appid + "userprefs";
            JObject settings = (JObject)settingsjson["userPrefs"];
            ApplicationDataContainer sett = MainPage.PersistanceData.CreateContainer(container, ApplicationDataCreateDisposition.Always);
            foreach (var x in settings)
            {
                JObject tempjson = new JObject();
                sett.Values.Add(x.Key, x.Key);
            }
            Response.Success(settingsjson["id"].ToString(), false);
        }
        public static void GetSetting(JObject obj)
        {
            string appid = MainPage.CurrentAppId;
            string container = appid + "userprefs";
            string key = obj["key"].ToString();
            JObject setting = new JObject();
            if (MainPage.PersistanceData.Containers.ContainsKey(container))
            {
                Object val = "";
                MainPage.PersistanceData.Containers[container].Values.TryGetValue(key, out val);
                setting["value"] = val.ToString();
                Response.Success(obj["id"].ToString(), setting);
            }
            else
            {

                Response.Fail(obj["id"].ToString(), "");
            }
        }
        public static void GetSettings(JObject obj)
        {
            string appid = MainPage.CurrentAppId;
            string container = appid + "userprefs";
            bool hasContainer = MainPage.PersistanceData.Containers.ContainsKey(container);
            if (!hasContainer)
                Response.Success(obj["id"].ToString(), new JObject());
            JObject settings = new JObject();
            settings["userPrefs"] = new JObject();
            foreach (var x in MainPage.PersistanceData.Containers[container].Values)
            {
                settings["userPrefs"][x.Key] = (string)x.Value;
            }
            //Log.Debug("userPrefs ::" + settings.ToString());
            Response.Success(obj["id"].ToString(), settings);
        }
    }
}
