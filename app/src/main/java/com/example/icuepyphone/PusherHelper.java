package com.example.icuepyphone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.Toast;
import androidx.palette.graphics.Palette;

import com.pusher.rest.Pusher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PusherHelper {
    private Toast ToastMessage;
    private Pusher pusher;
    public PusherClient pusherClient;
    public ArrayList<String> pusherCredentials = new ArrayList<>();

    public PusherHelper(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        //Get parse and store record for pusher credentials only if record exists.
        Cursor data = databaseHelper.getDataFromPusherCredentials();
        if((data != null) && (data.getCount() > 0)){
            while(data.moveToNext()){
                pusherCredentials.add(data.getString(DatabaseHelper.PusherCredentialsIndexes.AppID));
                pusherCredentials.add(data.getString(DatabaseHelper.PusherCredentialsIndexes.Key));
                pusherCredentials.add(data.getString(DatabaseHelper.PusherCredentialsIndexes.Secret));
                pusherCredentials.add(data.getString(DatabaseHelper.PusherCredentialsIndexes.Cluster));
            }
        }
        /*Check that Pusher credentials exist before establishing Pusher
        * connections for sending and receiving data*/
        if(!pusherCredentials.isEmpty()){
            //Create Pusher object for sending data
            pusher = new Pusher(pusherCredentials.get(0), pusherCredentials.get(1), pusherCredentials.get(2));
            pusher.setCluster(pusherCredentials.get(3));
            //Create Pusher object for receiving data
            pusherClient = new PusherClient(pusherCredentials, context);
            /*Create thread that listens to Pusher channel for messages from iCue connect api*/
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run(){
                    try {
                        pusherClient.connectionListener();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    //Request devices connect iCUE from iCue connect api
    public void requestDevices(Context context){
        try { Thread.sleep(500); } catch (final InterruptedException e) { e.printStackTrace(); }
        if(!pusherCredentials.isEmpty()){
            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("Request_SubDevices", ""));
        }
        try { Thread.sleep(500); } catch (final InterruptedException e) { e.printStackTrace(); }
    }

    //Send Pusher data to iCue connect api to update leds according to device.
    public void trigger(Context context, int DeviceIndex, int r, int g, int b){
        if(!pusherCredentials.isEmpty()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Integer> rgbValues = new ArrayList<Integer>();
                        rgbValues.clear();
                        rgbValues.add(r);
                        rgbValues.add(g);
                        rgbValues.add(b);

                        Map<String, List<Integer>> data = new HashMap<String, List<Integer>>();
                        data.put("RGB_SOLID", rgbValues);
                        data.put("RGB_DEVICE", Collections.singletonList(DeviceIndex));
                        pusher.trigger("RGB_CONN", "PULSE", data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        //Toast if no credentials in DB
        else {
            if(ToastMessage != null){ ToastMessage.cancel(); }
            ToastMessage = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
            ToastMessage.show();
        }
    }

    //Give control back to iCUE
    @SuppressLint("ShowToast")
    public void resetControl(Context context){
        if(pusher != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_RESET", "[0,0,0]"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            if(ToastMessage != null){ ToastMessage.cancel(); }
            ToastMessage = Toast.makeText(context, "Reverting iCue's Control", Toast.LENGTH_SHORT);
        }
        else{
            if(ToastMessage != null){ ToastMessage.cancel(); }
            ToastMessage = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
        }
        ToastMessage.show();
    }

    //Set leds according to notification's app color
    public void setLedNotification(Context context, String packageName){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = null;
                    try {
                        Drawable drawable = context.getPackageManager().getApplicationIcon(packageName);
                        if (drawable instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) drawable).getBitmap();
                        } else if (drawable instanceof AdaptiveIconDrawable) {
                            Drawable backgroundDr = ((AdaptiveIconDrawable) drawable).getBackground();
                            Drawable foregroundDr = ((AdaptiveIconDrawable) drawable).getForeground();
                            Drawable[] drr = new Drawable[2];
                            drr[0] = backgroundDr;
                            drr[1] = foregroundDr;
                            LayerDrawable layerDrawable = new LayerDrawable(drr);
                            int width = layerDrawable.getIntrinsicWidth();
                            int height = layerDrawable.getIntrinsicHeight();
                            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            layerDrawable.draw(canvas);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Palette palette = Palette.from(bitmap).generate();
                    int appColor = palette.getDominantColor(Color.GREEN);
                    List<Integer> rgbValues = new ArrayList<Integer>();
                    rgbValues.clear();
                    rgbValues.add(Color.red(appColor));
                    rgbValues.add(Color.green(appColor));
                    rgbValues.add(Color.blue(appColor));
                    pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_PULSE", rgbValues));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
