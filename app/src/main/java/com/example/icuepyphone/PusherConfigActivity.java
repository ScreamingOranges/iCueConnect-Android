package com.example.icuepyphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.icuepyphone.databinding.ActivityPusherConfigBinding;

import java.util.ArrayList;

public class PusherConfigActivity extends AppCompatActivity {
    private ActivityPusherConfigBinding binding;
    private Context context;
    private String app_id;
    private String key;
    private String secret;
    private String cluster;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPusherConfigBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = getApplicationContext();
        mDatabaseHelper = new DatabaseHelper(this);

        Cursor data = mDatabaseHelper.getData();
        if((data != null) && (data.getCount() > 0)){
            ArrayList<String> listData = new ArrayList<>();
            while(data.moveToNext()){
                listData.add(data.getString(1));
                listData.add(data.getString(2));
                listData.add(data.getString(3));
                listData.add(data.getString(4));
            }
            listData.forEach(System.out::println);
            binding.inputAppId.setText(listData.get(0));
            binding.inputKey.setText(listData.get(1));
            binding.inputSecret.setText(listData.get(2));
            binding.inputCluster.setText(listData.get(3));
        }


        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_id = binding.inputAppId.getText().toString();
                key = binding.inputKey.getText().toString();
                secret = binding.inputSecret.getText().toString();
                cluster = binding.inputCluster.getText().toString();
                if(app_id.isEmpty()||key.isEmpty()||secret.isEmpty()||cluster.isEmpty()){
                    Toast.makeText(context, "Please Fill In All Fields.", Toast.LENGTH_SHORT).show();
                }
                else {
                    boolean dbInsertResult = mDatabaseHelper.addData(app_id, key, secret, cluster);
                    String dbInsertToast = dbInsertResult ? "Pusher Credentials Updated." : "Pusher Credentials Update Failed.";
                    Toast.makeText(context, dbInsertToast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ButtonLedNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" )) ;
            }
        });
    }
}