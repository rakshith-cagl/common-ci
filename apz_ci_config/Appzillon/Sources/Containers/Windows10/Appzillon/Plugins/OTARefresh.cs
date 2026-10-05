using System;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Windows.Storage;
using Windows.UI.Xaml.Controls;
using System.Diagnostics;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using Appzillon.Constants;
using Windows.UI.Xaml;

namespace Appzillon.Plugins
{
    class OTARefresh
    {
        public static bool upgrade_required = false;
        public async static Task CopyAppsToLocalStorage()
        {
            try
            {
                Log.Debug("------------------->Inside CopyAppsToLocalStorage");
                var localFolder = ApplicationData.Current.LocalFolder;
                var installedLocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
                var apps = await installedLocation.GetFolderAsync("appzillonapps");
                await CopyFilestolocal(apps, localFolder, "appzillonapps");
                //var appzillonfold = await installedLocation.GetFolderAsync("appzillonapps");
               // await CopyFilestolocal(appzillonfold, localFolder,"appzillon");
                Log.Debug("------------------->Inside CopyAppsToLocalStorage");
            }
            catch (Exception)
            {
                // string s =e.Message;
            }
        }
        public static async Task OTACheck()
        {
            Log.Debug("------------------->Inside OTACheck");
            bool t = StoreRetrieve("WINDOWS10");
            if (!t)
            {
                // Stopwatch stopWatch = new Stopwatch();
                // stopWatch.Start();
                await CopyAppsToLocalStorage();
                // stopWatch.Stop();                                           // to check efficiency of file copy
                //  TimeSpan ts = stopWatch.Elapsed;
                //  AppzillonDebug.NativeConsole("time elapsed--------------------------->" +ts);
                MainPage.PersistanceData.CreateContainer("WINDOWS10", ApplicationDataCreateDisposition.Always);
                MainPage.PersistanceData.Containers["WINDOWS10"].Values.Add("COPIEDTOLOCAL", true);
            }
        }
        public async static Task CopyFilestolocal1(StorageFolder installFolder, StorageFolder localFolder)
        {
            StorageFolder destFolder = await localFolder.CreateFolderAsync(installFolder.Name, CreationCollisionOption.OpenIfExists);
            var files = await installFolder.GetFilesAsync();
            var subfolders = await installFolder.GetFoldersAsync();
            foreach (var file in files)
            {
                try
                {
                    await file.CopyAsync(destFolder);
                }
                catch (Exception e)
                {
                    Log.Warning(e.Message);
                }
            }
            foreach (var folder in subfolders)
            {
                if (folder.Name == "sqlite" || folder.Name == "staticfiles")
                    continue;
                await CopyFilestolocal1(folder, destFolder);
            }
        }

        public async static Task CopyFilestolocal(StorageFolder apps, StorageFolder localFolder,string dest)
        {
            StorageFolder destFolder = await localFolder.CreateFolderAsync(dest, CreationCollisionOption.OpenIfExists);
            try
            {
                foreach (string dirPath in Directory.GetDirectories(apps.Path, "*", SearchOption.AllDirectories))
                {
                    string lastPart = dirPath.Split('\\').Last();

                    if ((lastPart.Equals("sqlite") || lastPart.Equals("staticfiles")) || lastPart.Equals("docs"))
                    {
                        continue;
                    }

                    Directory.CreateDirectory(dirPath.Replace(apps.Path, destFolder.Path));
                }
            }
            catch (Exception ex)
            {
                Log.Error("----Error----->" + ex.Message);
            }

            foreach (string newPath in Directory.GetFiles(apps.Path, "*.*", SearchOption.AllDirectories))
            {
                try
                {
                    if (newPath.Contains("sqlite") || newPath.Contains("staticfiles"))
                    {
                        continue;
                    }
                    File.Copy(newPath, newPath.Replace(apps.Path, destFolder.Path), true);
                }
                catch (Exception e)
                {
                    Log.Error(e.Message);
                }
            }
        }


        //public async static Task CopyFunctionOverridefile()
        //{
        //    Log.Debug("------------------->Copying functionoverride.js file");                // Deprecated  since function overriding is not required to achieve OTA in win10 anymore
        //    StorageFolder dest1 = ApplicationData.Current.LocalFolder;
        //    string pathtoovr = dest1.Path + @"\apps";
        //    var installedLocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
        //    var file = await installedLocation.GetFileAsync("FunctionOverriden.js");
        //    StorageFolder f = await StorageFolder.GetFolderFromPathAsync(pathtoovr);
        //    await file.CopyAsync(f, "FunctionOverriden.js", NameCollisionOption.ReplaceExisting);
        //    MainPage.PersistanceData.CreateContainer("WINDOWS10", ApplicationDataCreateDisposition.Always);
        //    MainPage.PersistanceData.Containers["WINDOWS10"].Values.Add("COPIEDTOLOCAL",true);
        //    Log.Debug("------------------->Copied functionoverride.js file");
        //}
        public static bool StoreRetrieve(string key)
        {

            bool hasContainer = MainPage.PersistanceData.Containers.ContainsKey("WINDOWS10");
            bool hasSetting = false;

            if (hasContainer)
            {
                hasSetting = MainPage.PersistanceData.Containers["WINDOWS10"].Values.ContainsKey("COPIEDTOLOCAL");
            }
            return hasSetting;
        }
        public static void LaunchWebViewOverride(MainPage mp, WebView wbAppzillon, string appId)
        {

            Log.Debug("------------------->Launch Webviewoverride");
            try
            {
                Uri url = wbAppzillon.BuildLocalStreamUri("MyTag", "/local/appzillonapps/" + appId + ".html");
                StreamUriWinRTResolver myResolver = new StreamUriWinRTResolver();
                wbAppzillon.NavigateToLocalStreamUri(url, myResolver);// Pass the resolver object to the navigate call.
                wbAppzillon.Width = Window.Current.Bounds.Width;
                wbAppzillon.Height = Window.Current.Bounds.Height;
            }
            catch (Exception e)
            {
                Log.Error(e.Message);
            }
        }
        public static async void LaunchChildApp(JObject obj, MainPage lobject)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string childappid = obj[JsonKey.APP_ID].ToString();
                MainPage.CurrentAppId = childappid;               
                DirectoryInfo di = new DirectoryInfo(ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + childappid);
                if (!di.Exists)
                {
                    var installedLocation = Windows.ApplicationModel.Package.Current.InstalledLocation;
                    try
                    {
                        DirectoryInfo from_di = new DirectoryInfo(installedLocation.Path + "\\appzillonapps\\apps\\" + childappid);
                        if (!from_di.Exists)
                        {
                            Response.Fail(id, ErrorCode.CHILD_APP_DOES_NOT_EXISTS);
                            return;
                        }
                        var localFolder = ApplicationData.Current.LocalFolder;
                        var appzillonfold = await installedLocation.GetFolderAsync(@"appzillonapps\apps\" + childappid);
                        var childapploc = await localFolder.CreateFolderAsync(@"appzillonapps\apps\" + childappid, CreationCollisionOption.OpenIfExists);
                        await CopyFilestolocal(appzillonfold, childapploc, childappid);
                        AppSettings.InitializeSettings();
                    }
                    catch (Exception e)
                    {
                        Response.Fail(id, ErrorCode.CHILD_APP_LAUNCH_FAIL);
                    }
                }
                OTARefresh.LaunchWebViewOverride(lobject, (WebView)lobject.FindName("wbAppzillon"), childappid);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.CHILD_APP_LAUNCH_FAIL);
                Log.FatalError(e.Message);
            }
        }
        internal static async void DeleteApp(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string app = obj[JsonKey.APP_ID].ToString();
                try
                {
                    DirectoryInfo di = new DirectoryInfo(ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + app);
                    if (!di.Exists)
                        {
                        Response.Fail(id, ErrorCode.CHILD_APP_DOES_NOT_EXISTS);
                        return;
                    }
                    StorageFolder appfolder = await ApplicationData.Current.LocalFolder.GetFolderAsync("appzillonapps\\apps\\" + app);
                    await appfolder.DeleteAsync(StorageDeleteOption.PermanentDelete);
                }
                catch (Exception) {
                    Response.Fail(id,ErrorCode.SUB_APP_DEL_RUN_TIME_EXCEPTION);
                }
                Response.Success(id, false);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.CHILD_APP_DELETE_FAIL);
                Log.FatalError(e.Message);
            }
        }

        internal static async void UpgradeApp(JObject obj, MainPage page)
        {
            JObject values = await GetValues(obj);
            string url = Utils.GetServerUrl();
            string File_res = await GenericUtils.SendRequestAsync(url, values.ToString());
            if (File_res == "")
            {
                Log.Error("error while sending request to server");
                Response.Fail(obj[JsonKey.ID].ToString(),ErrorCode.SERVER_ERROR_WHILE_UPGRADING_APP);
                return;
            }
            JObject file_res_obj = JObject.Parse(File_res);
            bool res_status = (bool)file_res_obj["appzillonHeader"]["status"];
            if (res_status)
            {
                string appId = file_res_obj["appzillonBody"]["appId"].ToString();
                var updated_file_list = file_res_obj["appzillonBody"][appId];
                MainPage.PersistanceData.CreateContainer("UPGRADEFILES", ApplicationDataCreateDisposition.Always);
                foreach (JObject updated_file in updated_file_list)
                {
                    try
                    {
                        string app_version = updated_file["appVersion"].ToString();
                        string fp = updated_file["filepath"].ToString();
                        updated_file["requested"] = false;
                        updated_file["appId"] = appId;
                        MainPage.PersistanceData.Containers["UPGRADEFILES"].Values.Add(fp, updated_file.ToString());
                        //MainPage.PersistanceData.Values[fp] = updated_file.ToString();                   
                    }
                    catch (Exception) { }

                }
                var file_arr = MainPage.PersistanceData.Containers["UPGRADEFILES"].Values;
              foreach(var files in file_arr)
                {
                    var file = JObject.Parse(files.Value.ToString());                       
                    string fp = file["filepath"].ToString();
                    string filename = file["filename"].ToString();
                    string appVersion = file["appVersion"].ToString();
                    string os = file["os"].ToString();
                    string appID = file["appId"].ToString();
                    string action = file["action"].ToString();
                    MainPage.PersistanceData.Containers["UPGRADEFILES"].Values.Remove(fp);
                    StorageFolder dest1 = ApplicationData.Current.LocalFolder;
                    if (os == "WINDOWS" || os == "ALL")
                    {
                        if (action == "DELETE")
                        {

                            string pathtoovr = dest1.Path + @"\appzillonapps\apps\" + fp;
                            try
                            {
                                await (await StorageFile.GetFileFromPathAsync(pathtoovr)).DeleteAsync();
                            }
                            catch (Exception e)
                            {
                                Response.Fail(obj[JsonKey.ID].GetString(), ErrorCode.ERROR_WHILE_UPGRADING_APP);
                                Log.FatalError(e.Message);
                                return;
                            }
                        }
                        else
                        {
                            JObject js = new JObject();
                            js["appId"] = appId; //obj["appId"].ToString();
                            js[JsonKey.FILE_NAME] = filename;
                            js[JsonKey.FILE_PATH] = fp;
                            js["appVersion"] = appVersion;
                            JObject json = GetVal(js);
                            string ota_files = await GenericUtils.SendRequestAsync(url, json.ToString());
                            JObject ota_file_res = JObject.Parse(ota_files);
                            bool ota_res_status = (bool)file_res_obj["appzillonHeader"]["status"];
                            if (ota_res_status)
                            {
                                try
                                {
                                    var ota_body = (JObject)ota_file_res["appzillonBody"]["appzillonOTAFileDownloadResponse"];
                                    string fi_Pat = ota_body.GetValue("filePath").ToString();
                                    string fullpath = dest1.Path + @"\appzillonapps\apps\" + fi_Pat;
                                    string b64content = ota_body["file"].ToString();
                                    string fileName = ota_body["fileName"].ToString();
                                    byte[] bitarr = Convert.FromBase64String(b64content);
                                    fullpath = fullpath.Replace('/', '\\');
                                    FileInfo fileinfo = new FileInfo(fullpath);
                                    if (!fileinfo.Exists)
                                    {
                                        fileinfo.Create().Dispose();
                                    }
                                    StorageFile dest_file = await StorageFile.GetFileFromPathAsync(fullpath);
                                    Stream write_bytes =await dest_file.OpenStreamForWriteAsync();
                                    await write_bytes.WriteAsync(bitarr,0,bitarr.Length);
                                    await write_bytes.FlushAsync();
                                    write_bytes.Dispose();
                                    
                                }
                                catch (Exception e)
                                {
                                    Response.Fail(obj[JsonKey.ID].GetString(), ErrorCode.ERROR_WHILE_UPGRADING_APP);
                                    Log.FatalError(e.Message);
                                    return;
                                }
                            }
                        }
                        string cont = appId + "appVersion";
                        MainPage.PersistanceData.Containers[cont].Values["appVersion"] = appVersion;
                        Response.Success(obj[JsonKey.ID].ToString(),false);
                    }
                }
            }
            else
            {
                Response.Fail(obj[JsonKey.ID].GetString(), ErrorCode.ERROR_WHILE_UPGRADING_APP);
            }

        }

        internal static void checkOTA(JObject reqObject)
        {
            var res = new JObject();
            if (upgrade_required)
                res[JsonKey.TEXT] = "Y";
            else
                res[JsonKey.TEXT] = "N";
            res["upgradeRequired"] = res[JsonKey.TEXT];
            Response.Success(reqObject[JsonKey.ID].ToString(),res);
        }

        private static async Task<JObject> GetValues(JObject js)
        {
            JObject obj = new JObject();
            string deviceId = Utils.GetDeviceID();
            string appId = js["appId"].ToString();
            try
            {
                obj["appzillonHeader"] = new JObject();
                obj["appzillonHeader"]["deviceId"] = deviceId;
                obj["appzillonHeader"]["appId"] = appId;
                obj["appzillonHeader"]["sessionId"] = "";
                obj["appzillonHeader"]["screenId"] = "";
                obj["appzillonHeader"]["userId"] = "";
                obj["appzillonHeader"]["requestKey"] = "";
                obj["appzillonHeader"]["status"] = true;
                obj["appzillonHeader"]["interfaceId"] = "appzillonGetAppFile";
                obj["appzillonHeader"]["originaration"] =  Utils.GetIP();

                obj["appzillonBody"] = new JObject();
                obj["appzillonBody"]["appzillonAppFilesRequest"] = new JObject();
                obj["appzillonBody"]["appzillonAppFilesRequest"]["appId"] = appId;
                obj["appzillonBody"]["appzillonAppFilesRequest"]["os"] = Utils.GetOS();
                string container = MainPage.CurrentAppId + "appVersion";
                obj["appzillonBody"]["appzillonAppFilesRequest"]["appVersion"] = MainPage.PersistanceData.Containers[container].Values["appVersion"].ToString();

            }
            catch (Exception e)
            { }
            return obj;
        }
        private static JObject GetVal(JObject js)
        {
            string appId = js["appId"].ToString();
            string deviceId = Utils.GetDeviceID();
            JObject obj = new JObject();
            obj["appzillonHeader"] = new JObject();
            obj["appzillonHeader"]["preLogin"] = true;
            obj["appzillonHeader"]["appId"] = appId;
            obj["appzillonHeader"]["deviceId"] = deviceId;
            obj["appzillonHeader"]["sessionId"] = "";
            obj["appzillonHeader"]["userId"] = "";
            obj["appzillonHeader"]["requestKey"] = "";
            obj["appzillonHeader"]["status"] = true;
            obj["appzillonHeader"]["interfaceId"] = "appzillonOTAFileDownloadReq";
            obj["appzillonHeader"]["async"] = true;

            obj["appzillonBody"] = new JObject();
            obj["appzillonBody"]["appzillonOTAFileDownloadReq"] = new JObject();
            obj["appzillonBody"]["appzillonOTAFileDownloadReq"]["appId"] = appId;
            obj["appzillonBody"]["appzillonOTAFileDownloadReq"]["os"] = Utils.GetOS();
            obj["appzillonBody"]["appzillonOTAFileDownloadReq"]["appVersion"] = js.GetValue("appVersion").ToString();
            obj["appzillonBody"]["appzillonOTAFileDownloadReq"]["fileName"] = js[JsonKey.FILE_NAME];
            obj["appzillonBody"]["appzillonOTAFileDownloadReq"]["filepath"] = js[JsonKey.FILE_PATH];

            return obj;
        }
    }
}
