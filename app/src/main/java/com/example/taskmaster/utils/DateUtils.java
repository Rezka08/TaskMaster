package com.example.taskmaster.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    public static String getCurrentTime() {
        return timeFormat.format(new Date());
    }

    public static String getCurrentMonthYear() {
        return monthYearFormat.format(new Date());
    }

    public static String getMonthYear(int monthOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthOffset);
        return monthYearFormat.format(calendar.getTime());
    }

    public static String getMonthName(String monthYear) {
        try {
            Date date = monthYearFormat.parse(monthYear);
            SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            return monthNameFormat.format(date);
        } catch (ParseException e) {
            return monthYear;
        }
    }

    public static long getDaysDifference(String dateString) {
        try {
            Date taskDate = dateFormat.parse(dateString);
            Date currentDate = dateFormat.parse(getCurrentDate());
            long diffInMillies = taskDate.getTime() - currentDate.getTime();
            return diffInMillies / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            return 0;
        }
    }

    public static boolean isToday(String dateString) {
        return getCurrentDate().equals(dateString);
    }

    public static boolean isPast(String dateString) {
        return getDaysDifference(dateString) < 0;
    }
}