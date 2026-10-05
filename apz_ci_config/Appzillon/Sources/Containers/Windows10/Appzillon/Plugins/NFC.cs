using System;
using System.Linq;
using Newtonsoft.Json.Linq;
using Windows.Networking.Proximity;
using System.Runtime.InteropServices.WindowsRuntime;
using NdefLibrary.Ndef;
using System.Globalization;
using Appzillon.Native;
using Appzillon.Constants;

namespace Appzillon.Plugins
{
    public class NFC
    {
        ProximityDevice proximityDevice;
        long publishedMessageId = -1;
        long subscribeMessageId = -1;
        string id = "";
        string rcv_id = "";
        string st_id = "";
    
#region Singleton Pattern
        private static NFC instance;
        public NFC()
        {
            if (proximityDevice == null)
                proximityDevice = ProximityDevice.GetDefault();
        }
        public static NFC Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new NFC();
                }
                return instance;
            }
        }
#endregion

        internal void SendNFC(JObject o)
        {
            string action = o[JsonKey.ACTION] == null ? "null" : o[JsonKey.ACTION].ToString().ToUpper();
            string type = o[JsonKey.TYPE] == null ? "null" : o[JsonKey.TYPE].ToString().ToUpper();
            string str = o[JsonKey.CONTENT] == null ? "null" : o[JsonKey.CONTENT].ToString();
            if (proximityDevice != null)
            {
                switch (action)
                {
                    case "TAG":
                        if (type == "URL")
                            PublishURLToTag(str);
                        else
                            PublishMessageToTag(str);

                        break;
                    case "DEVICE":
                        if (type == "URL")
                            PublishURLToDevice(str);
                        else
                            PublishMessageToDevice(str);
                        break;
                }
            }
            else
            {
                Response.Fail(id,ErrorCode.NFC_NOT_SUPPORTED_BY_THE_DEVICE);
                Log.Error("Either NFC is not turned on or Device does not support NFC");
            }
        }
       internal void ReceiveNFC(JObject obj)
        {
            rcv_id = obj[JsonKey.ID].ToString();
            subscribeMessageId = proximityDevice.SubscribeForMessage("NDEF", NDEFHandler);
        }

        private void NDEFHandler(ProximityDevice sender, ProximityMessage message)
        {
            string s = "";
            //parse NdefMessage
            var ndefMessage = NdefMessage.FromByteArray(message.Data.ToArray());
            foreach (NdefRecord record in ndefMessage)
            {
                var specializedType = record.CheckSpecializedType(false);
                if (specializedType == typeof(NdefSpRecord))
                {
                    var spRecord = new NdefSpRecord(record);
                    //s += spRecord.Type.ToString() + "\n";
                    s += spRecord.Uri;
                }
                else if (specializedType == typeof(NdefUriRecord))
                {
                    var uriRecord = new NdefUriRecord(record);
                    // s += "Type: " + uriRecord.Type.ToString() + "\n";
                    s += uriRecord.Uri;
                }
                else if (specializedType == typeof(NdefTextRecord))
                {
                    var textRecord = new NdefTextRecord(record);
                    //  s += "Type: " + textRecord.Type.ToString() + " " + textRecord.LanguageCode + "\n";
                    s += textRecord.Text;
                }
                else
                {
                    //s += "Type: " + specializedType.ToString() + "\n";

                }
                //s += "\n\n";
            }

        //    proximityDevice.StopSubscribingForMessage(subscribeMessageId);
            var jsonstring = new JObject();
            jsonstring[JsonKey.SUCCESS_MESSAGE] = s;
            Response.Success(rcv_id,jsonstring);
        }

        internal void stopNFC(JObject obj)
        {
            st_id = obj[JsonKey.ID].ToString();
            int i = 0;
            try
            {
                proximityDevice.StopSubscribingForMessage(subscribeMessageId);
            }
            catch (Exception) {
                i++;
            }
            try
            {
                proximityDevice.StopPublishingMessage(publishedMessageId);
            } catch (Exception)
            {
                i++;
            }
            if (i == 2)
            {
                Response.Fail(st_id,ErrorCode.NFC_STOP_FAIL);
                Log.Error("Either NFC is not turned on or Device does not support NFC or NFC Access has been denied by user");
            }
            else
            {
                subscribeMessageId = -1;
                publishedMessageId = -1;
                var js = new JObject();
                js[JsonKey.SUCCESS_MESSAGE] = "nfc stopped successfully";
                Response.Success(st_id,js);
            }
        }


        private void PublishURLToTag(string writeURL)
        {
            using (Windows.Storage.Streams.DataWriter dataWriter = new Windows.Storage.Streams.DataWriter())
            {
                dataWriter.UnicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.Utf16LE;
                dataWriter.WriteString(writeURL);
                publishedMessageId = proximityDevice.PublishBinaryMessage("WindowsUri:WriteTag", dataWriter.DetachBuffer(),
                    PublishURLToTagHandler);
            }

        }
        void PublishURLToTagHandler(ProximityDevice d, long lid)
        {
            var jsonstring = new JObject();
            jsonstring[JsonKey.SUCCESS_MESSAGE] = "URL sent to tag successfully";
            Response.Success(id,jsonstring);
        }

        private void PublishMessageToTag(string msg)
        {

            string text = msg;
            var ndefRecord = new NdefTextRecord
            {
                Text = text,
                LanguageCode = CultureInfo.CurrentCulture.TwoLetterISOLanguageName
            };
            var ndefMessage = new NdefMessage { ndefRecord };
            //publish binary NDEF message.
            publishedMessageId = proximityDevice.PublishBinaryMessage("NDEF:WriteTag", ndefMessage.ToByteArray().AsBuffer(), publishHandler);
        }
        private void publishHandler(ProximityDevice sender, long messageId)
        {
            var jsonstring = new JObject();
            jsonstring[JsonKey.SUCCESS_MESSAGE] = "Message sent successfully";
            Response.Success(id,jsonstring);
        }


        private void PublishMessageToDevice(string msg)
        {

            using (Windows.Storage.Streams.DataWriter dataWriter = new Windows.Storage.Streams.DataWriter())
            {
                dataWriter.UnicodeEncoding = Windows.Storage.Streams.UnicodeEncoding.Utf8;
                dataWriter.WriteString(msg);

                publishedMessageId = proximityDevice.PublishBinaryMessage("Windows:WriteTag.Appzillon", dataWriter.DetachBuffer(),
                    PublishMessageToDeviceHandler);

            }

        }

        void PublishMessageToDeviceHandler(ProximityDevice sender, long messageId)
        {
            {
                // var jsonstring = "{\"successMessage\":\"Message sent to device successfully\"}";
                var js = new JObject();
                js[JsonKey.SUCCESS_MESSAGE] = "Message sent to device successfully";
                Response.Success(id,js);
                // stopPublishing();
            }
        }

        private void PublishURLToDevice(string url)
        {

            if (proximityDevice != null)
            {
                // Stop publishing the current message.
                if (publishedMessageId != -1)
                {
                    proximityDevice.StopPublishingMessage(publishedMessageId);
                }
                // Publish the new URL
                try
                {
                    Uri u = new Uri(url);
                    publishedMessageId = proximityDevice.PublishUriMessage(u);//"ms-settings-bluetooh:"
                }
                catch (Exception e)
                {
                   // string json = "{\"errorCode\":\"APZ-CNT-131\"}";
                    Response.Fail(id,ErrorCode.NFC_SEND_FAIL);
                    Log.Error(e.Message);
                }
                //publishedMessageId = proximityDevice.PublishUriMessage(new Uri(url));//"ms-settings-bluetooh:"
            }

        }

        private void stopPublishing()
        {
            if (publishedMessageId != -1)
            {
                proximityDevice.StopPublishingMessage(publishedMessageId);
            }

        }
    }
}
