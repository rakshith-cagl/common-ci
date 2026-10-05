using Newtonsoft.Json.Linq;
using System;
//--------Native Service----------------------
namespace Appzillon.Plugins
{
    public class NativeService
    {
        public static void NativeServiceEntry(JObject json, MainPage lobject)
        {
            try
            {
                json["param1"] = "Value1";
                json["param2"] = "Value2";
                json["param3"] = "Value3";
                json["param4"] = "Value4";
                json["param5"] = "Value5";


                //Use this function to pass parameters to Web View.
                //This method takes javascript callback function name and json string as parameter.
                NativeExt ext = new NativeExt();
                ext.GetDevStat(json);
                lobject.InvokeScript("javascript_function_name", json.ToString());
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message);
            }
        }
    }
}
