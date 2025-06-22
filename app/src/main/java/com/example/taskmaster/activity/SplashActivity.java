package com.example.taskmaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmaster.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hide action bar if it's showing
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Use Handler to delay loading the main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Apply fade transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Close splash activity so it's not accessible with back button
            finish();
        }, SPLASH_DURATION);
    }
}
