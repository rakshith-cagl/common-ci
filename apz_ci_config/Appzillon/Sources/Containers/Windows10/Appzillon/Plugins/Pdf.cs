using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Pdf;
using Windows.Foundation;
using Windows.Graphics.Display;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.System.Profile;
using Windows.UI.Core;
using Windows.UI.Input;
using Windows.UI.Text;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;

namespace Appzillon.Plugins
{
    class Pdf
    {
        private static Rect bounds;
        private static MainPage ect;
        private static StorageFile tempfile;
        private static PdfDocument pdfdoc;
        private static Image img;
        private static uint index = 0;
        private static uint max = 0;
        private static bool busy = false;
        private static double startx = 0;
        private static bool istouchenabled = false;
        internal static async void PdfViewer(JObject obj, MainPage lobject)
        {
            ect = lobject;
            if (new Windows.Devices.Input.TouchCapabilities().TouchPresent > 0)
            {
                istouchenabled = true;

            }

            StorageFile pdf_file = null;
            string newfilepath = obj[JsonKey.FILE_PATH].ToString();
            FileInfo file = new FileInfo(newfilepath);
            if (!file.Exists)
            {                              
                newfilepath = Filepathresolver.PathResolver(newfilepath);               
                FileInfo fip = new FileInfo(newfilepath);
                if (!fip.Exists)
                {
                    Response.Fail(obj[JsonKey.ID].ToString(), ErrorCode.FILE_DOES_NOT_EXISTS);
                    return;
                }
                file = fip;
            }
            if (file.Extension != ".pdf")
            {
                {
                    Response.Fail(obj[JsonKey.ID].ToString(), "APZ-CNT-022");
                    return;
                }
            }
            StorageFolder localFolder = ApplicationData.Current.LocalFolder;
            try
            {
                //  Uri ur = new Uri(newfilepath);
                pdf_file = await StorageFile.GetFileFromPathAsync(newfilepath);
                pdfdoc = await PdfDocument.LoadFromFileAsync(pdf_file);
                max = pdfdoc.PageCount;
                bounds = ApplicationView.GetForCurrentView().VisibleBounds;
                tempfile = await ApplicationData.Current.TemporaryFolder.CreateFileAsync(Guid.NewGuid().ToString() + ".jpeg", CreationCollisionOption.ReplaceExisting);
                SetView();
                await GetPage(0);
            }
            catch (Exception e)
            {
                Response.Fail(obj[JsonKey.ID].ToString(), ErrorCode.FILE_GET_FAIL);
                Log.FatalError(e.Message);
            }
        }
        private static async Task GetPage(uint pageno)
        {
            //   if (istouchenabled)
            //   {
            //       mgr.ManipulationDelta -= Mgr_ManipulationDelta;
            //   }
            if (pdfdoc != null && pdfdoc.PageCount > 0)
            {
                PdfPage page = pdfdoc.GetPage(pageno);

                if (tempfile != null)
                {
                    busy = true;
                    using (IRandomAccessStream randomStream = await tempfile.OpenAsync(FileAccessMode.ReadWrite))
                    {
                        await page.RenderToStreamAsync(randomStream);
                        await randomStream.FlushAsync();
                        page.Dispose();
                    }
                    await DisplayImage(tempfile);
                }
            }

        }
        private static async Task DisplayImage(StorageFile pdfimg)
        {
            BitmapImage src = new BitmapImage();
            src.SetSource(await pdfimg.OpenAsync(FileAccessMode.Read));
            img.Source = src;
            busy = false;
            //   if (istouchenabled)
            //  {
            //      mgr.ManipulationDelta += Mgr_ManipulationDelta;
            //   }
        }

        private static void SetView()
        {
            double ht = bounds.Height;
            double wt = bounds.Width;
            Grid mgr = (Grid)ect.FindName("MainGrid");
            Canvas overlay = new Canvas();
            overlay.Name = "PDFView";
            Canvas.SetZIndex(overlay, 999);
            overlay.Height = ht;
            overlay.Width = wt;
            overlay.Opacity = 1;
            if (!istouchenabled)
            {
                Button nextbt = new Button();
                Button prebt = new Button();
                nextbt.Click += Nextbt_Click;
                prebt.Click += Prebt_Click;
                nextbt.Width = 60;
                nextbt.Height = 45;
                prebt.Width = 60;
                prebt.Height = 45;
                nextbt.Content = ">";
                nextbt.FontWeight = FontWeights.Bold;
                prebt.Content = "<";
                overlay.Children.Add(prebt);
                overlay.Children.Add(nextbt);
                Canvas.SetLeft(nextbt, bounds.Right - nextbt.Width);
                Canvas.SetTop(nextbt, ht - 100);
                Canvas.SetTop(prebt, ht - 100);
            }
            else
            {
                mgr.PointerPressed += Mgr_PointerPressed;
                mgr.PointerReleased += Mgr_PointerReleased;
                // Canvas.SetLeft(number, bounds.Right - number.Width);
            }
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += Closedocument;
            // Show UI in title bar if opted-in and in-app backstack is not empty.
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
                AppViewBackButtonVisibility.Visible;
            WebView wbApp = (WebView)ect.FindName("wbAppzillon");
            wbApp.Visibility = Windows.UI.Xaml.Visibility.Collapsed;
            mgr.Background = new SolidColorBrush(Windows.UI.Colors.Gray);
            img = new Image();
            mgr.Children.Add(img);
            Canvas.SetZIndex(img, 3);
            mgr.Children.Add(overlay);

        }

        private static void Closedocument(object sender, BackRequestedEventArgs e)
        {
            Grid mgr = (Grid)ect.FindName("MainGrid");
            WebView wbApp = (WebView)ect.FindName("wbAppzillon");
            wbApp.Visibility = Windows.UI.Xaml.Visibility.Visible;
            mgr.Background = new SolidColorBrush(Windows.UI.Colors.Transparent);
            mgr.Children.Remove(img);
            Canvas.SetZIndex(img, -2);
            Canvas.SetZIndex(wbApp, 2);
            Canvas overlay = (Canvas)ect.FindName("PDFView");
            mgr.Children.Remove(overlay);
            pdfdoc = null;
            e.Handled = true;
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested -= Closedocument;
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
                AppViewBackButtonVisibility.Collapsed;
        }

        private static async void Mgr_PointerReleased(object sender, Windows.UI.Xaml.Input.PointerRoutedEventArgs e)
        {
            if (busy)
                return;
            double dx = e.GetCurrentPoint(null).Position.X - startx;
            if (Math.Abs(dx) > 100)
            {

                await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
                {
                    if (dx > 0)
                    {
                        index--;
                        if (index >= 0 && index < max)
                        {
                            await GetPage(index);
                        }
                        else
                        {
                            index = 0;
                           Log.Info("at the start of the document");
                        }
                    }
                    else
                    {
                        index++;
                        if (index < max && index > 0)
                        {
                            await GetPage(index);
                        }
                        else
                        {
                            index = max;
                            Log.Info("at the end of the document");

                        }
                    }

                });
            }
        }

        private static void Mgr_PointerPressed(object sender, Windows.UI.Xaml.Input.PointerRoutedEventArgs e)
        {
            startx = e.GetCurrentPoint(null).Position.X;
        }

        private static async void Mgr_ManipulationDelta(object sender, Windows.UI.Xaml.Input.ManipulationDeltaRoutedEventArgs e)
        {

            double x = e.Cumulative.Translation.X;
            if (Math.Abs(x) > 100)
            {
                if (busy)
                    return;
                await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
                {
                    if (x > 0)
                    {
                        index--;
                        if (index >= 0 && index < max)
                        {
                            await GetPage(index);
                        }
                        else
                        {
                            index = 0;
                            Log.Info("at the start of the document");
                        }
                    }
                    else
                    {
                        index++;
                        if (index < max && index > 0)
                        {
                            await GetPage(index);
                        }
                        else
                        {
                            index = max;
                            Log.Info("at the end of the document");

                        }
                    }

                });


            }
        }

        private static async void Mgr_ManipulationCompleted(object sender, Windows.UI.Xaml.Input.ManipulationCompletedRoutedEventArgs e)
        {
            if (busy)
                return;
            double x = e.Cumulative.Translation.X;
            if (Math.Abs(x) > 90)
            {

                await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
                {
                    if (x > 0)
                    {
                        index--;
                        if (index >= 0 && index < max)
                        {
                            await GetPage(index);
                        }
                        else
                        {
                            index = 0;
                            Log.Debug("at the start of the document");
                        }
                    }
                    else
                    {
                        index++;
                        if (index < max && index > 0)
                        {
                            await GetPage(index);
                        }
                        else
                        {
                            index = max;
                            Log.Debug("at the end of the document");

                        }
                    }
                });


            }
        }
        private static async void Nextbt_Click(object sender, Windows.UI.Xaml.RoutedEventArgs e)
        {
            if (busy)
                return;
            index++;
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
            {
                if (index < max && index > 0)
                {
                    await GetPage(index);
                }
                else
                {
                    index = max;
                    Log.Debug("at the end of the document");

                }
            });
        }

        private static async void Prebt_Click(object sender, Windows.UI.Xaml.RoutedEventArgs e)
        {
            if (busy)
                return;
            index--;
            await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, async () =>
            {
                if (index >= 0 && index < max)
                {
                    await GetPage(index);
                }
                else
                {
                    index = 0;
                    Log.Debug("at the start of the document");
                }
            });
        }
    }
}
