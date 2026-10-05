package com.iexceed.plugins.notification;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.errorlog.ApzLogger;

import java.util.Iterator;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
/**
 * Created by natasha.dawra on 4/5/17.
 */

public class NotificationReceiver extends BroadcastReceiver {
    boolean appstatus;
    int appNotRunning = 10;
    int appInBackground = 1;
    @Override
    public void onReceive(Context context, Intent intent) {

        appstatus = checkAppStatus(appNotRunning, context);
        boolean isAppRunning = checkAppStatus(appInBackground,context);
        String action_code = null;
        String notification_code = null;
        String message = "";
        String params = "";
        int notif_id = -1 ;
        if(intent.hasExtra("action_code"))
            action_code =  intent.getStringExtra("action_code");
        notification_code = intent.getStringExtra("notification_code");
        if(intent.hasExtra("message"))
            message = intent.getStringExtra("message");
        if(intent.hasExtra("notif_id"))
            notif_id = intent.getIntExtra("notif_id",-1);
        if(intent.hasExtra("msgParameters"))
        params= intent.getStringExtra("msgParameters");

            if (appstatus) {
                if(AppzillonMainScreen.APP_LAUNCHED){
                    Intent brIntent = new Intent();
                    brIntent.setAction("com.iexceed.notification");
                    brIntent.putExtra("notification_code",notification_code );
                    brIntent.putExtra("action_code",action_code);
                    brIntent.putExtra("appstatus", appstatus);
                    brIntent.putExtra("msgParameters", params);
                    brIntent.putExtra("message",message);
                    brIntent.putExtra("notif_id", notif_id);
                    brIntent.putExtra("title", intent.getStringExtra("title"));
                    brIntent.putExtra("image_url", intent.getStringExtra("image_url"));
                   //Natasha's Changes 30/9/2017 Security changes
					// AppzillonMainScreen.activity.sendBroadcast(brIntent);
					 LocalBroadcastManager.getInstance(context).sendBroadcast(brIntent);
                }
    }else {
            Intent i = new Intent(context,AppzillonMainScreen.class);
		//Natasha's changes 14-3-2018 for Bug 24249 started
            if(!(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)){
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
		//Natasha's changes 14-3-2018 for Bug 24249 ends
            i.putExtra("action_code", action_code);
            i.putExtra("notification_code", notification_code );
            i.putExtra("message",message);
            i.putExtra("notif_id", notif_id);
            i.putExtra("msgParameters",params);
            i.putExtra("title", intent.getStringExtra("title"));
            i.putExtra("image_url", intent.getStringExtra("image_url"));
            context.startActivity(i);
        }
    }

    private boolean checkAppStatus(int value,Context context) {
        boolean flag = false;
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List< ActivityManager.RunningTaskInfo > taskInfo = activityManager.getRunningTasks(value);
        final Iterator< ActivityManager.RunningTaskInfo > itr = taskInfo.iterator();
        while (itr.hasNext()) {
            final ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo)itr.next();
            final String topActivity = runningTaskInfo.topActivity.getPackageName();
            ApzLogger.d("Top Activity", "" + topActivity);
            if (context.getPackageName().equals(topActivity)) {
                flag = true;
            }
        }

        return flag;

    }
}

