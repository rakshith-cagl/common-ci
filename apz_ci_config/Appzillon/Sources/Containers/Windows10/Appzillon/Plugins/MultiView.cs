using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace Appzillon.Plugins
{
    class MultiView
    {
        private int currviewid = 0;
        private int defviewid;
        private WebView wbbrowser;
        private string id = "";

        #region Singleton Pattern
        private static MultiView instance;
        public MultiView()
        {
        }
        public static MultiView Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new MultiView();
                }
                return instance;
            }
        }
        #endregion

        public void launch(JObject json, MainPage lobject)
        {
            id = json[JsonKey.ID].ToString();
            try
            {
                WebView wbapp = (WebView)lobject.FindName("wbAppzillon");
                wbbrowser = new WebView();
                Grid appGrid = (Grid)lobject.FindName("MainGrid");
                appGrid.Children.Add(wbbrowser);
                string location = json[JsonKey.LOCATION].ToString();
                int percentage = int.Parse(json[JsonKey.PERCENTAGE].ToString());
                string targetView = json[JsonKey.TARGET_VIEW].ToString();
                string launchPage = json[JsonKey.LAUNCH_PAGE].ToString();
                defviewid = ApplicationView.GetForCurrentView().Id;
                var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
                var ht = bounds.Height;
                var wt = bounds.Width;
                double factor = (Double)percentage / 100;
                if (location == JsonKey.VERTICAL)
                {
                    wbapp.Height = ht;
                    wbbrowser.Height = ht;
                    wbapp.Width = wt * (1 - factor);
                    wbbrowser.Width = wt * (factor);
                    wbapp.HorizontalAlignment = HorizontalAlignment.Left;
                    wbbrowser.HorizontalAlignment = HorizontalAlignment.Right;
                }
                else
                {
                    wbapp.Height = ht * (1 - factor);
                    wbbrowser.Height = ht * factor;
                    wbapp.Width = wt;
                    wbbrowser.Width = wt;
                    wbapp.VerticalAlignment = VerticalAlignment.Top;
                    wbbrowser.VerticalAlignment = VerticalAlignment.Bottom;
                }
                wbbrowser.Visibility = 0;
                Uri intweburl = new Uri(launchPage);
                wbbrowser.Navigate(intweburl);
                JObject jn = new JObject();
                jn[JsonKey.SUCCESS] = "success";
                Response.Success(id,jn);
            }
            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.MULTIVIEW_LOAD_FAIL);
                Log.Error(e.Message);
            }
        }

        internal void CloseBrowser(JObject json, MainPage lobject)
        {
            string id = json[JsonKey.ID].ToString();
            try
            {
                WebView wbapp = (WebView)lobject.FindName("wbAppzillon");
                var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
                wbapp.Height = bounds.Height;
                wbapp.Width = bounds.Width;
                Grid appGrid = (Grid)lobject.FindName("MainGrid");
                appGrid.Children.Remove(wbbrowser);
                JObject jn = new JObject();
                jn[JsonKey.SUCCESS] = "success";
                Response.Success(id,jn);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.MULTIVIEW_CLOSE_FAIL);
                Log.Error(e.Message);
            }
        }
    }
}
