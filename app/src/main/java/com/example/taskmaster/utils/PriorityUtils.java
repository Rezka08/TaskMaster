package com.example.taskmaster.utils;

import android.graphics.Color;

public class PriorityUtils {
    public static final int PRIORITY_HIGH = 1;    // Red
    public static final int PRIORITY_MEDIUM = 2;  // Yellow
    public static final int PRIORITY_LOW = 3;     // Green

    public static int calculatePriority(String dateString) {
        long daysDiff = DateUtils.getDaysDifference(dateString);

        if (daysDiff <= 1) {
            return PRIORITY_HIGH;    // Today or tomorrow
        } else if (daysDiff <= 3) {
            return PRIORITY_MEDIUM;  // 2-3 days
        } else {
            return PRIORITY_LOW;     // 4+ days
        }
    }

    public static int getPriorityColor(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                return Color.parseColor("#F44336"); // Red
            case PRIORITY_MEDIUM:
                return Color.parseColor("#FFC107"); // Yellow
            case PRIORITY_LOW:
                return Color.parseColor("#4CAF50"); // Green
            default:
                return Color.parseColor("#9E9E9E"); // Gray
        }
    }

    public static String getPriorityText(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                return "Deadline Dekat";
            case PRIORITY_MEDIUM:
                return "Prioritas Sedang";
            case PRIORITY_LOW:
                return "Prioritas Rendah";
            default:
                return "Tidak Diketahui";
        }
    }
}
