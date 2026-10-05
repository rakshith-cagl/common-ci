using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;
using Windows.Storage.Pickers;
using Windows.Storage.Streams;

namespace Appzillon.Plugins
{
    class FileBrowser
    {
        #region Singleton Pattern
        private static FileBrowser instance;
        private FileBrowser()
        {
        }
        public static FileBrowser Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new FileBrowser();
                }
                return instance;
            }
        }
        #endregion

        public StorageFile file;
        public async void FileBrowse(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            string fileCategory = obj[JsonKey.FILE_CATEGORY].ToString();
            string save;
            try
            {
                save = obj[JsonKey.SAVE].ToString();
            }
            catch (Exception)
            {
                save = "N";
                Log.Debug("save key does not exist");
            }
            StorageFolder local = ApplicationData.Current.LocalFolder;
            string tempPath = local.Path + @"\temp\FileBrowser";
            if (Directory.Exists(tempPath))
            {
                string tempPath1 = local.Path + @"\temp";
                Directory.Delete(tempPath1, true);
            }
            FileOpenPicker picker = new FileOpenPicker();
            picker.ViewMode = PickerViewMode.Thumbnail;
            if (fileCategory == "")
            {
                //string location = obj["location"].ToString();
                if (obj[JsonKey.FILTER].ToString() != "")
                {
                    string[] ext = obj[JsonKey.FILTER].ToString().Split(',');
                    foreach (string word in ext)
                    {
                        picker.FileTypeFilter.Add(word);
                    }
                }
                else
                {
                    picker.FileTypeFilter.Add("*");
                }
            }
            else
            {
                string[] AUDIO = { ".mp3", ".amr" };
                string[] VIDEO = { ".mp4", ".mpeg", ".mpg" };
                string[] PHOTO = { ".jpg", ".png" };
                if (fileCategory == "AUDIO")
                {
                    foreach (string ext in AUDIO)
                        picker.FileTypeFilter.Add(ext);
                }
                else if (fileCategory == "VIDEO")
                {
                    foreach (string ext in VIDEO)
                        picker.FileTypeFilter.Add(ext);
                }
                else if (fileCategory == "PHOTO")
                {
                    foreach (string ext in PHOTO)
                        picker.FileTypeFilter.Add(ext);
                }
                else
                {
                    picker.FileTypeFilter.Add("*");
                }
            }

            file = await picker.PickSingleFileAsync();
            if (file == null)
            {
                Response.Fail(id, ErrorCode.FILE_NOT_SELECTED);
                return;
            }
            if (obj[JsonKey.OPEN_FILE].ToString() == "Y")
            {
                try
                {
                    var options = new Windows.System.LauncherOptions();
                    options.DisplayApplicationPicker = true;
                    var success = await Windows.System.Launcher.LaunchFileAsync(file, options);
                    if (success)
                        Response.Success(obj[JsonKey.ID].ToString(), false);
                    else
                        Response.Fail(obj[JsonKey.ID].ToString(), "FILE LAUNCH FAIL");
                }
                catch (Exception e)
                {
                   Log.Error(e.Message);
                }
            }
            else
            {
                try
                {
                    JObject j = new JObject();
                    if (save == "N")
                    {
                        IRandomAccessStream fileStream = await file.OpenAsync(FileAccessMode.Read);
                        Stream read = fileStream.AsStream();
                        string path = file.Path;
                        string result = Path.GetFileName(path);
                        DirectoryInfo temp = Directory.CreateDirectory(tempPath);
                        string tempfolder = temp.FullName;
                        StorageFolder fold = await StorageFolder.GetFolderFromPathAsync(tempfolder);
                        StorageFile fi = await fold.CreateFileAsync(result, CreationCollisionOption.ReplaceExisting);
                        FileStream write = new FileStream(fi.Path, FileMode.Open, FileAccess.Write);
                        Writer(read, write);
                        j[JsonKey.FILE_PATH] = "../" + "temp/FileBrowser/" + result;
                        j[JsonKey.TEXT] = j[JsonKey.FILE_PATH];
                    }
                    else
                    {
                        IRandomAccessStream fileStream = await file.OpenAsync(FileAccessMode.Read);
                        Stream read = fileStream.AsStream();
                        string path = file.Path;
                        string result = Path.GetFileName(path);
                        string folder = local.Path + @"\appzillonapps\apps\" + MainPage.CurrentAppId;
                        StorageFolder fold = await StorageFolder.GetFolderFromPathAsync(folder);
                        StorageFile fi = await fold.CreateFileAsync(result, CreationCollisionOption.ReplaceExisting);
                        FileStream write = new FileStream(fi.Path, FileMode.Open, FileAccess.Write);
                        Writer(read, write);
                        j[JsonKey.FILE_PATH] = @"apps\" + MainPage.CurrentAppId+ @"\"+ result;
                        j[JsonKey.TEXT] = j[JsonKey.FILE_PATH];
                    }
                    Response.Success(id,j);
                }
                catch (Exception e)
                {
                    Log.Error(e.Message);
                    Response.Fail(obj[JsonKey.ID].ToString(),"FILE BROWSER FAIL");
                }
            }
        }
        private void Writer(Stream read, Stream write)
        {
            int Length = 256;
            Byte[] buffer = new Byte[Length];
            int bytesRead = read.Read(buffer, 0, Length);
            while (bytesRead > 0)
            {
                write.Write(buffer, 0, bytesRead);
                bytesRead = read.Read(buffer, 0, Length);
            }
            read.Dispose();
            write.Dispose();
        }
        private string Resolver(string filePath)
        {
            string path = "";
            string pattern = "../";
            int count = 0;
            int i = 0;
            while ((i = filePath.IndexOf(pattern, i)) != -1)
            {
                i += pattern.Length;
                count++;
            }

            for (i = 0; i <= count; i++)
            {
                int index1 = filePath.IndexOf("from");
                if (index1 != -1)
                {
                    path = filePath.Remove(index1);

                }

            }
            if (i == 0)
            {
                path = @"apps/" + MainPage.CurrentAppId + @"screens/" + path;
            }
            if (i == 2)
            {
                path = @"apps/" + MainPage.CurrentAppId + @"/" + path;
            }
            if (i == 3)
            {
                path = @"apps/" + path;
            }
            StorageFolder local = ApplicationData.Current.LocalFolder;
            filePath = local.Path + @"/" + path;
            return filePath;
        }
    }
}
