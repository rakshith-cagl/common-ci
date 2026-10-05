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
using Windows.Services.Maps;
using Windows.UI;
using Windows.UI.Core;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Maps;

namespace Appzillon.Plugins
{
    class MapDrivingDirection
    {
        private MainPage ect;
        private Grid mapGrid;
        private string id = "";

#region Singleton Pattern
        private static MapDrivingDirection instance;
        public MapDrivingDirection()
        {
        }
        public static MapDrivingDirection Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new MapDrivingDirection();
                }
                return instance;
            }
        }
#endregion

        public void initializeMap(JObject json, MainPage lobject)
        {
            id = json[JsonKey.ID].ToString();
            ect = lobject;
            var fromLocation = json[JsonKey.FROM_LOCATION];
            var toLocation = json[JsonKey.TO_LOCATION];
            var bounds = ApplicationView.GetForCurrentView().VisibleBounds;
            var ht = bounds.Height;
            var wt = bounds.Width;
            mapGrid = new Grid();
            mapGrid.Height = ht;
            mapGrid.Width = wt;
            mapGrid.Visibility = Visibility.Visible;
            MapControl map = new MapControl();
            map.Name = "MapControl1";
            map.MapServiceToken = AppSettings.ContainerProperties.GetValue("BINGMAPSKEY").ToString();//"WFYMe7zHZDNFF6W0k7Ha~lwq0IoanjJ_FpWS7uk30Tw~ApTQE3GnQqkznyqxprC2OSfA5lEV8WyeZW3uEjE2jkK_eGy-dCPm7PiWJbuW3lER";
            map.Height = ht;
            map.Width = wt;
            //map.HorizontalAlignment = HorizontalAlignment.Stretch;
            // map.VerticalAlignment = VerticalAlignment.Stretch;
            Thickness nsize = new Thickness();
            nsize.Left = 0;
            nsize.Right = 0;
            nsize.Top = 0;
            nsize.Bottom = 0;
            map.Margin = nsize;
            mapGrid.Children.Add(map);
            //map.Children.Add(bt);
            Grid appGrid = (Grid)lobject.FindName("MainGrid");
            WebView wb = (WebView)lobject.FindName("wbAppzillon");
            Canvas.SetZIndex(wb, 0);
            Canvas.SetZIndex(mapGrid, 2);
            appGrid.Children.Add(mapGrid);
            //this.Frame.Navigate(typeof(MainPage));
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().BackRequested += back;
            // Show UI in title bar if opted-in and in-app backstack is not empty.
            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility =
                AppViewBackButtonVisibility.Visible;
            LoadMap(map, json);
        }

        private void back(object sender, BackRequestedEventArgs e)
        {
            try
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
            try
            {
                var fromLocation = json["fromLocation"].ToString();
                var toLocation = json["toLocation"].ToString();

                string[] fromLoc = fromLocation.Split(',');
                string[] toLoc = toLocation.Split(',');

                BasicGeoposition startLocation = new BasicGeoposition();
                startLocation.Latitude = Convert.ToDouble(fromLoc[0]);
                startLocation.Longitude = Convert.ToDouble(fromLoc[1]);
                Geopoint startPoint = new Geopoint(startLocation);

                BasicGeoposition endLocation = new BasicGeoposition();
                endLocation.Latitude = Convert.ToDouble(toLoc[0]);
                endLocation.Longitude = Convert.ToDouble(toLoc[1]);
                Geopoint endPoint = new Geopoint(endLocation);

                MapIcon startPin = new MapIcon();
                startPin.Location = startPoint;
                startPin.NormalizedAnchorPoint = new Point(0.5, 1.0);
                map.MapElements.Add(startPin);

                MapIcon endPin = new MapIcon();
                endPin.Location = endPoint;
                endPin.NormalizedAnchorPoint = new Point(0.5, 1.0);
                map.MapElements.Add(endPin);

                MapRouteFinderResult route = await MapRouteFinder.GetDrivingRouteAsync(startPoint, endPoint, MapRouteOptimization.Time);
                if (route.Status == MapRouteFinderStatus.Success)
                {
                    MapRouteView routeView = new MapRouteView(route.Route);
                    routeView.RouteColor = Colors.Blue;
                    routeView.OutlineColor = Colors.Black;
                    map.Routes.Add(routeView);
                    await map.TrySetViewBoundsAsync(
                        route.Route.BoundingBox,
                        null,
                        MapAnimationKind.None);
                }
                JObject success = new JObject();
                success[JsonKey.SUCCESS_MESSAGE] = "Driving directions loaded";
                Response.Success(id,success);
            }

            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.DRIVINING_DIRECTIONS_LOAD_FAIL);
                Log.Error(e.Message);
            }
        }
    }
}
