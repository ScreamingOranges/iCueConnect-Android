package com.example.icuepyphone;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.icuepyphone.databinding.ActivityMainBinding;
import com.pusher.rest.Pusher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String inputRGB;
    private Context context;
    private int DefaultColor;
    private List<Integer> myList = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Pusher pusher = new Pusher("1179962", "3b584ee38d8b91d475cd", "21a33f11e65ee31bf618");
        pusher.setCluster("mt1");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.commands_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.commandsSpinner.setAdapter(adapter);
        DefaultColor = 0;

        binding.colorPicker.setInitialColor(Color.GREEN);
        binding.colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                DefaultColor = color;
                binding.previewSelectedColor.setBackgroundColor(color);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            myList.clear();
                            myList.add(Color.red(color));
                            myList.add(Color.green(color));
                            myList.add(Color.blue(color));
                            pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap("RGB_SOLID", myList));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        binding.buttonUpdateiCUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chosenCommand = binding.commandsSpinner.getSelectedItem().toString();
                inputRGB = binding.inputRGBVal.getText().toString();
                String[] result = inputRGB.split("\\s+");
                if((inputRGB.isEmpty()) && (DefaultColor == 0) ){
                    Toast.makeText(context, "ENTER/CHOOSE AN RGB VALUE", Toast.LENGTH_SHORT).show();
                }
                else if(!inputRGB.isEmpty()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myList.clear();
                                myList.add(Integer.parseInt(result[0]));
                                myList.add(Integer.parseInt(result[1]));
                                myList.add(Integer.parseInt(result[2]));
                                switch (chosenCommand){
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
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myList.clear();
                                myList.add(Color.red(DefaultColor));
                                myList.add(Color.green(DefaultColor));
                                myList.add(Color.blue(DefaultColor));
                                switch (chosenCommand){
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
                binding.previewSelectedColor.setBackgroundColor(-5592406);
                binding.inputRGBVal.setText("");
            }
        });
    }
}