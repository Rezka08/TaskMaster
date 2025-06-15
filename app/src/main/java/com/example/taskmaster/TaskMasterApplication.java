package com.example.taskmaster;

import android.app.Application;
import com.example.taskmaster.utils.ThemeManager;

public class TaskMasterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize theme on app startup
        ThemeManager.initializeTheme(this);
    }
}