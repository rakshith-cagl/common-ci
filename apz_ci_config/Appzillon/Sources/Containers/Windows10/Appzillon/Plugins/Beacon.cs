using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Devices.Bluetooth.Advertisement;
using Windows.Storage.Streams;

namespace Appzillon.Plugins
{
    class Beacon
    {
        private BluetoothLEAdvertisementWatcher watcher;
        private MainPage ect;
        private BluetoothLEAdvertisementPublisher publisher;
        private string id = "";
    #region Singleton Pattern
        private static Beacon instance;
        private Beacon()
        {
        }
        public static Beacon Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Beacon();
                }
                return instance;
            }
        }
    #endregion
        internal void AdvertisementWatch(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string uuid = obj[JsonKey.UU_ID].ToString();
                Guid gid = new Guid(uuid);
                watcher = new BluetoothLEAdvertisementWatcher();
                watcher.ScanningMode = BluetoothLEScanningMode.Active;
                watcher.SignalStrengthFilter.InRangeThresholdInDBm = -70;
                watcher.SignalStrengthFilter.OutOfRangeThresholdInDBm = -75;
                watcher.SignalStrengthFilter.OutOfRangeTimeout = TimeSpan.FromMilliseconds(2000);
                watcher.AdvertisementFilter.Advertisement.ServiceUuids.Add(gid);
                watcher.Received += OnAdvertisementReceived;
                watcher.Start();
                var j = new JObject();
                j[JsonKey.EVENT] = "started";
                Response.Success(id, true,j);
            }
            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.BEACON_START_FAIL);
                Log.Error(e.Message);
            }
        }

        private void OnAdvertisementReceived(BluetoothLEAdvertisementWatcher sender, BluetoothLEAdvertisementReceivedEventArgs args)
        {
            var ad = args.RawSignalStrengthInDBm;
            var adv = args.Advertisement.DataSections.ToString();
            JObject j = new JObject();
            j[JsonKey.EVENT] = "beaconDetected";
            j["major"] = adv;
            Response.Success(id,true,j);
        }

        internal void AdvertisementPublish(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                publisher = new BluetoothLEAdvertisementPublisher();
                var manufacturerData = new BluetoothLEManufacturerData();
                manufacturerData.CompanyId = 0xFFFE;

                var writer = new DataWriter();
                writer.WriteString(""); // add msg

                // Make sure that the buffer length can fit within an advertisement payload (~20 bytes). 
                // Otherwise you will get an exception.
                manufacturerData.Data = writer.DetachBuffer();

                // Add the manufacturer data to the advertisement publisher:
                publisher.Advertisement.ManufacturerData.Add(manufacturerData);
                publisher.Start();
                Response.Success(id, false);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.BEACON_START_FAIL);
                Log.Error(e.Message);
            }
        }
        internal void AdvertisementWatchStop(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                watcher.Stop();
                watcher.Received -= OnAdvertisementReceived;
                Response.Success(id, false);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.BEACON_STOP_FAIL);
                Log.Error(e.Message);
            }
        }
    }
}
