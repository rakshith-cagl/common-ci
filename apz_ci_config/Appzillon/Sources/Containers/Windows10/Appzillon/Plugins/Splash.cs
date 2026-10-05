using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Graphics.Display;
using Windows.System.Profile;
using Windows.UI;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

namespace Appzillon.Plugins
{
    class Splash
    {
        private Image splashCtrl;

        #region Singleton Pattern
        private static Splash instance;
        public Splash()
        {
        }
        public static Splash Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Splash();
                }
                return instance;
            }
        }
        #endregion

        internal void ShowSplash(MainPage lobject)
        {
            //string id = obj[JsonKey.ID].ToString();
            WebView wb = (WebView)lobject.FindName("wbAppzillon");
            Grid mgrid = (Grid)lobject.FindName("MainGrid");
            splashCtrl = new Image();
            Canvas.SetZIndex(wb, -2);
            DisplayOrientations or = DisplayInformation.GetForCurrentView().CurrentOrientation;
            if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile" && (or == DisplayOrientations.Portrait || or == DisplayOrientations.PortraitFlipped))
            {
                splashCtrl.Source = new BitmapImage(new Uri("ms-appx:///Assets/SplashPortrait.png"));
            }
            else
            {
                splashCtrl.Source = new BitmapImage(new Uri("ms-appx:///Assets/SplashLandscape.png"));
            }
            if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
            {
                splashCtrl.Stretch = Windows.UI.Xaml.Media.Stretch.Fill;
                Canvas.SetZIndex(splashCtrl, 10);
            }

            else
                splashCtrl.Stretch = Stretch.None;
            splashCtrl.Visibility = Windows.UI.Xaml.Visibility.Visible;
            mgrid.Children.Add(splashCtrl);
            Canvas.SetZIndex(splashCtrl, 10);
            DisplayInformation.GetForCurrentView().OrientationChanged += Splash_OrientationChanged;
        }

        private void Splash_OrientationChanged(DisplayInformation sender, object args)
        {
            DisplayOrientations or = sender.CurrentOrientation;
            if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile" && (or == DisplayOrientations.Portrait || or == DisplayOrientations.PortraitFlipped))
            {
                splashCtrl.Source = new BitmapImage(new Uri("ms-appx:///Assets/SplashPortrait.png"));
            }
            else
            {
                splashCtrl.Source = new BitmapImage(new Uri("ms-appx:///Assets/SplashLandscape.png"));
            }
        }

        internal void HideSplash(JObject obj, MainPage lobject)
        {
            string id = obj[JsonKey.ID].ToString();
            WebView wb = (WebView)lobject.FindName("wbAppzillon");
            Canvas.SetZIndex(wb, 1);
            Canvas.SetZIndex(splashCtrl, -1);
            splashCtrl.Visibility = Windows.UI.Xaml.Visibility.Collapsed;
            wb.Visibility = Windows.UI.Xaml.Visibility.Visible;
            Grid mgrid = (Grid)lobject.FindName("MainGrid");
      
            mgrid.Children.Remove(splashCtrl);
           
            DisplayInformation.GetForCurrentView().OrientationChanged -= Splash_OrientationChanged;
        
        }
    }
}
