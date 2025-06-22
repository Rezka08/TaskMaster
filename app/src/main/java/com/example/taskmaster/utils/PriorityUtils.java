package com.example.taskmaster.utils;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import com.example.taskmaster.R;

public class PriorityUtils {
    public static final int PRIORITY_HIGH = 1;    // Red
    public static final int PRIORITY_MEDIUM = 2;  // Yellow
    public static final int PRIORITY_LOW = 3;     // Green

    public static int calculatePriority(String dateString) {
        long daysDiff = DateUtils.getDaysDifference(dateString);

        if (daysDiff < 0) { // Lewat waktu
            return PRIORITY_HIGH;
        } else if (daysDiff <= 1) {
            return PRIORITY_HIGH;    // Hari ini atau besok
        } else if (daysDiff <= 4) {
            return PRIORITY_MEDIUM;  // Dalam 2-4 hari
        } else {
            return PRIORITY_LOW;     // 5+ hari
        }
    }

    public static int getPriorityColor(Context context, int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                return ContextCompat.getColor(context, R.color.priority_high); // Red
            case PRIORITY_MEDIUM:
                return ContextCompat.getColor(context, R.color.priority_medium); // Yellow
            case PRIORITY_LOW:
                return ContextCompat.getColor(context, R.color.priority_low); // Green
            default:
                return ContextCompat.getColor(context, R.color.text_light_gray); // Gray
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