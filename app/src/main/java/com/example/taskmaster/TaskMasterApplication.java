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

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Clean up database connections if needed
        try {
            com.example.taskmaster.database.DatabaseHelper.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}