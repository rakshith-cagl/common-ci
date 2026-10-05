package com.iexceed.common;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.accelerometer.AccelerometerPlugin;
import com.iexceed.plugins.appidletimeout.AppIdleTimeOut;
import com.iexceed.plugins.audio.AudioPlugin;
import com.iexceed.plugins.augmentedreality.ApzARPlugin;
import com.iexceed.plugins.barcode.ApzBarcodePlugin;
import com.iexceed.plugins.barcodegenerator.BarcodeGenerator;
import com.iexceed.plugins.battery.BatteryPlugin;
import com.iexceed.plugins.calendar.CalendarPlugin;
import com.iexceed.plugins.call.ApzCallPlugin;
import com.iexceed.plugins.camera.ApzCameraPlugin;
import com.iexceed.plugins.changepassword.ChangePassword;
import com.iexceed.plugins.compass.CompassPlugin;
import com.iexceed.plugins.contacts.Contacts;
import com.iexceed.plugins.createnote.ApzCreateNote;
import com.iexceed.plugins.currentlocation.ApzCurrentLocation;
import com.iexceed.plugins.customservercall.CustomURLServerCall;
import com.iexceed.plugins.dataSecurity.ApzDataSecurity;
import com.iexceed.plugins.dataSecurity.BiometricAvailability;
import com.iexceed.plugins.deeplinking.ApzDeeplinkingPlugin;
import com.iexceed.plugins.deviceinfo.DeviceInfo;
import com.iexceed.plugins.devicelocale.Localization;
import com.iexceed.plugins.email.EmailPlugin;
import com.iexceed.plugins.encryption.ApzEncryptionPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.plugins.filecrypto.FileCrypto;
import com.iexceed.plugins.fileoperation.ApzFileOperationPlugin;
import com.iexceed.plugins.filetobase64.ApzFileToBase64Plugin;
import com.iexceed.plugins.fingerprintscan.DeviceFingerprintAccess;
import com.iexceed.plugins.geofencing.GeoFencing;
import com.iexceed.plugins.gesturesupport.GesturePlugin;
import com.iexceed.plugins.gpslocation.ApzGpsLocatorPlugin;
import com.iexceed.plugins.ibeacon.ApzBeaconPlugin;
//import com.iexceed.plugins.inboxsms.InboxSMS;
import com.iexceed.plugins.inappreview.ApzInAppReview;
import com.iexceed.plugins.keyboard.KeyboardPlugin;
import com.iexceed.plugins.launchwebview.ApzLaunchWebview;
import com.iexceed.plugins.appzillonsdk.ApzSDK;
import com.iexceed.plugins.map.ApzMapPlugin;
import com.iexceed.plugins.miscellaneous.Miscellaneous;
import com.iexceed.plugins.calllogs.CallLogs;
//import com.iexceed.plugins.missedcall.MissedCallPlugin;
import com.iexceed.plugins.multiapp.MultiappUtils;
import com.iexceed.plugins.multiview.ApzPluginMultiView;
import com.iexceed.plugins.nativeextensibility.ApzExtensibilityPlugin;
import com.iexceed.plugins.nfc.ApzNFCPlugin;
import com.iexceed.plugins.notification.NotificationPlugin;
import com.iexceed.plugins.notifydownload.NotifyDownload;
import com.iexceed.plugins.openbrowser.ApzOpenBrowserPlugin;
import com.iexceed.plugins.orientation.SetWebview;
import com.iexceed.plugins.orientationlistener.DeviceOrientationListener;
import com.iexceed.plugins.pdfgenerator.ApzCreatePDF;
import com.iexceed.plugins.printfile.ApzPrintPlugin;
import com.iexceed.plugins.pulldown.ApzPullDown;
import com.iexceed.plugins.pulldown.PullDown;
import com.iexceed.plugins.pushnotification.ApzPushNotification;
import com.iexceed.plugins.report.Report;
import com.iexceed.plugins.ringtone.Ringtone;
import com.iexceed.plugins.savebase64topdf.Base64toPDFPlugin;
import com.iexceed.plugins.screenrotation.ScreenRotation;
import com.iexceed.plugins.signature.ApzCaptureSignaturePlugin;
import com.iexceed.plugins.sms.Sms;
import com.iexceed.plugins.speechtotext.SpeechToText;
import com.iexceed.plugins.storage.StoragePlugin;
import com.iexceed.plugins.util.ApzUtilsPlugin;
import com.iexceed.plugins.util.NativeServerCall;
import com.iexceed.plugins.vibration.Vibration;
import com.iexceed.plugins.video.ApzVideoPlugin;
import com.iexceed.plugins.wipeout.WipeOut;
import com.iexceed.plugins.zip.ApzZipPlugin;
import com.iexceed.plugins.texttospeech.ApzTTS;
import com.iexceed.plugins.realtimetracklocation.ApzTrackLocation;
import com.iexceed.plugins.nativeshare.ApzNativeShare;
import com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin;
import com.iexceed.plugins.processimage.ApzProcessImgPlugin;
import com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin;
import com.iexceed.plugins.shortcut.ShortcutPlugin;
import com.iexceed.plugins.security.ApzSecurityPlugin;
import com.iexceed.plugins.checkgpsstatus.CheckGpsStatus;

import com.scanlibrary.ApzDocScannerPlugin;


import org.json.JSONException;
import org.json.JSONObject;

public class ApzPluginBridge {

    private final String TAG = "ApzPluginBridge";

    protected WebView webView;
    protected ApzActivity activity;
    protected Context context;
    protected CustomProgressDialog progressDialog;
    protected Dialog splashDialog;

    public static JSMsgIntentRcvr jsMsgIntentReceiver = null;
    IntentFilter intentFilter = new IntentFilter(JSNotifier.APZ_INTENT_JSNOTIFY);

    public ApzPluginBridge(WebView webView, ApzActivity activity, Context context) {
        this.webView = webView;
        this.activity = activity;
        this.context = context;

        this.registerJSIntentReceiver();

    }

    private void registerJSIntentReceiver() {
        if (jsMsgIntentReceiver == null) {
            jsMsgIntentReceiver = new JSMsgIntentRcvr(activity, webView);
        }
        this.activity.registerReceiver(jsMsgIntentReceiver, intentFilter);
    }


    @JavascriptInterface
    public void executePlugin(String jsonText) {
        JSONObject request = null;
        try {
            request = new JSONObject(jsonText);

            ApzPlugin plugin = getPlugin(request);
            if (plugin != null) {
                plugin.execute(request);

            }
        } catch (Exception exp) {
            ApzLogger.e(TAG, exp.getMessage());
            JSONObject json = new JSONObject();
            String callbackId = null;
            try {
                callbackId = request.getString("id");
                json.put("text", exp.toString());
            } catch (JSONException e) {
            }
            ApzPluginUtil.sendError(callbackId, "", json, this.activity,
                    this.webView, true);
        }
    }

    @SuppressWarnings("finally")
    private ApzPlugin getPlugin(JSONObject jsonObject) {
        String command = null;
        String callbackId = null;
        ApzPlugin apzPlugin = null;
        try {
            callbackId = (String) jsonObject.get("id");
            command = (String) jsonObject.get("command");
            if (command == null || command.isEmpty()) {
                if (callbackId == null || callbackId.isEmpty()) {
                    ApzPluginUtil.sendPluginNotSupported(callbackId,
                            this.activity, this.webView, true);
                }

            } else {
                switch (command) {


                    case ApzPlugin.APZ_PLUGIN_CAMERA:
                        if (ApzCameraPlugin.IsCamera()) {
                            apzPlugin = ApzCameraPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                      case ApzPlugin.APZ_PLUGIN_AUTOCAPTURE:
                        if (ApzAutoCapturePlugin.isAutoCapture()) {
                            apzPlugin = ApzAutoCapturePlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                      case ApzPlugin.APZ_PLUGIN_SELFIECAPTURE:
                        if (ApzSelfieCapturePlugin.isSelfieCapture()) {
                            apzPlugin = ApzSelfieCapturePlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;

                      case ApzPlugin.APZ_PLUGIN_IMG_PROC:
                        if (ApzProcessImgPlugin.IsImgProcessing()) {
                            apzPlugin = ApzProcessImgPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_BEACON_START:
                        if (ApzBeaconPlugin.isBeaconPlugin()) {
                            apzPlugin = ApzBeaconPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_BEACON_STOP:
                        if (ApzBeaconPlugin.isBeaconPlugin()) {
                            apzPlugin = ApzBeaconPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CALL:
                        if (ApzCallPlugin.IsCall()) {
                            apzPlugin = ApzCallPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }

                        break;
                    case ApzPlugin.APZ_PLUGIN_BASE64TOFILE:
                        apzPlugin = Base64toPDFPlugin.createPlugin(this.webView,
                                this.activity);
                        break;
//				case ApzPlugin.APZ_PLUGIN_SCANFINGER:
//					apzPlugin = FingerprintScanPlugin.createPlugin(this.webView,
//							this.activity);
//					break;
                    case ApzPlugin.APZ_PLUGIN_AUTH:
                        if (DeviceFingerprintAccess.isPlugin()) {
                            apzPlugin = DeviceFingerprintAccess.createPlugin(
                                    this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
//                    case ApzPlugin.APZ_PLUGIN_MISSEDCALLS:
//                        if (MissedCallPlugin.IsCall()) {
//                            apzPlugin = MissedCallPlugin.createPlugin(this.webView, this.activity);
//                        } else {
//                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
//                        }
//                        break;
                    case ApzPlugin.APZ_PLUGIN_GETCALLLOGS:
                        if (CallLogs.IsCallLog()) {
                            apzPlugin = CallLogs.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
//                    case ApzPlugin.APZ_PLUGIN_CALLLISTENER:
//                        if (MissedCallPlugin.IsCall()) {
//                            apzPlugin = MissedCallPlugin.createPlugin(this.webView, this.activity);
//                        } else {
//                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
//                        }
//                        break;
                    case ApzPlugin.APZ_PLUGIN_OPENURL:
                        apzPlugin = ApzOpenBrowserPlugin.createPlugin(this.webView,
                                this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILETOBASE64:
                        apzPlugin = ApzFileToBase64Plugin.createPlugin(this.webView,
                                this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_LAUNCHWEBVIEW:
                        if (ApzLaunchWebview.isPlugin()) {
                            apzPlugin = ApzLaunchWebview.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CLOSEWEBVIEW:
                        if (ApzLaunchWebview.isPlugin()) {
                            apzPlugin = ApzLaunchWebview.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
//                    case ApzPlugin.APZ_PLUGIN_INBOXSMS:
//                        if (InboxSMS.IsSMS()) {
//                            apzPlugin = InboxSMS.createPlugin(this.webView, this.activity);
//                        } else {
//                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
//                        }
//                        break;
                    case ApzPlugin.APZ_PLUGIN_RINGTONE:
                        apzPlugin = Ringtone.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_NOTIF_SHOW:
                        if (ApzPushNotification.isPushNotification()) {
                            apzPlugin = ApzPushNotification.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_NOTIF_DELETE:
                        if (ApzPushNotification.isPushNotification()) {
                            apzPlugin = ApzPushNotification.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_SHORT_LSTN_STRT:
                        apzPlugin = ShortcutPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_SHORT_LSTN_STOP:
                        apzPlugin = ShortcutPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_SEND_NFC:
                        if (ApzNFCPlugin.isNFCDeviceActivity()) {
                            if (AppzillonUtils.isNFCSupported(context)) {
                                apzPlugin = ApzNFCPlugin.createPlugin(this.webView, this.activity);
                            } else {
                                ApzPluginUtil.sendError(callbackId, "APZ-CNT-116",
                                        null, this.activity, this.webView, true);
                            }
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_REC_NFC:
                        if (ApzNFCPlugin.isNFCDeviceActivity()) {
                            if (AppzillonUtils.isNFCSupported(context)) {
                                apzPlugin = ApzNFCPlugin.createPlugin(this.webView, this.activity);
                            } else {
                                ApzPluginUtil.sendError(callbackId, "APZ-CNT-116",
                                        null, this.activity, this.webView, true);
                            }
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_STOP_NFC:
                        if (ApzNFCPlugin.isNFCDeviceActivity()) {
                            if (AppzillonUtils.isNFCSupported(context)) {
                                apzPlugin = ApzNFCPlugin.createPlugin(this.webView, this.activity);
                            } else {
                                ApzPluginUtil.sendError(callbackId, "APZ-CNT-116",
                                        null, this.activity, this.webView, true);
                            }
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CURRENTLOCATION:
                        apzPlugin = ApzCurrentLocation.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_ZIP:
                        if (ApzZipPlugin.isZipPlugin()) {
                            apzPlugin = ApzZipPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_UNZIP:
                        if (ApzZipPlugin.isZipPlugin()) {
                            apzPlugin = ApzZipPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.PLGN_DEV_INFO:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_NETWORK_MONITOR:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_SET_PREF:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_GET_PREF:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_APP_AVAILABILITY:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_GET_USER_PREF:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_ENCRPT_DATA:
                        apzPlugin = ApzEncryptionPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_DECRPT_DATA:
                        apzPlugin = ApzEncryptionPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_HASH_PWD:
                        apzPlugin = ApzEncryptionPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_HASH_SHA256:
                        apzPlugin = ApzEncryptionPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_SPLASH_SHOW:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_SPLASH_HIDE:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_NTV_EXT:
                        apzPlugin = ApzExtensibilityPlugin.createPlugin(this.webView, this.activity);
                    default:
                        break;

                    // Natasha plugin 3.2
                    case ApzPlugin.APZ_PLUGIN_ACCE_START:
                        if (AccelerometerPlugin.isAccelerometer()) {
                            apzPlugin = AccelerometerPlugin.createPlugin(this.webView, this.activity, this.context);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_ACCE_STOP:
                        if (AccelerometerPlugin.isAccelerometer()) {
                            apzPlugin = AccelerometerPlugin.createPlugin(this.webView, this.activity, this.context);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.PLGN_SCN_BARCODE:
                        if (ApzBarcodePlugin.isBarcodeActivity()) {
                            apzPlugin = ApzBarcodePlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.PLGN_GALRY__SCN_BARCODE:
                        if (ApzBarcodePlugin.isBarcodeActivity()) {
                            apzPlugin = ApzBarcodePlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_CAL_CREATE:
                        if (CalendarPlugin.isCalendarPlugin()) {
                            apzPlugin = CalendarPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CAL_EDIT:
                        if (CalendarPlugin.isCalendarPlugin()) {
                            apzPlugin = CalendarPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CAL_DELETE:
                        if (CalendarPlugin.isCalendarPlugin()) {
                            apzPlugin = CalendarPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_COMP_START:
                        if (CompassPlugin.isCompassPlugin()) {
                            apzPlugin = CompassPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_COMP_STOP:
                        if (CompassPlugin.isCompassPlugin()) {
                            apzPlugin = CompassPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

//					case ApzPlugin.APZ_PLUGIN_ENCRYPT_DATA:
//						if(EncryptDecryptUtility.isEncryptDecryptUtility()){
//							apzPlugin = EncryptDecryptUtility.createPlugin(this.webView, this.activity);
//						}else{
//							ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
//						}
//					break;
//					
//					case ApzPlugin.APZ_PLUGIN_DECRYPT_DATA:
//						if(EncryptDecryptUtility.isEncryptDecryptUtility()){
//							apzPlugin = EncryptDecryptUtility.createPlugin(this.webView, this.activity);
//						}else{
//							ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
//						}
//					break;
//					
                    case ApzPlugin.APZ_PLUGIN_SQL:
                        if (StoragePlugin.isStoragePlugin()) {
                            apzPlugin = StoragePlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_DEVICEINFO:
                        if (DeviceInfo.isDeviceInfo()) {
                            apzPlugin = DeviceInfo.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_BATRY_START:
                        if (BatteryPlugin.isBatteryPlugin()) {
                            apzPlugin = BatteryPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_BATRY_STOP:
                        if (BatteryPlugin.isBatteryPlugin()) {
                            apzPlugin = BatteryPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_ENCRPT_FILE:
                        if (FileCrypto.isFileCryptoPlugin()) {
                            apzPlugin = FileCrypto.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_DECRPT_FILE:
                        if (FileCrypto.isFileCryptoPlugin()) {
                            apzPlugin = FileCrypto.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_GEOFENCING:
                        if (GeoFencing.isGeoFencing()) {
                            apzPlugin = GeoFencing.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_GPS_START:
                        if (ApzGpsLocatorPlugin.isGpsLocator()) {
                            apzPlugin = ApzGpsLocatorPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);

                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_GPS_STOP:
                        if (ApzGpsLocatorPlugin.isGpsLocator()) {
                            apzPlugin = ApzGpsLocatorPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);

                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_GESTURE_START:
                        if (GesturePlugin.isGesturePlugin()) {
                            apzPlugin = GesturePlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);

                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_GESTURE_STOP:
                        if (GesturePlugin.isGesturePlugin()) {
                            apzPlugin = GesturePlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId,
                                    this.activity, this.webView, true);

                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_FILE_BROWSER:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILE_CONTENT:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILE_CREATE:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILE_DELETE:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILE_OPEN:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_GETFILESIZE:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                        case ApzPlugin.APZ_PLUGIN_FILEDOWNLOADMGR:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILEUPLOAD:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_FILEDOWNLOAD:
                        if (ApzFileOperationPlugin.isFileOperation()) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(this.webView,
                                    this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_MAIL:
                        if (EmailPlugin.IsEmail()) {
                            apzPlugin = EmailPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_CONTACT_ADD:
                        if (Contacts.isContacts()) {
                            apzPlugin = Contacts.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CONTACT_DELETE:
                        if (Contacts.isContacts()) {
                            apzPlugin = Contacts.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CONTACT_SEARCH:
                        if (Contacts.isContacts()) {
                            apzPlugin = Contacts.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CONTACT_EDIT:
                        if (Contacts.isContacts()) {
                            apzPlugin = Contacts.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CONTACT_FETCH:
                        if (Contacts.isContacts()) {
                            apzPlugin = Contacts.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;


                    case ApzPlugin.APZ_PLUGIN_DRVNG_DIRCTN:
                        if (ApzMapPlugin.IsMap()) {
                            apzPlugin = ApzMapPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_LOAD_MAP:
                        if (ApzMapPlugin.IsMap()) {
                            apzPlugin = ApzMapPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_LOCATN_SELECTR:
                        if (ApzMapPlugin.IsMap()) {
                            apzPlugin = ApzMapPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_LOCALE:
                        if (Localization.isLocalization()) {
                            apzPlugin = Localization.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_REPORT:
                        if (Report.isReport()) {
                            Report.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_SIGNATUREPAD:
                        if (ApzCaptureSignaturePlugin.isSignauturePlugin()) {
                            apzPlugin = ApzCaptureSignaturePlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_IDLETIMEOUT:
                        if (AppIdleTimeOut.isPlugin()) {
                            apzPlugin = AppIdleTimeOut.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_LCK_ROTN:
                        apzPlugin = ScreenRotation.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_UNLCK_ROTN:
                        apzPlugin = ScreenRotation.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_ORIENTATION:
                        apzPlugin = SetWebview.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_VIBRATE:
                        if (Vibration.isVibratePlugin()) {
                            apzPlugin = Vibration.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_VOICE:
                        if (SpeechToText.isSpeechToTextPlugin()) {
                            apzPlugin = SpeechToText.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CONTROLEVENTS:
                        apzPlugin = Miscellaneous.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_WIPEOUT:
                        apzPlugin = WipeOut.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_GET_IP:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_APP_VERSION:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_CLS_APPCTN:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_MULTIVW_OPEN:
                        apzPlugin = ApzPluginMultiView.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_MULTIVW_CLOSE:
                        apzPlugin = ApzPluginMultiView.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_MULTIVW_RESIZE:
                        apzPlugin = ApzPluginMultiView.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_CREATENOTE:
                        apzPlugin = ApzCreateNote.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_PULDWN_ENABLE:
                        if (PullDown.isDummy) {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        } else {
                            apzPlugin = ApzPullDown.createPlugin(this.webView, this.activity);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_PULDWN_DISABLE:
                        if (PullDown.isDummy) {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        } else {
                            apzPlugin = ApzPullDown.createPlugin(this.webView, this.activity);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_HIDEREFRESH:
                        if (PullDown.isDummy) {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        } else {
                            apzPlugin = ApzPullDown.createPlugin(this.webView, this.activity);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_STARTAR:
                        if (ApzARPlugin.isPlugin()) {
                            apzPlugin = ApzARPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_RELOADAR:
                        if (ApzARPlugin.isPlugin()) {
                            apzPlugin = ApzARPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_VIDEO:
                        if (ApzVideoPlugin.isPlugin()) {
                            apzPlugin = ApzVideoPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_AUDIO:
                        if (AudioPlugin.isAudioPlugin()) {
                            apzPlugin = AudioPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;


                    case ApzPlugin.APZ_PLUGIN_LAUNCHAPP:
                        apzPlugin = MultiappUtils.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_DELETESUBAPP:
                        apzPlugin = MultiappUtils.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_GETINSTRUCTIONS:
                        apzPlugin = MultiappUtils.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_UPGRADEREQUIRED:
                        apzPlugin = MultiappUtils.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_UPDATE_ACTION:
                        apzPlugin = MultiappUtils.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_UPGRD_APP:
                        apzPlugin = MultiappUtils.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_SETSETTINGS:
                        apzPlugin = UserSettings.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_LOADSETTINGS:
                        apzPlugin = UserSettings.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_SENDSMS:
                        if (Sms.IsSMS()) {
                            apzPlugin = Sms.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_SMS_LSTN_STRT:
                        if (Sms.IsSMS()) {
                            apzPlugin = Sms.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_SMS_LSTN_STOP:
                        if (Sms.IsSMS()) {
                            apzPlugin = Sms.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_KEYBD_LSTN_STRT:
                        apzPlugin = KeyboardPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_KEYBD_LSTN_STOP:
                        apzPlugin = KeyboardPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_ORIENTATION_LISTN:
                        apzPlugin = DeviceOrientationListener.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_NOTIF_LSTN_STRT:
                        apzPlugin = NotificationPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_NOTIF_LSTN_STOP:
                        apzPlugin = NotificationPlugin.createPlugin(this.webView, this.activity);
                        break;
//                    case ApzPlugin.APZ_PLUGIN_SENDSMS_BY_SID:
//                        if (Sms.IsSMS()) {
//                            apzPlugin = Sms.createPlugin(this.webView, this.activity);
//                        } else {
//                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
//                        }
//
//                        break;
                    case ApzPlugin.APZ_PLUGIN_DOCUMENT_SCANNER:
                        if (ApzDocScannerPlugin.isDocScannerPlugin()) {
                            apzPlugin = ApzDocScannerPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_PRINT_SCR:
                        if (ApzPrintPlugin.isPlugin()) {
                            apzPlugin = ApzPrintPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_PRINT_FILE:
                        if (ApzPrintPlugin.isPlugin()) {
                            apzPlugin = ApzPrintPlugin.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_DEEPLNKNG:
                        apzPlugin = ApzDeeplinkingPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_PLUGIN_TRACK_LOC:
                        if (ApzTrackLocation.isPlugin()) {
                            apzPlugin = ApzTrackLocation.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_TEXT_TO_SPEECH:
                        if (ApzTTS.isPlugin()) {
                            apzPlugin = ApzTTS.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_NATIVE_SERVER_CALL:
                        apzPlugin = NativeServerCall.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_PLUGIN_CREATE_PDF:
                        if (ApzCreatePDF.isPlugin()) {
                            apzPlugin = ApzCreatePDF.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_GEN_BARCODE:
                        if (BarcodeGenerator.isPlugin()) {
                            apzPlugin = BarcodeGenerator.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_SECURE:
                        if (ApzDataSecurity.isPlugin()) {
                            apzPlugin = ApzDataSecurity.createPlugin(this.webView, this.activity);
                        } else {
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;

                    case ApzPlugin.APZ_PLUGIN_RESETNONCE:
                        apzPlugin = NativeServerCall.createPlugin(webView, activity);
                        break;

                    case ApzPlugin.APZ_BIOMET_AVAIL:
                        apzPlugin = BiometricAvailability.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_CHANG_PCHANGE:
                        apzPlugin = ChangePassword.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.APZ_NATIVE_SHARE:
                        apzPlugin = ApzNativeShare.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_NOTIFY_DOWNLOAD:
                        apzPlugin = NotifyDownload.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.APZ_CUSTOM_URL:
                        apzPlugin = CustomURLServerCall.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_SDK_CALLBACK:
                        if(ApzSDK.isApzSDK()){
                            apzPlugin = ApzSDK.createPlugin(this.webView,this.activity);
                        }else{
                            ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.PLGN_IS_APPTOKENSET:
                        apzPlugin = ApzUtilsPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_SECURITY_UTIL:
                        apzPlugin = ApzSecurityPlugin.createPlugin(this.webView, this.activity);
                        break;
                    case ApzPlugin.PLGN_INAPP_REVIEW:
                        apzPlugin = ApzInAppReview.createPlugin(this.webView, this.activity);
                        break;

                    case ApzPlugin.PLGN_CHECK_GPS_STATUS:
                        apzPlugin = CheckGpsStatus.createPlugin(this.webView, this.activity);
                        break;
                }
            }
        } catch (Exception e) {

            if (callbackId == null || callbackId.isEmpty()) {
                ApzPluginUtil.sendPluginNotSupported(callbackId, this.activity,
                        this.webView, true);
            }
        } finally {
            return apzPlugin;
        }
    }
}



