using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.System.Profile;
using Windows.UI;
using Windows.UI.Core;
using Windows.UI.Input.Inking;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;

namespace Appzillon.Plugins
{
    class SignaturePad
    {
        private MainPage ect;
        private Popup pop;
        private Canvas hcanvas;
        private InkCanvas ink;
        private InkPresenter dboard;
        private Grid MainGrid;
        private string id;

#region Singleton Pattern
        private static SignaturePad instance;
        private SignaturePad()
        {
        }
        public static SignaturePad Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new SignaturePad();
                }
                return instance;
            }
        }
#endregion

        public void StartSignPad(JObject obj, MainPage lobject)
        {
            id = obj[JsonKey.ID].ToString();
            try
            {
                ect = lobject;
                MainGrid = (Grid)lobject.FindName("MainGrid");
                pop = new Popup();
                hcanvas = new Canvas();
                hcanvas.Background = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.White);
                Button clear = new Button();
                Button accept = new Button();
                Button cancel = new Button();
                clear.Width = 64;
                accept.Width = 64;
                cancel.Width = 64;
                clear.Height = 40;
                accept.Height = 40;
                cancel.Height = 40;
                clear.Content = "Clear";
                accept.Content = "Accept";
                cancel.Content = "Cancel";
                clear.Click += SignClear;
                accept.Click += SignAccept;
                cancel.Click += SignCancel;
                Border border = new Border();
                border.BorderBrush = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.Black);
                ink = new InkCanvas();
                border.Child = ink;
                hcanvas.Children.Add(border);
                hcanvas.Children.Add(clear);
                hcanvas.Children.Add(accept);
                hcanvas.Children.Add(cancel);
                pop.Child = hcanvas;
                MainGrid.Children.Add(pop);
                var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
                var ht = bounds.Height;
                var wt = bounds.Width;
                if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
                {
                    pop.Width = wt;
                    pop.Height = ht;
                    hcanvas.Width = wt;
                    hcanvas.Height = ht;
                    ink.Height = ht - 70;
                    ink.Width = wt - 5;
                    border.Height = ht - 64;
                    border.Width = wt;
                    Canvas.SetLeft(clear, 10);
                    Canvas.SetTop(clear, ht - 50);
                    Canvas.SetLeft(accept, wt / 3);
                    Canvas.SetTop(accept, ht - 50);
                    Canvas.SetLeft(cancel, 2 * wt / 3);
                    Canvas.SetTop(cancel, ht - 50);

                }
                else
                {
                    pop.Width = 500;
                    pop.Height = 500;
                    hcanvas.Width = 500;
                    hcanvas.Height = 500;
                    ink.Height = 430;
                    ink.Width = 495;
                    Canvas.SetLeft(clear, 10);
                    Canvas.SetTop(clear, 460);
                    Canvas.SetLeft(accept, 250);
                    Canvas.SetTop(accept, 460);
                    Canvas.SetLeft(cancel, 425);
                    Canvas.SetTop(cancel, 460);
                    ink.Opacity = 1;
                    border.Height = 436;
                    border.Width = 500;


                }
                border.BorderThickness = new Thickness(2);
                pop.IsOpen = true;
                dboard = ink.InkPresenter;
                dboard.InputDeviceTypes = CoreInputDeviceTypes.Mouse |
                                                CoreInputDeviceTypes.Pen |
                                                CoreInputDeviceTypes.Touch;
                InkDrawingAttributes setting = dboard.CopyDefaultDrawingAttributes();
                setting.Color = Colors.Black;
                setting.Size = new Size(2, 4);
                dboard.UpdateDefaultDrawingAttributes(setting);

            }
            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.FAILED_TO_LOAD_SIGNATURE_PAD);
                Log.Error("Signature Pad , "+ e.Message);
            }
        }

        private void SignCancel(object sender, RoutedEventArgs e)
        {
            dboard.StrokeContainer.Clear();
            pop.IsOpen = false;
            MainGrid.Children.Remove(pop);
        }

        private async void SignAccept(object sender, RoutedEventArgs e)
        {
            try
            {

                string fileName = "signature.gif";
                StorageFile newfile = await ApplicationData.Current.LocalFolder.CreateFileAsync(fileName, CreationCollisionOption.ReplaceExisting);


                using (IRandomAccessStream stream = await newfile.OpenAsync(FileAccessMode.ReadWrite))
                {
                    await dboard.StrokeContainer.SaveAsync(stream);
                }
                Byte[] bytes = File.ReadAllBytes(newfile.Path);
                String result = Convert.ToBase64String(bytes);
                dboard.StrokeContainer.Clear();
                JObject jn = new JObject();
                jn[JsonKey.ENCODED_IMAGE] = result;
                pop.IsOpen = false;
                Response.Success(id,jn);
                MainGrid.Children.Remove(pop);
            }
            catch (Exception ex)
            {
                Response.Fail(id,ErrorCode.FAILED_TO_ACCEPT_SIGNATURE);
                Log.Error("Signature Pad , " + ex.Message);
            }
        }

        private void SignClear(object sender, RoutedEventArgs e)
        {
            try
            {
                dboard.StrokeContainer.Clear();
            }
            catch (Exception ex)
            {
                Log.Error("Signature Pad , " + ex.Message);
            }
        }
    }
}
