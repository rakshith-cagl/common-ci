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
    class LinkedIn
    {
#region Singleton Pattern
        private static LinkedIn instance;
        private LinkedIn()
        {
        }
        public static LinkedIn Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new LinkedIn();
                }
                return instance;
            }
        }
#endregion

        private async Task<JObject> GetInfo(string str)
        {
            string code = str.Split('?')[1];// str.Substring(str.IndexOf("code="), str.IndexOf(@"&"));
            try
            {
                string secretKey = AppSettings.AppProperties.GetValue("linkedinSecretKey").ToString();
                var clientID = AppSettings.AppProperties.GetValue("linkedinClientId").ToString();
                var callbackUrl = "http://localhost";
                Uri res_url = new Uri("https://www.linkedin.com/uas/oauth2/accessToken");
                string param = code + "&client_id=" + clientID + "&client_secret=" + secretKey + "&redirect_uri=" + Uri.EscapeDataString(callbackUrl) + "&grant_type=authorization_code";
                var client = new Windows.Web.Http.HttpClient();
                var stringContent = new Windows.Web.Http.HttpStringContent(param, Windows.Storage.Streams.UnicodeEncoding.Utf8, "application/x-www-form-urlencoded");
                var result = await client.PostAsync(res_url, stringContent);
                //Log.Debug(result.Content.ToString());
                JObject userRes = JObject.Parse(result.Content.ToString());

                string access_token = userRes["access_token"].ToString();
                var client2 = new Windows.Web.Http.HttpClient();
                var resultRes = await client.GetStringAsync(new Uri("https://api.linkedin.com/v1/people/~:(id,picture-url,first-name,last-name,maiden-name,formatted-name,headline,location,industry,summary,specialties,positions,email-address)?format=json&oauth2_access_token=" + access_token));

                //Log.Debug(resultRes);
                JObject userInfo = JObject.Parse(resultRes);
                JObject userDetail = new JObject();

                userDetail["linId"] = userInfo[JsonKey.ID].ToString();
                userDetail[JsonKey.EMAIL] = userInfo["emailAddress"].ToString();
                userDetail[JsonKey.NAME] = userInfo["formattedName"].ToString();
                userDetail[JsonKey.FIRST_NAME] = userInfo[JsonKey.FIRST_NAME].ToString();
                userDetail[JsonKey.LAST_NAME] = userInfo[JsonKey.LAST_NAME].ToString();
                try
                {
                    userDetail[JsonKey.PICTURE_URL] = userInfo[JsonKey.PICTURE_URL].ToString();
                } catch (Exception)
                {
                    userDetail[JsonKey.PICTURE_URL] = "";
                    Log.Warning("No Picture Found For This User Profile");
                }
                try
                {
                    userDetail[JsonKey.GENDER] = userInfo["gender"].ToString();
                    userDetail[JsonKey.LOCALE] = userInfo["locale"].ToString();
                    userDetail[JsonKey.IS_VERIFIED] = userInfo["verified_email"].ToString() != null ? true : false;
                }
                catch (Exception)
                {
                    userDetail[JsonKey.GENDER] =     "";
                    userDetail[JsonKey.LOCALE] =     "";
                    userDetail[JsonKey.IS_VERIFIED] = "";
                }
                return userDetail;

            }
            catch (Exception ex)
            {
                Log.Error("linkedin get profile error= " + ex.Message);
                return new JObject();
            }
        }

        public async void StartLoginProcess(JObject json)
        {
            string id = json[JsonKey.ID].ToString();
            try
            {
                string clientID = AppSettings.AppProperties.GetValue("linkedinClientId").ToString();
                string callbackUrl = "http://localhost";
                string startUrl = "https://www.linkedin.com/uas/oauth2/authorization?";
                startUrl += "response_type=code&client_id=" + clientID +
                      "&scope=r_basicprofile r_emailaddress&state=STATE&redirect_uri=" + Uri.EscapeDataString(callbackUrl);

                Uri StartUri = new Uri(startUrl);
                // When using the desktop flow, the success code is displayed in the html title of this end uri
                Uri EndUri = new Uri(callbackUrl);

                Log.Info("Navigating to URL");

                WebAuthenticationResult WebAuthenticationResult = await WebAuthenticationBroker.AuthenticateAsync(WebAuthenticationOptions.None, StartUri, EndUri);
                if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.Success)
                {
                    JObject str = await GetInfo(WebAuthenticationResult.ResponseData.ToString());
                    Response.Success(id,str);
                    Log.Debug("Web Authenticatio Response recieved");
                }
                else if (WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.ErrorHttp)
                {
                    Response.Fail(id,ErrorCode.HTTP_FAIL);
                    Log.Error("HTTP Error returned by AuthenticateAsync()");
                }
                else
                {
                    Response.Fail(id,ErrorCode.COULD_NOT_LOGIN);
                    Log.Error("Error returned by AuthenticateAsync()");
                }
            }
            catch (Exception)
            {
                Response.Fail(id, ErrorCode.COULD_NOT_LOGIN);
                Log.Error("Error logging into linkedin account");
            }
        }
    }
}
