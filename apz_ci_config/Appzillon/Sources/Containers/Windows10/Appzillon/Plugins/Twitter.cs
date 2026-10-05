using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Authentication.Web;
using Windows.Security.Cryptography;
using Windows.Web.Http;
namespace Appzillon.Plugins
{
    class Twitter
    {
#region Singleton Pattern
        private static Twitter instance;
        private Twitter()
        {
        }
        public static Twitter Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Twitter();
                }
                return instance;
            }
        }
#endregion
        private string twitterURL;
        private string id ="";
        public async void StartLoginProcess(JObject strJson)
        {
            id = strJson[JsonKey.ID].ToString();
            twitterURL = "https://api.twitter.com/oauth/request_token";
            string twitterclientid = AppSettings.AppProperties.GetValue("twitterClientId").ToString();
            string secretid =  AppSettings.AppProperties.GetValue("twitterSecretKey").ToString();
            string callbackURL = "http://localhost";
            double timestamp = Math.Round(DateTime.Now.Subtract(DateTime.MinValue.AddYears(1969)).TotalSeconds);
            var nonce = new Random().NextDouble();
            nonce = Math.Floor(nonce * 1000000000);
            var sigBaseStringParams = "oauth_callback=" + Uri.EscapeDataString(callbackURL);//+ EncodeURIComponent(callbackURL);
            sigBaseStringParams += "&" + "oauth_consumer_key=" + twitterclientid;
            sigBaseStringParams += "&" + "oauth_nonce=" + nonce;//  Math.random
            sigBaseStringParams += "&" + "oauth_signature_method=HMAC-SHA1";
            sigBaseStringParams += "&" + "oauth_timestamp=" + timestamp;
            sigBaseStringParams += "&" + "oauth_version=1.0";
            var sigBaseString = "POST&";
            sigBaseString += Uri.EscapeDataString(twitterURL) + "&" + Uri.EscapeDataString(sigBaseStringParams);
            var keyText = secretid + "&";
            var signature = getSignature(sigBaseString, keyText);

            var dataToPost = "oauth_callback=\"" + Uri.EscapeDataString(callbackURL)
          + "\", oauth_consumer_key=\"" + twitterclientid
          + "\", oauth_nonce=\"" + nonce
          + "\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"" + timestamp
          + "\", oauth_version=\"1.0\", oauth_signature=\"" + Uri.EscapeDataString(signature) + "\"";
            HttpStringContent httpContent = new HttpStringContent("", Windows.Storage.Streams.UnicodeEncoding.Utf8, "application/x-www-form-urlencoded");
            HttpClient client = new HttpClient();
            HttpResponseMessage response = null;
            client.DefaultRequestHeaders.Authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue("OAuth", dataToPost);
            try
            {
                 response = await client.PostAsync(new Uri(twitterURL),httpContent);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.HTTP_FAIL);
                Log.Error(e.Message);
                return;
            }
            twitterLogin(response.Content.ToString(), twitterclientid, secretid);
        }

        private async void twitterLogin(string response, string twitterclientid, string secretid)
        {
            string oauth_token = "";
            string oauth_token_secret = "";
            string callbackURL = "http://localhost";
            var keyValPairs = response.Split('&');

            for (var i = 0; i < keyValPairs.Length; i++)
            {
                var splits = keyValPairs[i].Split('=');
                switch (splits[0])
                {
                    case "oauth_token":
                        oauth_token = splits[1];
                        break;
                    case "oauth_token_secret":
                        oauth_token_secret = splits[1];
                        break;
                }
            }
            twitterURL = "https://api.twitter.com/oauth/authenticate?oauth_token=" + oauth_token + '&' + "include_email=true";
            var startURI = new Uri(twitterURL);
            var endURI = new Uri(callbackURL);
            try
            {
                WebAuthenticationResult war = await WebAuthenticationBroker.AuthenticateAsync(WebAuthenticationOptions.None, startURI, endURI);
                if (war.ResponseStatus == WebAuthenticationStatus.ErrorHttp)
                {
                    Response.Fail(id, ErrorCode.HTTP_FAIL);
                }
                else
                {
                    JObject userinfo = await GetInfo(war);
                }
            }
            catch (Exception e)
            {
                Log.Error(e.Message);
            }
        }

        private async Task<JObject> GetInfo(WebAuthenticationResult war)
        {
            string request_token = "";
            string oauth_verifier = "";
            string twitterclientid =  AppSettings.AppProperties.GetValue("twitterClientId").ToString();
            string secretid = AppSettings.AppProperties.GetValue("twitterSecretKey").ToString();
            string responseData = war.ResponseData.Substring(war.ResponseData.IndexOf("oauth_token"));//webAuthResultResponseData.substring(webAuthResultResponseData.indexOf("oauth_token"));
            var keyValPairs = responseData.Split('&');

            for (var i = 0; i < keyValPairs.Length; i++)
            {
                var splits = keyValPairs[i].Split('=');
                switch (splits[0])
                {
                    case "oauth_token":
                        request_token = splits[1];
                        break;
                    case "oauth_verifier":
                        oauth_verifier = splits[1];
                        break;
                }
            }

            var twitterURL = "https://api.twitter.com/oauth/access_token";
            var url = new Uri(twitterURL);
            double timestamp = Math.Round(DateTime.Now.Subtract(DateTime.MinValue.AddYears(1969)).TotalSeconds); 
            var nonce = new Random().NextDouble();
            nonce = Math.Floor(nonce * 1000000000);
            var sigBaseStringParams = "oauth_consumer_key=" + twitterclientid;
            sigBaseStringParams += "&" + "oauth_nonce=" + nonce;
            sigBaseStringParams += "&" + "oauth_signature_method=HMAC-SHA1";
            sigBaseStringParams += "&" + "oauth_timestamp=" + timestamp;
            sigBaseStringParams += "&" + "oauth_token=" + request_token;
            sigBaseStringParams += "&" + "oauth_version=1.0";
            var sigBaseString = "POST&";
            sigBaseString += Uri.EscapeDataString(twitterURL) + "&" + Uri.EscapeDataString(sigBaseStringParams);
            var keyText = secretid + "&";
            var signature = getSignature(sigBaseString, keyText);
            var authorizationHeaderParams = "OAuth oauth_consumer_key=\"" + twitterclientid
            + "\", oauth_nonce=\"" + nonce
                + "\", oauth_signature_method=\"HMAC-SHA1\", oauth_signature=\"" + Uri.EscapeDataString(signature)
                + "\", oauth_timestamp=\"" + timestamp
                + "\", oauth_token=\"" + Uri.EscapeDataString(request_token) + "\", oauth_version=\"1.0\"";

            try
            {
                var httpContent = new Windows.Web.Http.HttpStringContent("oauth_verifier=" + oauth_verifier + '&' + "include_email=true", Windows.Storage.Streams.UnicodeEncoding.Utf8, "application/x-www-form-urlencoded");
                var client = new Windows.Web.Http.HttpClient();
                client.DefaultRequestHeaders.Authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue("OAuth", authorizationHeaderParams);
                var msg = await client.PostAsync(new Uri(twitterURL), httpContent);
                GetAuthToken(msg.Content.ToString(), oauth_verifier, twitterclientid, secretid);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.HTTP_FAIL);
                Log.Error(e.Message);
            }
            return new JObject();
        }

        private async void GetAuthToken(string response, string oauth_verifier, string twitterclientid, string secretid)
        {
            string access_token = "";
            string oauth_token_secret = "";
            string screen_name = "";
            var keyValPairs = response.Split('&');

            for (var j = 0; j < keyValPairs.Length; j++)
            {
                var tokens = keyValPairs[j].Split('=');
                switch (tokens[0])
                {
                    case "oauth_token":
                        access_token = tokens[1];
                        break;
                    case "oauth_token_secret":
                        oauth_token_secret = tokens[1];
                        break;
                    case "screen_name":
                        screen_name = tokens[1];
                        break;
                }
            }

            try
            {
                var ntwitterURL = "https://api.twitter.com/1.1/account/verify_credentials.json";
                double timestamp = Math.Round(DateTime.Now.Subtract(DateTime.MinValue.AddYears(1969)).TotalSeconds);
                var nonce = new Random().NextDouble();
                nonce = Math.Floor(nonce * 1000000000);
                var nsigBaseStringParams = "include_email=" + true;
                nsigBaseStringParams += "&" + "oauth_consumer_key=" + twitterclientid;
                nsigBaseStringParams += "&" + "oauth_nonce=" + nonce;
                nsigBaseStringParams += "&" + "oauth_signature_method=HMAC-SHA1";
                nsigBaseStringParams += "&" + "oauth_timestamp=" + timestamp;
                nsigBaseStringParams += "&" + "oauth_token=" + access_token;
                nsigBaseStringParams += "&" + "oauth_version=1.0";
                var nsigBaseString = "GET&";
                nsigBaseString += Uri.EscapeDataString(ntwitterURL) + "&" + Uri.EscapeDataString(nsigBaseStringParams);

                //Calculate Signature
                var nkeyText = secretid + "&" + oauth_token_secret;
                var nsignature = getSignature(nsigBaseString, nkeyText);

                ntwitterURL += "?" + nsigBaseStringParams + "&oauth_signature=" + Uri.EscapeDataString(nsignature);
                var nauthorizationHeaderParams = "OAuth oauth_consumer_key=\"" + twitterclientid + "\", oauth_nonce=\"" + nonce + "\", oauth_signature=\"" + Uri.EscapeDataString(nsignature) + "\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"" + timestamp + "\", oauth_token=\"" + access_token + "\", oauth_version=\"1.0\"";
                var httpContent = new Windows.Web.Http.HttpStringContent("oauth_verifier=" + oauth_verifier + '&' + "include_email=true", Windows.Storage.Streams.UnicodeEncoding.Utf8, "application/json");
                var client = new Windows.Web.Http.HttpClient();
                client.DefaultRequestHeaders.Authorization = new Windows.Web.Http.Headers.HttpCredentialsHeaderValue("OAuth", nauthorizationHeaderParams);
                var details = await client.GetStringAsync(new Uri(ntwitterURL));
                var userInfo = JObject.Parse(details);
                var userDetail = new JObject();
                userDetail["twitId"] = userInfo[JsonKey.ID];
                userDetail[JsonKey.NAME] = userInfo[JsonKey.NAME];
                userDetail[JsonKey.TWITTER_NAME] = userInfo[JsonKey.SCREEN_NAME];
                userDetail[JsonKey.PICTURE_URL] = userInfo[JsonKey.PROFILE_IMAGE_URL];
                userDetail[JsonKey.EMAIL] = userInfo[JsonKey.EMAIL];
                Response.Success(id,userDetail);

            }

            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.COULD_NOT_LOGIN);
                Log.Error(e.Message);
            }
        }

        private string getSignature(string sigBaseString, string keyText)
        {
            var keyMaterial = CryptographicBuffer.ConvertStringToBinary(keyText, Windows.Security.Cryptography.BinaryStringEncoding.Utf8);
            var macAlgorithmProvider = Windows.Security.Cryptography.Core.MacAlgorithmProvider.OpenAlgorithm("HMAC_SHA1");
            var key = macAlgorithmProvider.CreateKey(keyMaterial);
            var tbs = CryptographicBuffer.ConvertStringToBinary(sigBaseString, Windows.Security.Cryptography.BinaryStringEncoding.Utf8);
            var signatureBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.Sign(key, tbs);
            string signature = CryptographicBuffer.EncodeToBase64String(signatureBuffer);
            return signature;
        }
    }
}
