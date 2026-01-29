package com.ahamedrahim.myeye;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    private EditText etBotToken, etChatId;
    private Button btnSave, btnClearLogs, btnBack;
    private DatabaseHelper databaseHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        databaseHelper = new DatabaseHelper(this);
        
        // Initialize views
        etBotToken = findViewById(R.id.etBotToken);
        etChatId = findViewById(R.id.etChatId);
        btnSave = findViewById(R.id.btnSave);
        btnClearLogs = findViewById(R.id.btnClearLogs);
        btnBack = findViewById(R.id.btnBack);
        
        // Load saved settings
        String savedToken = databaseHelper.getSetting("bot_token");
        String savedChatId = databaseHelper.getSetting("chat_id");
        
        if (savedToken != null) etBotToken.setText(savedToken);
        if (savedChatId != null) etChatId.setText(savedChatId);
        
        // Setup button listeners
        btnSave.setOnClickListener(v -> saveSettings());
        
        btnClearLogs.setOnClickListener(v -> {
            databaseHelper.clearAllLogs();
            Toast.makeText(this, "সমস্ত লগ ডিলিট করা হয়েছে", Toast.LENGTH_SHORT).show();
        });
        
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
    
    private void saveSettings() {
        String botToken = etBotToken.getText().toString().trim();
        String chatId = etChatId.getText().toString().trim();
        
        if (botToken.isEmpty() || chatId.isEmpty()) {
            Toast.makeText(this, "বট টোকেন এবং চ্যাট আইডি দিন", Toast.LENGTH_SHORT).show();
            return;
        }
        
        databaseHelper.saveSetting("bot_token", botToken);
        databaseHelper.saveSetting("chat_id", chatId);
        
        Toast.makeText(this, "সেটিংস সেভ করা হয়েছে", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
              }
