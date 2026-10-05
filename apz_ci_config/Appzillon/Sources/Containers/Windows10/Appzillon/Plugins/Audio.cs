using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Media.Capture;
using Windows.Media.Devices;
using Windows.Media.MediaProperties;
using Windows.Storage;
using Windows.Storage.Pickers;
using Windows.Storage.Streams;
using Windows.UI.Core;
using Windows.UI.Xaml.Controls;

namespace Appzillon.Plugins
{
    class Audio
    {
        private MainPage ect;
        private MediaCapture cap;
        private  bool isrecording;
        private  bool isrecordingpaused;
        private  bool isplaying;
        private  string filename;
        private InMemoryRandomAccessStream str;
        private string b64 = null;
        private  bool issaved;
        private  string location = "default";
        private  MediaElement PlayMusic;
        private string id = "";

#region Singleton Pattern
        private static Audio instance;
        private Audio()
        {
        }
        public static Audio Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Audio();
                }
                return instance;
            }
        }
#endregion

        public void AudioPlug(JObject obj,MainPage lobject)
        {
            ect = lobject;
            id = obj["id"].ToString();
            string action = obj[JsonKey.ACTION].ToString();
            try
            {

                filename = obj[JsonKey.FILE_NAME].ToString();
                location = obj[JsonKey.LOCATION].ToString();
            }
            catch (Exception ex)
            {
                Log.Warning(ex.Message);
            }
            try
            {
                b64 = obj[JsonKey.BASE64].ToString();
            }
            catch (Exception ec)
            {
                Log.Warning(ec.Message);
            }
            switch (action)
            {
                case "play":
                    if (isrecording)
                    {

                        Response.Fail(id,ErrorCode.CANT_PALY_WHILE_RECORDING);
                        Log.Error("Can't Play Audio While Recoding is in progress");
                        return;
                    }
                    PlayAudio();
                    break;
                case "record":
                    if (isrecordingpaused)
                    {
                        ResumeRecording();
                        return;
                    }
                    Recorder();
                    break;
                case "save":
                    if (issaved)
                    {
                        Response.Fail(id,ErrorCode.COULD_NOT_SAVE);
                        Log.Error("already saved press record to start a new recording");
                        return;
                    }
                    SaveAudio();
                    break;
                case "pause":
                    if (isrecording)
                    {
                        PauseRecording();
                    }
                    else if (isplaying)
                    {
                        PausePlaying();
                    }
                    else
                    {
                        Response.Fail(id,ErrorCode.OPERATION_NOT_POSSIBLE_IN_CURRENT_STATE);
                        Log.Error("Pause Is Not Valid Since There Is Neither Recording Nor Play Is Action Is Currently In Progress");
                    }
                    break;

            }
        }

        private async void ResumeRecording()
        {
            try
            {
                await cap.ResumeRecordAsync();
                isrecording = true;
                isrecordingpaused = false;
                JObject j = new JObject();
                j[JsonKey.SUCCESS_MESSAGE] = "Audio recording resumed" + str.Size;
                Response.Success(id,j);
            }
            catch (Exception e)
            {
                await cap.StopRecordAsync();
                Response.Fail(id,ErrorCode.RECORDING_FAIL);
                Log.FatalError(e.Message);
            }
        }

        private async void SaveAudio()
        {
            isrecordingpaused = false;
            StorageFolder fold = null;
            try
            {
                await cap.StopRecordAsync();
            }
            catch (Exception e)
            {
                Log.Info(e.Message);
            }

            if (str != null)
            {
                if (location == "default")
                {
                    StorageFolder local = ApplicationData.Current.LocalFolder;
                    string path = local.Path + @"\appzillonapps\apps\" + MainPage.CurrentAppId + @"\Audio";

                    if (!Directory.Exists(path))
                    {
                        DirectoryInfo f1 = Directory.CreateDirectory(path);
                        string fo = f1.FullName;
                        fold = await StorageFolder.GetFolderFromPathAsync(fo);
                    }
                    else
                    {
                        fold = await StorageFolder.GetFolderFromPathAsync(path);
                    }
                }
                else
                {
                    StorageFolder music = KnownFolders.MusicLibrary;
                    fold = music;
                }
                if (fold != null)
                {
                    Stream read = str.AsStream();
                    StorageFile fi = await fold.CreateFileAsync(filename + ".mp3", CreationCollisionOption.ReplaceExisting);
                    //FileStream write = new FileStream(fi.Path, FileMode.Open, FileAccess.Write);
                    // Writer(read, write);
                    if (fi != null)
                    {
                        using (var dataReader = new DataReader(str.GetInputStreamAt(0)))
                        {
                            await dataReader.LoadAsync((uint)str.Size);
                            byte[] buffer = new byte[(int)str.Size];
                            dataReader.ReadBytes(buffer);
                            await FileIO.WriteBytesAsync(fi, buffer);
                        }
                        str.Dispose();
                    }
                }
            }
            else
            {
                Response.Fail(id,ErrorCode.UNABLE_TO_SAVE_RECORD);
                return;
            }
            issaved = true;
            JObject j = new JObject();
            j[JsonKey.SUCCESS_MESSAGE] = "Audio recording saved";
            Response.Success(id,j);
        }

        private void PausePlaying()
        {
            try
            {
                PlayMusic.Pause();
                try
                {
                    Grid gr = (Grid)ect.FindName("MainGrid");
                    gr.Children.Remove(PlayMusic);
                }
                catch (Exception)
                {
                    //safe execution
                }
                JObject j = new JObject();
                j[JsonKey.SUCCESS_MESSAGE] = "Audio play paused";
                Response.Success(id,j);
            }
            catch (Exception)
            {
                Response.Fail(id,ErrorCode.PAUSE_FAIL);
            }

        }

        private async void PauseRecording()
        {
            try
            {
                await cap.PauseRecordAsync(MediaCapturePauseBehavior.RetainHardwareResources);
                isrecordingpaused = true;
                isrecording = false;
                JObject j = new JObject();
                j[JsonKey.SUCCESS_MESSAGE] = "Audio recording paused";
                Response.Success(id,j);
            }
            catch (Exception)
            {
                Response.Fail(id, ErrorCode.PAUSE_FAIL);
            }
        }

        private async void Recorder()
        {
            try
            {
                isplaying = false;
                issaved = false;
                cap = new MediaCapture();
                var settings = new MediaCaptureInitializationSettings();
                settings.StreamingCaptureMode = StreamingCaptureMode.Audio;
                await cap.InitializeAsync(settings);
                cap.Failed += OnFailed;
                cap.RecordLimitationExceeded += OnLimitExceed;
                var encodingProfile = MediaEncodingProfile.CreateMp3(AudioEncodingQuality.Auto);
                 var file = await  ApplicationData.Current.LocalFolder.CreateFileAsync("audio",CreationCollisionOption.ReplaceExisting);
                var st =await file.OpenAsync(FileAccessMode.ReadWrite);
                str = new InMemoryRandomAccessStream();
                await cap.StartRecordToStreamAsync(encodingProfile,str);
                isrecording = true;
                JObject j = new JObject();
                j[JsonKey.SUCCESS_MESSAGE] = "Audio record started";
                Response.Success(id, j);
            }catch(Exception e)
            {
                Response.Fail(id, ErrorCode.RECORDING_FAIL);
                Log.Error(e.Message);
            }
        }

        private async void OnLimitExceed(MediaCapture sender)
        {
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                sender.Dispose();
                Response.Fail(id, ErrorCode.LIMIT_EXCEEDED);
                Log.FatalError("Limit Exceeded");
            });
        }

        private async void OnFailed(MediaCapture sender, MediaCaptureFailedEventArgs errorEventArgs)
        {
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                JObject j = new JObject();
                Response.Fail(id,ErrorCode.AUDIO_FAIL);
                Log.FatalError("Audio Process Fail");
            });
        }

        private async void PlayAudio()
        {
            try
            {
                FileOpenPicker picker = new FileOpenPicker();
                picker.ViewMode = PickerViewMode.Thumbnail;
                picker.FileTypeFilter.Add(".mp3");
                picker.FileTypeFilter.Add(".amr");
                picker.FileTypeFilter.Add(".wav");
                picker.SuggestedStartLocation = PickerLocationId.MusicLibrary;
                StorageFile file = await picker.PickSingleFileAsync();
                var options = new Windows.System.LauncherOptions();
                //isplaying = await Windows.System.Launcher.LaunchFileAsync(file, options);
                PlayMusic = new MediaElement();
                PlayMusic.AudioCategory = Windows.UI.Xaml.Media.AudioCategory.Media;
                Grid gr = (Grid)ect.FindName("MainGrid");
                gr.Children.Add(PlayMusic);
                var stream = await file.OpenAsync(Windows.Storage.FileAccessMode.Read);
                PlayMusic.SetSource(stream, file.ContentType);
                PlayMusic.Play();
                PlayMusic.MediaFailed += PlayMusic_MediaFailed;
                isplaying = true;
                JObject j = new JObject();
                j[JsonKey.SUCCESS_MESSAGE] = "Audio play started";
                Response.Success(id,j);
            }
            catch (Exception e)
            {
                JObject jo = new JObject();
                Response.Fail(id,ErrorCode.PLAY_FAIL);
                Log.Error(e.Message);
                return;
            }

        }

        private async void PlayMusic_MediaFailed(object sender, Windows.UI.Xaml.ExceptionRoutedEventArgs e)
        {
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
            {
                try
                {
                    Grid gr = (Grid)ect.FindName("MainGrid");
                    gr.Children.Remove(PlayMusic);
                }
                catch (Exception)
                {
                    //safe execution
                }
                Response.Fail(id, ErrorCode.AUDIO_FAIL);
                Log.Error("Audio Play Failed");
            });
        }
    }
}
