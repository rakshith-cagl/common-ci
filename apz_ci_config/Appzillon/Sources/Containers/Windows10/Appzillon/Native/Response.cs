using System;
using Appzillon.Constants;
using Newtonsoft.Json.Linq;

namespace Appzillon.Native
{
    class Response
    {
        #region CallBack Logic

        internal const string SCRIPT_CALLBACK_NAME = "Apz.nativeServiceCB";

        internal static Action<string, string> CallBack = null;
        #region Success Overloaded method
        internal static void Success(string id, bool keepAlive)
        {
            Success(id, true, keepAlive, new JObject());
        }

        internal static void Success(string id, JObject body)
        {
            Success(id, true, false, body);
        }

        internal static void Success(string id, bool keepAlive, JObject body)
        {
            Success(id, true, keepAlive, body);
        }
        #endregion
        internal static void Success(string id, bool status, bool keepAlive, JObject response)
        {
            if (response == null)
                response = new JObject();           
            response[JsonKey.ID] = id;
            response[JsonKey.STATUS] = status;
            response[JsonKey.KEEP_ALIVE] = keepAlive;
            CallBack(SCRIPT_CALLBACK_NAME, response.ToString());
        }
        #region Fail Overloaded method
        internal static void Fail(string id, string code, string errorMessage)
        {
            Fail(id, code);
        }
        internal static void Fail(JObject response)
        {
            response[JsonKey.STATUS] = false;
            CallBack(SCRIPT_CALLBACK_NAME, response.ToString());
        }
        #endregion
        internal static void Fail(string id, string code)
        {
            JObject response = new JObject();
            response[JsonKey.ID] = id;
            response[JsonKey.STATUS] = false;
            //response[JSON_KEY.ERROR_CODE] = code;

            //response[JsonKey.BODY] = new JObject();
            response[JsonKey.ERROR_CODE] = code;

            CallBack(SCRIPT_CALLBACK_NAME, response.ToString());
        }
        
       
        #endregion
    }
}
