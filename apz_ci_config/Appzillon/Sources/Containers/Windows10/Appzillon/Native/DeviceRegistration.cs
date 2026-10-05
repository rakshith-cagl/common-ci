using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;
using Appzillon.Plugins;
using System.Diagnostics;
using Appzillon.Constants;

namespace Appzillon.Native
{
    class DeviceRegistration
    {
        public DeviceRegistration()
        {
        }
        public async Task<bool> RegisterDevice()
        {
            string url = Utils.GetServerUrl();
            string values = await GetValues();
            bool t = await SendDeviceRegisterRequest(url, values);
            return t;
            //------------send app master request------------------
        }
        public async Task<bool> SendDeviceRegisterRequest(string url, string data, string comment)
        {
            using (var client = new HttpClient())
            {
                try
                {
                    HttpContent content = new StringContent(data, Encoding.UTF8);
                    var response = await client.PostAsync(url, content);
                    Log.Debug("Device Registration Status = " + response.IsSuccessStatusCode.ToString());
                    var responseString = await response.Content.ReadAsStringAsync();
                    return IsDeviceRegistered(responseString);
                }
                catch (Exception ex)
                {
                    Log.Error("Device Registration Request Failed\n" + ex.Message);
                    return false;
                }
            }
        }
        public async Task<bool> SendDeviceRegisterRequest(string url, string data)
        {
            string responseString = await GenericUtils.SendRequestAsync(url, data);
            return IsDeviceRegistered(responseString);
        }
        private bool IsDeviceRegistered(string responseString)
        {
            try
            {
                JObject res = JObject.Parse(responseString);
                Debug.WriteLine("Device Register response = " + res.ToString());
                Log.Debug("Device Register response = " + res["appzillonHeader"]["status"].ToString());
                if ((bool)res["appzillonHeader"]["status"])
                {
                    Utils.localSettings.Values["DeviceSatus"] = "True";
                    return true;
                }
                else
                {
                    if (res["appzillonErrors"][0]["errorCode"].ToString() == "APZ-DM-039")
                    {
                        // Utils.localSettings.Values["DeviceSatus"] = "True";
                        Log.Warning(res["appzillonErrors"][0]["errorMessage"].ToString());
                        return true;
                    }
                    else
                    {
                        return false;
                    }

                }
            }
            catch (Exception ex)
            {
                Log.FatalError("Device Registration Exception\n" + ex.Message);
                return false;
            }
        }
        public static async Task<string> GetValues()
        {
            JObject locate = await Utils.Location();
            string longitude = locate["longitude"].ToString();
            string latitude = locate["latitude"].ToString();
            string appId = MainPage.CurrentAppId;
            string userId = "windows10";
            string os = Utils.GetOS();
            string osVersion = Utils.GetOSVersion();
            string deviceId = Utils.GetDeviceID();
            string deviceName = Utils.GetDeviceName();
            string mobile1 = Utils.GetMobile1();
            string mobile2 = Utils.GetMobile2();
            string model = Utils.GetModel();
            string make = Utils.GetMake();
            string screenResolution = Utils.GetScreenResolution();

            string screenId = "launchApp";
            string interfaceId = "appzillonDeviceRegistration";
            string sessionId = "";
            string requestKey = "000NEW";
            string ipaddress = Utils.GetIP();
            JObject obj = new JObject();
            try
            {
                obj["appzillonHeader"] = new JObject();
                obj["appzillonHeader"]["preLogin"] = "true";
                obj["appzillonHeader"]["appId"] = appId;
                obj["appzillonHeader"]["screenId"] = screenId;
                obj["appzillonHeader"]["requestKey"] = requestKey;
                obj["appzillonHeader"]["interfaceId"] = interfaceId;
                obj["appzillonHeader"]["status"] = "true";
                obj["appzillonHeader"]["sessionId"] = sessionId;
                obj["appzillonHeader"]["deviceId"] = deviceId;
                obj["appzillonHeader"]["userId"] = userId;
                obj["appzillonHeader"]["longitude"] = longitude;
                obj["appzillonHeader"]["latitude"] = latitude;
                obj[JsonKey.APPZILLON_HEADER]["origination"] = ipaddress;
                obj[JsonKey.APPZILLON_HEADER]["source"] = "APPZILLON";
                obj["appzillonBody"] = new JObject();
                obj["appzillonBody"]["deviceRegisterRequest"] = new JObject();
                obj["appzillonBody"]["deviceRegisterRequest"]["appId"] = appId;
                obj["appzillonBody"]["deviceRegisterRequest"]["os"] = os;
                obj["appzillonBody"]["deviceRegisterRequest"]["osVersion"] = osVersion;
                obj["appzillonBody"]["deviceRegisterRequest"]["deviceId"] = deviceId;
                obj["appzillonBody"]["deviceRegisterRequest"]["mobile1"] = mobile1;
                obj["appzillonBody"]["deviceRegisterRequest"]["mobile2"] = mobile2;
                obj["appzillonBody"]["deviceRegisterRequest"]["model"] = model;
                obj["appzillonBody"]["deviceRegisterRequest"]["make"] = make;
                obj["appzillonBody"]["deviceRegisterRequest"]["screenResolution"] = screenResolution;
                obj["appzillonBody"]["deviceRegisterRequest"]["deviceName"] = deviceName;
                obj["appzillonBody"]["deviceRegisterRequest"]["longitude"] = longitude;
                obj["appzillonBody"]["deviceRegisterRequest"]["latitude"] = latitude;
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message);
            }
            return obj.ToString();
        }
    }
}

