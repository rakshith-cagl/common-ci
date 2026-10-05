//--Error Code----



WinContainer.notify = function (reqObject) {
    var Command = WinContainer.Command;
    try {
        switch (reqObject.command) {

            // ACCELEROMETER
            case Command.ACCELERO_START:
                WinContainer.AcceleroMeter.start(reqObject);
                break;
            case Command.ACCELERO_STOP:
                WinContainer.AcceleroMeter.stop(reqObject);
                break;

                //App Basics
            case Command.GET_APP_VERSION:
                WinContainer.getAppversion(reqObject);
                break;

            case Command.CLOSE_APP:
                WinContainer.terminate();
                break;

                // App idle time out
            case Command.APP_IDLE_TIME_OUT:
                WinContainer.AppIdleTimeOut.Start(reqObject);
                break;

                // Augment Reality
                // Barcode
            case Command.BARCODE:
                WinContainer.plugin.barcode(reqObject);
                break;

            case Command.BATTERY_START:
                WinContainer.isPhone() ? WinContainer.plugin.startBatteryMonitor(reqObject) : WinContainer.failureCallback(reqObject.id, ErrorCode.NOT_SUPPORTED);
                break;
            case Command.BATTERY_STOP:
                WinContainer.isPhone() ? WinContainer.plugin.stopBatteryMonitor(reqObject) : WinContainer.failureCallback(reqObject.id, ErrorCode.NOT_SUPPORTED);
                break;


                // Beacon

                //BIOMETRIC

                // Calendar Completed
            case Command.CALENDAR:
                WinContainer.plugin.calendar(reqObject);
                break;
            case Command.CALENDAR_DELETE:
                WinContainer.plugin.deleteCalendarEvent(reqObject);
                break;
            case Command.CALENDAR_EDIT:
                WinContainer.plugin.editCalendarEvent(reqObject);
                break;
                // Call
            case Command.CALL:
                WinContainer.call(reqObject);
                break;


                // Camera
            case Command.CAMERA:
                WinContainer.plugin.camera(reqObject);
                break;


                // Compass 
            case Command.COMPASS_START:
                WinContainer.Compass.Start(reqObject);
                break;
            case Command.COMPASS_STOP:
                WinContainer.Compass.Stop(reqObject);
                break;


                // Contact
            case Command.CONTACT_CREATE:
                WinContainer.Contact.add(reqObject);
                break;
            case Command.CONTACT_DELETE:
                WinContainer.Contact.del(reqObject);
                break;
            case Command.CONTACT_SEARCH:
                WinContainer.Contact.search(reqObject);
                break;
            case Command.CONTACT_EDIT:
                WinContainer.Contact.edit(reqObject);
                break;
            case Command.CONTACT_FETCH:
                WinContainer.Contact.fetch(reqObject);
                break;
                // Device
            case Command.DEVICE:
                WinContainer.Device.getStatus(reqObject);
                break;
            case Command.DEVICE_INFO:
                WinContainer.GetDeviceInfo(reqObject);
                break;
            case Command.IP_GET:
                WinContainer.getIP(reqObject);
                break;

                //deeplink
            case Command.DEEPLINK:
                WinContainer.deepLink(reqObject);
                break;

                //Events
            case Command.CTRL_EVTS:
                WinContainer.isPhone() ? WinContainer.plugin.controlEvent(reqObject) : WinContainer.failureCallback(reqObject.id, ErrorCode.NOT_SUPPORTED);
                break;
                // Email
            case Command.EMAIL:
                WinContainer.Mail.InvokeEmail(reqObject);
                break;


                // Encryption and Decryption
            case Command.ENCRYPT_FILE:
                WinContainer.plugin.fileEncrypt(reqObject);
                break;
            case Command.DECRYPT_FILE:
                WinContainer.plugin.fileDecrypt(reqObject);
                break;

            case Command.ENCRYPT_STRING:
                try {
                    WinContainer.Crypto.EncryptData(reqObject);
                } catch (e) {
                    WinContainer.failureCallback(reqObject.id, "APZ-CNT-102");
                }
                break;
            case Command.DECRYPT_STRING:
                try {
                    WinContainer.Crypto.DecryptData(reqObject);
                } catch (e) {
                    WinContainer.failureCallback(reqObject.id, "APZ-CNT-102");
                }
                break;

                // File Operation
            case Command.FILE_BROWSER:
                WinContainer.filebrowser(reqObject);
                break;
            case Command.FILE_TO_BASE64:
                WinContainer.FileOperation.fileToBase64(reqObject);
                break;
            case Command.FILE_UPLOAD:
                WinContainer.FileOperation.upload(reqObject);
                break;
            case Command.FILE_DOWNLOAD:
                WinContainer.FileOperation.download(reqObject);
                break;
            case Command.FILE_UPLOAD_WS:
                WinContainer.FileOperation.uploadWS(reqObject);
                break;
                // WS - Without Session
            case Command.FILE_DOWNLOAD_WS:
                WinContainer.FileOperation.downloadWS(reqObject);
                break;
            case Command.GET_FILE_SIZE:
                WinContainer.FileOperation.getfilesize(reqObject);
                break;
            case Command.BASE64_TO_PDF:
                WinContainer.FileOperation.base64ToFile(reqObject);
                break;
            case Command.FILE_READ:
                WinContainer.FileOperation.openfile(reqObject);
                break;
            case Command.FILE_CREATE:
                WinContainer.FileOperation.create(reqObject);
                break;
            case Command.PLGN_FILE_CONTENT:
                WinContainer.FileOperation.content(reqObject);
                break;
            case Command.FILE_DEL:
                WinContainer.FileOperation.del(reqObject);
                break;

            case Command.READ_FILE:
                WinContainer.FileOperation.fileread(reqObject);
                break;
                //Gesture
            case Command.GESTURE_START:
                WinContainer.Gesture.start(reqObject);
                break;
            case Command.GESTURE_STOP:
                WinContainer.Gesture.stop(reqObject);
                break;

                // Get Instructions
            case Command.GET_INSTRUCTION:
                WinContainer.GetAppInstruction(reqObject);
                break;

                // GPS
            case Command.GPS_START:
                WinContainer.GPS.Start(reqObject);
                break;
            case Command.GPS_STOP:
                WinContainer.GPS.Stop(reqObject);
                break;
            case Command.GET_LOC:
                var _device = Windows.Devices.Geolocation.Geolocator();
                if (_device != null) {
                    _device.getGeopositionAsync().then(function (cord) {
                        var r = cord.coordinate;
                        var res = {};
                        res.id = reqObject.id;
                        res.latitude = r.latitude;
                        res.longitude = r.longitude;
                        WinContainer.successCallback(res);
                    });
                }
                else {
                    WinContainer.failureCallback(reqObject.id, "");
                }
                break;
            case Command.PLGN_KEYBD_LISTR:
                WinContainer.startKeybdLstn(reqObject);
                break;
            case Command.PLGN_KEYBD_LISTR_STOP:
                WinContainer.stopKeybdLstn(reqObject);
                break;
                // Map
            case Command.MAP_LOCATE:
                WinContainer.plugin.loadMap(reqObject);
                break;
            case Command.MAP_DRIVE_DIRECTION:
                WinContainer.plugin.drivingDirection(reqObject);
                break;
            case Command.MAP_AREA_SELECTOR:
                WinContainer.plugin.locationSelector(reqObject);
                break;
            case Command.GEO_FENCE:
                WinContainer.GeoFencing.activate(reqObject);
                break;

                // Media
            case Command.AUDIO:
                WinContainer.audio(reqObject);
                break;
            case Command.VIDEO_RECORD:
                WinContainer.plugin.videoRecording(reqObject);
                break;

                //NativeserviceExt
            case Command.NATIVE_SERVICE:
                WinContainer.nativeServiceExt(reqObject);
                break;

                //NFC
            case Command.NFC_SEND:
                WinContainer.NFC.send(reqObject);
                break;
            case Command.NFC_STOP:
                WinContainer.NFC.stop(reqObject);
                break;
            case Command.NFC_RECIEVE:
                WinContainer.NFC.receive(reqObject);
                break;

                //Notification
            case Command.PLGN_LTN_NTF:
                WinContainer.LstnNotif(reqObject);
                break;

            case Command.PLGN_LTN_NTF_STP:
                WinContainer.LstnNotifstop(reqObject);
                break;

                // Orientation and Rotation
            case Command.ORIENTATION_SET:
                WinContainer.setOrientation(reqObject);
                break;
            case Command.ROTATION_LOCK:
                WinContainer.lockRotation(reqObject);
                break;
            case Command.ROTATION_UNLOCK:
                WinContainer.unlockRotation(reqObject);
                break;
	        
			//Ruthvik 18-04-17 modified by adding new commands for orientation listener

            case Command.OR_LSTN_START:
                WinContainer.startOrtLstn(reqObject);
                break;
            case Command.OR_LSTN_STOP:
                WinContainer.stopOrientationListener(reqObject);
                break;
				
            case Command.TERMINATE_APP:
                window.close();
                break;

                // OTA
            case Command.LAUNCH_CHILD_APP:
                // case Command.TERMINATE_APP:
                WinContainer.failureCallback(reqObject.id, ErrorCode.NOT_SUPPORTED);
                break;

                // OTP
            case Command.GENERATE_OTP:
                WinContainer.generateOTP(reqObject);
                break;

                //Pulldown
            case Command.HIDE_REFRESH:
                if ((('ontouchstart' in window) || (navigator.maxTouchPoints > 0) || (navigator.msMaxTouchPoints > 0))) {
                    pullDown.hideRefresh(reqObject);
                } else
                    WinContainer.failureCallback(reqObject.id, "APZ-CNT-232");
                break;
            case Command.PLGN_EN_PLDN:
                if ((('ontouchstart' in window) || (navigator.maxTouchPoints > 0) || (navigator.msMaxTouchPoints > 0))) {
                    pullDown.enablePullDown(reqObject);
                } else
                    WinContainer.failureCallback(reqObject.id, "APZ-CNT-232");
                break;
            case Command.PLGN_DIS_PLDN:
                if ((('ontouchstart' in window) || (navigator.maxTouchPoints > 0) || (navigator.msMaxTouchPoints > 0))) {
                    pullDown.disablePullDown(reqObject);
                } else
                    WinContainer.failureCallback(reqObject.id, "APZ-CNT-232");
                break;

                // Setttings
            case Command.GET_SETTING:
                WinContainer.Settings.getuserpreference(reqObject);
                break;
            case Command.GET_SETTING_S:
                WinContainer.Settings.getUserPrefs(reqObject);
                break;
            case Command.SET_SETTING:
                WinContainer.Settings.setuserpreference(reqObject);
                break;
            case Command.SET_SETTING_S:
                WinContainer.Settings.setUserPrefs(reqObject);
                break;

            case Command.LOAD_SETTING_S:
                WinContainer.Settings.getAppProps(reqObject);
                break;
            case Command.SAVE_SETTING_S:
                WinContainer.Settings.setAppProps(reqObject);
                break;

                // Signature Pad
            case Command.SIGNATURE_PAD:
                WinContainer.isPhone() ? WinContainer.plugin.signaturePad(reqObject) : WinContainer.Signature.execute(reqObject);
                break;

                //Skype
            case Command.CALL_SKYPE:
                WinContainer.Skype.call(reqObject);
                break;

                // SMS
            case Command.SMS_SEND:
                WinContainer.SMS.send(reqObject);
                break;

                // Social Media
            case Command.GOOGLE_LOGIN:
                WinContainer.GooglePlus.Login(reqObject);
                break;
            case Command.FACEBOOK_LOGIN:
                WinContainer.Facebook.Login(reqObject);
                break;
            case Command.LINKEDIN_LOGIN:
                WinContainer.LinkedIn.Login(reqObject);
                break;
            case Command.TWITTER_LOGIN:
                WinContainer.Twitter.Login(reqObject);
                break;

                //Splash
            case Command.SPLASH_HIDE:
                WinContainer.hideSplash();
                break;

                // Sqlite
            case Command.EXECUTE_SQL:
                WinContainer.SQLite.execute(reqObject);
                break;

                // Vibrate
            case Command.VIBRATE_DEVICE:
                WinContainer.vibrate(reqObject);
                break;

                //URL
            case Command.OPEN_URL:
                WinContainer.openUrl(reqObject);
                break;
            case Command.PLGN_YOUTUBE:
                WinContainer.youtube(reqObject);
                break;
                // Voice Support
            case Command.VOICE:
                WinContainer.Voice.execute(reqObject);
                break;

                //Whatsapp
            case Command.WHATSAPP:
                WinContainer.isPhone() ? WinContainer.Whatsapp.msg(reqObject) : WinContainer.failureCallback(reqObject.id, ErrorCode.NOT_SUPPORTED);
                break;

                //Wipeout
            case Command.WIPEOUT:
                WinContainer.wipeOut(reqObject);
                break;

                // Webview
            case Command.WEBVIEW_LAUNCH:
                WinContainer.WebView.Launch(reqObject);
                break;
            case Command.WEBVIEW_CLOSE:
                WinContainer.WebView.Close(reqObject);
                break;

                // ZIP & UnZip
            case Command.ZIP:
                WinContainer.zip(reqObject);
                break;
            case Command.UNZIP:
                WinContainer.unzip(reqObject);
                break;


                // Native Purpose
            default:
                WinContainer.Log.error("Command Not Found !!!!");
                break;

        }
    } catch (e) {
        WinContainer.Log.fatal(e.message);
    }
}
//-----------
