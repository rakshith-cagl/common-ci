using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;
using Windows.System;
using Windows.UI.Xaml.Controls;

namespace Appzillon.Plugins
{
    class VideoPlayer
    {
#region Singleton Pattern
        private static VideoPlayer instance;
        private VideoPlayer()
        {
        }
        public static VideoPlayer Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new VideoPlayer();
                }
                return instance;
            }
        }
#endregion
        public async void StartVideoPlay(JObject obj, MainPage lobject)
        {
            string id = obj[JsonKey.ID].ToString();
            string filename = obj[JsonKey.FILE_NAME].ToString();
                /*  MediaElement vPlayer = new MediaElement();                                          // For Custom VideoPlayer Implementation
                vPlayer.Visibility = Windows.UI.Xaml.Visibility.Visible;                             
            WebView wb = (WebView)lobject.FindName("wbAppzillon");
                Grid gr = (Grid)lobject.FindName("MainGrid");
                wb.Visibility = Windows.UI.Xaml.Visibility.Collapsed;
                gr.Children.Add(vPlayer);
                StorageFolder fold = ApplicationData.Current.LocalFolder;
                string path = fold.Path + "\\vid.mp4";
                StorageFile file = await StorageFile.GetFileFromPathAsync(path);
                var stream = await file.OpenAsync(Windows.Storage.FileAccessMode.Read);
                vPlayer.SetSource(stream, file.ContentType);
                vPlayer.MediaFailed += VPlayer_MediaFailed;
                vPlayer.PartialMediaFailureDetected += VPlayer_PartialMediaFailureDetected;
                vPlayer.RealTimePlayback = true;
                vPlayer.Play();*/
            try
            {
                StorageFolder fold = ApplicationData.Current.LocalFolder;
                string path = fold.Path +"\\"+ filename;
                StorageFile file = await StorageFile.GetFileFromPathAsync(path);
                await Launcher.LaunchFileAsync(file);
                Response.Success(id, false);
            }
            catch (Exception)
            {
                Response.Fail(id,ErrorCode.VIDEO_PLAY_OPERATION_FAILED);
            }
        }
    }
}