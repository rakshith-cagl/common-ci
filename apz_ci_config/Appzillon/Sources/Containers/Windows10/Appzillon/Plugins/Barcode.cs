using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Graphics.Imaging;
using Windows.Media.Capture;
using Windows.Media.MediaProperties;
using Windows.Storage.Streams;
using Windows.System.Profile;
using Windows.UI.Core;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Imaging;
using ZXing;

namespace Appzillon.Plugins
{
    class Barcode
    {
#region Singleton Pattern
        private static Barcode instance;
        private Barcode()
        {
        }
        public static Barcode Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Barcode();
                }
                return instance;
            }
        }
        #endregion
        private MainPage ect;
        private string id = "";
        private MediaCapture mcap;
        private int i;

        public async void StartScan(JObject obj, MainPage page)
        {
            ect = page;
            id = obj[JsonKey.ID].ToString();
            string text = null;
            mcap = new MediaCapture();
            try
            {
                await mcap.InitializeAsync();
            }
            catch (Exception)
            {
                Response.Fail(id, ErrorCode.CAMERA_ACCESS_DENIED);
                return;
            }
            var mgcam = (Grid)page.FindName("MainGrid");
            var ncanvas = new Canvas();
            ncanvas.Name = "BarCodeCanvas";
            var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
            double ht = bounds.Height;
            double wt = bounds.Width;
            ncanvas.Visibility = Windows.UI.Xaml.Visibility.Visible;
            var capele = new CaptureElement();
            capele.Visibility = Windows.UI.Xaml.Visibility.Visible;
            ncanvas.Children.Add(capele);
            mgcam.Children.Add(ncanvas);
            WebView wbAppzillon = (WebView)page.FindName("wbAppzillon");
            Canvas.SetZIndex(wbAppzillon, 0);
            Canvas.SetZIndex(ncanvas, 2);
            if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
            {
                capele.Width = wt;
                capele.Height = ht;
                ncanvas.Height = ht;
                ncanvas.Width = wt;
                capele.HorizontalAlignment = HorizontalAlignment.Center;
                capele.VerticalAlignment = VerticalAlignment.Center;
                ncanvas.Background = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.Gray);
            }
            else
            {
                capele.Height = ht/2;
                capele.Width = wt/2;
                ncanvas.Height = ht / 2;
                ncanvas.Width = wt/2;
                ncanvas.Background = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.White);
                ncanvas.HorizontalAlignment = HorizontalAlignment.Center;
                ncanvas.VerticalAlignment = VerticalAlignment.Center;
            }
            capele.Source = mcap;
            await mcap.StartPreviewAsync();
            var barcodereader = new ZXing.BarcodeReader();
            barcodereader.AutoRotate = true;
          //  barcodereader.Options.TryHarder = true;
            barcodereader.TryInverted = true;

            bool focusAvailable = true;
            try
            {
                var focusSettings = new Windows.Media.Devices.FocusSettings();
                focusSettings.AutoFocusRange = Windows.Media.Devices.AutoFocusRange.FullRange;
                focusSettings.WaitForFocus = true;
                mcap.VideoDeviceController.FocusControl.Configure(focusSettings);
            }
            catch (Exception e)
            {
                focusAvailable = false;
            }
            
            Result res = null;
       
            i = 0;
            SystemNavigationManager.GetForCurrentView().BackRequested += onbackpressed_Evt;
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
           AppViewBackButtonVisibility.Visible;
           var photoprops = ImageEncodingProperties.CreateJpeg();
            while (res == null && i < 200)
            { if (focusAvailable)
                    await mcap.VideoDeviceController.FocusControl.FocusAsync();
                using (var str = new InMemoryRandomAccessStream())
                {
                    try
                    {
                        await mcap.CapturePhotoToStreamAsync(photoprops, str);
                        var bitmap = await BitmapDecoder.CreateAsync(str);
                        var wbmp = new WriteableBitmap((int)bitmap.PixelWidth, (int)bitmap.PixelHeight);
                        str.Seek(0);
                        await wbmp.SetSourceAsync(str);
                        res = barcodereader.Decode(wbmp);
                        i++;
                    }
                    catch (Exception e)
                    {
                        return;
                    }

                }
            }
            mcap.Dispose();
            mgcam.Children.Remove(ncanvas);
            Canvas.SetZIndex(wbAppzillon, 2);
            SystemNavigationManager.GetForCurrentView().BackRequested -= onbackpressed_Evt;
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
            AppViewBackButtonVisibility.Collapsed;
            if (res == null)
            {
              
                Response.Fail(id, ErrorCode.BARCODE_READ_FAIL);
                return;
            }
            else
            {
                var js = new JObject();
                js["text"] = res.Text;
                Response.Success(id,js);
            }     
            /*   MobileBarcodeScanner scanner = new MobileBarcodeScanner(Window.Current.Dispatcher);
               scanner.UseCustomOverlay = false;
               scanner.TopText = "Hold camera near barcode to start";
               scanner.BottomText = "Tap anywhere to focus Manually";
               try
               {
                   await scanner.Scan().ContinueWith(async t =>
                      {
                          if (t.Result != null)
                          {   
                              await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
                              {
                                  JObject js = new JObject();
                                  js[JsonKey.TEXT] = t.Result.Text;
                                  Response.Success(id, js);
                              });
                          }
                          else
                          {
                              Response.Fail(id, ErrorCode.BARCODE_READ_FAIL);
                              Log.Error("BarCode Reading Failed");
                          }

                      });

               }
               catch (Exception e)
               {
                   Response.Fail(id, ErrorCode.BARCODE_READ_FAIL);
                    Log.Error(e.Message);
               }*/
        }

        private void onbackpressed_Evt(object sender, BackRequestedEventArgs e)
        {
            i = 10000;
            e.Handled = true;          
        }
    }
}
