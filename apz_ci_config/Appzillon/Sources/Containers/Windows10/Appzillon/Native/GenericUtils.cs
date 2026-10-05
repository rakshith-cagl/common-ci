using Appzillon.CommonClass;
using Appzillon.Constants;
using Appzillon.Plugins;
using Newtonsoft.Json.Linq;
using System;
using System.IO;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Native
{
 /// <summary>
 /// Author :Anand Kumar 
 ///   Genric Class to Call Server method through encryption  and payload.
 ///  Encryption & Decryption fo RSA ,AES, HASh256 
 /// </summary>
     public class GenericUtils
    {
    
        internal static string safetoken = string.Empty;
        internal static bool isfirstrequest = false;

        public static async Task<string> SendRequestAsync(string url, string data)
        {
            try
            {
                string requeststring = string.Empty;
                string CNonce = string.Empty;
                string aeskey = string.Empty;
                using (var client = new HttpClient())
                {

                    JObject _jobjectInterfaceid = JObject.Parse(data);
                  
                    string _interfaceid = (string)_jobjectInterfaceid[JsonKey.APPZILLON_HEADER][JsonKey.INTERFACE_ID];

                    if (_interfaceid != JsonKey.APPZLION_GET_APP_SEC_TOKEN)
                    {
                      //  isfirstrequest = false;
                        JObject _jobjectDeviceRegReq = JObject.Parse(data);
                        _jobjectDeviceRegReq[JsonKey.APPZILLON_HEADER][JsonKey.SERVERNONCE] = AppzillonGetAppSecTokensResponse.serverNonce;
                        _jobjectDeviceRegReq[JsonKey.APPZILLON_HEADER][JsonKey.SAFETOKEN] = AppzillonGetAppSecTokensResponse.safeToken;
                        _jobjectDeviceRegReq[JsonKey.APPZILLON_HEADER][JsonKey.SESSIONTOKEN] = AppzillonGetAppSecTokensResponse.sessionToken;
                        _jobjectDeviceRegReq[JsonKey.APPZILLON_HEADER]["clientNonce"] = Utils.CurrentTimeMillis().ToString();

                        string  headerrequest = _jobjectDeviceRegReq[JsonKey.APPZILLON_HEADER].ToString();
                        string  bodyrequest = _jobjectDeviceRegReq[JsonKey.APPZILLON_BODY].ToString();
                        JObject Jsondata = JObject.Parse(_jobjectDeviceRegReq.ToString());
                        JToken Jtokenheader = Jsondata[JsonKey.APPZILLON_HEADER];
                        JObject Jobjheader = JObject.Parse(Jtokenheader.ToString());
                        CNonce = (string)Jobjheader["clientNonce"];
                        safetoken = AppzillonGetAppSecTokensResponse.safeToken;
                        aeskey = safetoken;

                        if (AppProperty.payloadEncryption == "Y" && AppProperty.dataIntegrity == "Y")
                        {
                              string payloadEncryptionstring = await PayloadEncryption. GetPayloadEncryptedValue(headerrequest, bodyrequest, safetoken);
                            string finalpayloadString = payloadEncryptionstring.Remove(0, 1);
                            string payloadhasing = DataIntegrity .GetDataIntegrityValue(headerrequest, bodyrequest, CNonce);
                            string JsonappzlionQOPS = "{\"" + "appzillonQop" + "\":" + "\"" + payloadhasing + "\"" + "," + "\n" + finalpayloadString;
                            requeststring = JsonappzlionQOPS;

                        }
                        else if (AppProperty.payloadEncryption == "Y" && AppProperty.dataIntegrity == "N")
                        {
                            string payloadEncryptionstring = await PayloadEncryption.GetPayloadEncryptedValue(headerrequest, bodyrequest, safetoken);
                            requeststring = payloadEncryptionstring;
                        }
                        else if (AppProperty.payloadEncryption == "N" && AppProperty.dataIntegrity == "Y")
                        {
                            {
                                string payloadhasing =  DataIntegrity.GetDataIntegrityValue(headerrequest, bodyrequest, CNonce);
                                string apzlionstring = await CommonMethods.GetAppzlionString(headerrequest,bodyrequest);
                                string finalpayloadstring = apzlionstring.Remove(0, 2);

                                string JsonappzlionQOPS = "{\"" + "appzillonQop" + "\":" + "\"" + payloadhasing + "\"" + "," + "\n" + finalpayloadstring;
                                requeststring = JsonappzlionQOPS;
                            }
                        }

                        else if(AppProperty.payloadEncryption == "N" && AppProperty.dataIntegrity == "N")
                        {
                            string apzlionstring = await CommonMethods.GetAppzlionString(headerrequest, bodyrequest);
                            requeststring = apzlionstring;
                        }
                    }
                   
                    else
                    {
                        AppProperty.payloadEncryption = AppSettings.AppProperties["payloadEncryption"].ToString();
               		    AppProperty.dataIntegrity = AppSettings.AppProperties["dataIntegrity"].ToString();
                	    AppProperty.serverToken = AppSettings.AppProperties["serverToken"].ToString();
               		    string padservertokenkey = AESCommon.Instance.GetPaddingKey(Utils.ServerTokenkey);
                	    AesBase64Wrapper aes = new AesBase64Wrapper(padservertokenkey);
                        AppProperty.decryptredserverToken = await AESCommon.Instance.GetAESDecryptedValue(AppProperty.serverToken, padservertokenkey);
                	 
                        isfirstrequest = true;
                        JObject _jobjectReq = JObject.Parse(data);
                        string headerrequest = _jobjectReq[JsonKey.APPZILLON_HEADER].ToString();
                        string bodyrequest = _jobjectReq[JsonKey.APPZILLON_BODY].ToString();
                        if (AppProperty.payloadEncryption == "Y")
                        {
                       
                            aeskey = AESCommon.Instance.GetRandomNumber().ToString();
                            requeststring = await PayloadEncryption.GetPayloadEncryptedValue(headerrequest, bodyrequest, aeskey);
                        }
                        else
                        {
                            string apzlionstring = await CommonMethods.GetAppzlionString(headerrequest, bodyrequest);
                            requeststring = apzlionstring;
                        }
                    
                    }
                        JObject requestjobject = JObject.Parse(requeststring);
                        HttpContent content = new StringContent(requeststring, Encoding.UTF8, "application/json");
                        var response = await client.PostAsync(url, content);
                        var responseString = await response.Content.ReadAsStringAsync();
                    
                    string completeresponse = string.Empty;
                    if (AppProperty.payloadEncryption == "Y")
                    {
                        string appzlionbodyresponse = await PayloadEncryption.PayloadDecrypyt(responseString, aeskey);
                        completeresponse = appzlionbodyresponse;
                    }
                    else
                    {
                        completeresponse = responseString;
                    }
                   // Debug.WriteLine("ResponseString" + appzlionbodyresponse);
                  
                    return completeresponse;
                }
            }
            catch (Exception ex)
            {
                Log.Debug(ex.Message);
                return null;
            }
            
        }

       
      
  
     }
}
