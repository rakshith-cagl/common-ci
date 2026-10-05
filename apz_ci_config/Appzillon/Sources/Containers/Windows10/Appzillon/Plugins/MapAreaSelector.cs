using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Geolocation;
using Windows.Foundation;
using Windows.Storage.Streams;
using Windows.UI.Core;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Maps;
using Windows.UI.Xaml.Shapes;

namespace Appzillon.Plugins
{
    class MapAreaSelector
    {
        private MapControl map;
        public string jsonString;
        private Grid mapGrid;
        private MainPage ect;
        private string id = "";

#region Singleton Pattern
        private static MapAreaSelector instance;
       public MapAreaSelector()
        {
        }
        public static MapAreaSelector Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new MapAreaSelector();
                }
                return instance;
            }
        }
#endregion

        public void initializeMap(JObject json, MainPage lobject)
        {
            id = json[JsonKey.ID].ToString();
            ect = lobject;            
            try
            {
                MapAreaSelector areaSelector = new MapAreaSelector();
                areaSelector.jsonString = jsonString.ToString();
            }
            catch (Exception)
            { }
            double radius = (double)json[JsonKey.RADIUS];
            var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
            var ht = bounds.Height;
            var wt = bounds.Width;
            mapGrid = new Grid();
            mapGrid.Height = ht;
            mapGrid.Width = wt;
            MapControl map = new MapControl();
            map.Name = "MapControl1";
            map.MapServiceToken = AppSettings.ContainerProperties.GetValue("BINGMAPSKEY").ToString();//"WFYMe7zHZDNFF6W0k7Ha~lwq0IoanjJ_FpWS7uk30Tw~ApTQE3GnQqkznyqxprC2OSfA5lEV8WyeZW3uEjE2jkK_eGy-dCPm7PiWJbuW3lER";
            map.Height = ht;
            map.Width = wt;
            map.HorizontalAlignment = HorizontalAlignment.Stretch;
            map.VerticalAlignment = VerticalAlignment.Stretch;
            Thickness nsize = new Thickness();
            nsize.Left = 0;
            nsize.Right = 0;
            nsize.Top = 0;
            nsize.Bottom = 0;
            map.Margin = nsize;

            mapGrid.Children.Add(map);

            Grid appGrid = (Grid)lobject.FindName("MainGrid");
            appGrid.Children.Add(mapGrid);
            WebView wb = (WebView)lobject.FindName("wbAppzillon");
            Canvas.SetZIndex(wb, 0);
            Canvas.SetZIndex(mapGrid, 2);
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += back;
            // Show UI in title bar if opted-in and in-app backstack is not empty.
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
                AppViewBackButtonVisibility.Visible;
            LoadMap(map, json);

        }
        private void back(object sender, BackRequestedEventArgs e)
        {try
            {
                WebView wb = (WebView)ect.FindName("wbAppzillon");
                Grid appGrid = (Grid)ect.FindName("MainGrid");
                appGrid.Children.Remove(mapGrid);
                Canvas.SetZIndex(wb, 1);
                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
                      AppViewBackButtonVisibility.Collapsed;
                e.Handled = true;
            }
            catch (Exception) { }
        }

        private async void LoadMap(MapControl map, JObject json)
        {
            GeolocationAccessStatus accessStatus = await Geolocator.RequestAccessAsync();
            double currentLatitude = new double();
            double currentLongitude = new double();

            if ((int)accessStatus == 1)
            {
                Geolocator locator = new Geolocator();
                locator.DesiredAccuracy = PositionAccuracy.Default;
                Geoposition pos = await locator.GetGeopositionAsync();
                Geocoordinate coord = pos.Coordinate;
                currentLatitude = coord.Point.Position.Latitude;
                currentLongitude = coord.Point.Position.Longitude;

                BasicGeoposition position = new BasicGeoposition();
                position.Latitude = currentLatitude;
                position.Longitude = currentLongitude;
                Geopoint mapCenter = new Geopoint(position);

                map.Center = mapCenter;
                map.ZoomLevel = 16;
                map.LandmarksVisible = true;
                map.BusinessLandmarksVisible = true;
                map.PedestrianFeaturesVisible = true;

                MapIcon pin = new MapIcon();
                pin.Location = mapCenter;
                pin.NormalizedAnchorPoint = new Point(0.5, 1.0);
                map.MapElements.Add(pin);

                Border border = new Border();
                border.Height = 50;
                border.Width = 50;
                border.BorderThickness = new Thickness(5);
                map.Children.Add(border);
                MapControl.SetLocation(border, mapCenter);
                MapControl.SetNormalizedAnchorPoint(border, new Point(0.5, 0.5));

                SetTargetPoints(json, currentLatitude, currentLongitude, map);
                map.MapTapped += (map1, e) => GetLocation(map, e, json);

                Rectangle infoBox = new Rectangle();
                map.Children.Add(infoBox);
                infoBox.DataContext = "My Location";
                MapControl.SetLocation(infoBox, mapCenter);
                MapControl.SetNormalizedAnchorPoint(infoBox, new Point(0.5, 0.5));

                Response.Success(id,false);
            }
            else
            {
                Response.Fail(id,ErrorCode.MAP_LOCATION_NOT_FOUND);
                Log.Error("Location Not Found");
            }
        }

        private void GetLocation(MapControl map, MapInputEventArgs e, JObject json)
        {
            map.MapElements.Clear();
            MapIcon currentPin = new MapIcon();
            Geopoint pos = e.Location;
            BasicGeoposition position = pos.Position;
            currentPin.Location = e.Location;
            currentPin.NormalizedAnchorPoint = new Point(0.5, 1.0);
            map.MapElements.Add(currentPin);
            SetTargetPoints(json, position.Latitude, position.Longitude, map);

        }

        private void SetTargetPoints(JObject json, double latitude, double longitude, MapControl map)
        {
            MapAreaSelector ms = new MapAreaSelector();
            var searchLocations = json["nearbyplaces"];
            double radius = (double)json["radius"];
            int count = json["nearbyplaces"].Count();
            double[] nearbyLat = new double[count];
            double[] nearbyLong = new double[count];
            int i = 0;
            foreach (JObject location in searchLocations)
            {
                nearbyLat[i] = (double)location["locationLatitude"];
                nearbyLong[i] = (double)location["locationLongitude"];
                i++;
            }

            for (var loc = 0; loc < count; loc++)
            {
                double distance = GetDistance(latitude, longitude, nearbyLat[loc], nearbyLong[loc]);
                if (distance <= radius)
                {
                    MapIcon targetPin = new MapIcon();
                    targetPin.Image =
        RandomAccessStreamReference.CreateFromUri(new Uri("ms-appx:///Assets/green.png"));

                    BasicGeoposition targetPosition = new BasicGeoposition();
                    targetPosition.Latitude = nearbyLat[loc];
                    targetPosition.Longitude = nearbyLong[loc];
                    targetPin.Location = new Geopoint(targetPosition);
                    map.MapElements.Add(targetPin);
                }
            }
        }

        private double GetDistance(double currentLat, double currentLong, double targetLat, double targetLong)
        {
            int earthRadius = 6378137;
            double diffLat = ToRadian(targetLat - currentLat);
            double diffLong = ToRadian(targetLong - currentLong);
            double a = Math.Sin(diffLat / 2) * Math.Sin(diffLat / 2) +
                    Math.Cos(ToRadian(currentLat)) * Math.Cos(ToRadian(targetLat)) *
                    Math.Sin(diffLong / 2) * Math.Sin(diffLong / 2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            double dist = (earthRadius * c);
            return dist;
        }

       private double ToRadian(double degrees)
        {
            return degrees * (Math.PI / 180);
        }
    }
}
