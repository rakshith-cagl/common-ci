using Appzillon.Constants;
using Appzillon.Plugins;
using Newtonsoft.Json.Linq;
using System;

namespace Appzillon.Native
{

    public class Request
    {
        MainPage page = null;
        public Request(MainPage page)
        {
            this.page = page;
        }

        public void ExecuteCommand(string req)
        {
            JObject reqObject;
            string command;

            #region Id validation
            try
            {
                reqObject = JObject.Parse(req);
                string id = reqObject[JsonKey.ID].ToString();
                command = reqObject[JsonKey.COMMAND].ToString();
                if (String.IsNullOrEmpty(id))
                {
                   Log.Warning("Invalid request id");
                }
            }
            catch (Exception e)
            {
               Log.Error("Invalid request :" + e.Message);
                return;
            }
            #endregion

            switch (command)
            {

                #region AcceleroMeter Completed
#if ACCELEROMETER
                case Command.ACCELERO_START:
                    AcceleroMeter.Instance.Start(reqObject);
                    break;
                case Command.ACCELERO_STOP:
                    AcceleroMeter.Instance.Stop(reqObject);
                    break;
#endif
                #endregion

                #region App idle time out
#if IDLETIMEOUT
                case Command.APP_IDLE_TIME_OUT:
                    AppIdleTimeOut.Execute(reqObject,page);
                    break;
#endif
                #endregion

                #region Augment Reality
                case Command.AUGMENT_START:
                    break;
                case Command.AUGMENT_STOP:
                    break;
                #endregion

                #region Barcode
                #if BARCODE
                case Command.BARCODE:
                    Barcode.Instance.StartScan(reqObject, page);
                    break;
                #endif
                #endregion

                #region Battery Completed
#if BATTERY
                case Command.BATTERY_START:
                    Battery.BatteryMonitorStart(reqObject);
                    break;
                case Command.BATTERY_STOP:
                    Battery.StopBatteryMonitor(reqObject);
                    break;
#endif
                #endregion

                #region Beacon
				#if BEACON
                case Command.BEACON_START:
                    Beacon.Instance.AdvertisementWatch(reqObject);
                    break;
                case Command.BEACON_STOP:
                    break;
			    #endif
                #endregion

                #region BioMetric Auth
#if BIOMETRIC
                case Command.FINGERPRINT_VERIFY:
                    BiometricAuthentication.Instance.FingerPrintAuthenticate(reqObject);
                    break;
#endif
                #endregion

                #region Calendar Completed
#if CALENDAR
                case Command.CALENDAR:
                    Calendar.Instance.saveAppointment(reqObject);
                    break;
                case Command.CALENDAR_EDIT:
                    Calendar.Instance.EditAppointment(reqObject);
                    break;
                case Command.CALENDAR_DELETE:
                    Calendar.Instance.DeleteAppointment(reqObject);
                    break;
#endif
                #endregion

                #region Call
                case Command.CALL:
                    PhoneCall.Call(reqObject);
                    break;
#endregion

                #region Camera
#if CAMERA
                case Command.CAMERA:
                    Camera.Instance.InitCamera(reqObject, page);
                    break;
                case Command.VIDEO_RECORD:
                    Camera.Instance.StartVideoRecord(reqObject, page);
                    break;
#endif
                #endregion

                #region Compass 
#if COMPASS
                case Command.COMPASS_START:
                    Compass.Instance.CompassStart(reqObject);
                    break;
                case Command.COMPASS_STOP:
                    Compass.Instance.CompassEnd(reqObject);
                    break;
#endif
                #endregion

                #region Contact
#if CONTACTS
                case Command.CONTACT_CREATE:
                    Contacts.Instance.ContactsPlugin(reqObject);
                    break;
                case Command.CONTACT_DELETE:
                    Contacts.Instance.ContactsDeletePlugin(reqObject);
                    break;
                case Command.CONTACT_SEARCH:
                    Contacts.Instance.ContactsSearchPlugin(reqObject);
                    break;
                case Command.CONTACT_EDIT:
                    Contacts.Instance.ContactsEditPlugin(reqObject);
                    break;
                    break;
                case Command.CONTACT_FETCH:
                    Contacts.Instance.FetchContact(reqObject);
                    break;
#endif
                #endregion

                case Command.DEVICE_INFO:
                    Device.GetDeviceInfo(reqObject);
                    break;

                #region Device
#if DEVICEINFO
                case Command.DEVICE:
                    Device.GetStatus(reqObject);
                    break;
#endif
                case Command.GET_IP:
                    Device.GetIP(reqObject);
                    break;

                #endregion

                #region DeepLinking
                case Command.DEEP_LINK:
                    DeepLinking.Instance.OpenApp(reqObject);
                    break;
                #endregion

                #region Email
#if MAIL
                case Command.EMAIL:
                    Mail.invokeEMail(reqObject);
                    break;
#endif
                #endregion

                #region Encryption and Decryption
#if CRYPTO
                case Command.ENCRYPT_FILE:
                    CryptoFile.Instance.Encryptfile(reqObject);
                    break;
                case Command.DECRYPT_FILE:
                    CryptoFile.Instance.DecryptFile(reqObject);
                    break;
#endif
                case Command.ENCRYPT_STRING:
#if CRYPTO
                    Crypto.Instance.Encryptstring(reqObject);
#else
                    Response.Fail(reqObject[JsonKey.ID].ToString(), "APZ-CNT-102");              
#endif
                    break;
                case Command.DECRYPT_STRING:
#if CRYPTO
                    Crypto.Instance.Decryptstring(reqObject);
#else
                    Response.Fail(reqObject[JsonKey.ID].ToString(), "APZ-CNT-102");
#endif
                    break;

                #endregion

                #region Control Events

                case Command.EVENTS:
                    Events.Instance.UpdateEvents(reqObject);
                    break;

                #endregion
                case "SERVER":
                   new ServerCall().Server_await(reqObject);
                    break;

                #region File Operation
#if FILEOPERATION
                case Command.PLGN_READ_FILE:
                    FileOperation.fileLaunch(reqObject);
                    break;
                case Command.FILE_BROWSER:
                    FileBrowser.Instance.FileBrowse(reqObject);
                    break;
                case Command.FILE_TO_BASE64:
                    FileOperation.GetBase64Code(reqObject);
                    break;
                case Command.FILE_UPLOAD:
                    FileOperation.UploadFile(reqObject);
                    break;
                case Command.CREATE_FILE:
                    FileOperation.FileCreate(reqObject);
                    break;
                case Command.DELETE_FILE:
                    FileOperation.FileDelete(reqObject);
                    break;
                case Command.FILE_CONTENT:
                    FileOperation.FileContent(reqObject);
                    break;
                case Command.FILE_DOWNLOAD:
                    FileOperation.DownloadFile(reqObject);
                    break;
                case Command.ZIP:
                    FileOperation.Zipit(reqObject);
                    break;
                case Command.UNZIP:
                    FileOperation.Unzip(reqObject);
                    break;
                case Command.GET_FILE_SIZE:
                    FileOperation.GetFileSize(reqObject);
                    break;
                case Command.BASE64_TO_PDF:
                    FileOperation.Base64ToPdf(reqObject,page);
                    break;
                case Command.BASE64_TO_FILE:
                    FileOperation.Base64ToFile(reqObject);
                    break;
#endif
                #endregion

                #region Get Instructions
                case Command.GET_INSTRUCTION:
                    new AppMasterRequest(page).GetAppInstruction(reqObject);
                    break;

                #endregion

                #region GPS
#if GPS
                case Command.GPS_START:
                    Gps.Instance.startGPS(reqObject);
                    break;
                case Command.GPS_STOP:
                    Gps.Instance.endGPS(reqObject);
                    break;
#endif
                #endregion
                case Command.PLGN_GET_LOCATION:
                    Utils.GetLoc(reqObject);
                    break;

#region pdf
                case Command.PLGN_OPN_FILE:
                    Pdf.PdfViewer(reqObject,page);
                break;
                #endregion

                #region Keyboard visibility
                case Command.PLGN_KEYBD_LISTR_STP:
                    Utils.KeyBrdListnerStop(reqObject);
                    break;
                case Command.PLGN_LTN_KEY:
                    Utils.KeyBrdListner(reqObject);
                    break;
                case Command.SHOW_KEYBOARD:
                    Utils.SetKeyBoardVisibility("SHOW");
                break;
                case Command.HIDE_KEYBOARD:
                    Utils.SetKeyBoardVisibility("HIDE");
                break;
#endregion

#region Map
#if MAP
                case Command.MAP_LOCATE:
                    Map.Instance.initializeMap(reqObject, page);
                    break;
                case Command.MAP_DRIVE_DIRECTION:
                    MapDrivingDirection.Instance.initializeMap(reqObject, page);
                    break;
                case Command.MAP_AREA_SELECTOR:
                    MapAreaSelector.Instance.initializeMap(reqObject, page);
                    break;
#endif
#if GEOFENCING
                case Command.GEO_FENCE:
                    GeoFencing.Instance.GeoFence(reqObject);
                    break;
#endif
#endregion

#region Media Completed
#if MEDIA

                case Command.AUDIO:
                    Audio.Instance.AudioPlug(reqObject, page);
                    break;
                case Command.VIDEO_PLAY:
                    VideoPlayer.Instance.StartVideoPlay(reqObject, page);
                    break;
#endif
#endregion

#region Natve service extension
                case Command.NATIVE_SERVICE_EXT:
                    NativeService.NativeServiceEntry(reqObject,page);
                    break;
#endregion

#region NFC
                #if NFC
                case Command.SEND_NFC:
                    NFC.Instance.SendNFC(reqObject);
                    break;
                case Command.RECEIVE_NFC:
                    NFC.Instance.ReceiveNFC(reqObject);
                    break;
                case Command.STOP_NFC:
                    NFC.Instance.stopNFC(reqObject);
                    break;
                #endif
#endregion

//#region Notification
//                #if NOTIFICATION
//                case Command.GET_NOTIFICATION:
//                 //   Notificatons.Instance.GetNotification(reqObject);
//                    break;
//                case Command.PLGN_LTN_NTF_STP:
//                   new Notification().UnRegisterNotification(reqObject);
//                    break;
//                case Command.PLGN_LTN_NTF:
//                    Utils.RegisterNotification(reqObject);
//                    break;
//                case Command.SHOW_NOTIFICATION:
//                 //   Notificatons.Instance.ShowNotification(reqObject);
//                    break;

//                case Command.DELETE_NOTIFICATION:
//                 //   Notificatons.Instance.DeleteNotification(reqObject);
//                    break;

//                case Command.UPDATE_NOTIFICATION:
//                //    Notificatons.Instance.UpdateNotification(reqObject);
//                    break;
//                #endif
//#endregion

#region Orientation and Rotation
                case Command.ORIENTATION_SET:
                    Utils.SetOrientation(reqObject);
                    break;
                case Command.ROTATION_LOCK:
                    Utils.LockRotation(reqObject);
                    break;
                case Command.ROTATION_UNLOCK:
                    Utils.UnlockRotation(reqObject);
                    break;
                case Command.PLGN_SET_ORTN_LSTR:
                    Utils.SetOrientaionListener(reqObject);
                    break;
#endregion

#region OTA
                case Command.LAUNCH_CHILD_APP:
                    OTARefresh.LaunchChildApp(reqObject, page);
                    break;
                case Command.DELETE_CHILD_APP:
                    OTARefresh.DeleteApp(reqObject);
                    break;
                case Command.TERMINATE_APP:
                    Utils.Terminate();
                    break;
                case Command.UPGRADE_APP:
                    OTARefresh.UpgradeApp(reqObject,page);
                    break;
                case Command.UPGRADE_REQ:
                    OTARefresh.checkOTA(reqObject);
                    break;

#endregion

                #region OTP
                case Command.GENERATE_OTP:
                    HashSHA256.GetOTP(reqObject);
                    break;
#endregion

#region Setttings
                case Command.GET_SETTING:
                    AppSettings.GetSetting(reqObject);
                    break;
                case Command.GET_SETTING_S:
                    AppSettings.GetSettings(reqObject);
                    break;
                case Command.SET_SETTING:
                    AppSettings.SetSetting(reqObject);
                    break;
                case Command.SET_SETTING_S:
                    AppSettings.SetSettings(reqObject);
                    break;
#endregion

#region Signature Pad
#if SIGNATUREPAD
                case Command.SIGNATURE_PAD:
                    SignaturePad.Instance.StartSignPad(reqObject, page);
                    break;
#endif
#endregion

#region Screen Map
                case Command.SET_SCREEN_MAP:
                    Utils.SetScreenMapping(reqObject);
                    break;
#endregion

#region SMS
                case Command.SMS_SEND:
                    PhoneSMS.SmsSend(reqObject);
                    break;
#endregion

#region Social Media

#if GOOGLE
                case Command.GOOGLE_LOGIN:
                    Google.Instance.StartLoginProcess(reqObject);
                    break;
#endif

#if FACEBOOK
                case Command.FACEBOOK_LOGIN:
                    Facebook.Instance.StartLoginProcess(reqObject);
                    break;
#endif
#if LINKEDIN
                case Command.LINKEDIN_LOGIN:
                    LinkedIn.Instance.StartLoginProcess(reqObject);
                    break;
#endif
#if TWITTER
                case Command.TWITTER_LOGIN:
                    Twitter.Instance.StartLoginProcess(reqObject);
                    break;
#endif
#if WHATSAPP
                case Command.PLGN_OP_WP:
                    Whatsapp.InitWhatsapp(reqObject);
                    break;
#endif
#endregion

#region Skype
#if SKYPE
                case Command.SKYPE:
                    SkypeCall.Instance.StartSkypeCall(reqObject);
                    break;
#endif
#endregion

#region Splash
                case Command.SHOW_SPLASH:
                    //  Splash.Instance.ShowSplash(reqObject,page);
                    break;
                case Command.HIDE_SPLASH:
                    Splash.Instance.HideSplash(reqObject, page);
                    break;
#endregion

#region Sqlite
#if STORAGE
                case Command.EXECUTE_SQL:
                    Storage.Instance.ExecuteQuery(reqObject);
                    break;
#endif
                #endregion

                #region Url
                    #if STORAGE

                case Command.UPDATE_WHITELIST:
                    WhiteList.Instance.UpdateWhiteList(reqObject);
                    break;
                case Command.UPDATE_WHITELIST_TO_LOCAL:
                    WhiteList.Instance.UpdateLocal(reqObject);
                    break;
                case Command.VALIDATE_WHITELIST:
                    WhiteList.Instance.ValidateUrl(reqObject);
                    break;
#endif
#endregion

                #region Vibrate
#if VIBRATE
                case Command.VIBRATE_DEVICE:
                    Vibrate.VibrateDevice(reqObject);
                    break;
#endif
#endregion

#region View
#if WEBVIEW
                case Command.WEBVIEW_LAUNCH:
                    Webview.Instance.LaunchWebview(reqObject, page);
                    break;
                case Command.WEBVIEW_CLOSE:
                    Webview.Instance.CloseWebview();
                    break;
#endif
                case Command.MULTIVIEW_LAUNCH:
                    MultiView.Instance.launch(reqObject, page);
                    break;
                case Command.MULTIVIEW_CLOSE:
                    MultiView.Instance.CloseBrowser(reqObject, page);
                    break;
#endregion

#region Voice Support
#if VOICE
                case Command.VOICE:
                    Voice.Instance.VoiceSupport(reqObject);
                    break;
#endif
                #endregion

                case Command.URL_OPEN:
                    UrlLaunch.Instance.Launch(reqObject);
                    break;
					
                case Command.WIPE_OUT:
                    Utils.WipeOut(reqObject);
                    break;
                case Command.GET_APPVERSION:
                    Utils.GetAppVersion(reqObject);
                    break;


#region Native Purpose
                case Command.DEBUG:
                    AppzillonDebug.JavascriptConsole(reqObject["message"].ToString());
                    break;
                case Command.ALERT:
                   Utils.Alert(reqObject["message"].ToString());
                    break;
#endregion

                default:
                    Log.Error("Command Not Found!!!!!");
                    break;
            }
        }

    }
}
