package com.iexceed.common

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.res.AssetManager
import android.content.res.Configuration
import android.location.LocationManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Base64
import android.util.DisplayMetrics
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonConstants.CRYPTO_ALGORITHM
import com.iexceed.common.AppzillonConstants.IV_LENGTH
import com.iexceed.common.StringUtils.getString
import com.iexceed.plugins.ApzPluginUtil
import com.iexceed.plugins.encryption.ApzEncryptionPlugin
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.miscellaneous.Miscellaneous
import com.iexceed.utils.common.PackageInfoUtils
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.nio.file.Paths
import java.security.Key
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.system.exitProcess


object  AppzillonUtils {

    private var TAG = "AppzillonUtils"

    /**
     * Intent used to display a message in the screen.
     */
    const val DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE"

    private val encryptionKeyForServerValue = "APPZILLONDECRYPT"
    private val REQUEST_DEVICEINFO = 0
    private val apzPluginUtil = ApzPluginUtil()

    //Device rooted or not check
    fun isDeviceRooted(context: Context?): Boolean {
        val rootBeer = RootBeer(context)
        return rootBeer.isRooted || rootBeer.detectRootCloakingApps()
    }

    //Get Device ID
    fun getDeviceId(aEncryptedSharedPreferences: SharedPreferences): String
    {
        var deviceId = aEncryptedSharedPreferences.getString(AppzillonConstants.sDeviceId, "")
        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
            setDeviceId(deviceId, aEncryptedSharedPreferences)
        }
        return deviceId
    }

    //Get Device ID
    fun getDeviceId(): String {
        val lPref = EncryptedPrefHelper.getPrefs()
        var deviceId = lPref.getString(AppzillonConstants.sDeviceId, "")
        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
            setDeviceId(deviceId, lPref)
        }
        return deviceId
    }

    //Sets DeviceId
    fun setDeviceId(deviceId: String?, aEncryptedSharedPreferences: SharedPreferences) {
        val editor = aEncryptedSharedPreferences.edit()
        editor.putString(AppzillonConstants.sDeviceId, deviceId)
        editor.apply()
    }

    //Get OS Details
    fun getOsDetails(aContext: Context): String {
        return Build.VERSION.RELEASE // Build.VERSION.SDK_INT;
    }

    //Get App Current vVersion
    fun getCurrentAppStoreVersion(aContext: Context): String {
        var versionNumber = "" // Build.VERSION.SDK_INT;
        try {
            versionNumber = PackageInfoUtils.getPackageDetails(aContext).versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionNumber
    }

    //Check If app is running on Emulator or not
    fun isRunningOnEmulator(): Boolean {
        var result = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.MANUFACTURER == "unknown" || Build.MODEL.contains("Android SDK built for x86"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == Build.PRODUCT)
        return result
    }

    //Get Device Model Name
    fun getDeviceType(): String {
        return Build.MODEL
    }

    //Get Device Manufacturer Name
    fun getDeviceMake(): String {
        return Build.MANUFACTURER
    }

    //Check if NFC Supported or not
    fun isNFCSupported(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)
    }

    //Check if device is tablet or Mobile
    fun isTablet(context: Context): Boolean {
        val screenSize = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        val density = context.resources.displayMetrics.densityDpi

        return screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE && density >= DisplayMetrics.DENSITY_MEDIUM
    }


    fun getScreenSize(aContext: Context): String {
        val lwidth: Int = aContext.resources.displayMetrics.widthPixels
        val lheight: Int = aContext.resources.displayMetrics.heightPixels
        return "$lwidth X $lheight"
    }

    fun getPPI(aContext: Context): String {
        val density: Int = aContext.resources.displayMetrics.densityDpi
        return density.toString()
    }

    fun closeApplication(activity: Activity) {
        ApzLogger.d(TAG, " closeApplication ")
        activity.finish()
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic") || Build.PRODUCT.contains("sdk")
    }

    // remove this
    fun ipAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress!! }
        }
        return ""
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun getDeviceValue(aActivity: Activity): String {
        val metrics = aActivity.resources.displayMetrics
        var width = metrics.widthPixels
        var height = metrics.heightPixels

        val dpi = metrics.densityDpi.toFloat()
        val n = Math.round(dpi / 160)
        width /= n
        height /= n
        val manager = aActivity.assets
        val devicegrp: JSONArray
        var orientation: String? = null
        var devname: String? = null
        var count = 0
        var temp = 0
        try {
            val `is`: InputStream = getInputStream(manager)
            val reader = BufferedReader(InputStreamReader(`is`))
            val out = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                out.append(line)
            }
            try {
                devicegrp = JSONObject(out.toString()).getJSONArray("deviceGroups")
                val pair = pair(devicegrp, width, height, orientation, devname, count, temp)
                devname = pair.first
                orientation = pair.second
            } catch (e: JSONException) {
                return ""
            } finally {
                `is`.close()
            }
        } catch (e1: IOException) {
            //Sonar fix
        }
        return if (!devname.equals("", ignoreCase = true) && !orientation.equals("", ignoreCase = true)) {
            var lockRotation = true
            if (orientation.equals("ANY", ignoreCase = true)) {
                lockRotation = false
                val rotationInt = aActivity.resources.configuration.orientation
                orientation = if (rotationInt == 1) {
                    "PORTRAIT"
                } else {
                    "LANDSCAPE"
                }
            }

            handleLockRotation(lockRotation, orientation, aActivity)
            return "$devname,$orientation,$lockRotation"
        } else ""
    }

    private fun pair(
        devicegrp: JSONArray,
        width: Int,
        height: Int,
        orientation: String?,
        devname: String?,
        count: Int,
        temp: Int
    ): Pair<String?, String?> {
        var orientation1 = orientation
        var devname1 = devname
        var count1 = count
        var temp1 = temp
        for (i in 0 until devicegrp.length()) {
            val devval = devicegrp.getJSONObject(i)
            val devwidth = devval.getString("width").toInt()
            val devheight = devval.getString("height").toInt()
            var deltawidth = width - devwidth
            var deltaheight = height - devheight
            if (deltawidth == 0 && deltaheight == 0) {
                orientation1 = devval.getString("orientation")
                devname1 = devval.getString("name")
                break
            }
            if (deltawidth < 0) {
                deltawidth = 0 - deltawidth
            }
            if (deltaheight < 0) {
                deltaheight = 0 - deltaheight
            }
            var deltaWidthHeight = deltawidth + deltaheight
            if (deltaWidthHeight < 0) {
                deltaWidthHeight = 0 - deltaWidthHeight
            }
            if (count1 == 0) {
                temp1 = deltaWidthHeight
                count1++
                orientation1 = devval.getString("orientation")
                devname1 = devval.getString("name")
            }
            if (temp1 > deltaWidthHeight) {
                temp1 = deltaWidthHeight
                orientation1 = devval.getString("orientation")
                devname1 = devval.getString("name")
            }
        }
        return Pair(devname1, orientation1)
    }

    private fun handleLockRotation(
        lockRotation: Boolean,
        orientation: String?,
        aActivity: Activity
    ) {
        if (lockRotation) {
            if (orientation.equals("LANDSCAPE", ignoreCase = true)) {
                aActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else if (orientation.equals("PORTRAIT", ignoreCase = true)) {
                aActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                aActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    private fun getInputStream(manager: AssetManager) =
        if (AppzillonMainScreen.OTAREQUIRED.equals("Y")) {
            val jsonFile =
                File(AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "screens/config/devicegroups.json")
            FileInputStream(jsonFile)
        } else {
            manager.open(AppzillonMainScreen.ASSET_APP_LOC + "screens/config/devicegroups.json")
        }

    fun isAppExpired(context: Context, appName: String?): Boolean {
        var appExpired = false
        val aEncryptedSharedPreferences =EncryptedPrefHelper.getPrefs(context)
        if ("false".equals(UserSettings.isApplicationExpired(appName!!, aEncryptedSharedPreferences), ignoreCase = true)) {

            try {
                val curDate = Date()
                val sdf = SimpleDateFormat(getString(StringUtils.EXPIRY_DATE_FORMAT))
                val currDate = sdf.parse(sdf.format(curDate))
                val expdate = getString(StringUtils.EXPIRY_DATE)
                val expirydate = sdf.parse(expdate)
                if (currDate != null && currDate > expirydate) {
                    appExpired = true
                }
            } catch (ex: Exception) {
                //handle exception
            }
        } else {
            appExpired = true
        }
        return appExpired
    }

    fun getDeviceRunTimeInfo(activity: Activity): JSONObject {
        // 3.2 changes
        val obj = JSONObject()
        val deviceGroup = getDeviceValue(activity)
        val deviceInfo: Array<String?> = deviceGroup.split(",").toTypedArray()
        try {
            if (AppzillonConstants.IS_SERVER) {
                obj.put("deviceId", getDeviceId())
            }
            obj.put("deviceName", getBluetoothName(activity))
            obj.put("deviceOs", "ANDROID")
            obj.put("deviceType", "ANDROID")
            obj.put("screenSize", getScreenSize(activity))
            obj.put("screenPpi", getPPI(activity)) // 3.2 changes
            obj.put("deviceGroup", deviceInfo[0])
            obj.put("orientation", deviceInfo[1])
            obj.put("lockRotation", deviceInfo[2])
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }

    fun getAllSettingsValue(jsonNodeVal: String?, aEncryptedSharedPreferences: SharedPreferences): JSONObject? {
        val defaultval = ""
        var json: JSONObject? = null
        val sb = java.lang.StringBuilder()
        sb.append("{")
        var i = 0
        try {
            val settingjson = JSONObject(jsonNodeVal)
            val settingskey = settingjson.keys()
            while (settingskey.hasNext()) {
                val name = settingskey.next()
                val str: String = UserSettings.getAppValue(
                    AppzillonMainScreen.APP_NAME,
                    name,
                    defaultval,
                    aEncryptedSharedPreferences
                )
                sb.append("\"" + name + "\"")
                sb.append(":")
                sb.append("\"" + str + "\"")
                i++
                if (i < settingjson.length()) {
                    sb.append(",")
                }
            }
            sb.append("}")
            json = JSONObject(sb.toString())
        } catch (e: java.lang.Exception) {
            //handle exception
        }
        return json
    }

    fun decyyptString(key: String, textToDecrypt: String): String {
        getIV(key)
        val salt: String = getSalt(key)
        return decryptString(key, textToDecrypt, salt)
    }

    fun getIV(key: String?): ByteArray {
        val iv = ByteArray(16)
        Arrays.fill(iv, 0.toByte())
        val or = StringBuffer(key)
        val nw = or.reverse().toString()
        var keyBytes: ByteArray? = null
        try {
            keyBytes = nw.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            //handle exception
        }
        val rawIV = ByteArray(keyBytes!!.size)
        for (i in keyBytes.indices) {
            rawIV[i] = (keyBytes[i].toInt() shr 1).toByte()
        }
        for (i in iv.indices) {
            iv[i] = rawIV[i]
        }
        return iv
    }

    fun getSalt(key: String): String {
        val c = key.toCharArray()
        // Replace with a "swap" function, if desired:
        var temp = c[0]
        c[0] = c[1]
        c[1] = temp
        temp = c[c.size - 1]
        c[c.size - 1] = c[c.size - 2]
        c[c.size - 2] = temp
        return String(c)
    }

    /*	// decrypt
	private static String decryptString(String cypher, String key, String textToDecrypt, String salt, byte[] iv) {
		SecretKeySpec skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
		try {
			Cipher cipher = Cipher.getInstance(cypher);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);
			byte[] plaintext = cipher.doFinal(Base64.decode(textToDecrypt, Base64.NO_WRAP | Base64.NO_PADDING));
			String plainrStr = new String(plaintext, "UTF-8");
			if (plainrStr == null)
				return null;

			return new String(plainrStr);
		} catch (Exception e) {
		}
		return null;
	}*/

    //decrypt
    private fun decryptString(
        key: String,
        textToDecrypt: String,
        salt: String
    ): String {
        val skeySpec = SecretKeySpec(hmacSha1(salt, key), "AES")
        try {
            // Master sync
            /*val cipher = Cipher.getInstance(cypher)
            val ivParams = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams)*/

            val plainText: ByteArray
            try {
                val ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP or Base64.NO_PADDING)
                val cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
                //use first 12 bytes for iv
                val gcmIv: AlgorithmParameterSpec = GCMParameterSpec(128, ciphertext, 0, IV_LENGTH)
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmIv)
                plainText = cipher.doFinal(
                    ciphertext,
                    IV_LENGTH,
                    ciphertext.size - IV_LENGTH
                )
                val plainrStr = String(plainText, Charset.forName("UTF-8"))
                return String(plainrStr.toByteArray())
            } catch (e: Exception) {
                //handle exception
            }
        } catch (e: java.lang.IllegalArgumentException) {
            return "plain"
        } catch (e: IllegalBlockSizeException) {
            return "plain"
        } catch (e: java.lang.Exception) {
            //handle exception
        }
        return ""
    }

    private fun hmacSha1(salt: String, key: String): ByteArray {
        val factory: SecretKeyFactory?
        var keyByte: Key? = null
        val keyLength: Int
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            keyLength = 256
            val keyspec: KeySpec =
                PBEKeySpec(key.toCharArray(),
                    salt.toByteArray(charset("UTF-8")),
                    2, keyLength)

            keyByte = factory.generateSecret(keyspec)
        } catch (e: Exception) {
            //handle exception
        }
        return keyByte!!.encoded
    }

     fun getBluetoothName(activity: Activity): String {
        val securebluetooth= getSecureBluetooth(activity)
        if(securebluetooth.isNullOrEmpty()){
            var devicename = "ANDROID"
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            devicename = mBluetoothAdapter.name
            if (devicename == null) {
                devicename = mBluetoothAdapter.address
            }
            return  devicename
        }else{
            return securebluetooth
        }
        return securebluetooth
    }

    fun getSecureBluetooth(activity: Activity) : String{
        try{
           return  Settings.Secure.getString(activity.contentResolver, "bluetooth_name")
        }catch (e:Exception){
            return Settings.Global.getString(activity.contentResolver, Settings.Global.DEVICE_NAME) ?: ""
        }
    }

    private lateinit var album: Array<Signature>
    fun getCurrentSong(context: Context): String
    {
        try {
            album = if (Build.VERSION.SDK_INT >= 28) {
                context.packageManager.getPackageInfo(context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.apkContentsSigners
            } else {
                context.packageManager.getPackageInfo(context.packageName,
                    PackageManager.GET_SIGNATURES).signatures
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return ""
        }
        if (album.isNotEmpty())
        {
            val song = album[0]
            try {
                val md = MessageDigest.getInstance("SHA-512")
                md.update(song.toByteArray())
                //Comment out below line, once we get the Key
//				ApzLogger.d("GET_KEY",currentSong);
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {
                //handle exception
            }
        }
        return ""
    }

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    fun displayMessage(context: Context, message: String?) {
        val intent = Intent(DISPLAY_MESSAGE_ACTION)
        intent.putExtra(AppzillonConstants.EXTRA_MESSAGE, message)
        context.sendBroadcast(intent, AppzillonConstants.APPZILLON_BROADCAST_PERMISSION)
    }

    //Wipeout for clearing the preferences
    fun wipeOutMainApp(context: Context) {
        ApzLogger.d(TAG, "wipeOutMainApp")

        val lencryptedPref = EncryptedPrefHelper.getPrefs()
        val encryptedEditor: SharedPreferences.Editor = lencryptedPref.edit()
        encryptedEditor.clear()
        encryptedEditor.apply()

        val uninstall = Intent(Intent.ACTION_DELETE)
        uninstall.data = Uri.parse("package:" + context.getPackageName())
        context.startActivity(uninstall)
    }

    /**
     * To check current volume level of the device
     */
    fun checkVolumeState(aActivity: AppCompatActivity,aWebView: WebView, volUpDown: String)
    {
        val result = JSONObject()
        val audioManager = aActivity.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val currVolLevel = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        if (volUpDown == "U") {
            result.put("event", "volumeUp")
            result.put("volumeLevel", currVolLevel)
            if (UserSettings.getAppValue(
                    AppzillonMainScreen.APP_NAME,
                    "allEvents", "off",
                    EncryptedPrefHelper.getPrefs()) == "on" ||
                UserSettings.getAppValue(
                    AppzillonMainScreen.APP_NAME,
                    "volUpButtonEvent", "off",
                    EncryptedPrefHelper.getPrefs()) == "on") {
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId,
                    result, true,
                    aActivity, aWebView, true)
            }
        } else if (volUpDown == "D") {
            result.put("event", "volumeDown")
            result.put("volumeLevel", currVolLevel)
            if (UserSettings.getAppValue(
                    AppzillonMainScreen.APP_NAME,
                    "allEvents", "off",
                    EncryptedPrefHelper.getPrefs()) == "on" ||
                UserSettings.getAppValue(
                    AppzillonMainScreen.APP_NAME,
                    "volDownButtonEvent", "off",
                    EncryptedPrefHelper.getPrefs()) == "on") {
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId,
                    result, true,
                    aActivity, aWebView, true)
            }
        }
    }

    fun getDecryptedValue(serverValue: String): String? {
        return ApzEncryptionPlugin.decryptPassword(
            encryptionKeyForServerValue,
            serverValue)
    }

    fun getApzFile(path: String?, exten: String?): File {
        return File(validatePath(path, exten))
    }

    /**
     * <pre>
     * Paths.get(path) Checks if a string is a valid path.
     * Null safe.
     *
     * Calling examples:
     * validatePath("c:/test");      //returns true
     * validatePath("c:/te:t");      //returns false
     * validatePath("c:/te?t");      //returns false
     * validatePath("c/te*t");       //returns false
     * validatePath("good.txt");     //returns true
     * validatePath("not|good.txt"); //returns false
     * validatePath("not:good.txt"); //returns false
    </pre> *
     */
    fun validatePath(path: String?, ext: String?): String
    {
        var extension = ""
        var fullpth = path
        if (ext == null) {
            if(path.isNullOrEmpty()){
                return ""
            }
            //Matches for Ringtone
            if (!path.startsWith(AppzillonMainScreen.SANDBOX_LOC) && !path.startsWith(
                    AppzillonMainScreen.ASSET_APP_LOC) && !path.endsWith("AppzillonRingtone")
            ) {
                return ""
            }

        }else {
            val allowedExtensions = arrayOf("jpg", "gif", "png", "json", "jpeg", "mp4")
            for (allowedExtension in allowedExtensions) {
                if (allowedExtension == ext) {
                    extension = ext
                    break
                }
            }
        }
        if (extension.isNotEmpty()) {
            fullpth = "$fullpth.$extension"
        }
        try {
            Paths.get(path)
        } catch (ex: java.lang.Exception) {
            fullpth = ""
        }
        return getFinalPath(fullpth)
    }

    private fun getFinalPath(fullpth: String?) =
        if (fullpth.isNullOrEmpty() && fullpth?.contains("../") == true) {
            ""
        } else fullpth!!

    /**
     * To check if Google Play Services are available in device
     */
    fun checkGooglePlayServicesAvailability(activity: Context): Boolean{
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity.applicationContext)
        return resultCode == ConnectionResult.SUCCESS

    }

    fun isGpsHardWarePresent(aContext: Context):Boolean {
        val pm: PackageManager = aContext.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }
    fun isSystemLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
    fun setSetting( aKey: String, aValue: String): Boolean {
        return try {
            UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,
                aKey, aValue, EncryptedPrefHelper.getPrefs())
            true
        } catch (e1: Exception) {
            false
        }
    }

    fun getSetting(aContext: Context, params: JSONObject): String {
        return try {
            val key = params.getString("key")
            UserSettings.getAppValue(AppzillonMainScreen.APP_NAME, key, "", EncryptedPrefHelper.getPrefs(aContext))
        } catch (e: JSONException) {
            "Key is empty"
        }
    }

    fun getAppVersion(params: JSONObject): String {
        return try {
            val appName = params.getString("appId")
            var appVersion = UserSettings.getAppVersion(appName, EncryptedPrefHelper.getPrefs())
            if (appVersion.equals("0.0.0", ignoreCase = true)) {
                appVersion = getString(StringUtils.APP_VERSION)
            }
            appVersion
        } catch (e: Exception) {
            ""
        }

    }

    fun setSettingValue(activity: ApzActivity<*>, jsonResult: JSONObject):Boolean
        {
            var lValue = false
            try {
                val settingskey = jsonResult.keys()
                while (settingskey.hasNext()) {
                val name = settingskey.next() as String
                val keyval = jsonResult.getString(name)
                lValue = setSetting(name, keyval)
            }
            } catch (e: java.lang.Exception) {
            //handle exception
            }
            return lValue
        }

    fun validateApzWebViewURL(url: String): String {
        var u = url
        if (u.contains("url")) {
            u = ""
        }
        if (!u.endsWith(".html")) {
            if (u.contains("api_key")) {
                val fPart =
                    u.split("api_key".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                if (!fPart.endsWith("screens/Map.html?")) {
                    u = ""
                }
            } else {
                u = ""
            }
        }
        return if (u.startsWith("file:///" + AppzillonMainScreen.SANDBOX_LOC) || u.startsWith("file:///android_asset/")) u else ""
    }

    fun validateMapURL(url: String): String? {
        var u = url
        if (u.contains("url")) {
            u = ""
        }
        if (u.contains("http://") || u.contains("https://") || u.contains("redirect")) {
            u = ""
        }
        return if (u.startsWith("javascript:(function() { showPosition(") || u.startsWith("javascript:(function() { initializmap.select")) u else ""
    }

}
