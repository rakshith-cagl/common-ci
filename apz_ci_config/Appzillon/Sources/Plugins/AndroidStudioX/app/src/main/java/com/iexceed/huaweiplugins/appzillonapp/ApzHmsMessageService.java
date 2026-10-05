package com.iexceed.appzillonapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.DatabaseHandler;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.UserSettings;
import com.iexceed.plugins.createnote.Notes;
import com.iexceed.plugins.notification.NotificationReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.iexceed.appzillonapp.AppzillonMainScreen.IS_APP_INITIALIZED;
import static com.iexceed.appzillonapp.AppzillonMainScreen.IS_SERVER;


public class ApzHmsMessageService extends HmsMessageService {

    String TAG = "ApzHmsMessageService";

    private static boolean appstatus;
    public static boolean IS_BEFORE_LAUNCH = false;
    static int m = 1;
    public static NotificationManager notificationManager = null;
    String msg_params = "";
    String title = "";
    String image_url = "";
    public static String CHANNEL_ID="4";
    public static String CHANNEL_NAME="Notification";

    final static String app_props = "APP_PREFS";

    @Override
    public void onMessageReceived(RemoteMessage message) {
//        if (message == null) {
//            Log.e(TAG, "Received message entity is null!");
//            return;
//        }
//        Log.i(TAG, "getCollapseKey: " + message.getCollapseKey()
//                + "\n getData: " + message.getData()
//                + "\n getFrom: " + message.getFrom()
//                + "\n getTo: " + message.getTo()
//                + "\n getMessageId: " + message.getMessageId()
//                + "\n getSendTime: " + message.getSentTime()
//                + "\n getDataMap: " + message.getDataOfMap()
//                + "\n getMessageType: " + message.getMessageType()
//                + "\n getTtl: " + message.getTtl()
//                + "\n getToken: " + message.getToken());
//
//        RemoteMessage.Notification notification = message.getNotification();
//        if (notification != null) {
//            Log.i(TAG, "\n getImageUrl: " + notification.getImageUrl()
//                    + "\n getTitle: " + notification.getTitle()
//                    + "\n getTitleLocalizationKey: " + notification.getTitleLocalizationKey()
//                    + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.getTitleLocalizationArgs())
//                    + "\n getBody: " + notification.getBody()
//                    + "\n getBodyLocalizationKey: " + notification.getBodyLocalizationKey()
//                    + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.getBodyLocalizationArgs())
//                    + "\n getIcon: " + notification.getIcon()
//                    + "\n getSound: " + notification.getSound()
//                    + "\n getTag: " + notification.getTag()
//                    + "\n getColor: " + notification.getColor()
//                    + "\n getClickAction: " + notification.getClickAction()
//                    + "\n getChannelId: " + notification.getChannelId()
//                    + "\n getLink: " + notification.getLink()
//                    + "\n getNotifyId: " + notification.getNotifyId());
//        }
        Map<String,String> data = message.getDataOfMap();
        generateNofication(data);
    }

    @Override
    public void onMessageDelivered(String s, Exception e) {
        super.onMessageDelivered(s, e);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onNewToken(String refreshedToken) {
        // send the token to your app server.
        if (!TextUtils.isEmpty(refreshedToken)) {
            try {
                SharedPreferences apps = getSharedPreferences(app_props, 0);
                //When app is in foreground then only perform these operations.
                if(checkAppStatus(10)){
                    UserSettings.setNotificationToken(getResources().getString(R.string.MAINAPPID),refreshedToken,apps);
                    if(IS_SERVER && IS_APP_INITIALIZED){
                        ServerUtilities.register(getApplicationContext(),  refreshedToken);
                    }
                }

            } catch (Exception e) { }
        }
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }

    public void generateNofication(Map<String,String> data){
        String notification_code;
        JSONArray arr = null;
        Notification n = null;
        int appNotRunning = 10;
        int appInBackground = 1;
        Intent notificationIntent = null;
        String msg = "";
        try {

            int icon = R.drawable.appicon;
            JSONObject json = new JSONObject(data);
            title = json.optString("title");
            msg =  json.optString("message");
            if(json.has("message_param")) {
                msg_params = json.getString("message_param");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon = R.drawable.notification;
            }

            Notification.Builder nb=  new Notification.Builder(this)
                    .setSmallIcon(icon)
                    .setStyle(new Notification.BigTextStyle().bigText(msg))
                    .setContentText(msg);

            if(!"".equals(title)){
                nb.setContentTitle(title);
            }

            //For image Notification
            if (json.has("image_url")) {
                Bitmap imageBitmap = null;
                try {

                    image_url = json.getString("image_url");
                    URL url = new URL(image_url);
                    imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) { }
                nb.setStyle(new Notification.BigPictureStyle().bigPicture(imageBitmap));
            }

            //For Notification action/button
            if (json.has("notification_code")) {
                notification_code = json.getString("notification_code");
                arr = new JSONArray();
                //Reading the notif_file if the app is not active
                if(AppzillonMainScreen.mapjson == null){
                    try{
                        InputStream isNotif = null;
                        StringBuilder sbNotif = new StringBuilder();
                        BufferedReader notifbr;
                        String notifline;
                        JSONArray notificationJson = null;
                        if ( getResources().getString(R.string.OTAREQUIRED).equalsIgnoreCase("Y")) {

                            File notifFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Android/data/" + getResources().getString(R.string.MAINAPPID) + "/screens/config/notif_details.json");
                            if(notifFile.exists())
                                isNotif = new FileInputStream(notifFile);
                        } else {
                            isNotif = getAssets().open("apps/"+ getResources().getString(R.string.MAINAPPID) + "/screens/config/notif_details.json");
                        }

                        if (isNotif != null) {
                            notifbr = new BufferedReader(new InputStreamReader(isNotif));
                            while ((notifline = notifbr.readLine()) != null) {
                                sbNotif.append(notifline);
                            }
                            notifbr.close();
                        }

                        if (isNotif != null) {
                            try {
                                notificationJson = new JSONArray(sbNotif.toString());

                                AppzillonMainScreen.mapjson = new JSONArray();
                                for (int j = 0; j < notificationJson.length(); j++) {
                                    JSONObject jsonObject = notificationJson.getJSONObject(j);
                                    JSONArray jsonArr = jsonObject.getJSONArray("actions");
                                    String notification = jsonObject.getString("notification_code");
                                    JSONObject jsonObj = new JSONObject();
                                    jsonObj.put("notification_code", notification);
                                    jsonObj.put("actions", jsonArr);
                                    AppzillonMainScreen.mapjson.put(j,jsonObj);

                                }


                            } catch (JSONException ex) { }
                        }
                    } catch (IOException io) { }
                }
                //Create the notification buttons
                if( AppzillonMainScreen.mapjson != null) {
                    int len = AppzillonMainScreen.mapjson.length();

                    for (int i = 0; i < len; i++) {
                        String var = AppzillonMainScreen.mapjson.getJSONObject(i).getString("notification_code");
                        if (AppzillonMainScreen.mapjson.getJSONObject(i).getString("notification_code").equals(notification_code)) {
                            arr = AppzillonMainScreen.mapjson.getJSONObject(i).getJSONArray("actions");
                        }
                    }
                }
                if (arr.length() > 0) {
                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject resJson = arr.getJSONObject(x);
                        String action_code = resJson.getString("action_code");
                        String action_display = resJson.getString("action_display");
                        nb.addAction(0, action_display, pIntent(action_code, notification_code, action_display,msg));
                    }
                }else{
                    appstatus = checkAppStatus(appNotRunning);
                    boolean isAppRunning = checkAppStatus(appInBackground);

                    if (appstatus && isAppRunning) {
                        if(!AppzillonMainScreen.APP_LAUNCHED){
                        }
                        notificationIntent = new Intent();
                    } else {
                        notificationIntent = new Intent(getApplicationContext(), AppzillonMainScreen.class);
                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    }
                    notificationIntent.putExtra("message", msg);
                    notificationIntent.putExtra("msgParameters", msg_params);
                    notificationIntent.putExtra("title", title);
                    notificationIntent.putExtra("image_url", image_url);
                    final PendingIntent intent = PendingIntent.getActivity(this, 0,notificationIntent, 0|PendingIntent.FLAG_IMMUTABLE);
                    nb.setContentIntent(intent);
                }
            }else{
                //If the notification contains just message
                appstatus = checkAppStatus(appNotRunning);
                boolean isAppRunning = checkAppStatus(appInBackground);

                if (appstatus && isAppRunning) {
                    notificationIntent = new Intent();
                } else {
                    notificationIntent = new Intent(getApplicationContext(), AppzillonMainScreen.class);
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                notificationIntent.putExtra("message", msg);
                notificationIntent.putExtra("msgParameters", msg_params);
                notificationIntent.putExtra("image_url", image_url);
                final PendingIntent intent = PendingIntent.getActivity(this, 0,notificationIntent, 0|PendingIntent.FLAG_IMMUTABLE);
                nb.setContentIntent(intent);
            }

            if (appstatus && !(json.has("notification_code"))) {

                if(AppzillonMainScreen.APP_LAUNCHED){
                    IS_BEFORE_LAUNCH = false;

                    Intent brIntent = new Intent();
                    brIntent.setAction("com.iexceed.notification");
                    brIntent.putExtra("message", msg);
                    brIntent.putExtra("appstatus", appstatus);
                    brIntent.putExtra("msgParameters", msg_params);
                    brIntent.putExtra("title", title);
                    brIntent.putExtra("image_url", image_url);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(brIntent);
                }else {
                    IS_BEFORE_LAUNCH = true;
                }
            }

            if(AppzillonMainScreen.APP_LAUNCHED){
                saveInLocalDB(getApplicationContext(),msg);
            }


            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                nb.setChannelId(CHANNEL_ID);
            }
            n = nb.build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(m, n);
            m += 1;
        } catch (JSONException e) { }



    }

    public PendingIntent pIntent(String action_code, String notification_code, String action_display,String message){
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction("com.broadcast.notification");
        intent.putExtra("action_code", action_code);
        intent.putExtra("notification_code", notification_code);
        intent.putExtra("message",message);
        intent.putExtra("notif_id",m);
        intent.putExtra("msgParameters", msg_params);
        intent.putExtra("title", title);
        intent.putExtra("image_url", image_url);
        int i = 0;
        int t = (int) System.currentTimeMillis()+ ++i;
        PendingIntent pIntent = PendingIntent.getBroadcast(this, t, intent, PendingIntent.FLAG_CANCEL_CURRENT |PendingIntent.FLAG_IMMUTABLE);
        return pIntent;
    }

    private boolean checkAppStatus(int value) {
        boolean flag = false;
        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final List< ActivityManager.RunningTaskInfo > taskInfo = activityManager.getRunningTasks(value);
        final Iterator< ActivityManager.RunningTaskInfo > itr = taskInfo.iterator();
        while (itr.hasNext()) {
            final ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo)itr.next();
            final String topActivity = runningTaskInfo.topActivity.getPackageName();
            if (getPackageName().equals(topActivity)) {
                flag = true;
            }
        }

        return flag;

    }

    private static void saveInLocalDB(Context contxt, String message) {

        final long when = System.currentTimeMillis();
        final Date date = new Date(when);
        final Notes p_notes = new Notes(String.valueOf(date), message,"N");
        final DatabaseHandler dh = new DatabaseHandler(contxt);
        dh.addPushMsg(p_notes);
    }
}
