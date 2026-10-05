notif_id = null;
WinContainer.Notification = (function () {
   /* var checkForNotification = function (msg) {
        if (msg)
            if (appzillon.app.onNotification) {
                var param = msg.split("#APZ");
                appzillon.app.onNotification(param[0], param[1]);//--msg and ref

            }
            else
                setTimeout(function () { appzillon.plugin.checkForNotification(msg); }, 500);
    }*/
    var initNotification = function () {
        var notificationEnabled =  WinContainer.Settings.containerProperties["NOTIFICATION"];
        if (notificationEnabled != "Y") return;
        var localSettings = Windows.Storage.ApplicationData.current.localSettings;
        var localChannelURL = localSettings.values["NOTIFICATIONCHANNELURL"];

        Windows.Networking.PushNotifications.PushNotificationChannelManager
        .createPushNotificationChannelForApplicationAsync()
        .done(function (channel) {
            var currChannelURL = channel.uri;
            channel.onpushnotificationreceived = pushNotificationReceivedHandler;

            if (localChannelURL == undefined || (localChannelURL != currChannelURL)) {
                var deviceid = WinContainer.deviceId();
                var devicetype = WinContainer.getDeviceType();
                var appid = WinContainer.Settings.appProperties["appId"];
                var osId = new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation().operatingSystem.toUpperCase();

                var deviceInformation = Windows.Networking.Connectivity.NetworkInformation.getHostNames();
                var deviceName = deviceInformation[0].displayName

                function notificationCallBack(pcallid, lifaceid, pstatus, lerrorCode, lbodyobj) {
                    if (pstatus == "success") {
                        var applicationData = Windows.Storage.ApplicationData.current;
                        var localSettings = applicationData.localSettings;
                        localSettings.values["NOTIFICATIONCHANNELURL"] = currChannelURL;
                      //  appzillon.plugin.storeLog("Device registration for notification service completed.", "I");
                    } else {
                       // appzillon.plugin.storeLog("Device registration for notification service failed.", "I");
                    }
                }
                //appzillon.plugin.store('USERID','windows');
                //var body = {};
                //body.osId = osId;
                //body.osVersion = "8.1";
                //body.appId = appid;
                //body.deviceName = deviceName;
                //body.deviceId = deviceid;
                //body.regId = currChannelURL;
                //gchennal = currChannelURL;
                var obj = {};
                obj.appzillonHeader = {};
                obj.appzillonHeader.preLogin = "true";
                obj.appzillonHeader.appId = appid;
                obj.appzillonHeader.screenId = "lauchApp";
                obj.appzillonHeader.requestKey = "000NEW";
                obj.appzillonHeader.interfaceId = "appzillonNotificationRegistration";
                obj.appzillonHeader.status = true;
                obj.appzillonHeader.sessionId = "null";
                obj.appzillonHeader.deviceId = deviceid;
                obj.appzillonHeader.userId = "windows";
                obj.appzillonHeader.longitude = "";
                obj.appzillonHeader.latitude = "";
                obj.appzillonHeader.origination = WinContainer.getIP();
                obj.appzillonHeader.source = "APPZILLON";

                obj.appzillonBody = {};
                obj.appzillonBody.osId = osId;
                obj.appzillonBody.osVersion = "8.1";
                obj.appzillonBody.appId = appid;
                obj.appzillonBody.deviceName = deviceName;
                obj.appzillonBody.deviceId = deviceid;
                obj.appzillonBody.regId = currChannelURL;



                var json = JSON.stringify(obj);
                //var json = obj;
                var linternalserverurl = WinContainer.Settings.appProperties["serverUrl"];

                $.ajax({
                    url: linternalserverurl,
                    type: "POST",
                    cache: false,
                    data: json,
                    contentType: "application/json",
                    dataType: 'json',
                    success: function (presp) {
                        if (presp.appzillonBody.status == "success") {
                            var applicationData = Windows.Storage.ApplicationData.current;
                            var localSettings = applicationData.localSettings;
                            localSettings.values["NOTIFICATIONCHANNELURL"] = currChannelURL;
                            WinContainer.Log.info("Device registration for notification service completed.");
                        } else {
                            WinContainer.Log.info("Device registration for notification service failed.");
                        }
                    },
                    error: function (e) {
                        WinContainer.Log.error('Notification Registraion Status=' + e.status + ' StatusText=' + e.statusText);
                    }
                });
            }
        },
       function (e) {
           WinContainer.Log.error(e.message);
       });
    }

    function pushNotificationReceivedHandler(e) {

        var pushNotifications = Windows.Networking.PushNotifications;
        var notificationContent;
        var st = e.timeStamp;
        var applicationData = Windows.Storage.ApplicationData.current;
        //var localSettings = applicationData.localSettings;
        var js = {};
        switch (e.notificationType) { 
            case pushNotifications.PushNotificationType.toast:
                notificationTypeName = "Toast";
                notificationContent = e.toastNotification.content;
                //-----Changed for reference number
                var param = e.toastNotification.content.getElementsByTagName("toast")[0].getAttribute("launch");
                //localSettings.values["isFromNotifHandler"] = true;
                //appzillon.plugin.notif("active", param);
                break;
            case pushNotifications.PushNotificationType.tile:
                notificationTypeName = "Tile";
                notificationContent = e.tileNotification.content;
                break;
            case pushNotifications.PushNotificationType.badge:
                notificationTypeName = "Badge";
                notificationContent = e.badgeNotification.content;
                break;
        }
        WinContainer.storeNotif(Math.floor(Date.now() / 1000), notificationContent.innerText, "N");
    
        if (notif_id != undefined && notif_id != null && notif_id != "") {
            js.id = notif_id;
			js.event = "notification";
            js.text = notificationContent;
            js.keepAlive = true;
            js.status = true;
            WinContainer.successCallback(js);
        }
    }
    var getNotification = function (req) {
        var query = "SELECT * FROM tb_notifications";
        req.id = 1;
        req.databaseName = "APPSDB";
        req.query = query;
        apz.executeSql(req);
    }
    var updateNotification = function (req) {
        
        req.databaseName = "APPSDB";
        //req.query = query;
        apz.executeSql(req);
    }

    var deleteNotification = function (req) {
        var ID = req.notificationID;
        var query = "DELETE FROM tb_notifications WHERE id = " + ID + ";";
        req.databaseName = "APPSDB";
        req.executeQuery = query;
        apz.executeSql(req);
    }
    return {
        initnotification: initNotification,
        get: getNotification,
        update: updateNotification,
        remove: deleteNotification
    }
})();

