package com.ahamedrahim.myeye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DeveloperProfileActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        
        // Initialize views
        ImageView developerImage = findViewById(R.id.developerImage);
        TextView developerName = findViewById(R.id.developerName);
        TextView developerBio = findViewById(R.id.developerBio);
        TextView appPurpose = findViewById(R.id.appPurpose);
        Button btnWebsite = findViewById(R.id.btnWebsite);
        Button btnBack = findViewById(R.id.btnBack);
        
        // Load developer image
        String imageUrl = "https://ibb.co/tpJ02rMC";
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(developerImage);
        
        // Set developer info
        developerName.setText("Ahamed Rahim");
        developerBio.setText("অ্যান্ড্রয়েড ডেভেলপার\n" +
                           "CBC Student, I Love very NBN person\n" +
                           "মোবাইল অ্যাপ ডেভেলপমেন্টে আগ্রহী\n" +
                           "জাভা ও কোটলিন এ দক্ষ");
        
        appPurpose.setText("এই অ্যাপটি তৈরি করা হয়েছে সম্পূর্ণ শিক্ষামূলক উদ্দেশ্যে। " +
                          "অ্যান্ড্রয়েড সিস্টেম সার্ভিস, পারমিশন ম্যানেজমেন্ট, " +
                          "ডাটাবেস এবং নেটওয়ার্কিং সম্পর্কে হাতে-কলমে শেখার জন্য। " +
                          "বৈধ ব্যবহার নিশ্চিত করুন এবং অনুমতি ছাড়া কারো প্রাইভেসি ভঙ্গ করবেন না।");
        
        // Setup button listeners
        btnWebsite.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://ahamed-rahim.pages.dev/"));
            startActivity(browserIntent);
        });
        
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}
