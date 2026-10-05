using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;
using Appzillon.Constants;
using Appzillon.Native;
using Windows.System;

namespace Appzillon.Plugins
{
    class DeepLinking
    {
        #region Singleton Pattern
        private static DeepLinking instance;
        private DeepLinking()
        {
        }
        public static DeepLinking Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new DeepLinking();
                }
                return instance;
            }
        }

        internal async void OpenApp(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string protocol = obj["packageName"].ToString()+":";
                Uri app = new Uri(protocol);
                bool x = await Launcher.LaunchUriAsync(app);
                    if (x)
                    {
                        var j = new JObject();
                        j[JsonKey.SUCCESS_MESSAGE] = "app launched successfully.";
                        Response.Success(id, j);
                    }
                    else
                    {
                        Response.Fail(id, ErrorCode.APP_OPEN_FAIL);
                    }
               
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.APP_OPEN_FAIL);
                Log.Error(e.Message);
            }
        }
        #endregion

    }
}
