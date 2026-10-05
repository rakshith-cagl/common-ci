using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Media.Capture;
using Windows.Media.SpeechRecognition;

namespace Appzillon.Plugins
{
    class Voice
    {
        private bool access;
        private string id;

#region Singleton Pattern
        private static Voice instance;
        private Voice()
        {
        }
        public static Voice Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Voice();
                }
                return instance;
            }
        }
#endregion

        public async void VoiceSupport(JObject obj)
        {
            id = obj[JsonKey.ID].ToString();
            string resulttext = "not done.";
            try
            {
                MediaCaptureInitializationSettings initSettings = new MediaCaptureInitializationSettings();
                initSettings.StreamingCaptureMode = StreamingCaptureMode.Audio;
                initSettings.MediaCategory = MediaCategory.Speech;
                MediaCapture capture = new MediaCapture();
                await capture.InitializeAsync(initSettings);
                access = true;
            }
            catch (Exception)
            {
                access = false;
            }
            if (access)
            {
                try
                {
                    SpeechRecognizer voicebot = new SpeechRecognizer();
                    await voicebot.CompileConstraintsAsync();
                   // Log.Info("start speaking :");
                    voicebot.UIOptions.AudiblePrompt = "Speak what you want to do";
                    voicebot.UIOptions.ExampleText = "Logout";
                    voicebot.UIOptions.IsReadBackEnabled = true;
                    voicebot.UIOptions.ShowConfirmation = true;
                    SpeechRecognitionResult speech = await voicebot.RecognizeWithUIAsync();

                    if (speech.Status == SpeechRecognitionResultStatus.Success)
                    {
                        resulttext = speech.Text;
                        voicebot.Dispose();
                        JObject j = new JObject();
                        j["text"] = resulttext;
                        Response.Success(id, j);
                    }
                    else
                    {
                        Response.Fail(id, ErrorCode.SPEECH_RECOGNITION_FAILED);

                    }
                }
                catch (Exception e)
                {                    
                    try
                    {
                        Response.Fail(id, ErrorCode.SPEECH_RECOGNITION_FAILED);
                        Log.Error(e.Message);
                        if (e.HResult == unchecked((int)0x80045509))
                            Utils.Alert("You need to accept the speech privacy policy in order to use speech recognition in this app.");
                    }
                    catch (Exception) { }
                }
            }
            else
            {
                Response.Fail(id, ErrorCode.MICROPHONE_ACCESS_DENIED);
                Log.Error("User has denied the access");
            }
        }
    }
}
