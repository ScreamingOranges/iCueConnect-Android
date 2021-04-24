package com.example.icuepyphone;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationListener extends NotificationListenerService {
    private Context context;
    static InterfaceNotificationListener INL;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        INL.setValue(sbn.getPackageName());
    }


    public void setListener(InterfaceNotificationListener INL){
        NotificationListener.INL = INL;
    }
}
