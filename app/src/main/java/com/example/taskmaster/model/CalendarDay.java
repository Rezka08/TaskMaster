package com.example.taskmaster.model;

public class CalendarDay {
    private String day;
    private boolean isToday;
    private boolean hasTask;
    private String fullDate; // yyyy-MM-dd format

    public CalendarDay() {}

    public CalendarDay(String day, boolean isToday, boolean hasTask) {
        this.day = day;
        this.isToday = isToday;
        this.hasTask = hasTask;
    }

    public CalendarDay(String day, boolean isToday, boolean hasTask, String fullDate) {
        this.day = day;
        this.isToday = isToday;
        this.hasTask = hasTask;
        this.fullDate = fullDate;
    }

    // Getters and Setters
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public boolean isToday() { return isToday; }
    public void setToday(boolean today) { isToday = today; }

    public boolean hasTask() { return hasTask; }
    public void setHasTask(boolean hasTask) { this.hasTask = hasTask; }

    public String getFullDate() { return fullDate; }
    public void setFullDate(String fullDate) { this.fullDate = fullDate; }
}