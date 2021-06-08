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
import android.widget.CompoundButton;
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
    private Boolean isLive = false;
    private PusherHelper pusherHelper;


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
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                return true;
            case R.id.reset_Control:
                pusherHelper.resetControl(context);
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

        DefaultColor = 0;
        new NotificationListener().setListener(this) ;
        pusherHelper = new PusherHelper(context);
        binding.colorPicker.setInitialColor(Color.GREEN);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.commands_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.commandsSpinner.setAdapter(adapter);





        binding.switchLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLive = !isLive;
            }
        });

        binding.colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                DefaultColor = color;
                binding.previewSelectedColor.setBackgroundColor(color);
                if (isLive){
                    pusherHelper.trigger(context, Color.red(color), Color.green(color), Color.blue(color));
                }
            }
        });

        binding.buttonUpdateiCUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    pusherHelper.trigger(context, Integer.parseInt(result[0]), Integer.parseInt(result[1]), Integer.parseInt(result[2]));
                }
                else{
                    pusherHelper.trigger(context, Color.red(DefaultColor), Color.green(DefaultColor), Color.blue(DefaultColor));
                    DefaultColor = 0;
                }
                binding.previewSelectedColor.setBackgroundColor(-5592406);
                binding.inputRGBVal.setText("");
            }
        });
    }

    @Override
    public void setValue(String packageName) {
        pusherHelper.setLedNotification(context, packageName);
    }

}