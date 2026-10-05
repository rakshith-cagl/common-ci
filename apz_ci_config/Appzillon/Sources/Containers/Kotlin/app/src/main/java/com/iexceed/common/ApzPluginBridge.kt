package com.iexceed.common

import android.content.Context
import android.content.IntentFilter
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.ApzPluginUtil
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.appidletimeout.AppIdleTimeOut
import com.iexceed.plugins.audio.AudioPlugin
import com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin
import com.iexceed.plugins.barcode.ApzBarcodePlugin
import com.iexceed.plugins.barcodegenerator.BarcodeGenerator
import com.iexceed.plugins.battery.BatteryPlugin
import com.iexceed.plugins.calendar.CalendarPlugin
import com.iexceed.plugins.call.ApzCallPlugin
import com.iexceed.plugins.camera.ApzCameraPlugin
import com.iexceed.plugins.changepassword.ChangePassword
import com.iexceed.plugins.checkgpsstatus.CheckGpsStatus
import com.iexceed.plugins.contacts.Contacts
import com.iexceed.plugins.currentlocation.ApzCurrentLocation
import com.iexceed.plugins.dataSecurity.ApzDataSecurity
import com.iexceed.plugins.dataSecurity.BiometricAvailability
import com.iexceed.plugins.database.DatabaseOperation
import com.iexceed.plugins.deeplinking.ApzDeeplinkingPlugin
import com.iexceed.plugins.deviceinfo.DeviceInfo
import com.iexceed.plugins.devicelocale.Localization
import com.iexceed.plugins.email.ApzEmailPlugin
import com.iexceed.plugins.encryption.ApzEncryptionPlugin
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.filecrypto.FileCrypto
import com.iexceed.plugins.fileoperation.ApzFileOperationPlugin
import com.iexceed.plugins.filetobase64.ApzFileToBase64Plugin
import com.iexceed.plugins.fingerprintscan.DeviceFingerprintAccess
import com.iexceed.plugins.gesturesupport.ApzGesturePlugin
import com.iexceed.plugins.gpslocation.ApzGpsLocatorPlugin
import com.iexceed.plugins.inappreview.ApzInAppReview
import com.iexceed.plugins.keyboard.KeyboardPlugin
import com.iexceed.plugins.launchwebview.ApzLaunchWebView
import com.iexceed.plugins.map.ApzMapPlugin
import com.iexceed.plugins.miscellaneous.Miscellaneous
import com.iexceed.plugins.multiapp.MultiappUtils
import com.iexceed.plugins.nativeextensibility.ApzExtensibilityPlugin
import com.iexceed.plugins.nativeshare.ApzNativeShare
import com.iexceed.plugins.nfcsupport.ApzIsNfcSupported
import com.iexceed.plugins.notification.NotificationPlugin
import com.iexceed.plugins.notifydownload.NotifyDownload
import com.iexceed.plugins.openbrowser.ApzOpenBrowserPlugin
import com.iexceed.plugins.orientation.SetWebView
import com.iexceed.plugins.orientationlistener.DeviceOrientationListener
import com.iexceed.plugins.pdfgenerator.ApzCreatePDF
import com.iexceed.plugins.processimage.ApzProcessImgPlugin
import com.iexceed.plugins.pulldown.ApzPullDown
import com.iexceed.plugins.realtimetracklocation.ApzTrackLocation
import com.iexceed.plugins.savebase64topdf.Base64toPDFPlugin
import com.iexceed.plugins.screenrotation.ScreenRotation
import com.iexceed.plugins.security.ApzSecurityPlugin
import com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin
import com.iexceed.plugins.shortcut.ShortcutPlugin
import com.iexceed.plugins.signature.ApzCaptureSignaturePlugin
import com.iexceed.plugins.sms.Sms
import com.iexceed.plugins.speechtotext.SpeechToText
import com.iexceed.plugins.storage.StoragePlugin
import com.iexceed.plugins.utils.ApzUtilsPlugin
import com.iexceed.plugins.utils.NativeServerCall
import com.iexceed.plugins.utils.RedirectToSettings
import com.iexceed.plugins.vibration.Vibration
import com.iexceed.plugins.wipeout.WipeOut
import com.iexceed.plugins.zip.ApzZipPlugin
import org.json.JSONException
import org.json.JSONObject

class ApzPluginBridge(
    aWebView: WebView,
    aMainScreenActivity: ApzActivity<*>,
    aContext: Context,
    private val apzPluginUtil: IapzPluginUtil
) {
    private val tag = "ApzPluginBridge"
    private var webView: WebView = aWebView
    private var activity: ApzActivity<*> = aMainScreenActivity
    private var context: Context? = null

    private var jsMsgIntentReceiver: JSMsgIntentRcvr? = null
    private var intentFilter: IntentFilter = IntentFilter(JSNotifier.APZ_INTENT_JSNOTIFY)

    init {
        this.webView = aWebView
        this.activity = aMainScreenActivity
        this.context = aContext
        this.registerJSIntentReceiver()
    }


    private fun registerJSIntentReceiver() {
        if (jsMsgIntentReceiver == null) {
            jsMsgIntentReceiver = JSMsgIntentRcvr(activity, webView)
        }
        activity.registerReceiver(jsMsgIntentReceiver, intentFilter,
            AppzillonConstants.APPZILLON_BROADCAST_PERMISSION, null)
    }

    @JavascriptInterface
    fun executePlugin(jsonText: String) {
        var request: JSONObject? = null
        //commented for now,will work on it later
        try {
            request = JSONObject(jsonText)
            val plugin: ApzPlugin? = getPlugin(request)
            plugin?.execute(request)
        } catch (exp: Exception) {
            exp.message?.let { ApzLogger.e(tag, it) }
            val json = JSONObject()
            var callbackId: String? = null
            try {
                callbackId = request!!.getString("id")
                json.put("text", exp.toString())
            } catch (e: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(callbackId, "", json, activity, webView, true)
        }
    }

    private fun getPlugin(request: JSONObject): ApzPlugin? {
        val command: String?
        val callbackId: String?
        var apzPlugin: ApzPlugin? = null
        try {
            callbackId = request.get("id") as String
            command = request.get("command") as String
            if (command.isNullOrEmpty()) {
                if (callbackId.isNullOrEmpty()) {
                    apzPluginUtil.sendPluginNotSupported(
                        callbackId,
                        this.activity,
                        this.webView,
                        true
                    )
                }
            } else {
                when (command) {
                    PluginConstants.PLGN_DEV_INFO,
                    PluginConstants.APZ_NETWORK_MONITOR,
                    PluginConstants.APZ_APP_AVAILABILITY,
                    PluginConstants.APZ_NOTIF_STATUS,
                    PluginConstants.PLGN_GET_USER_PREF,
                    PluginConstants.APZ_PLUGIN_SETSETTINGS,
                    PluginConstants.PLGN_IS_APPTOKENSET,
                    PluginConstants.PLGN_IS_NETWORK_AVAILABLE,
                    PluginConstants.APZ_PLUGIN_CLS_APPCTN,
                    PluginConstants.APZ_PLUGIN_GET_IP,
                    PluginConstants.APZ_PLUGIN_APP_VERSION,
                    PluginConstants.APZ_PLUGIN_SET_PREF,
                    PluginConstants.APZ_PLUGIN_GET_PREF ->
                        apzPlugin = ApzUtilsPlugin.createPlugin(webView, activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_TRACK_LOC ->
                        if (ApzTrackLocation.isPlugin()) {
                            apzPlugin = ApzTrackLocation.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_CURRENTLOCATION ->
                        apzPlugin = ApzCurrentLocation.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_PLUGIN_NOTIF_LSTN_STRT,
                    PluginConstants.APZ_PLUGIN_NOTIF_LSTN_STOP ->
                        apzPlugin = NotificationPlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_PLUGIN_WIPEOUT ->
                        apzPlugin = WipeOut.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_CONTROLEVENTS ->
                        apzPlugin =
                            Miscellaneous.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_DEEPLNKNG -> {
                        apzPlugin = ApzDeeplinkingPlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )
                    }

                    PluginConstants.APZ_PLUGIN_DB_OPERATION -> {
                        apzPlugin = DatabaseOperation.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )
                    }

                    PluginConstants.APZ_PLUGIN_AUTOCAPTURE -> {
                        apzPlugin = ApzAutoCapturePlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )
                    }
                    PluginConstants.APZ_PLUGIN_SELFIECAPTURE -> {
                        apzPlugin = ApzSelfieCapturePlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )
                    }
                    PluginConstants.APZ_PLUGIN_IMG_PROC -> {
                        if (ApzProcessImgPlugin.isPlugin()) {
                            apzPlugin = ApzProcessImgPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                    }

                    PluginConstants.APZ_PLUGIN_LOCALE -> {
                        if (Localization.isLocalization) {
                            apzPlugin = Localization.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                    }

                    PluginConstants.APZ_PLUGIN_SHORT_LSTN_STRT,
                    PluginConstants.APZ_PLUGIN_SHORT_LSTN_STOP -> {
                        apzPlugin =
                            ShortcutPlugin.createPlugin(this.webView, this.activity, apzPluginUtil)
                    }
                    PluginConstants.APZ_PLUGIN_DELETESUBAPP,
                    PluginConstants.APZ_PLUGIN_GETINSTRUCTIONS,
                    PluginConstants.APZ_PLUGIN_UPGRADEREQUIRED,
                    PluginConstants.APZ_PLUGIN_UPGRD_APP,
                    PluginConstants.APZ_PLUGIN_UPDATE_ACTION ->
                        apzPlugin =
                            MultiappUtils.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.PLGN_NTV_EXT ->
                        apzPlugin = ApzExtensibilityPlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_NATIVE_SHARE ->
                        apzPlugin =
                            ApzNativeShare.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_NATIVE_SERVER_CALL,
                    PluginConstants.APZ_PLUGIN_RESETNONCE ->
                        apzPlugin = NativeServerCall.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_CHANG_PWD ->
                        apzPlugin =
                            ChangePassword.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_AUTH -> {
                        if (DeviceFingerprintAccess.isPlugin) {
                            apzPlugin = DeviceFingerprintAccess.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                    }
                    PluginConstants.APZ_PLUGIN_CAMERA -> {
                        if (ApzCameraPlugin.IsCamera()) {
                            apzPlugin = ApzCameraPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                    }
                    PluginConstants.APZ_PLUGIN_SECURE ->
                        if (ApzDataSecurity.isPlugin) {
                            apzPlugin = ApzDataSecurity.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_FILE_CONTENT,
                    PluginConstants.APZ_PLUGIN_FILE_CREATE,
                    PluginConstants.APZ_PLUGIN_FILE_DELETE,
                    PluginConstants.APZ_PLUGIN_FILE_OPEN,
                    PluginConstants.APZ_PLUGIN_GETFILESIZE,
                    PluginConstants.APZ_PLUGIN_FILE_BROWSER,
                    PluginConstants.APZ_PLUGIN_FILEDOWNLOADMGR,
                    PluginConstants.APZ_PLUGIN_FILEUPLOAD,
                    PluginConstants.APZ_PLUGIN_FILEDOWNLOAD ->
                        if (ApzFileOperationPlugin.isFileOperation) {
                            apzPlugin = ApzFileOperationPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_CREATE_PDF ->
                        if (ApzCreatePDF.isPlugin()) {
                            apzPlugin = ApzCreatePDF.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_SENDSMS,
                    PluginConstants.APZ_PLUGIN_SMS_LSTN_STRT,
                    PluginConstants.APZ_PLUGIN_SMS_LSTN_STOP ,
                    PluginConstants.APZ_PLUGIN_SMS_SIMDET ->
                        if (Sms.IsSMS()) {
                            apzPlugin = Sms.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }

                    PluginConstants.APZ_PLUGIN_IS_NFC_SUPPORTED ->
                        apzPlugin = ApzIsNfcSupported.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_PLUGIN_BATRY_START,
                    PluginConstants.APZ_PLUGIN_BATRY_STOP ->
                        if (BatteryPlugin.isBatteryPlugin()) {
                            apzPlugin = BatteryPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                }
                when(command){
                    PluginConstants.APZ_PLUGIN_VOICE ->
                        if (SpeechToText.isSpeechToTextPlugin()) {
                            apzPlugin = SpeechToText.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_CONTACT_ADD,
                    PluginConstants.APZ_PLUGIN_CONTACT_DELETE,
                    PluginConstants.APZ_PLUGIN_CONTACT_SEARCH,
                    PluginConstants.APZ_PLUGIN_CONTACT_EDIT,
                    PluginConstants.APZ_PLUGIN_CONTACT_FETCH ->
                        if (Contacts.isPlugin()) {
                            apzPlugin =
                                Contacts.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }

                    PluginConstants.APZ_PLUGIN_AUDIO ->
                        if (AudioPlugin.isAudioPlugin()) {
                            apzPlugin =
                                AudioPlugin.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }

                    PluginConstants.PLGN_SCN_BARCODE,
                    PluginConstants.PLGN_GALRY__SCN_BARCODE ->
                        if (ApzBarcodePlugin.isBarcodeActivity) {
                            apzPlugin = ApzBarcodePlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_GEN_BARCODE ->
                        if (BarcodeGenerator.isPlugin) {
                            apzPlugin = BarcodeGenerator.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_CAL_CREATE,
                    PluginConstants.APZ_PLUGIN_CAL_EDIT,
                    PluginConstants.APZ_PLUGIN_CAL_DELETE ->
                        if (CalendarPlugin.isPlugin()) {
                            apzPlugin = CalendarPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_CALL ->
                        if (ApzCallPlugin.isPlugin()) {
                            apzPlugin = ApzCallPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_MAIL ->
                        if (ApzEmailPlugin.isPlugin()) {
                            apzPlugin = ApzEmailPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                    PluginConstants.PLGN_LOAD_MAP ->
                        if (ApzMapPlugin.isPlugin()) {
                            apzPlugin = ApzMapPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }
                    PluginConstants.APZ_PLUGIN_IDLETIMEOUT ->
                        if (AppIdleTimeOut.isPlugin()) {
                            apzPlugin = AppIdleTimeOut.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_GESTURE_START,
                    PluginConstants.APZ_PLUGIN_GESTURE_STOP ->
                        if (ApzGesturePlugin.isPlugin()) {
                            apzPlugin = ApzGesturePlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_KEYBD_LSTN_STRT,
                    PluginConstants.APZ_PLUGIN_KEYBD_LSTN_STOP ->
                        apzPlugin =
                            KeyboardPlugin.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_FILETOBASE64 ->
                        if (ApzFileToBase64Plugin.isPlugin()) {
                            apzPlugin = ApzFileToBase64Plugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_VIBRATE ->
                        if (Vibration.isVibratePlugin) {
                            apzPlugin =
                                Vibration.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }

                    PluginConstants.APZ_PLUGIN_GPS_START,
                    PluginConstants.APZ_PLUGIN_GPS_STOP ->
                        if (ApzGpsLocatorPlugin.isPlugin()) {
                            apzPlugin = ApzGpsLocatorPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_LAUNCHWEBVIEW,
                    PluginConstants.APZ_PLUGIN_CLOSEWEBVIEW ->
                        if (ApzLaunchWebView.isPlugin()) {
                            apzPlugin = ApzLaunchWebView.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_OPENURL ->
                        apzPlugin = ApzOpenBrowserPlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_PLUGIN_SIGNATUREPAD ->
                        if (ApzCaptureSignaturePlugin.isPlugin()) {
                            apzPlugin = ApzCaptureSignaturePlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_PULDWN_ENABLE,
                    PluginConstants.APZ_PLUGIN_PULDWN_DISABLE,
                    PluginConstants.APZ_PLUGIN_HIDEREFRESH ->
                        if (ApzPullDown.isPlugin()) {
                            apzPlugin =
                                ApzPullDown.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }
                }
                when (command) {
                    PluginConstants.APZ_PLUGIN_ORIENTATION ->
                        apzPlugin =
                            SetWebView.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_ORIENTATION_LISTN ->
                        apzPlugin = DeviceOrientationListener.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_PLUGIN_LCK_ROTN,
                    PluginConstants.APZ_PLUGIN_UNLCK_ROTN ->
                        apzPlugin =
                            ScreenRotation.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_ZIP,
                    PluginConstants.APZ_PLUGIN_UNZIP ->
                        if (ApzZipPlugin.isZipPlugin) {
                            apzPlugin = ApzZipPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_PLUGIN_ENCRPT_FILE,
                    PluginConstants.APZ_PLUGIN_DECRPT_FILE ->
                        if (FileCrypto.isFileCryptoPlugin) {
                            apzPlugin =
                                FileCrypto.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }

                    PluginConstants.PLGN_ENCRPT_DATA,
                    PluginConstants.PLGN_DECRPT_DATA,
                    PluginConstants.PLGN_HASH_PWD ->
                        apzPlugin = ApzEncryptionPlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.APZ_PLUGIN_DEVICEINFO ->
                        if (DeviceInfo.isPlugin()) {
                            apzPlugin =
                                DeviceInfo.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }

                    PluginConstants.PLGN_SECURITY_UTIL ->
                        apzPlugin = ApzSecurityPlugin.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )

                    PluginConstants.PLGN_INAPP_REVIEW ->
                        apzPlugin =
                            ApzInAppReview.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.PLGN_CHECK_GPS_STATUS ->
                        apzPlugin =
                            CheckGpsStatus.createPlugin(this.webView, this.activity, apzPluginUtil)


                    PluginConstants.PLGN_REDIRECT_TO_SETTINGS->
                        apzPlugin =
                            RedirectToSettings.createPlugin(this.webView, this.activity,apzPluginUtil)


                    PluginConstants.APZ_BIOMET_AVAIL ->
                        apzPlugin = BiometricAvailability.createPlugin(
                            this.webView,
                            this.activity,
                            apzPluginUtil
                        )


                    PluginConstants.APZ_PLUGIN_LOADSETTINGS ->
                        apzPluginUtil.sendPluginNotSupported(
                            callbackId,
                            this.activity,
                            this.webView,
                            true
                        )

                    PluginConstants.APZ_PLUGIN_BASE64TOFILE ->
                        if (Base64toPDFPlugin.isPlugin()) {
                            apzPlugin = Base64toPDFPlugin.createPlugin(
                                this.webView,
                                this.activity,
                                apzPluginUtil
                            )
                        }

                    PluginConstants.APZ_NOTIFY_DOWNLOAD ->
                        apzPlugin =
                            NotifyDownload.createPlugin(this.webView, this.activity, apzPluginUtil)

                    PluginConstants.APZ_PLUGIN_SQL ->
                        if (StoragePlugin.isStoragePlugin()) {
                            apzPlugin = StoragePlugin.createPlugin(this.webView, this.activity, apzPluginUtil)
                        }
                }
                if (apzPlugin == null) {
                    apzPluginUtil.sendPluginNotSupported(callbackId, activity, webView, true)
                }
            }
        } catch (e: Exception) {
            //handle exception
        }
        return apzPlugin
    }
}
