package com.example.icuepyphone;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.example.icuepyphone.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.Map;

public class Utility {

    private Utility(){}
    
    public static void assignSpinner(Map<String,String> passed, Context context, ActivityMainBinding binding){
        ArrayList<String> deviceList = new ArrayList<>();
        deviceList.add("All Devices");

        if(passed != null){
            for (Map.Entry<String,String> entry : passed.entrySet()){
                deviceList.add(entry.getValue());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, deviceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.commandsSpinner.setAdapter(adapter);
    }
}
