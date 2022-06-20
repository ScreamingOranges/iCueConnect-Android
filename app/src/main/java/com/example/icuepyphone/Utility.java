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

    //Pop up notification helper
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
