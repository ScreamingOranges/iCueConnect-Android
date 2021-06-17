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
        mDatabaseHelper = new DatabaseHelper(context);
        Cursor data = mDatabaseHelper.getData();

        if((data != null) && (data.getCount() > 0)){
            while(data.moveToNext()){
                pusherCredentials.add(data.getString(1));
                pusherCredentials.add(data.getString(2));
                pusherCredentials.add(data.getString(3));
                pusherCredentials.add(data.getString(4));
            }
        }

        if(!pusherCredentials.isEmpty()){
            pusher = new Pusher(pusherCredentials.get(0), pusherCredentials.get(1), pusherCredentials.get(2));
            pusher.setCluster(pusherCredentials.get(3));
            pusherClient = new PusherClient(pusherCredentials);
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

    public void requestDevices(Context context, ActivityMainBinding binding){
        if(!pusherCredentials.isEmpty()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("Request_SubDevices", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
            if (null != msg) { msg.cancel(); }
            msg = Toast.makeText(context, "Requesting Devices From API", Toast.LENGTH_SHORT);
            Integer counter = 0;
            while (pusherClient.devices == null) {
                counter +=1;
                if(counter==20){
                    break;
                }
                try { Thread.sleep(100); }
                catch (final InterruptedException e) { e.printStackTrace(); }
            }
            if(counter == 20){
                Utility.showNotice(context, "Error",
                "Unable To Communicate With The iCueConnect API. Make Sure It's Installed And Running On Your PC.");
                return;
            }
            Utility.assignSpinner(pusherClient.devices, context, binding);
        }
        else {
            if(null != msg){ msg.cancel(); }
            msg = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
        }
        msg.show();
    }

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
        else {
            if(null != msg){
                msg.cancel();
            }
            msg = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
            msg.show();
        }
    }

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
