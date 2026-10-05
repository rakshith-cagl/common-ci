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
    class Mail
    {
        public static void invokeEMail(JObject mail)
        {
            string id = mail[JsonKey.ID].ToString();
            string recipientMailId = mail[JsonKey.RECIPIENT_MAIL_ID].ToString();
            string senderMailId = mail[JsonKey.ID].ToString();
            string ccIdList = mail[JsonKey.CCID_LIST].ToString();
            string internalMail = mail[JsonKey.INTERNAL].ToString();
            string subject = mail[JsonKey.SUBJECT].ToString();
            string body = mail[JsonKey.BODY].ToString();
          
            try
            {
                InitiateMail(senderMailId, recipientMailId, ccIdList.Replace(',', ';'), subject, body);
                JObject success = new JObject();
                success[JsonKey.SUCCESS_MESSAGE] = "Mail sent successfully";
                Response.Success(id,success);
            }

            catch (Exception e)
            {
              //  JObject failure = new JObject();
                //failure["errorDescription"] = "APZ-CNT-107";
                Response.Fail(id, ErrorCode.MAIL_SENDING_FAILED);
                Log.Error("Mail, Error "+e.Message);
            }
        }

        public static async void InitiateMail(string id, string recipientMailId, string ccIdList, string subject, string body)
        {
            var mailto = new System.Uri("mailto:" + recipientMailId + "?subject=" + subject + "&body=" + body + "&cc=" + ccIdList);
            var options = new Windows.System.LauncherOptions();
            options.DisplayApplicationPicker = true;
            await Windows.System.Launcher.LaunchUriAsync(mailto, options);
        }
    }
}
