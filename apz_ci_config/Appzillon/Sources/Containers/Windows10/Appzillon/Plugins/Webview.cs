using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.System.Profile;
using Windows.UI.Core;
using Windows.UI.Xaml.Controls;

namespace Appzillon.Plugins
{
    class Webview
    {
#region Singleton Pattern
        private static Webview instance;
        private Webview()
        {
        }
        public static Webview Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Webview();
                }
                return instance;
            }
        }
#endregion

        private string id = "";
        private MainPage ect;
        internal void LaunchWebview(JObject obj,MainPage lobject) {
            id = obj[JsonKey.ID].ToString();
            ect = lobject;
            try
            {
                string URL = obj[JsonKey.URL_WEBVIEW].ToString();
                WebView mainscr = (WebView)lobject.FindName("wbAppzillon");
                Grid maingrid = (Grid)lobject.FindName("MainGrid");
                WebView subscr = new WebView();
                subscr.Name = "subscreen";
                Canvas.SetZIndex(mainscr, -2);
                maingrid.Children.Add(subscr);
                Canvas.SetZIndex(subscr, 2);
                subscr.Visibility = Windows.UI.Xaml.Visibility.Visible;
                mainscr.Visibility = Windows.UI.Xaml.Visibility.Collapsed;
                subscr.Navigate(new Uri(URL));
                subscr.LoadCompleted += Subscr_LoadCompleted;
                SystemNavigationManager.GetForCurrentView().BackRequested += onbackpressed_Evt;
                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
               AppViewBackButtonVisibility.Visible;
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.WEBVIEW_LAUNCH_FAIL);
                Log.Error(e.Message);
            }
        }

        private void onbackpressed_Evt(object sender, BackRequestedEventArgs e)
        {
            SystemNavigationManager.GetForCurrentView().BackRequested -= onbackpressed_Evt;
            CloseWebview();
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
              AppViewBackButtonVisibility.Collapsed;
            e.Handled = true;
        }

        private void Subscr_LoadCompleted(object sender, Windows.UI.Xaml.Navigation.NavigationEventArgs e)
        {
            var j = new JObject();
            j["URL"] = e.Uri.ToString();
            j[JsonKey.TEXT] = j["URL"];
            Response.Success(id,true,j);
        }

        internal void CloseWebview() {
            WebView mainscr = (WebView)ect.FindName("wbAppzillon");
            Grid maingrid = (Grid)ect.FindName("MainGrid");
            mainscr.Visibility = Windows.UI.Xaml.Visibility.Visible;
            WebView subscr = (WebView)ect.FindName("subscreen");
            subscr.Visibility = Windows.UI.Xaml.Visibility.Collapsed;
            Canvas.SetZIndex(subscr, -2);
            Canvas.SetZIndex(mainscr, 1);
            maingrid.Children.Remove(subscr);
            subscr = null;
        }
    }
}
