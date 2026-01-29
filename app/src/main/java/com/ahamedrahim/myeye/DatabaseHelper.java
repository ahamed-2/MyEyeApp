package com.ahamedrahim.myeye;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "MyEyeDB";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    private static final String TABLE_SMS_LOGS = "sms_logs";
    private static final String TABLE_MEDIA_LOGS = "media_logs";
    private static final String TABLE_SETTINGS = "settings";
    
    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TIMESTAMP = "timestamp";
    
    // SMS Logs table columns
    private static final String KEY_SMS_DATA = "sms_data";
    
    // Media Logs table columns
    private static final String KEY_MEDIA_DATA = "media_data";
    
    // Settings table columns
    private static final String KEY_SETTING_NAME = "setting_name";
    private static final String KEY_SETTING_VALUE = "setting_value";
    
    // Create table queries
    private static final String CREATE_TABLE_SMS_LOGS = 
            "CREATE TABLE " + TABLE_SMS_LOGS + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_SMS_DATA + " TEXT," +
            KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
    
    private static final String CREATE_TABLE_MEDIA_LOGS = 
            "CREATE TABLE " + TABLE_MEDIA_LOGS + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_MEDIA_DATA + " TEXT," +
            KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
    
    private static final String CREATE_TABLE_SETTINGS = 
            "CREATE TABLE " + TABLE_SETTINGS + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_SETTING_NAME + " TEXT UNIQUE," +
            KEY_SETTING_VALUE + " TEXT" + ")";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SMS_LOGS);
        db.execSQL(CREATE_TABLE_MEDIA_LOGS);
        db.execSQL(CREATE_TABLE_SETTINGS);
        
        // Insert default settings
        ContentValues values = new ContentValues();
        values.put(KEY_SETTING_NAME, "monitoring_enabled");
        values.put(KEY_SETTING_VALUE, "0");
        db.insert(TABLE_SETTINGS, null, values);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }
    
    // SMS Logs methods
    public void saveSMSLog(String smsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SMS_DATA, smsData);
        db.insert(TABLE_SMS_LOGS, null, values);
    }
    
    public Cursor getAllSMSLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SMS_LOGS, 
                new String[]{KEY_ID, KEY_SMS_DATA, KEY_TIMESTAMP},
                null, null, null, null, 
                KEY_TIMESTAMP + " DESC");
    }
    
    // Media Logs methods
    public void saveMediaLog(String mediaData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MEDIA_DATA, mediaData);
        db.insert(TABLE_MEDIA_LOGS, null, values);
    }
    
    public Cursor getAllMediaLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MEDIA_LOGS,
                new String[]{KEY_ID, KEY_MEDIA_DATA, KEY_TIMESTAMP},
                null, null, null, null,
                KEY_TIMESTAMP + " DESC");
    }
    
    // Settings methods
    public void saveSetting(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SETTING_NAME, name);
        values.put(KEY_SETTING_VALUE, value);
        
        int rows = db.update(TABLE_SETTINGS, values, 
                KEY_SETTING_NAME + " = ?", new String[]{name});
        
        if (rows == 0) {
            db.insert(TABLE_SETTINGS, null, values);
        }
    }
    
    public String getSetting(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SETTINGS,
                new String[]{KEY_SETTING_VALUE},
                KEY_SETTING_NAME + " = ?",
                new String[]{name},
                null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            String value = cursor.getString(0);
            cursor.close();
            return value;
        }
        
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
    
    // Clear all logs
    public void clearAllLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SMS_LOGS, null, null);
        db.delete(TABLE_MEDIA_LOGS, null, null);
    }
    
    // Delete old logs (older than 30 days)
    public void deleteOldLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_TIMESTAMP + " < datetime('now', '-30 days')";
        db.delete(TABLE_SMS_LOGS, whereClause, null);
        db.delete(TABLE_MEDIA_LOGS, whereClause, null);
    }
}
