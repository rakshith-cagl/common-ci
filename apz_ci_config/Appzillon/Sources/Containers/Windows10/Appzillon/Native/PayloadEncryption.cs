using Appzillon.CommonClass;
using Appzillon.Constants;
using Appzillon.Plugins;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Native
{
   public static class PayloadEncryption
    {
        internal static async Task<string> GetPayloadEncryptedValue(string headerrequest, string bodyrequest, string key)
        {
            string aeskey = AESCommon.Instance.GetPaddingKey(key);
            AesBase64Wrapper aes = new AesBase64Wrapper(aeskey);
            string encryptedheaderrequest = await AESCommon.Instance.GetAESEncryptedValue(headerrequest, aeskey);
            string encryptedbodyrequest = await AESCommon.Instance.GetAESEncryptedValue(bodyrequest, aeskey);

            StringBuilder stringToRead = new StringBuilder();
            stringToRead.AppendLine("{" + "\n" + "\"" + JsonKey.APPZILLON_HEADER + "\"" + ":" + "\"" + encryptedheaderrequest + "\"" + ",");
            stringToRead.AppendLine("\"" + JsonKey.APPZILLON_BODY + "\"" + ":" + "\"" + encryptedbodyrequest + "\"" + "}");
            string payloadstring = await CommonMethods.GetStringValue(stringToRead.ToString());
            string apzlionsafe = await GetRSAEncrypt(aeskey);
            string finalpayloadstring = payloadstring.Remove(0, 1);
            string JsonappzlionSafe = "{\"" + JsonKey.APPZILLON_SAFE + "\":" + "\"" + apzlionsafe + "\"" + "," + "\n" + finalpayloadstring;
            return JsonappzlionSafe;
        }



        internal static async Task<string> GetRSAEncrypt(string key)
        {
            return await RSAEncrypyt.Instance.RSAkey(key);
        }
        internal static async Task<string> GetRSADecrypt(string encrypytedkey)
        {
            return await RSADecrypyt.Instance.decrypt(encrypytedkey);
        }
        internal static async Task<string> PayloadDecrypyt(string encryptedresponse, string key)
        {
            AppzlionResponse.appzillonSafe = string.Empty;
            AppzlionResponse.appzillonHeader = string.Empty;
            AppzlionResponse.appzillonBody = string.Empty;
            string decryptedresponse = string.Empty;
            JObject jobjectresponse = JObject.Parse(encryptedresponse);
            if (encryptedresponse.Contains("appzillonErrors"))
            {
                if (GenericUtils.isfirstrequest)
                {
                    GenericUtils.isfirstrequest = false;
                    string errorresponsee = (string)(jobjectresponse["appzillonErrors"]);
                    string errorresponse = await AESCommon.Instance.GetAESDecryptedValue(errorresponsee, AppzlionResponse.appzillonSafe);
                    decryptedresponse = errorresponse;
                }
                else
                {
                    string errorresponsee = (string)(jobjectresponse["appzillonErrors"]);
                    AppzlionResponse.appzillonErrors = await AESCommon.Instance.GetAESDecryptedValue(errorresponsee, key);
                    AppzlionResponse.appzillonQop = (string)(jobjectresponse[JsonKey.APPZILLON_QOP]);
                    string appzillonHeader = (string)(jobjectresponse[JsonKey.APPZILLON_HEADER]);
                    AppzlionResponse.appzillonHeader = await AESCommon.Instance.GetAESDecryptedValue(appzillonHeader, key);
                    string appzillonBody = (string)(jobjectresponse[JsonKey.APPZILLON_BODY]);
                    AppzlionResponse.appzillonBody = await AESCommon.Instance.GetAESDecryptedValue(appzillonBody, key);
                    decryptedresponse = await CommonMethods.FinalResponseJSON(AppzlionResponse.appzillonHeader, AppzlionResponse.appzillonBody, key, AppzlionResponse.appzillonQop, AppzlionResponse.appzillonErrors);

                }
            }
            else
            {
                if (!encryptedresponse.Contains(JsonKey.APPZILLON_QOP))
                {
                    string appzillonSafe = (string)(jobjectresponse[JsonKey.APPZILLON_SAFE]);
                    AppzlionResponse.appzillonSafe = await GetRSADecrypt(appzillonSafe);
                    AppzlionResponse.appzillonSafe = AESCommon.Instance.GetPaddingKey(AppzlionResponse.appzillonSafe);
                    string appzillonHeader = (string)(jobjectresponse[JsonKey.APPZILLON_HEADER]);
                    AppzlionResponse.appzillonHeader = await AESCommon.Instance.GetAESDecryptedValue(appzillonHeader, AppzlionResponse.appzillonSafe);
                    string appzillonBody = (string)(jobjectresponse[JsonKey.APPZILLON_BODY]);
                    AppzlionResponse.appzillonBody = await AESCommon.Instance.GetAESDecryptedValue(appzillonBody, AppzlionResponse.appzillonSafe);
                    decryptedresponse = await CommonMethods. FinalResponseJSON(AppzlionResponse.appzillonHeader, AppzlionResponse.appzillonBody, AppzlionResponse.appzillonSafe, AppzlionResponse.appzillonQop = null, null);

                }
                else if (encryptedresponse.Contains(JsonKey.APPZILLON_QOP))
                {
                    AppzlionResponse.appzillonQop = (string)(jobjectresponse[JsonKey.APPZILLON_QOP]);

                    string appzillonSafe = (string)(jobjectresponse[JsonKey.APPZILLON_SAFE]);
                    AppzlionResponse.appzillonSafe = await GetRSADecrypt(appzillonSafe);

                    string appzillonHeader = (string)(jobjectresponse[JsonKey.APPZILLON_HEADER]);
                    AppzlionResponse.appzillonHeader = await AESCommon.Instance.GetAESDecryptedValue(appzillonHeader, key);
                    string appzillonBody = (string)(jobjectresponse[JsonKey.APPZILLON_BODY]);
                    AppzlionResponse.appzillonBody = await AESCommon.Instance.GetAESDecryptedValue(appzillonBody, key);
                    decryptedresponse = await CommonMethods.FinalResponseJSON(AppzlionResponse.appzillonHeader, AppzlionResponse.appzillonBody, GenericUtils.safetoken, AppzlionResponse.appzillonQop, null);

                }
            }
            return decryptedresponse;
        }
    }
}
