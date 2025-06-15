package com.example.taskmaster.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import com.example.taskmaster.R;
import com.example.taskmaster.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private CardView cvLightMode, cvDarkMode, cvSystemMode;
    private ImageView ivLightCheck, ivDarkCheck, ivSystemCheck;
    private TextView tvAppVersion;

    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply theme before setContentView
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        setContentView(R.layout.activity_settings);

        initViews();
        setupClickListeners();
        updateThemeSelection();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        cvLightMode = findViewById(R.id.cv_light_mode);
        cvDarkMode = findViewById(R.id.cv_dark_mode);
        cvSystemMode = findViewById(R.id.cv_system_mode);
        ivLightCheck = findViewById(R.id.iv_light_check);
        ivDarkCheck = findViewById(R.id.iv_dark_check);
        ivSystemCheck = findViewById(R.id.iv_system_check);
        tvAppVersion = findViewById(R.id.tv_app_version);

        // Set app version
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvAppVersion.setText("Version " + version);
        } catch (Exception e) {
            tvAppVersion.setText("Version 1.0");
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        cvLightMode.setOnClickListener(v -> {
            themeManager.setTheme(ThemeManager.THEME_LIGHT);
            updateThemeSelection();
            recreateActivity();
        });

        cvDarkMode.setOnClickListener(v -> {
            themeManager.setTheme(ThemeManager.THEME_DARK);
            updateThemeSelection();
            recreateActivity();
        });

        cvSystemMode.setOnClickListener(v -> {
            themeManager.setTheme(ThemeManager.THEME_SYSTEM);
            updateThemeSelection();
            recreateActivity();
        });
    }

    private void updateThemeSelection() {
        // Reset all checkmarks
        ivLightCheck.setVisibility(android.view.View.GONE);
        ivDarkCheck.setVisibility(android.view.View.GONE);
        ivSystemCheck.setVisibility(android.view.View.GONE);

        // Show checkmark for current theme
        int currentTheme = themeManager.getCurrentTheme();
        switch (currentTheme) {
            case ThemeManager.THEME_LIGHT:
                ivLightCheck.setVisibility(android.view.View.VISIBLE);
                break;
            case ThemeManager.THEME_DARK:
                ivDarkCheck.setVisibility(android.view.View.VISIBLE);
                break;
            case ThemeManager.THEME_SYSTEM:
                ivSystemCheck.setVisibility(android.view.View.VISIBLE);
                break;
        }
    }

    private void recreateActivity() {
        // Delay recreation to allow animation to complete
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            recreate();
        }, 200);
    }

    @Override
    public void finish() {
        super.finish();
        // Add smooth transition
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}