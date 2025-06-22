package com.example.taskmaster.model;

import com.example.taskmaster.utils.PriorityUtils;

public class Task {
    private int id;
    private String title;
    private String description;
    private String date; // Format: yyyy-MM-dd
    private String startTime; // Format: HH:mm
    private String endTime; // Format: HH:mm
    private int categoryId;
    private boolean isCompleted;
    private int priority; // 1=High(Red), 2=Medium(Yellow), 3=Low(Green)
    private long createdAt;
    private long updatedAt;

    // Default constructor
    public Task() {}

    // Constructor untuk membuat task baru
    public Task(String title, String description, String date, String startTime,
                String endTime, int categoryId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.categoryId = categoryId;
        this.isCompleted = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        // PERBAIKAN: Hitung prioritas secara otomatis saat objek dibuat
        this.priority = PriorityUtils.calculatePriority(date);
    }

    // Constructor with ID (for database retrieval)
    public Task(int id, String title, String description, String date, String startTime,
                String endTime, int categoryId, boolean isCompleted, int priority,
                long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.categoryId = categoryId;
        this.isCompleted = isCompleted;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}