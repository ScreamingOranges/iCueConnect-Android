package com.example.icuepyphone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.example.icuepyphone.databinding.ActivityMainBinding;
import com.pusher.rest.Pusher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import top.defaults.colorpicker.ColorObserver;

public class MainActivity extends AppCompatActivity implements InterfaceNotificationListener{
    private ActivityMainBinding binding;
    private String inputRGB;
    private Context context;
    private int DefaultColor;
    private String chosenCommand;
    private List<Integer> myList = new ArrayList<Integer>();
    private ArrayList<String> listData = new ArrayList<>();
    Pusher pusher;
    PusherClient pusherClient;
    DatabaseHelper mDatabaseHelper;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        context = getApplicationContext();
        switch (item.getItemId()) {
            case R.id.help:
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/ScreamingOranges/iCueConnect-Android/blob/master/README.md"));
                startActivity(intent);
                return true;
            case R.id.settings:
                Intent pusher_config_intent = new Intent(this, Settings.class);
                startActivity(pusher_config_intent);
                return true;
            case R.id.reset_Control:
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
                    Toast.makeText(context, "Reverting iCue's Control", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        new NotificationListener().setListener(this) ;

        mDatabaseHelper = new DatabaseHelper(this);
        Cursor data = mDatabaseHelper.getData();
        if((data != null) && (data.getCount() > 0)){
            while(data.moveToNext()){
                listData.add(data.getString(1));
                listData.add(data.getString(2));
                listData.add(data.getString(3));
                listData.add(data.getString(4));
            }
        }

        if(!listData.isEmpty()){
            pusher = new Pusher(listData.get(0), listData.get(1), listData.get(2));
            pusher.setCluster(listData.get(3));

            pusherClient = new PusherClient(listData);
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
            thread = new Thread(new Runnable() {
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
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.commands_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.commandsSpinner.setAdapter(adapter);

        DefaultColor = 0;

        binding.colorPicker.setInitialColor(Color.GREEN);

        binding.colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                chosenCommand = binding.commandsSpinner.getSelectedItem().toString();
                DefaultColor = color;
                binding.previewSelectedColor.setBackgroundColor(color);
                if(!listData.isEmpty()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myList.clear();
                                myList.add(Color.red(color));
                                myList.add(Color.green(color));
                                myList.add(Color.blue(color));
                                if (chosenCommand.equals("LIVE")) {
                                    pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_SOLID", myList));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }
        });

        binding.buttonUpdateiCUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenCommand = binding.commandsSpinner.getSelectedItem().toString();
                inputRGB = binding.inputRGBVal.getText().toString();
                String[] result = inputRGB.split("\\s+");
                if((inputRGB.isEmpty()) && (DefaultColor == 0) ){
                    Toast.makeText(context, "ENTER/CHOOSE AN RGB VALUE", Toast.LENGTH_SHORT).show();
                }
                else if(!inputRGB.isEmpty()) {
                    if(!inputRGB.matches("\\d{1,3}\\s+\\d{1,3}\\s+\\d{1,3}")){
                        binding.inputRGBVal.setText("");
                        Toast.makeText(context, "Improper Input Format!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!listData.isEmpty()) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    myList.clear();
                                    myList.add(Integer.parseInt(result[0]));
                                    myList.add(Integer.parseInt(result[1]));
                                    myList.add(Integer.parseInt(result[2]));
                                    switch (chosenCommand) {
                                        case "PULSE":
                                            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_PULSE", myList));
                                            break;
                                        case "SOLID":
                                            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_SOLID", myList));
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        Toast.makeText(context, "Sent To iCue", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(!listData.isEmpty()) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    myList.clear();
                                    myList.add(Color.red(DefaultColor));
                                    myList.add(Color.green(DefaultColor));
                                    myList.add(Color.blue(DefaultColor));
                                    switch (chosenCommand) {
                                        case "PULSE":
                                            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_PULSE", myList));
                                            break;
                                        case "SOLID":
                                            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_SOLID", myList));
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        Toast.makeText(context, "Sent To iCue", Toast.LENGTH_SHORT).show();
                        DefaultColor = 0;
                    }
                    else{
                        Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT).show();
                    }
                }
                binding.previewSelectedColor.setBackgroundColor(-5592406);
                binding.inputRGBVal.setText("");
            }
        });
    }

    @Override
    public void setValue(String packageName) {
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
                    myList.clear();
                    myList.add(Color.red(appColor));
                    myList.add(Color.green(appColor));
                    myList.add(Color.blue(appColor));
                    pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_PULSE", myList));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}