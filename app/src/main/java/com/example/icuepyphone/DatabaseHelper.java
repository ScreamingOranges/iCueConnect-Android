package com.example.icuepyphone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "PusherCredentials";
    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_app_id = "_app_id";
    private static final String COLUMN_NAME_key = "_key";
    private static final String COLUMN_NAME_secret = "_secret";
    private static final String COLUMN_NAME_cluster = "_cluster";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME_app_id      + " TEXT," +
                    COLUMN_NAME_key         + " TEXT," +
                    COLUMN_NAME_secret      + " TEXT," +
                    COLUMN_NAME_cluster     + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public boolean addData(String app_id, String key, String secret, String cluster) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_app_id, app_id);
        contentValues.put(COLUMN_NAME_key, key);
        contentValues.put(COLUMN_NAME_secret, secret);
        contentValues.put(COLUMN_NAME_cluster, cluster);

        //if date as inserted incorrectly it will return -1
        if (db.insert(TABLE_NAME, null, contentValues) == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        /*Get last record from table*/
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAME_ID + " DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
