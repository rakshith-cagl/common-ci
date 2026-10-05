using System;
using System.IO;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Navigation;
using Appzillon.Native;
using Windows.Data.Json;
using Windows.UI.Core;
using Windows.System.Profile;
using Appzillon.Plugins;
using Windows.ApplicationModel.Activation;
using Newtonsoft.Json.Linq;
using System.Threading.Tasks;
using Windows.Security.Cryptography.Core;
using System.Text;
using System.Diagnostics;
using Windows.Storage.Streams;
using Windows.Security.Cryptography;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Security.Cryptography;

namespace Appzillon
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public static string MainAppId { get; set; }
        public static string CurrentAppId = null;
        public Request request;

        public static Windows.Storage.ApplicationDataContainer PersistanceData =
            Windows.Storage.ApplicationData.Current.LocalSettings;

        public MainPage()
        {
            // string haspin = sha256("Anand" + "1234567890123456");

            this.NavigationCacheMode = NavigationCacheMode.Required;
            this.InitializeComponent();
         
            Response.CallBack = InvokeScript;
            Splash.Instance.ShowSplash(this);
            AppSettings.SetContainerProps();
            
            CheckOTASettings();
            InitializeVariables();
            #region Events registration for application
            Window.Current.SizeChanged += CurrentWindow_SizeChanged;
            Application.Current.Resuming += Current_Resuming;
            Application.Current.Suspending += Current_Suspending;
            Application.Current.UnhandledException += Current_UnhandledException;
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += onbackpressed;
            #endregion         
        }

        private static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };
        public static String sha256(String data)
        {
            byte[] ByteData = new byte[200];

            ByteData = Encoding.ASCII.GetBytes(data);
            //MD5 creating MD5 object.
            SHA256 sha = SHA256.Create();

            //Hash değerini hesaplayalım.
            byte[] HashData = sha.ComputeHash(ByteData);

            //convert byte array to hex format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < HashData.Length; i++)
            {
                int c = ((HashData[i]) >> 4) & 0xf;
                sb.Append(hex[c]);
                c = (HashData[i] & 0xf);
                sb.Append(hex[c]);
            }
            //for (int x = 0; x < HashData.Length; x++)
            //{
            //    //hexadecimal string value
            //    oSb.Append(HashData[x].ToString("x2"));
            //}
            return sb.ToString();
        }

        public static string HashMsg(String strAlgName, [ReadOnlyArray()] byte[] strMsg)
        {
            // Convert the message string to binary data.
            IBuffer buffUtf8Msg = CryptographicBuffer.CreateFromByteArray(strMsg);

            // Create a HashAlgorithmProvider object.
            HashAlgorithmProvider objAlgProv = HashAlgorithmProvider.OpenAlgorithm(strAlgName);

            // Demonstrate how to retrieve the name of the hashing algorithm.
            string strAlgNameUsed = objAlgProv.AlgorithmName;

            // Hash the message.
            IBuffer buffHash = objAlgProv.HashData(buffUtf8Msg);


            // Verify that the hash length equals the length specified for the algorithm.
            if (buffHash.Length != objAlgProv.HashLength)
            {
                throw new Exception("There was an error creating the hash");
            }

            // Convert the hash to a string (for display).
            string strHashBase64 = CryptographicBuffer.EncodeToBase64String(buffHash);

            string abcd;

            abcd = CryptographicBuffer.EncodeToHexString(buffHash);
            // Return the encoded string
            return abcd;
        }
        private void InitializeVariables()
        {
            request = new Request(this);
        }
        ServerUtils servutil = new ServerUtils();
        AppToken apptoken = new AppToken();
        public async void CheckOTASettings()
        {
            try
            {
                //Utils.GetTodayDate();
                bool x = false;
                string wiped = "N";
                var applicationData = Windows.Storage.ApplicationData.Current;
                var localSettings = applicationData.LocalSettings;
                try
                {
                    x = (bool)localSettings.Values["APPZILLONWIPEOUT"];

                }
                catch (Exception) { }
                try
                {
                    wiped = localSettings.Values["APPEXPIRED"].ToString();

                }
                catch (Exception) { }
                if (wiped == "Y" || x)
                {
                    Application.Current.Exit();
                }
            }
            catch (Exception)
            { }
            string s = (string)AppSettings.ContainerProperties.GetValue("OTAREQUIRED");
            if (s == "N")
            {
                AppSettings.InitializeSettings();

                //  servutil.SendRequestsToServer();
            }
            else
            {
                await OTARefresh.OTACheck();
                AppSettings.InitializeSettings();
                // servutil.SendRequestsToServer();
            }
            if (s == "Y")
            {
                servutil.BindingServerRequest();
                OTARefresh.LaunchWebViewOverride(this, wbAppzillon, CurrentAppId);
            }
            else
            {
                servutil.BindingServerRequest();

                LaunchWebView(MainAppId);
            }
        }
        #region Commented Codes
        // move to serverutil class
        //        private async void SendRequestsToServer()
        //        {
        //            DeviceRegistration dr = new DeviceRegistration();
        //            bool isDone = await dr.RegisterDevice();
        //            AppMasterRequest ar = new AppMasterRequest(this);
        //            if (isDone)
        //            {               
        //                ar.GetAppMasterDetails();
        //#if NOTIFICATION
        //                Notification ntf = new Notification();
        //                ntf.RegisterNotificationUrl();
        //#endif
        //            }
        //            await ar.FirstRun();
        //        }

        internal static async void StoreNotification(string arguments)
        {
            if (Utils.NotificationCB_id != "")
            {
                await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
                {
                    JObject js = new JObject();
                    js["event"] = "notification";
                    js["text"] = arguments;
                    Response.Success(Utils.NotificationCB_id, true, js);
                });
            }
            double timestamp = Math.Round(DateTime.Now.Subtract(DateTime.MinValue.AddYears(1969)).TotalSeconds);
            Storage.Instance.storeNotification(arguments, timestamp, 'N');
        }
        public void WorkAroundForOTA(string path)
        {
            //Uri url = wbAppzillon.BuildLocalStreamUri("MyTag", path);
            //StreamUriWinRTResolver myResolver = new StreamUriWinRTResolver();

            // Pass the resolver object to the navigate call.
            //wbAppzillon.NavigateToLocalStreamUri(url, myResolver);
        }
        #endregion

        public void LaunchWebView(string appid)
        {
            try
            {
                string path = "ms-appx-web:///appzillonapps/" + appid + ".html";
                Uri uri = new Uri(path, UriKind.Absolute);
                wbAppzillon.Navigate(uri);
                AdjustWebView();
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message);
            }
        }


        //Entry Point for Javascript 
        private void wbAppzillon_ScriptNotify(object sender, NotifyEventArgs e)
        {
            try
            {
                request.ExecuteCommand(e.Value);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message);
            }
        }

        public async void InvokeScript(string jScriptFn, string json)
        {
            try
            {
                await wbAppzillon.InvokeScriptAsync("callFunctionOrNamespace", new[] { jScriptFn, json });
            }
            catch (Exception ex)
            {
                AppzillonDebug.NativeConsole(ex.Message);      //purposefully left unchanged since changing it to log.error can cause infinite loop in certain cases
            }
        }
        #region Events definition for Application
        private void CurrentWindow_SizeChanged(object sender, Windows.UI.Core.WindowSizeChangedEventArgs e)
        {
            AdjustWebView();
        }
        private void AdjustWebView()
        {
            wbAppzillon.Width = Window.Current.Bounds.Width;
            wbAppzillon.Height = Window.Current.Bounds.Height;
        }
        //----------------------Events---------------------------------------------
        private void Current_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            Log.FatalError("Unhandled Exception Occured");
        }

        private void Current_Suspending(object sender, Windows.ApplicationModel.SuspendingEventArgs e)
        {
            Log.Debug("MainApp Suspended");
        }

        private void Current_Resuming(object sender, object e)
        {
            Log.Debug("MainApp is resuming");
        }
        #endregion

        //----------------------------------override Methods-----------------------
        #region Override method of base class Page
        protected override void OnDoubleTapped(DoubleTappedRoutedEventArgs e)
        {
            base.OnDoubleTapped(e);
        }
        protected override void OnNavigatedFrom(NavigationEventArgs e)
        {
            base.OnNavigatedFrom(e);
        }
        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
        }
        protected override void OnNavigatingFrom(NavigatingCancelEventArgs e)
        {
            base.OnNavigatingFrom(e);
        }
        #endregion
        /* public void Launchhelper(string s)
         {
             CurrentAppId = s;
            AppSettings.SetToPersistance();
            OTARefresh.LaunchWebViewOverride(this, wbAppzillon, s);
         }*/
        private void onbackpressed(object sender, BackRequestedEventArgs e)
        {
            e.Handled = true;
            string scrid = "";
            try
            {
                scrid = AppSettings.AppProperties.GetValue("firstPage").ToString();
            }
            catch (Exception) { }
            var js = new JObject();
            js["firstPage"] = scrid;
            InvokeScript("backPressed_evt", js.ToString());
        }
        public static void OnScrBackPressed(string scrid)
        {

            string startpage = AppSettings.AppProperties["firstPage"].ToString();
            if (scrid == startpage)
            {
                Application.Current.Exit();
            }
        }
    }
}