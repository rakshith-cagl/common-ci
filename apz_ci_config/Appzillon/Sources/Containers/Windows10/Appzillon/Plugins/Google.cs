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
    class Google
    {
#region Singleton Pattern
        private static Google instance;
        private Google()
        {
        }
        public static Google Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Google();
                }
                return instance;
            }
        }
#endregion

        private async Task<JObject> GetInfo(string str)
        {
            string code = str.Substring(str.IndexOf("code="), str.IndexOf("&"));
            try
            {
                string secretKey = AppSettings.AppProperties.GetValue("googlePlusSecretKey").ToString();
                var clientID = AppSettings.AppProperties.GetValue("googlePlusClientId").ToString();
                var callbackUrl = "urn:ietf:wg:oauth:2.0:oob";
                Uri res_url = new Uri("https://accounts.google.com/o/oauth2/token");
                string param = code + "&client_id=" + clientID + "&client_secret=" + secretKey + "&redirect_uri=" + callbackUrl + "&scope=&grant_type=authorization_code";
                var client = new Windows.Web.Http.HttpClient();
                var stringContent = new Windows.Web.Http.HttpStringContent(param, Windows.Storage.Streams.UnicodeEncoding.Utf8, "application/x-www-form-urlencoded");
                var result = await client.PostAsync(res_url, stringContent);
                //Log.Debug(result.Content.ToString());
                JObject userRes = JObject.Parse(result.Content.ToString());

                string access_token = userRes["access_token"].ToString();
                var client2 = new Windows.Web.Http.HttpClient();
                var resultRes = await client.GetStringAsync(new Uri("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + access_token));

              // Log.Debug(resultRes);
                JObject userInfo = JObject.Parse(resultRes);
                JObject userDetail = new JObject();

                userDetail["gplusId"] = userInfo[JsonKey.ID].ToString();
                userDetail[JsonKey.EMAIL] = userInfo[JsonKey.EMAIL].ToString();
                userDetail[JsonKey.NAME] = userInfo[JsonKey.NAME].ToString();
                userDetail[JsonKey.FIRST_NAME] = userInfo["given_name"].ToString();
                userDetail[JsonKey.LAST_NAME] = userInfo["family_name"].ToString();
                try
                {
                    userDetail[JsonKey.PICTURE_URL] = userInfo["picture"].ToString();
                }catch(Exception)
                {
                    userDetail[JsonKey.PICTURE_URL] = "Picture Not Found For The Profile";
                    Log.Warning("Picture Not Found For The Profile" );
                }
                JToken gen ="";
                userInfo.TryGetValue(JsonKey.GENDER,out gen);
                userDetail[JsonKey.GENDER] = gen;
                userDetail[JsonKey.LOCALE] = userInfo[JsonKey.LOCALE].ToString();
                userDetail[JsonKey.IS_VERIFIED] = userInfo["verified_email"].ToString() != null ? true : false;
                return userDetail;
               
            }
            catch (Exception ex)
            {
                Log.Error("Google Sign In, "+ex.Message);
                return new JObject();
            }
        }

        public async void StartLoginProcess(JObject json)
        {
            string id = json[JsonKey.ID].ToString();
            try
            {
                string googleClientID = AppSettings.AppProperties.GetValue("googlePlusClientId").ToString();
                string googleCallbackUrl = "urn:ietf:wg:oauth:2.0:oob";
                string startUrl = "https://accounts.google.com/o/oauth2/auth?client_id=";
                string endUrl = "https://accounts.google.com/o/oauth2/approval?";
                String GoogleURL = startUrl + Uri.EscapeDataString(googleClientID) +
                    "&redirect_uri=" + Uri.EscapeDataString(googleCallbackUrl) +
                    "&response_type=code&scope=openid email profile";// + Uri.EscapeDataString("http://picasaweb.google.com/data");

                Uri StartUri = new Uri(GoogleURL);
                // When using the desktop flow, the success code is displayed in the html title of this end uri
                Uri EndUri = new Uri(endUrl);
                Log.Debug("Navigating to URL");
                WebAuthenticationResult WebAuthenticationResult = await WebAuthenticationBroker.AuthenticateAsync(WebAuthenticationOptions.UseTitle, StartUri, EndUri);
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
                    Response.Fail(id, ErrorCode.COULD_NOT_LOGIN);
                    Log.Error("Could Not Log In");
                }
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.COULD_NOT_LOGIN);
                Log.Error(e.Message);
            }
        }
    }
}
