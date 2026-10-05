using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.System;

namespace Appzillon.Plugins
{
    class SkypeCall
    {
        #region Singleton Pattern
        private static SkypeCall instance;
        private SkypeCall()
        {
        }
        public static SkypeCall Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new SkypeCall();
                }
                return instance;
            }
        }
        #endregion

        public async void StartSkypeCall(JObject obj)
        {
            var id = obj[JsonKey.ID].ToString();
            string users = null;
            try
            {
                // var userid = obj[JsonKey.USER_ID].ToString();
                dynamic userarray = obj;
                var countvar = userarray.userId;
                int count = countvar.Count;
                users = countvar[0];
                if (count > 1)
                {
                    for (var i = 1; i <= (count - 1); i++)
                    {
                        string userarr = (string)countvar[i];
                        users = users + ";" + userarr;
                    }
                }

                string userid = users;


                string type = obj[JsonKey.TYPE].ToString();
                if (type == "chat")
                    await Launcher.LaunchUriAsync(new Uri("skype:" + userid + "?chat"));
                if (type == "call")
                    await Launcher.LaunchUriAsync(new Uri("skype:" + userid + "?call"));
                if (type == "video")
                    await Launcher.LaunchUriAsync(new Uri("skype:" + userid + "?call&video=true"));

                Response.Success(id, false);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.SKYPE_FAIL);
                Log.Error(e.Message);
            }

        }
    }
}
