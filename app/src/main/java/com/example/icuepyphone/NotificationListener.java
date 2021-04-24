package com.example.icuepyphone;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationListener extends NotificationListenerService {
    static InterfaceNotificationListener INL;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        INL.setValue(sbn.getPackageName());
    }


    public void setListener(InterfaceNotificationListener INL){
        NotificationListener.INL = INL;
    }
}
