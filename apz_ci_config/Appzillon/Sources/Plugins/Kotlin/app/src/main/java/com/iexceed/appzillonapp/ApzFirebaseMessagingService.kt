package com.iexceed.appzillonapp

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.iexceed.common.AppzillonConstants.IS_SERVER
import com.iexceed.common.ServerUtilities
import com.iexceed.common.UserSettings
import com.iexceed.db.PushNotificationData
import com.iexceed.db.RoomAppDb
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.notification.NotificationBody
import com.iexceed.plugins.notification.NotificationReceiver
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.Date


class ApzFirebaseMessagingService : FirebaseMessagingService()
{
    private var msgParams = ""
    private var title = ""
    private var imageUrl = ""
    private var TAG = "ApzFirebaseMessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data: Map<String?, String?> = remoteMessage.data
        ApzLogger.d(TAG, "onMessageReceived data : $data")
        if(data.isNullOrEmpty()){
            generateNotification(remoteMessage.notification)
        }else{
            generateNotification(data)
        }
    }

    private fun generateNotification(data: RemoteMessage.Notification?)
    {
        var msg = ""
        if(!data?.body.isNullOrEmpty()){
            msg = data?.body.toString()
        }

        if(!data?.title.isNullOrEmpty()){
            title = data?.title.toString()
        }

        if(data?.imageUrl?.toString()?.isNotEmpty() == true){
            imageUrl = data.imageUrl.toString()
        }

        val icon: Int = R.drawable.notification
        val lNotificationIntent: Intent?
        val lNotification: Notification?

        val nb: Notification.Builder =
            Notification.Builder(this,CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentText(msg)
        if (title.isNotEmpty()) {
            nb.setContentTitle(title)
        }

        if(imageUrl.isNotEmpty())
        {
            var imageBitmap: Bitmap? = null
            try {
                val url = URL(imageUrl)
                imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                //handle exception
            }
            nb.style = Notification.BigPictureStyle().bigPicture(imageBitmap)
        }

        lNotificationIntent = Intent(applicationContext, AppzillonMainScreen::class.java)
        lNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        lNotificationIntent.putExtra("message", msg)
        lNotificationIntent.putExtra("msgParameters", msgParams)
        lNotificationIntent.putExtra("title", title)
        lNotificationIntent.putExtra("imageUrl", imageUrl)
        val intent: PendingIntent = PendingIntent.getActivity(this, 0, lNotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        nb.setContentIntent(intent)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        nb.setChannelId(CHANNEL_ID)
        mNotificationManager?.createNotificationChannel(channel)
        lNotification = nb.build()
        lNotification.flags = lNotification.flags or Notification.FLAG_AUTO_CANCEL
        mNotificationManager?.notify(mNotificationId, lNotification)
        mNotificationId += 1
    }

    override fun onNewToken(refreshedToken: String)
    {
        super.onNewToken(refreshedToken)
        ApzLogger.d(TAG, "Refreshed token: $refreshedToken")
        val apps: SharedPreferences = EncryptedPrefHelper.init(applicationContext)
        UserSettings.setNotificationToken(applicationContext.resources.getString(R.string.MAINAPPID), refreshedToken, apps)
        try {
            if (IS_SERVER && AppzillonMainScreen.IS_APP_INITIALIZED) {
                ServerUtilities.register(applicationContext, refreshedToken)
            }
        } catch (e: Exception) {
            ApzLogger.d(TAG, "Exception run-time exception onTokenRefresh")
        }
    }

    private fun generateNotification(data: Map<String?, String?>?)
    {
        ApzLogger.d(TAG, "Inside generateNotification : $data")
        if(data.isNullOrEmpty()){return}
        val lNotification: Notification?
        val lNotificationIntent: Intent?
        val msg: String
        try {
            val lJsonObj = JSONObject(data)
            title = lJsonObj.getString("title")
            msg = lJsonObj.getString("message")
            if (lJsonObj.has("message_param")) {
                msgParams = lJsonObj.getString("message_param")
            }
            val icon: Int = R.drawable.notification
            val nb: NotificationCompat.Builder = getNotificationBuilder(icon, msg)

            if ("" != title) {
                nb.setContentTitle(title)
            }

            //For image Notification
            if (lJsonObj.has("imageUrl")) {
                var imageBitmap: Bitmap? = null
                try {
                    imageUrl = lJsonObj.getString("imageUrl")
                    val url = URL(imageUrl)
                    imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } catch (e: IOException) {
                    //handle exception
                }
                nb.setStyle(NotificationCompat.BigPictureStyle().bigPicture(imageBitmap))
            }

            //For Notification action/button
            if (lJsonObj.has("notification_code"))
            {
                handleActionableNotification(
                    lJsonObj,
                    nb,
                    msg
                )
            } else {
                //If the notification contains just message
                ApzLogger.d(TAG, "Else no NOTIFICATION CODE")

                appstatus = checkAppStatus()
                lNotificationIntent = Intent(applicationContext, AppzillonMainScreen::class.java)
                lNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                lNotificationIntent.putExtra("message", msg)
                lNotificationIntent.putExtra("msgParameters", msgParams)
                lNotificationIntent.putExtra("imageUrl", imageUrl)
                lNotificationIntent.putExtra("title", title)
                val intent: PendingIntent = PendingIntent.getActivity(this, 0, lNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                nb.setContentIntent(intent)
            }
            if (appstatus && !lJsonObj.has("notification_code")) {
                IS_BEFORE_LAUNCH = !AppzillonMainScreen.APP_LAUNCHED
            }

            if (AppzillonMainScreen.APP_LAUNCHED) {
                saveInLocalDB(applicationContext, msg)
            }
            mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nb.setChannelId(CHANNEL_ID)
            mNotificationManager?.createNotificationChannel(channel)
            lNotification = nb.build()
            lNotification.flags = lNotification.flags or Notification.FLAG_AUTO_CANCEL
            mNotificationManager?.notify(mNotificationId, lNotification)
            mNotificationId += 1
        } catch (e: JSONException) {
            //handle exception
        }
    }

    private fun getNotificationBuilder(
        icon: Int,
        msg: String
    ) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg)

    private fun handleActionableNotification(
        lJsonObj: JSONObject,
        nb: NotificationCompat.Builder,
        msg: String
    ) {
        var lJsonArray1: JSONArray?
        val lNotificationIntent1: Intent?
        val lNotificationCode1: String = lJsonObj.getString("notification_code")
        lJsonArray1 = JSONArray()
        //Reading the notif_file if the app is not active
        if (AppzillonMainScreen.mapjson == null) {
            handleWhenAppNotActive()
        }
        //Create the notification buttons
        if (AppzillonMainScreen.mapjson != null) {
            val len = AppzillonMainScreen.mapjson!!.length()
            for (i in 0 until len) {
                if (AppzillonMainScreen.mapjson!!.getJSONObject(i)
                        .getString("notification_code") == lNotificationCode1
                ) {
                    lJsonArray1 = AppzillonMainScreen.mapjson!!.getJSONObject(i)
                        .getJSONArray("actions")
                }
            }
        }
        if (lJsonArray1 != null && lJsonArray1.length() > 0) {
            ApzLogger.d(TAG, "Else  lJsonArray.length() > 0")
            for (x in 0 until lJsonArray1.length()) {
                val resJson: JSONObject = lJsonArray1.getJSONObject(x)
                val lActionCode: String = resJson.getString("action_code")
                val lActionDisplay: String = resJson.getString("action_display")
                with(nb) {
                    addAction(
                                0,
                                lActionDisplay,
                                pIntent(lActionCode, lNotificationCode1, msg)
                            )
                }
            }
        } else {
            appstatus = checkAppStatus()
            ApzLogger.d(TAG, "Else  lJsonArray.length() < 0")

            lNotificationIntent1 = Intent(applicationContext, AppzillonMainScreen::class.java)
            lNotificationIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            lNotificationIntent1.putExtra("message", msg)
            lNotificationIntent1.putExtra("msgParameters", msgParams)
            lNotificationIntent1.putExtra("title", title)
            lNotificationIntent1.putExtra("imageUrl", imageUrl)
            val intent: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                lNotificationIntent1,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            nb.setContentIntent(intent)
        }
    }

    private fun handleWhenAppNotActive() {
        try {
            var isNotif: InputStream? = null
            val sbNotif = StringBuilder()
            val notifbr: BufferedReader
            var notifline: String?
            var notificationJson: JSONArray? = null
            if (applicationContext.resources.getString(R.string.OTAREQUIRED)
                    .equals("Y", true)
            ) {
                val notifFile =
                    File(AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "screens/config/notif_details.json")
                if (notifFile.exists()) isNotif = FileInputStream(notifFile)
            } else {
                isNotif = applicationContext.assets.open(
                    "apps/" + applicationContext.resources.getString(R.string.MAINAPPID) + "/screens/config/notif_details.json"
                )
            }
            if (isNotif != null) {
                notifbr = BufferedReader(InputStreamReader(isNotif))
                while (notifbr.readLine().also { notifline = it } != null) {
                    sbNotif.append(notifline)
                }
                notifbr.close()
            }
            if (isNotif != null) {
                try {
                    notificationJson = JSONArray(sbNotif.toString())
                    AppzillonMainScreen.mapjson = JSONArray()
                    for (j in 0 until notificationJson.length()) {
                        val jsonObject = notificationJson.getJSONObject(j)
                        val jsonArr = jsonObject.getJSONArray("actions")
                        val notification = jsonObject.getString("notification_code")
                        val jsonObj = JSONObject()
                        jsonObj.put("notification_code", notification)
                        jsonObj.put("actions", jsonArr)
                        AppzillonMainScreen.mapjson?.put(j, jsonObj)
                    }
                } catch (ex: JSONException) {
                    //handle exception
                }
            }
        } catch (io: IOException) {
            //handle exception
        }
    }

    private fun pIntent(
        actionCode: String?,
        notificationCode: String?,
        message: String?
    ): PendingIntent {

        val intent = Intent(this, NotificationReceiver::class.java)
        intent.action = "com.broadcast.notification"

        NotificationBody(
            actionCode,
            notificationCode,
            message,mNotificationId,msgParams,title,imageUrl)

        intent.putExtra("action_code", actionCode)
        intent.putExtra("notification_code", notificationCode)
        intent.putExtra("message", message)
        intent.putExtra("notif_id", mNotificationId)
        intent.putExtra("msgParameters", msgParams)
        intent.putExtra("title", title)
        intent.putExtra("imageUrl", imageUrl)
        var i = 0
        val t = System.currentTimeMillis().toInt() + ++i
        return PendingIntent.getBroadcast(this, t, intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun checkAppStatus(): Boolean {
        return isAppRunning(this)
    }

    private fun isAppRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.appTasks
            .filter { it.taskInfo != null }
            .filter { it.taskInfo.baseActivity != null }
            .any { it.taskInfo.baseActivity!!.packageName == context.packageName }
    }

    private fun saveInLocalDB(aContext: Context, message: String?)
    {
        val `when` = System.currentTimeMillis()
        val date = Date(`when`)
        val lNotes = PushNotificationData(date.toString(), message.toString(), "N")
        val dh = RoomAppDb.getAppDatabase(aContext)
        dh.addPushMsg(lNotes)
    }

    companion object {
        private var appstatus = false
        var IS_BEFORE_LAUNCH = false
        var mNotificationId = 1
        var mNotificationManager: NotificationManager? = null
        var CHANNEL_ID = "4"
        var CHANNEL_NAME = "Notification"
    }
}
