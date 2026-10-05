using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.System;

namespace Appzillon.Plugins
{
    class Whatsapp
    {
        internal static async void InitWhatsapp(JObject obj)
        {
            string text = "";
            bool opened = false;
            string id = obj[JsonKey.ID].ToString();
            try
            {
                text = obj[JsonKey.MESSAGE].ToString();
            }
            catch (Exception)
            {

            }
            if (string.IsNullOrEmpty(text))
            {
                string uri = "whatsapp://app";
               opened = await Launcher.LaunchUriAsync(new Uri(uri));
            }
            else
            {
                string uri = "whatsapp://send?text="+text;
               opened = await Launcher.LaunchUriAsync(new Uri(uri));
            }
            if (opened)
            {
                Response.Success(id, false);
            }
            else
            {
                Response.Fail(id,ErrorCode.WHATSAPP_OPEN_FAIL);
            }
        }
    }
}
