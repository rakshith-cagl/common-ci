using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Enumeration;
using Windows.Foundation;
using Windows.Graphics.Imaging;
using Windows.Media.Capture;
using Windows.Media.MediaProperties;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.System.Profile;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Shapes;
using Windows.UI.Core;
using Windows.Storage.Pickers;

namespace Appzillon.Plugins
{
    class Camera
    {
        public MediaCapture mcap;
        public Canvas ncanvas;
        public Grid mgcam;
        public CaptureElement capele;
        public Button bt;
        public Button bt1;
        public bool Drawing = false;
        public Point StartPoint, EndPoint;
        public Image img;
        public Rectangle nrect;
        public bool isdrawn = false;
        public WriteableBitmap wbm;
        public IRandomAccessStream str;
        public StorageFile photofile;
        public CameraCaptureUI camui;
        public StorageFile croppedfile;
        public string filename = "";
        public MainPage ect;
        public string id;
        public string action = "";
        public string encoding = "jpeg";
        public string compressionLevel = "0";
        public bool uireq = true;
        public bool crop = false;
        public uint scaledht = 816;
        public uint scaledwdt= 612;
        public bool calcht = false;
        public bool calcwt = false;
        #region Singleton Pattern
        private static Camera instance;
        private Camera()
        {
        }
        public static Camera Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Camera();
                }
                return instance;
            }
        }
        #endregion

        public async void InitCamera(JObject camjson, MainPage lobject)
        {
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += onbackpressed;
            id = camjson[JsonKey.ID].ToString();
            try
            {
                action = camjson[JsonKey.ACTION].ToString();
            }
            catch (Exception)
            {
                action = "save_base64";
            }
           
             string nativecam = camjson["openNativeCamera"].ToString();
            filename = camjson[JsonKey.FILE_NAME].ToString();
            compressionLevel = camjson["quality"].ToString();
            encoding = camjson["encodingType"].ToString();
            ect = lobject;
            crop = camjson["crop"].ToString() == "Y" ? true : false;
                try
                {
                    scaledht = uint.Parse(camjson["targetHeight"].ToString());
                }
                catch (Exception) { calcht = true; }
                try
                {
                    scaledwdt = uint.Parse(camjson["targetWidth"].ToString());
                }
                catch (Exception) { calcwt = true; }

            if (AnalyticsInfo.VersionInfo.DeviceFamily != "Windows.Mobile" && nativecam == "Y")
            {
                uireq = false;
            }
           
            if (uireq)
            {
                mgcam = (Grid)lobject.FindName("MainGrid");
                ncanvas = new Canvas();
                var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
                double ht = bounds.Height;
                double wt = bounds.Width;
                ncanvas.Visibility = Windows.UI.Xaml.Visibility.Visible;
                capele = new CaptureElement();
                bt = new Button();
                bt.Width = 90;
                bt.Height = 40;
                bt.Click += ClickShot;
                bt.Content = "Capture";
                bt1 = new Button();
                bt1.Content = "Done";
                bt1.Width = 60;
                bt1.Height = 40;
                bt1.Click += OnCroppingDone;
                capele.Visibility = Windows.UI.Xaml.Visibility.Visible;
                ncanvas.Children.Add(bt);
                ncanvas.Children.Add(capele);
                mgcam.Children.Add(ncanvas);
                WebView wbAppzillon = (WebView)lobject.FindName("wbAppzillon");
                Canvas.SetZIndex(wbAppzillon, 0);
                Canvas.SetZIndex(ncanvas, 2);
                if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
                {
                    capele.Width = wt;
                    capele.Height = ht - 50;
                    ncanvas.Height = ht;
                    ncanvas.Width = wt;
                    ncanvas.Background = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.White);
                    Canvas.SetLeft(bt, wt / 2.5);
                    Canvas.SetTop(bt, ht - 50);

                }
                else
                {
                    double hgt = (800) + 50 > ht ? ht : (800);
                    ncanvas.Height = hgt;
                    ncanvas.Width = wt / 2;
                    ncanvas.Background = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.White);
                    Canvas.SetLeft(bt, (ncanvas.Width) / 2);
                    Canvas.SetTop(bt, 500);
                }
            }
            try
            {
                if (camjson["sourceType"].ToString() == "Photo")
                {
                    FileOpenPicker picker = new FileOpenPicker();
                    picker.ViewMode = PickerViewMode.Thumbnail;
                    string[] PHOTO = { ".jpg", ".png" };
                    foreach (string ext in PHOTO)
                        picker.FileTypeFilter.Add(ext);
                    var cphotofile = await picker.PickSingleFileAsync();
                    if (cphotofile == null)
                    {
                        Response.Fail(id, ErrorCode.FILE_NOT_SELECTED);
                        return;
                    }
                    else
                    {
                        photofile = await cphotofile.CopyAsync(ApplicationData.Current.LocalFolder, cphotofile.Name, NameCollisionOption.GenerateUniqueName);
                        if (crop)
                        {
                            LoadphotoCrop();
                        }
                        else
                        {
                            croppedfile = photofile;
                            try
                            {
                                mgcam.Children.Remove(ncanvas);
                                WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                                Canvas.SetZIndex(wbAppzillon, 1);
                            }
                            catch (Exception) { }
                            OnCroppingDone(new object(), new RoutedEventArgs());
                        }
                    }
                    return;
                }
            }
            catch (Exception e)
            {
                Response.Fail(id, "");
                return;
            }
            try
            {
                string exte = "jpeg";
                StorageFolder local = ApplicationData.Current.LocalFolder;
                if (encoding == "png")
                {
                    exte = "png";
                }
                photofile = await local.CreateFileAsync(@"Tmp\" + "Test." + exte, CreationCollisionOption.GenerateUniqueName);

              
                if (nativecam == "N")
                {
                    MediaCaptureInitializationSettings settings;
                    if (camjson["sourceType"].ToString() == "Camera")
                    {
                        var allVideoDevices = await DeviceInformation.FindAllAsync(DeviceClass.VideoCapture);
                        DeviceInformation desiredDevice = allVideoDevices.FirstOrDefault(x => x.EnclosureLocation != null && x.EnclosureLocation.Panel == Windows.Devices.Enumeration.Panel.Front);
                        var cameraDevice = desiredDevice ?? allVideoDevices.FirstOrDefault();
                        settings = new MediaCaptureInitializationSettings { VideoDeviceId = cameraDevice.Id };
                    }
                    else
                    {
                        var allVideoDevices = await DeviceInformation.FindAllAsync(DeviceClass.VideoCapture);
                        DeviceInformation desiredDevice = allVideoDevices.FirstOrDefault(x => x.EnclosureLocation != null && x.EnclosureLocation.Panel == Windows.Devices.Enumeration.Panel.Back);
                        var cameraDevice = desiredDevice ?? allVideoDevices.FirstOrDefault();
                        settings = new MediaCaptureInitializationSettings { VideoDeviceId = cameraDevice.Id };
                    }
                    mcap = new MediaCapture();
                    try
                    {
                     //   double x = Double.Parse(camjson["zoomLevel"].ToString());
                      //  mcap.VideoDeviceController.Zoom.TrySetValue(x);
                    }
                    catch (Exception) { }
                    try
                    {
                        bool flash = mcap.VideoDeviceController.FlashControl.Supported;
                        if (flash && camjson["flash"].ToString() == "Y")
                            mcap.VideoDeviceController.FlashControl.Auto = true;
                    }
                    catch (Exception) { }
                    await mcap.InitializeAsync(settings);
                    capele.Source = mcap;
                    await mcap.StartPreviewAsync();
                }
                else
                {
                    camui = new CameraCaptureUI();
                    if (encoding == "png")
                        camui.PhotoSettings.Format = CameraCaptureUIPhotoFormat.Png;
                    else
                        camui.PhotoSettings.Format = CameraCaptureUIPhotoFormat.Jpeg;
                    photofile = await camui.CaptureFileAsync(CameraCaptureUIMode.Photo);
                    if (photofile == null)
                    {
                        try
                        {
                            ncanvas.Children.Remove(img);
                        }
                        catch (Exception) { }
                        try
                        {
                            ncanvas.Children.Remove(nrect);
                        }
                        catch (Exception) { }
                        try
                        {
                            ncanvas.Children.Remove(bt);
                        }
                        catch (Exception) { }
                        try
                        {
                            ncanvas.Children.Remove(bt1);
                        }
                        catch (Exception) { }
                        mgcam.Children.Remove(ncanvas);
                        WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                        Canvas.SetZIndex(wbAppzillon, 1);
                        Response.Fail(id, ErrorCode.PHOTO_CAPTURE_FAIL);
                        return;
                    }
                    if (crop)
                        LoadphotoCrop();
                    else
                    {
                        croppedfile = photofile;
                        mgcam.Children.Remove(ncanvas);
                        WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                        Canvas.SetZIndex(wbAppzillon, 1);
                        OnCroppingDone(new object(),new RoutedEventArgs());
                    }
                }
            }
            catch (Exception)
            {
                var allVideoDevices = await DeviceInformation.FindAllAsync(DeviceClass.VideoCapture);
                DeviceInformation desiredDevice = allVideoDevices.FirstOrDefault(x => x.EnclosureLocation != null && x.EnclosureLocation.Panel == Windows.Devices.Enumeration.Panel.Front);
                var cameraDevice = desiredDevice ?? allVideoDevices.FirstOrDefault();
                var settings = new MediaCaptureInitializationSettings { VideoDeviceId = cameraDevice.Id };
                mcap = new MediaCapture();
                await mcap.InitializeAsync();
                capele.Source = mcap;
                await mcap.StartPreviewAsync();
            }

        }

        private void onbackpressed(object sender, BackRequestedEventArgs e)
        {
            try
            {
                ncanvas.Children.Remove(img);
            }
            catch (Exception) { }
            try
            {
                ncanvas.Children.Remove(nrect);
            }
            catch (Exception) { }
            try
            {
                ncanvas.Children.Remove(bt);
            }
            catch (Exception) { }
            try
            {
                ncanvas.Children.Remove(bt1);
            }
            catch (Exception) { }
            mgcam.Children.Remove(ncanvas);
            WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
            Canvas.SetZIndex(wbAppzillon, 1);
            Response.Fail(id,ErrorCode.PHOTO_CAPTURE_FAIL);
        }

        private async void OnCroppingDone(object sender, RoutedEventArgs e)
        {
            try
            {
                try
                {
                    mgcam.Children.Remove(ncanvas);
                    WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                    Canvas.SetZIndex(wbAppzillon, 1);
                }
                catch (Exception) { }
                await ResizePhoto(croppedfile);
                string extn = System.IO.Path.GetExtension(croppedfile.Path);
                if (action == JsonKey.SAVE_BASE64)
                {
                    string path = ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\photos";
                    //
                    if (!Directory.Exists(path))
                    {
                        DirectoryInfo dirinfo = new DirectoryInfo(path);
                        dirinfo.Create();
                    }
                    StorageFolder photofolder = await StorageFolder.GetFolderFromPathAsync(path);
                    await croppedfile.MoveAsync(photofolder, filename + "." + extn, NameCollisionOption.ReplaceExisting);
                    StorageFile finalfile = await photofolder.GetFileAsync(filename + "." + extn);
                    var buff = await Windows.Storage.FileIO.ReadBufferAsync(finalfile);
                    var base64 = Windows.Security.Cryptography.CryptographicBuffer.EncodeToBase64String(buff);
                    JObject js = new JObject();
                    js[JsonKey.ENCODED_IMAGE] = base64;
                    await photofile.DeleteAsync();
                    Response.Success(id, js);
                }
                else if (action == JsonKey.BASE64)
                {
                    var buff = await Windows.Storage.FileIO.ReadBufferAsync(croppedfile);
                    var base64 = Windows.Security.Cryptography.CryptographicBuffer.EncodeToBase64String(buff);
                    JObject js = new JObject();
                    js[JsonKey.ENCODED_IMAGE] = base64;
                    await croppedfile.DeleteAsync();
                    await photofile.DeleteAsync();
                    Response.Success(id, js);
                }
                else
                {
                    string path = ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\photos";
                    //
                    if (!Directory.Exists(path))
                    {
                        DirectoryInfo dirinfo = new DirectoryInfo(path);
                        dirinfo.Create();
                    }
                    StorageFolder photofolder = await StorageFolder.GetFolderFromPathAsync(path);
                    await croppedfile.MoveAsync(photofolder, filename + "." + extn, NameCollisionOption.ReplaceExisting);
                    StorageFile finalfile = await photofolder.GetFileAsync(filename + "." + extn);
                    JObject js = new JObject();
                    js[JsonKey.SUCCESS_MESSAGE] = "saved successfully";
                    js[JsonKey.FILE_PATH] = finalfile.Path;
                    await photofile.DeleteAsync();
                    Response.Success(id, js);
                }              
            }
            catch (Exception)
            {
                Response.Fail(id, ErrorCode.PHOTO_CONVERTION_TO_BASE64_FAIL);
            }
        }

        internal async void StartVideoRecord(JObject obj, MainPage page)
        {
            string id = obj[JsonKey.ID].ToString();
            string overwrite = obj[JsonKey.OVERWRITE].ToString();
            string filename = obj[JsonKey.FILE_NAME].ToString();
            StorageFolder fold = ApplicationData.Current.LocalFolder;
            string path = fold.Path + "\\videos";
            string fp = filename + ".mp4";
            DirectoryInfo fi = new DirectoryInfo(path);

            FileInfo finfo = new FileInfo(path + fp);
            if (finfo.Exists)
            {
                if (overwrite == "Y")
                {
                    StorageFolder fod = await StorageFolder.GetFolderFromPathAsync(fi.FullName);
                    StorageFile f = await fod.CreateFileAsync(fp, CreationCollisionOption.ReplaceExisting);
                    path = f.Path;
                }
                else
                {
                    Response.Fail(id, ErrorCode.FILE_ALREADY_EXISTS);
                    return;
                }
            }
            else
            {
                if (!fi.Exists)
                    fi.Create();
                StorageFolder fod = await StorageFolder.GetFolderFromPathAsync(fi.FullName);
                StorageFile f = await fod.CreateFileAsync(fp, CreationCollisionOption.ReplaceExisting);
                path = f.Path;
            }
            camui = new CameraCaptureUI();
            camui.VideoSettings.Format = CameraCaptureUIVideoFormat.Mp4;
            StorageFile file = await StorageFile.GetFileFromPathAsync(path);
            var file_1 = file;
            file = await camui.CaptureFileAsync(CameraCaptureUIMode.Video);
            if (file != null)
            {
                if (file.Path != file_1.Path)
                {
                    await file.MoveAndReplaceAsync(file_1);
                }
                JObject js = new JObject();
                js["filePath"] = file_1.Path;
                Response.Success(id, js);
            }
            else
            {
                Response.Fail(id, ErrorCode.VIDEO_RECORDING_FAIL);
            }

        }

        private async void ClickShot(object sender, RoutedEventArgs e)
        {
            try
            {
                ImageEncodingProperties ipro;
                if (encoding == "png")
                    ipro = ImageEncodingProperties.CreatePng();
                else
                    ipro = ImageEncodingProperties.CreateJpeg();
                await mcap.CapturePhotoToStorageFileAsync(ipro, photofile);
                await mcap.StopPreviewAsync();
                mcap.Dispose();
                if (crop)
                    LoadphotoCrop();
                else
                {
                    croppedfile = photofile;
                    mgcam.Children.Remove(ncanvas);
                    WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                    Canvas.SetZIndex(wbAppzillon, 1);
                    OnCroppingDone(new object(), new RoutedEventArgs());

                }
            }
            catch (Exception en)
            {
                mgcam.Children.Remove(ncanvas);
                WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                Canvas.SetZIndex(wbAppzillon, 1);
                Response.Fail(id, ErrorCode.PHOTO_CAPTURE_FAIL);
            }
        }

        private async void LoadphotoCrop()
        {
            if (AnalyticsInfo.VersionInfo.DeviceFamily != "Windows.Mobile")
            {                
                string ext = "jpeg";
                croppedfile = await photofile.CopyAsync(ApplicationData.Current.LocalFolder, "new." + ext, NameCollisionOption.ReplaceExisting);
                await ResizePhoto(croppedfile);
                try
                {
                    if (uireq)
                    {try
                        {
                            mgcam.Children.Remove(ncanvas);
                            WebView wbAppzillon = (WebView)ect.FindName("wbAppzillon");
                            Canvas.SetZIndex(wbAppzillon, 1);
                        }
                        catch (Exception) { }
                    }
                    string extn = System.IO.Path.GetExtension(croppedfile.Path);
                    if (action == JsonKey.SAVE_BASE64)
                    {
                        string path = ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\photos";
                        //
                        if (!Directory.Exists(path))
                        {
                            DirectoryInfo dirinfo = new DirectoryInfo(path);
                            dirinfo.Create();
                        }
                        StorageFolder photofolder = await StorageFolder.GetFolderFromPathAsync(path);
                        await croppedfile.MoveAsync(photofolder, filename + "." + extn, NameCollisionOption.ReplaceExisting);
                        StorageFile finalfile = await photofolder.GetFileAsync(filename + "." + extn);
                        var buff = await Windows.Storage.FileIO.ReadBufferAsync(finalfile);
                        var base64 = Windows.Security.Cryptography.CryptographicBuffer.EncodeToBase64String(buff);
                        JObject js = new JObject();
                        js[JsonKey.ENCODED_IMAGE] = base64;
                        await photofile.DeleteAsync();
                        Response.Success(id, js);
                    }
                    else if (action == JsonKey.BASE64)
                    {
                        var buff = await Windows.Storage.FileIO.ReadBufferAsync(croppedfile);
                        var base64 = Windows.Security.Cryptography.CryptographicBuffer.EncodeToBase64String(buff);
                        JObject js = new JObject();
                        js[JsonKey.ENCODED_IMAGE] = base64;
                        await croppedfile.DeleteAsync();
                        await photofile.DeleteAsync();
                        Response.Success(id, js);
                    }
                    else
                    {
                        string path = ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\photos";
                        //
                        if (!Directory.Exists(path))
                        {
                            DirectoryInfo dirinfo = new DirectoryInfo(path);
                            dirinfo.Create();
                        }
                        StorageFolder photofolder = await StorageFolder.GetFolderFromPathAsync(path);
                        await croppedfile.MoveAsync(photofolder, filename + "." + extn, NameCollisionOption.ReplaceExisting);
                        StorageFile finalfile = await photofolder.GetFileAsync(filename + "." + extn);
                        JObject js = new JObject();
                        js[JsonKey.SUCCESS_MESSAGE] = "saved successfully";
                        js[JsonKey.FILE_PATH] = finalfile.Path;
                        await photofile.DeleteAsync();
                        Response.Success(id, js);
                    }
                }
                catch (Exception)
                {
                    Response.Fail(id, ErrorCode.PHOTO_CONVERTION_TO_BASE64_FAIL);
                }
                return;
            }
                try
            {
                ncanvas.Children.Remove(capele);
                bt.Click -= ClickShot;
                bt.Content = "save";
                bt.Click += Cropsave;
                img = new Image();
                ncanvas.Children.Add(img);
                var bound = ApplicationView.GetForCurrentView().VisibleBounds;
                double ht = bound.Height;
                double wt = bound.Width;
                img.Height = ht - 80;
                img.PointerPressed += Pointerpress;
                img.PointerMoved += PointerMoved;
                img.PointerReleased += PointerReleased;
                var imgdata = await photofile.OpenReadAsync();
                var decoder = await BitmapDecoder.CreateAsync(imgdata);
                int h = (int)decoder.PixelHeight;
                int w = (int)decoder.PixelWidth;
                nrect = new Rectangle();
                ncanvas.Children.Add(nrect);
                str = await photofile.OpenAsync(FileAccessMode.ReadWrite);
                img.Visibility = Visibility.Visible;
                wbm = new WriteableBitmap(w, h);
                await wbm.SetSourceAsync(str);
                img.Source = wbm;
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.PHOTO_LOAD_FAIL);
                Log.Error(e.Message);
            }
        }

        private void PointerReleased(object sender, Windows.UI.Xaml.Input.PointerRoutedEventArgs e)
        {
            EndPoint = e.GetCurrentPoint(img).Position;
            if (!Drawing) return;
            Drawing = false;
            DrawSelectionBox(EndPoint);
            isdrawn = true;
        }

        private void PointerMoved(object sender, Windows.UI.Xaml.Input.PointerRoutedEventArgs e)
        {
            if (!Drawing) return;
            DrawSelectionBox(e.GetCurrentPoint(img).Position);
        }

        private void Pointerpress(object sender, Windows.UI.Xaml.Input.PointerRoutedEventArgs e)
        {
            if (isdrawn)
            {
                nrect.Visibility = Visibility.Collapsed;
                nrect.Width = 0;
                nrect.Height = 0;
                nrect.StrokeThickness = 0;
            }
            Drawing = true;
            StartPoint = e.GetCurrentPoint(img).Position;
        }
        private void DrawSelectionBox(Point pointdraw)
        {
            EndPoint = pointdraw;
            if (EndPoint.X < 0) EndPoint.X = 0;
            if (EndPoint.X >= img.Width) EndPoint.X = img.Width - 1;
            if (EndPoint.Y < 0) EndPoint.Y = 0;
            if (EndPoint.Y >= img.Height) EndPoint.Y = img.Height - 1;
            double x = (int)Math.Min(StartPoint.X, EndPoint.X);
            double y = (int)Math.Min(StartPoint.Y, EndPoint.Y);
            double width = Math.Abs(StartPoint.X - EndPoint.X);
            double height = Math.Abs(StartPoint.Y - EndPoint.Y);
            nrect.Visibility = Visibility.Visible;
            nrect.SetValue(Canvas.LeftProperty, (StartPoint.X < EndPoint.X) ? StartPoint.X : EndPoint.X);
            nrect.SetValue(Canvas.TopProperty, (StartPoint.Y < EndPoint.Y) ? StartPoint.Y : EndPoint.Y);
            nrect.Width = Math.Abs(EndPoint.X - StartPoint.X);
            nrect.Height = Math.Abs(EndPoint.Y - StartPoint.Y);
            nrect.Stroke = new Windows.UI.Xaml.Media.SolidColorBrush(Windows.UI.Colors.Black);
            nrect.StrokeThickness = 2;
        }

        private async void Cropsave(object sender, RoutedEventArgs e)
        {
            string ext = "jpeg";

            if (Double.IsNaN(nrect.Height) || Double.IsNaN(nrect.Width) || nrect.Height == 0 || nrect.Width == 0)
            {
                Utils.Alert("please select an area to crop if needed ");
                croppedfile = await photofile.CopyAsync(ApplicationData.Current.LocalFolder, "new." + ext, NameCollisionOption.ReplaceExisting);
                //  croppedfile =  await photofile.CopyAsync(ApplicationData.Current.LocalFolder,"new."+ext,NameCollisionOption.ReplaceExisting);
            }
            else
            {
                BitmapDecoder decoder = await BitmapDecoder.CreateAsync(str);
                BitmapTransform transform = new BitmapTransform();
                BitmapBounds bounds = new BitmapBounds();
                bounds.X = (uint)StartPoint.X;
                bounds.Y = (uint)StartPoint.Y;
                bounds.Height = (uint)nrect.Height;
                bounds.Width = (uint)nrect.Width;
                transform.Bounds = bounds;
                PixelDataProvider pix = await decoder.GetPixelDataAsync(
                BitmapPixelFormat.Bgra8,
                BitmapAlphaMode.Straight,
                transform,
                ExifOrientationMode.IgnoreExifOrientation,
                ColorManagementMode.ColorManageToSRgb);
                byte[] pixels = pix.DetachPixelData();
                WriteableBitmap cropBmp = new WriteableBitmap((int)nrect.Width, (int)nrect.Height);
                Stream pixStream = cropBmp.PixelBuffer.AsStream();
                pixStream.Write(pixels, 0, (int)(pixels.Length));
                StorageFolder local = ApplicationData.Current.LocalFolder;
                if (encoding == JsonKey.PNG)
                {
                    ext = "png";
                }
                croppedfile = await local.CreateFileAsync("new." + ext, CreationCollisionOption.GenerateUniqueName);

                using (var nstream = await croppedfile.OpenAsync(FileAccessMode.ReadWrite))
                {
                    double lvl = Double.Parse(compressionLevel);
                    lvl = lvl / 100;
                    lvl = 1 - lvl;
                    var propertySet = new BitmapPropertySet();
                    var qualityValue = new BitmapTypedValue(lvl, PropertyType.Single);
                    propertySet.Add("ImageQuality", qualityValue);
                    BitmapEncoder bmpEncoder;
                    if (ext == "png")
                    {
                        bmpEncoder = await BitmapEncoder.CreateAsync(BitmapEncoder.PngEncoderId, nstream);
                    }
                    else
                    {
                        bmpEncoder = await BitmapEncoder.CreateAsync(BitmapEncoder.JpegEncoderId, nstream, propertySet);
                    }
                    bmpEncoder.SetPixelData(BitmapPixelFormat.Bgra8, BitmapAlphaMode.Straight, (uint)nrect.Width, (uint)nrect.Height, decoder.DpiX, decoder.DpiY, pixels);
                    await bmpEncoder.FlushAsync();
                }
            }
            nrect.Visibility = Visibility.Collapsed;
            var imgbitmap = new BitmapImage(new Uri(croppedfile.Path));
            img.Source = imgbitmap;
            await str.FlushAsync();
            bt.Click -= Cropsave;
            bt.Content = "crop again";
            bt.Click += CropTrial;
            try
            {
                bt1.Visibility = Visibility.Visible;
                ncanvas.Children.Add(bt1);
            }
            catch (Exception)
            {
                //safe btn already added
            }
            var bound = ApplicationView.GetForCurrentView().VisibleBounds;
            double ht = bound.Height;
            double wt = bound.Width;
            if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
            {
                Canvas.SetLeft(bt, wt / 3);
                Canvas.SetTop(bt, ht - 50);
                Canvas.SetLeft(bt1, wt / 1.5);
                Canvas.SetTop(bt1, ht - 50);

            }
            else
            {
                Canvas.SetLeft(bt, (ncanvas.Width) / 1.8);
                Canvas.SetTop(bt, 500);
                Canvas.SetLeft(bt, (ncanvas.Width) / 2.5);
                Canvas.SetTop(bt, 500);
            }
           
        }

        private async 
        Task
ResizePhoto(StorageFile resizefile)
        {
            
                var resizedimg = await Windows.Storage.ApplicationData.Current.TemporaryFolder.CreateFileAsync(resizefile.Name, CreationCollisionOption.GenerateUniqueName);
                using (var resizeStr = await resizefile.OpenReadAsync())
                {
                    var resBmp = await BitmapDecoder.CreateAsync(resizeStr);
                if (calcwt || calcht)
                {
                    if (calcht && calcwt)
                    {
                        scaledht = 816;
                        scaledwdt = 612;
                    }
                    else
                    {
                        double gratio = (double)resBmp.PixelWidth / (double)resBmp.PixelHeight;
                        if (calcwt)
                        {
                            scaledwdt = (uint)(gratio*scaledht);

                        }
                        else
                        {
                            scaledht = (uint)(gratio * scaledwdt);
                        }
                    }
                }
                using (var resizewrite = await resizedimg.OpenAsync(FileAccessMode.ReadWrite))
                    {
                        resizewrite.Size = 0;
                        var resEnc = await BitmapEncoder.CreateForTranscodingAsync(resizewrite, resBmp);
                        resEnc.BitmapTransform.ScaledHeight = scaledht;
                        resEnc.BitmapTransform.ScaledWidth = scaledwdt;
                        await resEnc.FlushAsync();
                    }                
                croppedfile = resizedimg;
            }
        }

        private void CropTrial(object sender, RoutedEventArgs e)
        {
            var bound = ApplicationView.GetForCurrentView().VisibleBounds;
            double ht = bound.Height;
            double wt = bound.Width;
            if (AnalyticsInfo.VersionInfo.DeviceFamily == "Windows.Mobile")
            {
                Canvas.SetLeft(bt, wt / 2.5);
                Canvas.SetTop(bt, ht - 50);

            }
            else
            {
                Canvas.SetLeft(bt, (ncanvas.Width) / 2);
                Canvas.SetTop(bt, 500);
            }
            isdrawn = false;
            bt1.Visibility = Visibility.Collapsed;
            var imgbitmap = new BitmapImage(new Uri(photofile.Path));
            img.Source = imgbitmap;
            nrect.Visibility = Visibility.Collapsed;
            bt.Click -= CropTrial;
            bt.Click += Cropsave;
            bt.Content = "save";
        }
    }
}
