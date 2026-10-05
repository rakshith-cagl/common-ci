using System;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;
using Appzillon.Constants;
using Windows.Storage;
using Windows.UI.Xaml;
using Appzillon.Plugins;
using System.IO;
using Windows.Graphics.Display;
using Appzillon.CommonClass;
using SQLiteWinRT;

namespace Appzillon.Native
{/// <summary>
/// Author Anand Kumar
/// </summary>
    class AppMasterRequest
    {
        MainPage mainPage;
        string id = "";
        bool getInstruction = false;
        string appid = "";
      
        public AppMasterRequest()
        {

        }
        public AppMasterRequest(MainPage mp)
        {
            mainPage = mp;
        }
        public async Task GetAppMasterDetails()
        {
            appid = MainPage.CurrentAppId;
            getInstruction = false;
        //    getInstruction = true;
            string url = Utils.GetServerUrl();
            JObject val = GetValues(MainPage.CurrentAppId);
          await  SendAppMasterRequest(url, val.ToString());
        }
        public async Task GetAppInstruction(JObject json)
        {
            appid = MainPage.CurrentAppId;
            try
            {
                appid = json[JsonKey.APP_ID].ToString();
            }
            catch (Exception)
            { }
            id = json[JsonKey.ID].ToString();
            getInstruction = true;
            string url = Utils.GetServerUrl();
            JObject val = GetValues(appid);
         await SendAppMasterRequest(url, val.ToString());
        }
    
        private async Task SendAppMasterRequest(string url, string data)
        {
            string response = await GenericUtils.SendRequestAsync(url, data);
           await AppMasterDetails(response);
        }
        public static bool AppMasterResponseSattus { get; set; }
        private async Task AppMasterDetails(string response)
        {
            //  string appId = MainPage.CurrentAppId;
            try
            {
                JObject presp = JObject.Parse(response);
                Log.Debug("App Master Detail Response\n" + presp.ToString());
                AppzlionAppMasterResponse.status = (bool)presp[JsonKey.APPZILLON_HEADER][JsonKey.STATUS];
                bool isFailure = false;
                JObject json = new JObject();
                if (AppzlionAppMasterResponse.status)
                {
                    isFailure = false;
                    json = (JObject)presp[JsonKey.APPZILLON_BODY][appid];
                    Log.Debug("App Master Details\n" + json.ToString());
                    string WipeOut = "";
                    string appexpired = "";
                    try
                    {
                        WipeOut = json["wipeout"].ToString();
                        appexpired = json["expired"].ToString();
                        IEXC.expiryDate = json["expiryDate"].ToString();
                        IEXC.wipeout = WipeOut;
                        IEXC.expired = appexpired;
                    }
                    catch (Exception)
                    { }

                    if (WipeOut == "Y" || WipeOut == "true")
                    {
                        var applicationData = Windows.Storage.ApplicationData.Current;
                        var localSettings = applicationData.LocalSettings;
                        try
                        {
                            await applicationData.ClearAsync();
                        }
                        catch (Exception)
                        {
                            DispatcherTimer dispatcherTimer = new DispatcherTimer();
                            dispatcherTimer.Tick += async (sender, e) =>
                            {
                                dispatcherTimer.Stop();
                                await ApplicationData.Current.ClearAsync();
                            };
                            dispatcherTimer.Interval = new TimeSpan(0, 0, 0, 0, 500);
                            dispatcherTimer.Start();
                        }
                        localSettings.Values["APPEXPIRED"] = "Y";
                        localSettings.Values["APPZILLONWIPEOUT"] = true;
                        Application.Current.Exit();
                    }
                    if (appexpired == "Y")
                    {
                        var applicationData = Windows.Storage.ApplicationData.Current;
                        var localSettings = applicationData.LocalSettings;
                        localSettings.Values["APPEXPIRED"] = "Y";
                     
                        Utils.Alert("app has expired");
                        Application.Current.Exit();
                    }
                    string appversion = "";
                    try
                    {
                        appversion = json["appVersion"].ToString();
                    }
                    catch (Exception)
                    { }
                    if (appversion != MainPage.PersistanceData.Containers[MainPage.CurrentAppId + "appVersion"].Values["appVersion"].ToString())
                        OTARefresh.upgrade_required = true;
                    string DebugLevel = "";
                    try
                    {
                        DebugLevel = json["remoteDebug"].ToString();
                    }
                    catch (Exception) { }
                    if (DebugLevel == "Y")
                    {
                        Log.level = 4;
                    }
                    if (DebugLevel == "N")
                    {
                        Log.level = 0;
                    }
                }
                else
                {
                    try
                    {
                        isFailure = true;
                      
                    }
                    catch (Exception ex)
                    {
                        Log.Error(ex.Message);
                    }
                }
                
                if (getInstruction)
                {
                    if (!isFailure)
                    {
                        JObject instruction = JObject.Parse(presp[JsonKey.APPZILLON_BODY].ToString());
                        Response.Success(id, instruction);
                    }
                    else
                        Response.Fail(id, ErrorCode.GET_INSTRUCTIONS_FAIL);
                }
            }
            catch (Exception e)
            {
                if (getInstruction)
                {
                    Response.Fail(id, ErrorCode.GET_INSTRUCTIONS_FAIL);
                }
                Log.Error("Invalid response for appzillonGetAppMasterDetails!!" + e.Message);
            }
        }

        internal async Task<bool> FirstRun()
        {
            var setting = ApplicationData.Current.LocalSettings;
            try
            {
                var firstRanFlag = setting.Values["FIRSTRUNFLAG"];
                if ((bool)firstRanFlag== true)
                {
                    await createStorageFolders();
                    await createDatabase();
                    await CopyDatabase();
                    await CopyStaticFiles();
                    setDeviceOrientation();
                    setting.Values["FIRSTRUNFLAG"] = true;
                }
            }
            catch (Exception)
            {

            }
            return true;
        }

        private async Task<bool> createDatabase()
        {
            var arr = new string[3];
            arr[0] = "CREATE TABLE Notes (id TEXT PRIMARY KEY, notes TEXT)";
            arr[1] = "CREATE TABLE tb_asmi_offline_data(refno integer primary key autoincrement,userid varchar,screenid varchar,interfaceid varchar,interfacepayload varchar,screenpayload varchar,screenresponse varchar,status varchar,appjsonstr varchar,appreqbody varchar, starttime varchar, endtime varchar)";
            arr[2] = "CREATE TABLE TB_ASMI_TEMPLATE_DATA (TEMPLATEREFNO INTEGER PRIMARY KEY AUTOINCREMENT, TEMPLATENAME VARCHAR, SCREENID VARCHAR, SCREENPAYLOAD VARCHAR)";
            dynamic dataBase = await ApplicationData.Current.LocalFolder.CreateFileAsync(@"appzillonapps\apps\" + MainPage.CurrentAppId + @"\sqlite\APPSDB", CreationCollisionOption.OpenIfExists);
            using (Database db = new Database(dataBase))
            {
                try
                {
                    await db.OpenAsync();
                }
                catch (Exception) { }
                foreach (var cmd in arr)
                {
                    try
                    {
                        await db.ExecuteStatementAsync(cmd);

                    }
                    catch (Exception ex)
                    {
                        Log.Error("Sqlite Error =" + ex.Message);
                    }

                }
            }
            return true;
        }

        private async Task<bool> CopyDatabase()
        {
            try
            {
                await (await ApplicationData.Current.LocalFolder.GetFolderAsync("temp")).DeleteAsync();
            }
            catch (Exception) { }
            var installFolder = Windows.ApplicationModel.Package.Current.InstalledLocation;
            var localFolder = ApplicationData.Current.LocalFolder;
            try
            {
                var dest = await localFolder.GetFolderAsync(@"appzillonapps\apps\" + MainPage.CurrentAppId + @"\sqlite");
                var folder = await installFolder.GetFolderAsync(@"appzillonapps\apps\" + MainPage.CurrentAppId + @"\sqlite");
                var files = await folder.GetFilesAsync();
                if (files != null)
                {
                    foreach (var file in files)
                    {
                        string name = file.Name;
                        await file.CopyAsync(dest, name, NameCollisionOption.ReplaceExisting);
                    }
                }
            }
            catch (Exception) { }
            return true;
        }

        private async Task<bool> CopyStaticFiles()
        {
            var setting = Windows.Storage.ApplicationData.Current.LocalSettings;
            try
            {
                var check = (bool)setting.Values["APPZILLONCOPYSTATICFILE"];
                if (check)
                    return true;
            }
            catch (Exception)
            {
                try
                {
                    StorageFolder installFolder = Windows.ApplicationModel.Package.Current.InstalledLocation;
                    try
                    {
                        string appid = MainPage.CurrentAppId;
                        var json = File.ReadAllText(@"appzillonapps\apps\" + appid + @"\screens\config\Files.json");
                        var parsedfile = JObject.Parse(json);
                        var jsonarry = parsedfile["Files"];
                        StorageFolder localFolder = ApplicationData.Current.LocalFolder;
                        DirectoryInfo staticdir = new DirectoryInfo(@"appzillonapps\apps\" + appid + @"\staticfiles");
                        if (staticdir.Exists)
                        {
                            var stat_fold = await StorageFolder.GetFolderFromPathAsync(localFolder.Path + @"\appzillonapps\apps\" + appid + @"\staticfiles");
                            FileInfo[] st_files = staticdir.GetFiles();
                            foreach (var file in st_files)
                            {
                                string name = file.Name;
                                StorageFile fi = await StorageFile.GetFileFromPathAsync(file.FullName);
                                if (jsonarry[name].ToString() == "")
                                    await fi.CopyAsync(stat_fold, name, NameCollisionOption.ReplaceExisting);
                                else
                                {
                                    string path = jsonarry[name].ToString().Replace('/', '\\');
                                    try
                                    {
                                        var dest = await localFolder.CreateFolderAsync(@"appzillonapps\apps\" + MainPage.CurrentAppId + @"\" + path, CreationCollisionOption.OpenIfExists);
                                        await fi.CopyAsync(dest, name, NameCollisionOption.ReplaceExisting);
                                    }
                                    catch (Exception)
                                    { }
                                }
                            }
                            setting.Values["APPZILLONCOPYSTATICFILE"] = true;
                        }
                    }
                    catch (Exception)
                    {

                    }
                }
                catch (Exception)
                {
                    setting.Values["APPZILLONCOPYSTATICFILE"] = true;
                }
            }
            return true;
        }

        private async Task<bool> createStorageFolders()
        {
            try
            {
                StorageFolder localFolder = ApplicationData.Current.LocalFolder;
                StorageFolder apps = await localFolder.CreateFolderAsync(@"appzillonapps\apps\" + MainPage.CurrentAppId, CreationCollisionOption.OpenIfExists);
                // StorageFolder appfolder = await apps.CreateFolderAsync(MainPage.CurrentAppId, CreationCollisionOption.OpenIfExists);
                await apps.CreateFolderAsync("video", Windows.Storage.CreationCollisionOption.OpenIfExists);
                await apps.CreateFolderAsync("audio", Windows.Storage.CreationCollisionOption.OpenIfExists);
                await apps.CreateFolderAsync("photo", Windows.Storage.CreationCollisionOption.OpenIfExists);
                await apps.CreateFolderAsync("docs", Windows.Storage.CreationCollisionOption.OpenIfExists);
                await apps.CreateFolderAsync("staticfiles", CreationCollisionOption.OpenIfExists);
            }
            catch (Exception)
            { }
            return true;
        }

        private void setDeviceOrientation()
        {
            JObject resjs = Device.DeviceGroup();
            SetOrientation(resjs[JsonKey.ORIENTATION].ToString());
        }
        private void SetOrientation(string ort)
        {
            string neworientation = ort.Substring(0, 3).ToUpper();
            if (neworientation == "POR")
            {
                DisplayInformation.AutoRotationPreferences =
                   DisplayOrientations.Portrait;
            }
            else if (neworientation == "LAN")
            {
                DisplayInformation.AutoRotationPreferences =
                     DisplayOrientations.Landscape;
            }
            else
            {
                DisplayInformation.AutoRotationPreferences =
                DisplayOrientations.None;
            }
        }
        internal void CheckAppExpiry()
        {
            var val = "";
            bool fail = false;
            var applicationData = Windows.Storage.ApplicationData.Current;
            var localSettings = applicationData.LocalSettings;
            try
            {
                val = localSettings.Values["APPEXPIRED"].ToString();

            }
            catch (Exception)
            {
                fail = true;
                var app_exp = AppSettings.ContainerProperties["APPEXPIRED"].ToString();
                if (app_exp == "Y")
                {
                    localSettings.Values["APPEXPIRED"] = "Y";
                    Utils.Alert("Your App has expired");
                    Application.Current.Exit();
                }
                else
                {
                    localSettings.Values["APPEXPIRED"] = "N";
                }
            }

            if (!fail)
            {
                if (val == "Y")
                {
                    Utils.Alert("Your App has expired");
                    Application.Current.Exit();
                }
                else
                {
                    string dateString = AppSettings.AppProperties["APPEXPIRYDATE"].ToString();
                    var date = dateString.Split('/');

                    try
                    {
                        var final_date = new DateTime(int.Parse(date[2]), int.Parse(date[1]) - 1, int.Parse(date[0]));
                        var today = new DateTime();
                        var dd = today.Date.Day;
                        var mm = today.Month; //January is 0!

                        var yyyy = today.Year;

                        var final_today = new DateTime(yyyy, mm, dd, 0, 0, 0, 0);
                        if (final_date < final_today)
                        {
                            localSettings.Values["APPEXPIRED"] = "Y";
                            Utils.Alert("Your App has expired");
                            Application.Current.Exit();
                        }
                    }
                    catch (Exception)
                    {
                        Utils.Alert("App expiry date is not matching");
                        Application.Current.Exit();
                    }
                }
            }

        }

        private JObject GetValues(string appId)
        {
            
            string deviceId = Utils.GetDeviceID();
            string os = Utils.GetOS();
            string userId = JsonKey.WINDOWS_10;
            string interfaceId = JsonKey.APPZILLON_GET_APP_MASTER_DETAILS;

            JObject data = new JObject();
            data[JsonKey.APPZILLON_HEADER] = new JObject();
            data[JsonKey.APPZILLON_HEADER][JsonKey.PRELOGIN] = "true";
            data[JsonKey.APPZILLON_HEADER][JsonKey.APP_ID] = appId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.USER_ID] = userId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.DEVICE] = deviceId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.INTERFACE_ID] = interfaceId;
            data[JsonKey.APPZILLON_HEADER][JsonKey.SESSION_ID] = "";
            data[JsonKey.APPZILLON_HEADER][JsonKey.STATUS] = true;
            data[JsonKey.APPZILLON_HEADER][JsonKey.REQUEST_KEY] = JsonKey.NULL;
            data[JsonKey.APPZILLON_HEADER][JsonKey.SCREEN_ID] = JsonKey.LOGIN;

            data[JsonKey.APPZILLON_BODY] = new JObject();
            data[JsonKey.APPZILLON_BODY][JsonKey.APPZILLON_APP_MASTER_REQUEST] = new JObject();
            data[JsonKey.APPZILLON_BODY][JsonKey.APPZILLON_APP_MASTER_REQUEST][JsonKey.APP_ID] = appId;
            data[JsonKey.APPZILLON_BODY][JsonKey.APPZILLON_APP_MASTER_REQUEST][JsonKey.DEVICE] = deviceId;
            data[JsonKey.APPZILLON_BODY][JsonKey.APPZILLON_APP_MASTER_REQUEST][JsonKey.OS] = os;
            return data;
        }

    }
}
