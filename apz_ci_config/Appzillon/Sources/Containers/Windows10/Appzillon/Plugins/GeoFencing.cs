using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Geolocation;
using Windows.Services.Maps;

namespace Appzillon.Plugins
{
    class GeoFencing
    {
        public Geopoint cpoint;
        public BasicGeoposition location;

#region Singleton Pattern
        private static GeoFencing instance;
        private GeoFencing()
        {
        }
        public static GeoFencing Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new GeoFencing();
                }
                return instance;
            }
        }
#endregion

        public async void GeoFence(JObject json)
        {
            string id = json[JsonKey.ID].ToString();
            try
            {
                string region = json[JsonKey.REGION].ToString();
                bool match = false;
                await UpdateCurrLoc();
                if (region == JsonKey.COUNTRY)
                {
                    cpoint = new Geopoint(location);
                    MapLocationFinderResult res = await MapLocationFinder.FindLocationsAtAsync(cpoint);
                    if (res.Status == MapLocationFinderStatus.Success)
                    {
                        string countrycode = res.Locations[0].Address.CountryCode;
                       //Log.Debug("GeoFencing :Countrycode" + countrycode);
                        var codelist = json[JsonKey.COUNTRY_LIST];
                        foreach (string icountrycode in codelist)
                        {
                            if (countrycode == icountrycode)
                            {
                                match = true;
                                JObject j = new JObject();                             
                                j["allowLocation"] = true;
                                Response.Success(id, j);
                                //Log.Debug("CountryCode :" + icountrycode);
                            }
                        }
                        if (!match)
                        {
                            JObject j = new JObject();
                            j["allowLocation"] = false;
                            Response.Success(id, j);
                          
                        }
                    }
                    else
                    {

                        Response.Fail(id,ErrorCode.MAP_LOCATION_NOT_FOUND);
                        Log.Error("Location Not Found");
                    }
                }
                else
                {
                    try
                    {
                        string[] latlong = json[JsonKey.COORDINATES].ToString().Split(',');
                        double lati = Double.Parse(latlong[0]);
                        double longi = Double.Parse(latlong[1]);
                        double radius = Double.Parse(json[JsonKey.RADIUS].ToString());
                        var dist = Math.Sqrt(Math.Pow((lati - location.Latitude), 2) + Math.Pow((longi - location.Longitude), 2));
                        if (dist >= radius)
                        {
                            JObject j = new JObject();
                            j["allowLocation"] = true;
                            j[JsonKey.SUCCESS_MESSAGE] = "Successfully started";
                            Response.Success(id,j);
                        }
                        else
                        {
                            JObject j = new JObject();
                            j["allowLocation"] = false;
                            Response.Success(id, j);
                        }
                    }
                    catch (Exception e)
                    {
                        Response.Fail(id, ErrorCode.GEOFENCING_FAIL);
                        Log.Error("GeoFencing, error:"+e.Message);
                    }
                }
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.GEOFENCING_FAIL);
                Log.Error("GeoFencing, error:" + e.Message);
            }

        }

        private async Task UpdateCurrLoc()
        {
                var accessStatus = Geolocator.RequestAccessAsync();
                Geolocator locator = new Geolocator();
                locator.DesiredAccuracy = PositionAccuracy.High;
                Geoposition pos = await locator.GetGeopositionAsync();
                var time = pos.Coordinate.Timestamp;
                location = new BasicGeoposition();
                location.Latitude = pos.Coordinate.Point.Position.Latitude;
                location.Longitude = pos.Coordinate.Point.Position.Longitude;
           
        }
    }
}
