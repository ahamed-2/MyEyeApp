package com.ahamedrahim.myeye;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView statusText;
    private Switch monitoringSwitch;
    private Button btnPermissions, btnSettings, btnDeveloper;
    private DatabaseHelper databaseHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        statusText = findViewById(R.id.statusText);
        monitoringSwitch = findViewById(R.id.monitoringSwitch);
        btnPermissions = findViewById(R.id.btnPermissions);
        btnSettings = findViewById(R.id.btnSettings);
        btnDeveloper = findViewById(R.id.btnDeveloper);
        
        databaseHelper = new DatabaseHelper(this);
        
        // Check and request permissions
        checkPermissions();
        
        // Setup button listeners
        btnPermissions.setOnClickListener(v -> requestAllPermissions());
        
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        
        btnDeveloper.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeveloperProfileActivity.class);
            startActivity(intent);
        });
        
        monitoringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startMonitoringServices();
                statusText.setText("মনিটরিং চালু আছে");
                Toast.makeText(this, "মনিটরিং সার্ভিস শুরু হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                stopMonitoringServices();
                statusText.setText("মনিটরিং বন্ধ আছে");
                Toast.makeText(this, "মনিটরিং সার্ভিস বন্ধ হয়েছে", Toast.LENGTH_SHORT).show();
            }
            databaseHelper.saveSetting("monitoring_enabled", isChecked ? "1" : "0");
        });
        
        // Load saved state
        String savedState = databaseHelper.getSetting("monitoring_enabled");
        boolean isEnabled = "1".equals(savedState);
        monitoringSwitch.setChecked(isEnabled);
        statusText.setText(isEnabled ? "মনিটরিং চালু আছে" : "মনিটরিং বন্ধ আছে");
        
        // Show disclaimer dialog
        showDisclaimerDialog();
    }
    
    private void showDisclaimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("গুরুত্বপূর্ণ নোট")
                .setMessage("এই অ্যাপটি শুধুমাত্র শিক্ষামূলক উদ্দেশ্যে তৈরি করা হয়েছে। " +
                           "অনুমতি ছাড়া কারো ফোন মনিটরিং করা আইনত দণ্ডনীয় অপরাধ। " +
                           "শুধুমাত্র বৈধ উদ্দেশ্যে এবং সম্মতির সাথে ব্যবহার করুন।")
                .setPositiveButton("বুঝেছি", null)
                .setCancelable(false)
                .show();
    }
    
    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        // SMS Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        
        // Storage Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        
        // Notification Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                    permissionsNeeded.toArray(new String[0]), 
                    PERMISSION_REQUEST_CODE);
        }
    }
    
    private void requestAllPermissions() {
        checkPermissions();
        
        // Request Notification Access
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
        
        Toast.makeText(this, "অনুগ্রহ করে সমস্ত পারমিশন দিন", Toast.LENGTH_LONG).show();
    }
    
    private void startMonitoringServices() {
        // Start SMS Receiver
        Intent smsService = new Intent(this, SMSReceiver.class);
        startService(smsService);
        
        // Start Media Scanner
        Intent mediaService = new Intent(this, MediaScannerService.class);
        startService(mediaService);
        
        // Start Foreground Service
        Intent foregroundService = new Intent(this, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundService);
        } else {
            startService(foregroundService);
        }
    }
    
    private void stopMonitoringServices() {
        Intent smsService = new Intent(this, SMSReceiver.class);
        stopService(smsService);
        
        Intent mediaService = new Intent(this, MediaScannerService.class);
        stopService(mediaService);
        
        Intent foregroundService = new Intent(this, ForegroundService.class);
        stopService(foregroundService);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Toast.makeText(this, "সমস্ত পারমিশন দেওয়া হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "কিছু পারমিশন দেওয়া হয়নি", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
          }
