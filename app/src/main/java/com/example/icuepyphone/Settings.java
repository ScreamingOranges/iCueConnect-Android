package com.example.icuepyphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.example.icuepyphone.databinding.ActivitySettingsBinding;
import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private Context context;
    private String app_id;
    private String key;
    private String secret;
    private String cluster;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic page setup essentials
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = getApplicationContext();

        //Create DB object for getting pusher credentials
        mDatabaseHelper = new DatabaseHelper(this);
        //Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cluster_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.clusterSpinner.setAdapter(adapter);

        //Get Pusher credentials and populate fields
        Cursor data = mDatabaseHelper.getData();
        if((data != null) && (data.getCount() > 0)){
            ArrayList<String> pusherCredentials = new ArrayList<>();
            while(data.moveToNext()){
                pusherCredentials.add(data.getString(1));
                pusherCredentials.add(data.getString(2));
                pusherCredentials.add(data.getString(3));
                pusherCredentials.add(data.getString(4));
            }
            binding.inputAppId.setText(pusherCredentials.get(0));
            binding.inputKey.setText(pusherCredentials.get(1));
            binding.inputSecret.setText(pusherCredentials.get(2));
            binding.clusterSpinner.setSelection(adapter.getPosition(pusherCredentials.get(3)));
        }

        //On click save credentials to DB
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_id = binding.inputAppId.getText().toString();
                key = binding.inputKey.getText().toString();
                secret = binding.inputSecret.getText().toString();
                cluster = binding.clusterSpinner.getSelectedItem().toString();
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

        //Enable notification listener
        binding.ButtonLedNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" )) ;
            }
        });
    }
}