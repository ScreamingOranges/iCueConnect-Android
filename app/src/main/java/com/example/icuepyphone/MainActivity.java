package com.example.icuepyphone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.icuepyphone.databinding.ActivityMainBinding;
import top.defaults.colorpicker.ColorObserver;

public class MainActivity extends AppCompatActivity implements InterfaceNotificationListener{
    private ActivityMainBinding binding;
    //Global toast to track if a toast is set at a given moment
    private Toast msg;
    private Context context;
    private int DefaultColor = 0;
    private int DeviceIndex;
    private boolean isLive;
    private PusherHelper pusherHelper;
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
            case R.id.sync_devices:
                requestDeviceHelper(this);
                return true;
            case R.id.reset_Control:
                pusherHelper.resetControl(context);
                return true;
            case R.id.help:
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/ScreamingOranges/iCueConnect-Android/blob/master/README.md"));
                startActivity(intent);
                return true;
            case R.id.settings:
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic page setup essentials
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //check previous switch value from database and set
        mDatabaseHelper = new DatabaseHelper(this);
        Cursor data = mDatabaseHelper.getDataFromSwitchToggle();
        if((data != null) && (data.getCount() > 0)){
            while(data.moveToNext()){
                isLive = (data.getInt(1) == 1) ? true : false;
                binding.switchLive.setChecked(isLive);
            }
        }

        //Create notification listener for setting leds to apps color
        new NotificationListener().setListener(this) ;
        //Primary object for interacting with pusher
        pusherHelper = new PusherHelper(MainActivity.this);

        //Assigns map to spinner
        Utility.assignSpinner(null, this, binding);
        //request devices connected to iCUE from API
        requestDeviceHelper(this);

        binding.colorPicker.setInitialColor(Color.GREEN);

        binding.commandsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DeviceIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.switchLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLive = !isLive;
                boolean dbInsertResult = mDatabaseHelper.addDataToSwitchToggle(isLive);
            }
        });

        binding.colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                DefaultColor = color;
                binding.previewSelectedColor.setBackgroundColor(color);
                if (isLive){
                    pusherHelper.trigger(context, DeviceIndex, Color.red(color), Color.green(color), Color.blue(color));
                }
            }
        });

        binding.buttonUpdateiCUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRGB = binding.inputRGBVal.getText().toString();
                String[] result = inputRGB.split("\\s+");
                if((inputRGB.isEmpty()) && (DefaultColor == 0) ){
                    if (msg != null) { msg.cancel(); }
                    msg = Toast.makeText(context, "ENTER/CHOOSE AN RGB VALUE", Toast.LENGTH_SHORT);
                    msg.show();
                }
                else if(!inputRGB.isEmpty()) {
                    if(!inputRGB.matches("\\d{1,3}\\s+\\d{1,3}\\s+\\d{1,3}")){
                        binding.inputRGBVal.setText("");
                        if (msg != null) { msg.cancel(); }
                        msg = Toast.makeText(context, "Improper Input Format!", Toast.LENGTH_SHORT);
                        msg.show();
                        return;
                    }
                    pusherHelper.trigger(context, DeviceIndex, Integer.parseInt(result[0]), Integer.parseInt(result[1]), Integer.parseInt(result[2]));
                }
                else{
                    pusherHelper.trigger(context, DeviceIndex, Color.red(DefaultColor), Color.green(DefaultColor), Color.blue(DefaultColor));
                }
                DefaultColor = 0;
                binding.previewSelectedColor.setBackgroundColor(-5592406);
                binding.inputRGBVal.setText("");
            }
        });
    }

    @Override
    public void setValue(String packageName) {
        pusherHelper.setLedNotification(context, packageName);
    }

    //Helps preform request for devices connected to iCUE from API
    public void requestDeviceHelper(Context context){
        if(!pusherHelper.pusherCredentials.isEmpty()){
            if (msg != null) { msg.cancel(); }
            msg = Toast.makeText(context, "Requesting Devices From API", Toast.LENGTH_SHORT);
            new requestDeviceHandler().execute(context);
        }
        else{
            if(msg != null){ msg.cancel(); }
            msg = Toast.makeText(context, "CONFIGURE PUSHER IN SETTINGS", Toast.LENGTH_SHORT);
        }
        msg.show();
    }

    //AsyncTask that actually preforms request for devices in iCUE
    private class requestDeviceHandler extends AsyncTask<Context, Void, Context>{
        @Override
        protected Context doInBackground(Context... context) {
            pusherHelper.requestDevices(context[0]);
            return context[0];
        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);
            if(!pusherHelper.checkDeviceIfNull()){
                pusherHelper.setSpinner(context, binding);
            }
            else{
                Utility.showNotice(context, "Error",
                        "Unable To Communicate With The iCueConnect API. Make Sure It's Installed And Running On Your PC.");
            }
        }
    }

}