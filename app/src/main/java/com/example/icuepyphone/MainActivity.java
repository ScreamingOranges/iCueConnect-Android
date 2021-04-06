package com.example.icuepyphone;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String inputRGB;
    private Context context;
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

        binding.buttonUpdateiCUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chosenCommand = binding.commandsSpinner.getSelectedItem().toString();
                inputRGB = binding.inputRGBVal.getText().toString();
                String[] result = inputRGB.split("\\s+");

                if(inputRGB.isEmpty()){
                    Toast.makeText(context, "ENTER AN RGB VALUE", Toast.LENGTH_SHORT).show();
                }
                else{
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run(){
                            try{
                                List<Integer> myList = new ArrayList<Integer>();
                                myList.add(Integer.parseInt(result[0]));
                                myList.add(Integer.parseInt(result[1]));
                                myList.add(Integer.parseInt(result[2]));
                                pusher.trigger("RGB_CONN", "PULSE", Collections.singletonMap(chosenCommand, myList));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    Toast.makeText(context, inputRGB, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}