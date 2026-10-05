using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel.Calls;
using Windows.Media.Devices;

namespace Appzillon.Plugins
{
    class PhoneCall
    {
        public static string id = null;
        public static async void Call(JObject phoneJson)
        {
            string id = phoneJson[JsonKey.ID].ToString();
            string phone = phoneJson["phoneNo"].ToString();
            //PhoneCallManager.ShowPhoneCallUI(phone, "Xyz Abc");
            if (phone.Length > 0)
            {
                try
                {
                    PhoneCallStore store = await PhoneCallManager.RequestStoreAsync();
                    Guid gid = await store.GetDefaultLineAsync();
                    //Phone Line validation
                    PhoneLine line = await PhoneLine.FromIdAsync(gid);
                    bool registered = true;
                    try
                    {
                        var callctrl = CallControl.FromId(gid.ToString());
                        callctrl.HangUpRequested += Callctrl_HangUpRequested;
                    }
                    catch (Exception)
                    {
                        registered = false;
                    }
                    line.Dial(phone, "");
                    if (!registered)
                    {
                        var js = new JObject();
                        js["successMsg"] = "phone call success";
                        Response.Success(id, false);
                        id = null;
                    }
                }
                catch (Exception E)
                {
                    Response.Fail(id,ErrorCode.COULD_NOT_PLACE_THE_CALL_REQUESTED);
                    Log.Error("Could not place the call requesteds");
                }
            }

            else
            {
                Response.Fail(id, ErrorCode.COULD_NOT_PLACE_THE_CALL_REQUESTED);
                Log.Error("Could not place the call requesteds");
            }
        }

        private static void Callctrl_HangUpRequested(CallControl sender)
        {
            if(id != null)
            Response.Success(id,false);
        }
    }
}
