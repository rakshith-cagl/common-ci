using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Plugins
{
    class UrlLaunch
    {
        #region Singleton Pattern
        private static UrlLaunch instance;
        public UrlLaunch()
        {
        }
        public static UrlLaunch Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new UrlLaunch();
                }
                return instance;
            }
        }
        #endregion

        public async void Launch(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                bool valid = true;
                string add = obj[JsonKey.URL].ToString();
                Uri uri = null;
                string valadd = "";
                if (!add.Contains("https://") && !add.Contains("http://"))
                {
                    valadd = "http://" + add;
                    if (!Uri.TryCreate(valadd, UriKind.Absolute, out uri))
                    {
                        valid = false;
                        string svaladd = "https://" + add;
                        if (!Uri.TryCreate(svaladd, UriKind.Absolute, out uri))
                        {
                            valid = false;
                        }
                        else
                            valid = true;
                       
                    }

                }else
                {
                    uri = new Uri(add);
                }

                if (valid)
                {
                    bool x = await Windows.System.Launcher.LaunchUriAsync(uri);
                    if (x)
                    {
                        var j = new JObject();
                        j[JsonKey.SUCCESS_MESSAGE] = "Url launched successfully.";
                        Response.Success(id, j);
                    }
                    else
                    {
                        Response.Fail(id, ErrorCode.URL_OPEN_FAIL);
                    }
                }
                else
                    Response.Fail(id, ErrorCode.VALIDATE_URL_FAIL);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.URL_OPEN_FAIL);
                Log.Error(e.Message);
            }
        }
    }
}
