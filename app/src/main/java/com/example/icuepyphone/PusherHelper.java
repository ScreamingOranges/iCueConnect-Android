package com.example.icuepyphone;

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

import com.example.icuepyphone.databinding.ActivityMainBinding;
import com.pusher.rest.Pusher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PusherHelper {
    private Toast msg;
    private List<Integer> rgbValues = new ArrayList<Integer>();
    private Pusher pusher;
    private PusherClient pusherClient;
    private DatabaseHelper mDatabaseHelper;
    public ArrayList<String> pusherCredentials = new ArrayList<>();


    public PusherHelper(Context context){
        //Connect to database
        mDatabaseHelper = new DatabaseHelper(context);
        //Get last record from database table
        Cursor data = mDatabaseHelper.getData();
        //Parse and store record for pusher credentials only if record exists.
        if((data != null) && (data.getCount() > 0)){
            while(data.moveToNext()){
                //Store Pusher app_id
                pusherCredentials.add(data.getString(1));
                //Store Pusher key
                pusherCredentials.add(data.getString(2));
                //Store Pusher secret
                pusherCredentials.add(data.getString(3));
                //Store Pusher cluster
                pusherCredentials.add(data.getString(4));
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

    public Boolean checkDeviceIfNull(){
        return pusherClient.devices == null;
    }

    //Request devices connect iCUE from iCue connect api
    public void requestDevices(Context context){
        try { Thread.sleep(500); } catch (final InterruptedException e) { e.printStackTrace(); }
        if(!pusherCredentials.isEmpty()){
            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("Request_SubDevices", ""));
        }
        try { Thread.sleep(500); } catch (final InterruptedException e) { e.printStackTrace(); }
    }

    //Sets main activity spinner to contain all received devices from iCue connect api
    public void setSpinner(Context context, ActivityMainBinding binding){
        if(pusherClient.devices == null){
            Utility.showNotice(context, "Error",
                    "Unable To Communicate With The iCueConnect API. Make Sure It's Installed And Running On Your PC.");
            return;
        }
        Utility.assignSpinner(pusherClient.devices, context, binding);
    }

    //Send Pusher data to iCue connect api to update leds according to device.
    public void trigger(Context context, int DeviceIndex, int r, int g, int b){
        if(!pusherCredentials.isEmpty()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
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
            if(null != msg){
                msg.cancel();
            }
            msg = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
            msg.show();
        }
    }

    //Give control back to iCUE
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
            if(null != msg){
                msg.cancel();
            }
            msg = Toast.makeText(context, "Reverting iCue's Control", Toast.LENGTH_SHORT);
        }
        else{
            if(null != msg){
                msg.cancel();
            }
            msg = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
        }
        msg.show();
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
