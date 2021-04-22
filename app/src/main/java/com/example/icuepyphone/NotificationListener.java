package com.example.icuepyphone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

public class NotificationListener extends NotificationListenerService {
    private Context context;

    /*
    This function handles notification data
     */
    private String matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        return packageName;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String appName = matchNotificationCode(sbn);
        Toast.makeText(context, "onNotificationPosted" + appName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.example.icuepyphone");
        intent.putExtra("data", appName);
        sendBroadcast(intent);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String appName = matchNotificationCode(sbn);
        Toast.makeText(context, "onNotificationRemoved" + appName, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction("com.example.icuepyphone");
        intent.putExtra("data", appName);
        sendBroadcast(intent);
    }
}
