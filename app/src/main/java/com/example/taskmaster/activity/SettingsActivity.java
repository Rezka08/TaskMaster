package com.example.taskmaster.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmaster.R;
import com.example.taskmaster.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {
    private ThemeManager themeManager;
    private CardView cvLightMode, cvDarkMode, cvSystemMode;
    private ImageView ivLightCheck, ivDarkCheck, ivSystemCheck;
    private ImageView ivBack;
    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        updateThemeSelection();
        setupAppVersion();
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
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        cvLightMode.setOnClickListener(v -> {
            themeManager.setTheme(ThemeManager.THEME_LIGHT);
            updateThemeSelection();
            // Recreate activity to apply theme immediately
            recreate();
        });

        cvDarkMode.setOnClickListener(v -> {
            themeManager.setTheme(ThemeManager.THEME_DARK);
            updateThemeSelection();
            // Recreate activity to apply theme immediately
            recreate();
        });

        cvSystemMode.setOnClickListener(v -> {
            themeManager.setTheme(ThemeManager.THEME_SYSTEM);
            updateThemeSelection();
            // Recreate activity to apply theme immediately
            recreate();
        });
    }

    private void updateThemeSelection() {
        int currentTheme = themeManager.getCurrentTheme();

        // Hide all check marks first
        ivLightCheck.setVisibility(View.GONE);
        ivDarkCheck.setVisibility(View.GONE);
        ivSystemCheck.setVisibility(View.GONE);

        // Show appropriate check mark
        switch (currentTheme) {
            case ThemeManager.THEME_LIGHT:
                ivLightCheck.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_DARK:
                ivDarkCheck.setVisibility(View.VISIBLE);
                break;
            case ThemeManager.THEME_SYSTEM:
                ivSystemCheck.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupAppVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvAppVersion.setText("Version " + versionName);
        } catch (Exception e) {
            tvAppVersion.setText("Version 1.0");
        }
    }

    @Override
    public void finish() {
        super.finish();
        // Add smooth transition
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}