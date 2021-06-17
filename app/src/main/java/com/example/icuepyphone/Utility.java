package com.example.icuepyphone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, deviceList);
        binding.commandsSpinner.setAdapter(adapter);
    }

    public static void showNotice(Context context, String title, String message){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
}
