using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Plugins
{
    class PhoneSMS
    {
        public static async void SmsSend(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string type = obj[JsonKey.TYPE].ToString();
                string phoneNo = obj[JsonKey.PHONE_NUMBER].ToString();
                string text = obj[JsonKey.MESSAGE].ToString();
                var chatMessage = new Windows.ApplicationModel.Chat.ChatMessage();
                chatMessage.Body = text;
                chatMessage.Recipients.Add(phoneNo);
                if (type == "UI")
                {
                    await Windows.ApplicationModel.Chat.ChatMessageManager.ShowComposeSmsMessageAsync(chatMessage);
                    JObject j = new JObject();
                    j[JsonKey.SUCCESS_MESSAGE] = "Message Sent";
                    Response.Success(id, j);
                }
                else
                {
                    Response.Fail(id, ErrorCode.NO_SUPPORT_FOR_BG_MSG);
                    Log.Error("Background SMS not supported");
                }
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.COULD_NOT_SEND_SMS);
                Log.FatalError(e.Message);
            }
        }
    }
}
