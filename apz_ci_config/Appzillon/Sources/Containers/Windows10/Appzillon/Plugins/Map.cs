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
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Maps;

namespace Appzillon.Plugins
{
    class Map
    {
        public Grid mapGrid;
        public MainPage ect;

#region Singleton Pattern
        private static Map instance;
        private Map()
        {
        }
        public static Map Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Map();
                }
                return instance;
            }
        }
#endregion

        public async void initializeMap(JObject json, MainPage lobject)
        {
            string id = json[JsonKey.ID].ToString();
            ect = lobject;
            MapAreaSelector areaSelector = new MapAreaSelector();
            areaSelector.jsonString = json.ToString();
            mapGrid = new Grid();
            MapControl map = new MapControl();
            map.Name = "MapControl1";
            map.MapServiceToken = AppSettings.ContainerProperties.GetValue("BINGMAPSKEY").ToString();//"WFYMe7zHZDNFF6W0k7Ha~lwq0IoanjJ_FpWS7uk30Tw~ApTQE3GnQqkznyqxprC2OSfA5lEV8WyeZW3uEjE2jkK_eGy-dCPm7PiWJbuW3lER";
            map.Height = 750;
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

            try
            {
                Geolocator locator = new Geolocator();
                locator.DesiredAccuracy = PositionAccuracy.Default;
                Geoposition pos = await locator.GetGeopositionAsync();
                Geocoordinate coord = pos.Coordinate;
                double currentLatitude = coord.Point.Position.Latitude;
                double currentLongitude = coord.Point.Position.Longitude;
                BasicGeoposition currentPosition = new BasicGeoposition();
                currentPosition.Latitude = currentLatitude;
                currentPosition.Longitude = currentLongitude;
                Geopoint currentPoint = new Geopoint(currentPosition);


                MapIcon pin = new MapIcon();
                pin.Location = currentPoint;
                pin.NormalizedAnchorPoint = new Point(0.5, 1.0);
                map.MapElements.Add(pin);

                var searchLocations = json[JsonKey.MARKER_INFO];
                foreach (JObject location in searchLocations)
                {
                    BasicGeoposition iPosition = new BasicGeoposition();
                    currentPosition.Latitude = (double)location[JsonKey.LOCATION_LATITUDE];
                    currentPosition.Longitude = (double)location[JsonKey.LOCATION_LONGITUDE];
                    string locationName = location[JsonKey.LOCATION_NAME].ToString();
                    string locationDescription = location[JsonKey.LOCATION_DESCRIPTION].ToString();
                    MapIcon ipin = new MapIcon();
                    ipin.Location = new Geopoint(iPosition);
                    ipin.NormalizedAnchorPoint = new Point(0.5, 1.0);
                    map.MapElements.Add(ipin);

                }
                JObject success = new JObject();
                success[JsonKey.SUCCESS_MESSAGE] = "Map Loaded";
                Response.Success(id,success);
            }

            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.MAP_LOAD_FAIL);
                Log.Error(e.Message);
            }
        }
        private void back(object sender, BackRequestedEventArgs e)
        {
            WebView wb = (WebView)ect.FindName("wbAppzillon");
            Grid appGrid = (Grid)ect.FindName("MainGrid");
            appGrid.Children.Remove(mapGrid);
            Canvas.SetZIndex(wb, 1);
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            e.Handled = true;
        }
    }
}
