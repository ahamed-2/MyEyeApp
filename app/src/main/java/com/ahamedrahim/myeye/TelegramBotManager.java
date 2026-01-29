package com.ahamedrahim.myeye;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class TelegramBotManager {
    
    private Context context;
    
    public TelegramBotManager(Context context) {
        this.context = context;
    }
    
    public void sendMessage(String botToken, String chatId, String message) {
        new SendTelegramTask().execute(botToken, chatId, message);
    }
    
    public void sendPhoto(String botToken, String chatId, String photoUrl) {
        new SendPhotoTask().execute(botToken, chatId, photoUrl);
    }
    
    public void sendDocument(String botToken, String chatId, String filePath) {
        new SendDocumentTask().execute(botToken, chatId, filePath);
    }
    
    private class SendTelegramTask extends AsyncTask<String, Void, Boolean> {
        
        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length < 3) return false;
            
            String botToken = params[0];
            String chatId = params[1];
            String message = params[2];
            
            try {
                String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                JSONObject json = new JSONObject();
                json.put("chat_id", chatId);
                json.put("text", message);
                json.put("parse_mode", "HTML");
                
                String jsonString = json.toString();
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(),
                                StandardCharsets.UTF_8));
                
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                
                conn.disconnect();
                return responseCode == 200;
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(context, "Telegram এ মেসেজ পাঠানো যায়নি", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private class SendPhotoTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // Implement photo sending logic
            // This is simplified - actual implementation needs multipart form data
            return true;
        }
    }
    
    private class SendDocumentTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // Implement document sending logic
            // This is simplified - actual implementation needs multipart form data
            return true;
        }
    }
}
