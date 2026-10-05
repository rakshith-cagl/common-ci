using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Authentication.Web;

namespace Appzillon.Plugins
{
    class Facebook
    {
#region Singleton Pattern
        private static Facebook instance;
        private Facebook()
        {
        }
        public static Facebook Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Facebook();
                }
                return instance;
            }
        }
#endregion
        
        private async Task<JObject> GetInfo(string r)
        {
            try
            {
                string access_token = r.Split('#')[1];// r.Substring(r.IndexOf("access_token"), r.IndexOf("&"));
                var client = new Windows.Web.Http.HttpClient();
                string resultRes = await client.GetStringAsync(new Uri("https://graph.facebook.com/me?" + access_token + "&fields=picture,email,id,name,first_name,last_name,age_range,link,gender,locale,timezone,updated_time,verified"));
                //Log.Debug(resultRes);
                JObject userInfo = JObject.Parse(resultRes);
                JObject userDetail = new JObject();
                userDetail["fbId"] = userInfo[JsonKey.ID].ToString();
                userDetail[JsonKey.EMAIL] = userInfo[JsonKey.EMAIL].ToString();
                userDetail[JsonKey.NAME] = userInfo[JsonKey.NAME].ToString();
                JToken l_name = "";
                JToken f_name = "";
                userInfo.TryGetValue("first_name",out f_name);
                userInfo.TryGetValue("last_name", out l_name);
                userDetail[JsonKey.FIRST_NAME] = f_name;
                userDetail[JsonKey.LAST_NAME] = l_name;
                try
                {
                    userDetail[JsonKey.PICTURE_URL] = userInfo["picture"]["data"]["url"].ToString();
                }catch(Exception e)
                {
                    Log.Info("No Picture Url Found For The Profile");
                }
                userDetail[JsonKey.GENDER] = userInfo[JsonKey.GENDER].ToString();
                userDetail[JsonKey.LOCALE] = userInfo[JsonKey.LOCALE].ToString();
                userDetail[JsonKey.IS_VERIFIED] = userInfo["verified"].ToString() != null ? true : false;
                return userDetail;
            }
            catch (Exception ex)
            {
                Log.Error("Facebook GetProfile error= " + ex.Message);
                return new JObject();
            }
        }

        public async void StartLoginProcess( JObject strJson)
        {
            string id = strJson[JsonKey.ID].ToString();
            try
            {
                string clientID = AppSettings.AppProperties.GetValue("facebookAppId").ToString();
                string callbackURL = WebAuthenticationBroker.GetCurrentApplicationCallbackUri().AbsoluteUri;

                string fburl = "https://www.facebook.com/dialog/oauth?client_id=" + clientID + "&scope=public_profile,email&display=popup&response_type=token&redirect_uri=" + callbackURL;
                Uri StartUri = new Uri(fburl);
                // When using the desktop flow, the success code is displayed in the html title of this end uri
                Uri EndUri = new Uri(callbackURL);

                Log.Debug("Navigating to URL");

                WebAuthenticationResult WebAuthenticationResult = await WebAuthenticationBroker.AuthenticateAsync(WebAuthenticationOptions.None, StartUri, EndUri);
                if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.Success)
                {
                    JObject str = await GetInfo(WebAuthenticationResult.ResponseData.ToString());
                    Log.Debug("Web Authentication Response Recieved Successfully");
                    Response.Success(id,str);
                }
                else if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.ErrorHttp)
                {
                    Response.Fail(id, ErrorCode.HTTP_FAIL);
                    Log.Error("HTTP Error returned by AuthenticateAsync()");
                }
                else
                {
                    Log.Error("Error returned by AuthenticateAsync()");
                    Response.Fail(id,ErrorCode.COULD_NOT_LOGIN);
                }
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.COULD_NOT_LOGIN);
                Log.Error(" Facebook Login ,"+e.Message);
            }
        }
    }
}
