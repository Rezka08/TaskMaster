package com.example.taskmaster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    private Context context;
    private SharedPreferences sharedPreferences;

    public ThemeManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Set the app theme
     * @param theme One of THEME_LIGHT, THEME_DARK, or THEME_SYSTEM
     */
    public void setTheme(int theme) {
        sharedPreferences.edit().putInt(KEY_THEME, theme).apply();
        applyTheme();
    }

    /**
     * Get the currently selected theme
     * @return Current theme setting
     */
    public int getCurrentTheme() {
        return sharedPreferences.getInt(KEY_THEME, THEME_SYSTEM); // Default to system
    }

    /**
     * Apply the selected theme to the app
     */
    public void applyTheme() {
        int theme = getCurrentTheme();

        switch (theme) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Get theme name for display
     * @return Theme name string
     */
    public String getCurrentThemeName() {
        int theme = getCurrentTheme();

        switch (theme) {
            case THEME_LIGHT:
                return "Light Mode";
            case THEME_DARK:
                return "Dark Mode";
            case THEME_SYSTEM:
            default:
                return "Follow System";
        }
    }

    /**
     * Check if current theme is dark
     * @return true if dark theme is active
     */
    public boolean isDarkTheme() {
        int theme = getCurrentTheme();

        if (theme == THEME_DARK) {
            return true;
        } else if (theme == THEME_SYSTEM) {
            // Check system theme
            int nightMode = context.getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        }

        return false;
    }

    /**
     * Initialize theme on app startup
     * Should be called in Application class or first Activity
     */
    public static void initializeTheme(android.content.Context context) {
        ThemeManager themeManager = new ThemeManager(context);
        themeManager.applyTheme();
    }
}