using Appzillon.CommonClass;
using Appzillon.Constants;
using Appzillon.Plugins;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Native
{
   //Author Anand Kumar
   /// <summary>
   /// Create Request for appsectoken Send Appsectoken Request and capture response from local server
   /// </summary>
    public class AppToken
    {
        MainPage mainPage;
 
        public AppToken()
        {
        }
        public AppToken(MainPage mp)
        {
            mainPage = mp;
        }

        public async Task Getappsectoken()

        {
            string appid = MainPage.CurrentAppId;
            string url = Utils.GetServerUrl();
            Debug.WriteLine(url);
            JObject appsitetokenvalue =  Jobjectappsectokenvalue(appid);
            await SendAppsectokendetails(url, appsitetokenvalue.ToString());
        }
        public async Task SendAppsectokendetails(string url , string appsitetokenvalue)
        {
            string response = await GenericUtils.SendRequestAsync(url, appsitetokenvalue);
           // Debug.WriteLine("Response of appsectoken" + response);
            Log.Debug("Response of appsectoken\n" + response);
               await AppSecTokenResponse(response);
            return;
        }
             
      
       
        public  JObject Jobjectappsectokenvalue( string appid)
        {
            JObject apzjobject = new JObject();
            string deviceId = Utils.GetDeviceID();
            long currenttimemillis =Utils.CurrentTimeMillis();
            string os = Utils.GetOS();
            string ipaddress =  Utils.GetIP() ;
            Debug.WriteLine(ipaddress);

         
            apzjobject[JsonKey.APPZILLON_HEADER]  = new JObject();
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.APP_ID] = appid;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.SESSION_ID] = JsonKey.NULL;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.DEVICE] = deviceId;
            apzjobject[JsonKey.APPZILLON_HEADER]["async"] = false;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.USER_ID] = JsonKey.NULL;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.SCREEN_ID] = JsonKey.LOGIN;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.REQ_STATUS] = "true";
            apzjobject[JsonKey.APPZILLON_HEADER]["source"] = "APPZILLON";
            apzjobject[JsonKey.APPZILLON_HEADER]["clientNonce"] = currenttimemillis.ToString();
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.INTERFACE_ID] =JsonKey.APPZLION_GET_APP_SEC_TOKEN;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.OS] = os;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.REQUESTID] = JsonKey.CSNONCE;
            apzjobject[JsonKey.APPZILLON_HEADER]["origination"] = ipaddress;
            apzjobject[JsonKey.APPZILLON_HEADER][JsonKey.REQUEST_KEY] = "";
            apzjobject[JsonKey.APPZILLON_BODY] = new JObject();
            apzjobject[JsonKey.APPZILLON_BODY][JsonKey.APPZLION_GET_APP_SEC_TOKEN_REQUEST] = new JObject();
            apzjobject[JsonKey.APPZILLON_BODY][JsonKey.APPZLION_GET_APP_SEC_TOKEN_REQUEST][JsonKey.APP_ID] = appid;
            apzjobject[JsonKey.APPZILLON_BODY][JsonKey.APPZLION_GET_APP_SEC_TOKEN_REQUEST][JsonKey.DEVICE] = deviceId;

            //  Debug.WriteLine("Complete Request" + apzjobject);
            Log.Debug("Request appsectoken\n" + apzjobject);
            return apzjobject;


        }
        ServerUtils servutil = new ServerUtils();
        private async Task AppSecTokenResponse(string sucessresponse)
        {
            try
            {
                JObject _jobSucessResponse = JObject.Parse(sucessresponse);
                JToken _jtokenAppzlionbody = (JToken)_jobSucessResponse[JsonKey.APPZILLON_BODY][JsonKey.APPZLION_GET_APP_SEC_TOKEN_RESPONSE];
                JObject _jobjectAppzlionbody = JObject.Parse(_jtokenAppzlionbody.ToString());
                AppzillonGetAppSecTokensResponse.status = (string)_jobjectAppzlionbody[JsonKey.STATUS];

                if (AppzillonGetAppSecTokensResponse.status.ToLower() == JsonKey.SUCCESS)
                {
                    AppzillonGetAppSecTokensResponse.safeToken = (string)_jobjectAppzlionbody[JsonKey.SAFETOKEN];
                    AppzillonGetAppSecTokensResponse.serverNonce = (string)_jobjectAppzlionbody[JsonKey.SERVERNONCE];
                    AppzillonGetAppSecTokensResponse.sessionToken = (string)_jobjectAppzlionbody[JsonKey.SESSIONTOKEN];

                }

                else
                {
                    await Task.Delay(TimeSpan.FromSeconds(JsonKey.Timeout));
                    //await Getappsectoken();
                }

            }
            catch (Exception ex)
            {
                ex.Message.ToString();
            }


        }
    }
}
