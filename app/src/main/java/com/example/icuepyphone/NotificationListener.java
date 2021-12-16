package com.example.icuepyphone;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationListener extends NotificationListenerService {
    static InterfaceNotificationListener INL;
    private String previousApp;

    @Override
    public void onCreate() {
        super.onCreate();
        previousApp = "";
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        previousApp = "";
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //Prevent being called twice for apps that use group summary notification alongside other notifications.
        if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            return;
        }
        //If current notification is same as last notification, then ignore
        else if(previousApp.equals(sbn.getPackageName())){
            return;
        }
        //If new notification set as last notification and set value
        else{
            previousApp = sbn.getPackageName();
            INL.setValue(sbn.getPackageName());
        }
    }

    public void setListener(InterfaceNotificationListener INL){
        NotificationListener.INL = INL;
    }
}
