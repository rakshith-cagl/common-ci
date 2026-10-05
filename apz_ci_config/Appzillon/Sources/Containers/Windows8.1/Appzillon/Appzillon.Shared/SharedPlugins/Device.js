//--------Plugin files-----------
WinContainer.Device = (function () {
    function getDeviceStatus(req) {

        try {
			
            var conType = Windows.Networking.Connectivity.NetworkInformation.getInternetConnectionProfile();
            var NetworkNames = conType.getNetworkNames();
            var NetworkConnectivityLevel = conType.getNetworkConnectivityLevel();
            var profileName = conType.profileName;

            var onLine = navigator.onLine;
            var ver = navigator.appVersion;
            var osName = "Windows";//ver.substr(28, 14); //Windows NT 6.2
            var osVersion = ver.split("(")[1].split(';')[0];//ver.substr(28, 14); //Windows NT 6.2
            var devType = WinContainer.getDeviceType();//WINDOWS8SURFACE

            var screen = Windows.Graphics.Display.DisplayProperties.logicalDpi;
            var width = window.screen.availWidth;
            var height = window.screen.availHeight;
            var batteryStatus = null;
            try {
                var batteryStatus = Windows.Phone.Devices.Power.Battery.getDefault().remainingChargePercent;
            } catch (e) {
                WinContainer.Log.warn(e.description);
            }
            //var connectionProfileInfo = getConnectionProfileInfo(Windows.Networking.Connectivity.NetworkInformation.getInternetConnectionProfile());

            var connectionProfile = Windows.Networking.Connectivity.NetworkInformation.getInternetConnectionProfile();
            var isWifi = connectionProfile.isWlanConnectionProfile;
            var isMob = connectionProfile.isWwanConnectionProfile;
            var connectionType = "No-Connection";
            if (isWifi)
                connectionType = "WiFi";
            else if (isMob)
                connectionType = "Mobile Data";

            
            var res = {};
            res.simDetails = [];
            res.id = req.id;
            res.osName = osName;
            res.osVersion = osVersion;
            res.devType = devType;
            res.screenResolution = width + "x" + height;
            res.connectionType = connectionType;
            res.batteryStatus = batteryStatus;

            WinContainer.successCallback(res);
        }
        catch (e) {
            WinContainer.Log.error(e.description);
            WinContainer.failureCallback(req.id,"APZ-CNT-103");
        }
    }
    return {
        getStatus: getDeviceStatus
    }
})();
