using Appzillon.Native;
using Newtonsoft.Json.Linq;
namespace Appzillon.Plugins
{
    class AppIdleTimeOut
    {
        public static void Execute(JObject obj,MainPage lobject)
        {
            int time = int.Parse(AppSettings.AppProperties.GetValue("idleTimeOut").ToString());
            var js = new JObject();
            js["timeout"] = time;
            js["req"] = obj;
            lobject.InvokeScript("setIdleTimeout", js.ToString());
        }   
    }
}
