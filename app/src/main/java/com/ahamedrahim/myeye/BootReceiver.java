package com.ahamedrahim.myeye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Check if monitoring was enabled before reboot
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            String monitoringEnabled = dbHelper.getSetting("monitoring_enabled");
            dbHelper.close();
            
            if ("1".equals(monitoringEnabled)) {
                // Start services
                Intent mainService = new Intent(context, MainActivity.class);
                mainService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainService);
                
                Toast.makeText(context, "MyEye অ্যাপ পুনরায় শুরু হয়েছে", Toast.LENGTH_LONG).show();
            }
        }
    }
}
