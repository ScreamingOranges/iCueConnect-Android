package com.example.icuepyphone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final class PusherCredentialsIndexes{
        public static final int AppID = 1;
        public static final int Key = 2;
        public static final int Secret = 3;
        public static final int Cluster = 4;
    }

    public static final class SwitchToggleIndexes{
        public static final int Switch = 1;
    }

    private static final String iCueConnectDB = "iCueConnectDB";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;
    
    //Table PusherCredentials
    private static final String T1_NAME_PusherCredentials = "PusherCredentials";
    private static final String T1_COLUMN_NAME_ID = "ID";
    private static final String T1_COLUMN_NAME_app_id = "_app_id";
    private static final String T1_COLUMN_NAME_key = "_key";
    private static final String T1_COLUMN_NAME_secret = "_secret";
    private static final String T1_COLUMN_NAME_cluster = "_cluster";
    private static final String SQL_CREATE_PusherCredentialsTable =
            "CREATE TABLE " + T1_NAME_PusherCredentials + " (" +
                    T1_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    T1_COLUMN_NAME_app_id + " TEXT," +
                    T1_COLUMN_NAME_key + " TEXT," +
                    T1_COLUMN_NAME_secret + " TEXT," +
                    T1_COLUMN_NAME_cluster + " TEXT)";
    private static final String SQL_DELETE_PusherCredentialsTable =
            "DROP TABLE IF EXISTS " + T1_NAME_PusherCredentials;

    //Table SwitchToggle
    private static final String T2_NAME_SwitchToggle = "SwitchToggle";
    private static final String T2_COLUMN_NAME_ID = "ID";
    private static final String T2_COLUMN_NAME_switch = "_switch";
    private static final String SQL_CREATE_SwitchToggleTable =
            "CREATE TABLE " + T2_NAME_SwitchToggle + " (" +
                    T2_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    T2_COLUMN_NAME_switch + " INTEGER)";
    private static final String SQL_DELETE_SwitchToggleTable =
            "DROP TABLE IF EXISTS " + T2_NAME_SwitchToggle;



    public DatabaseHelper(Context context) {
        super(context, iCueConnectDB, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PusherCredentialsTable);
        db.execSQL(SQL_CREATE_SwitchToggleTable);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PusherCredentialsTable);
        db.execSQL(SQL_DELETE_SwitchToggleTable);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public boolean addDataToPusherCredentials(String app_id, String key, String secret, String cluster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T1_COLUMN_NAME_app_id, app_id);
        contentValues.put(T1_COLUMN_NAME_key, key);
        contentValues.put(T1_COLUMN_NAME_secret, secret);
        contentValues.put(T1_COLUMN_NAME_cluster, cluster);

        //if date as inserted incorrectly it will return -1
        if (db.insert(T1_NAME_PusherCredentials, null, contentValues) == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean addDataToSwitchToggle(boolean isEnabled){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int switchValue = (isEnabled == true)? 1 : 0;
        contentValues.put(T2_COLUMN_NAME_switch, switchValue);

        //if date as inserted incorrectly it will return -1
        if (db.insert(T2_NAME_SwitchToggle, null, contentValues) == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getDataFromPusherCredentials(){
        SQLiteDatabase db = this.getWritableDatabase();
        /*Get last record from table*/
        String query = "SELECT * FROM " + T1_NAME_PusherCredentials + " ORDER BY " + T1_COLUMN_NAME_ID + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getDataFromSwitchToggle(){
        SQLiteDatabase db = this.getWritableDatabase();
        /*Get last record from table*/
        String query = "SELECT * FROM " + T2_NAME_SwitchToggle + " ORDER BY " + T2_COLUMN_NAME_ID + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
