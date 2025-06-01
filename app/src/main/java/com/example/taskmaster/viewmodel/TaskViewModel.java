package com.example.taskmaster.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.repository.TaskRepository;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.utils.PriorityUtils;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    // Task operations
    public void getAllTasks(DatabaseListCallback<Task> callback) {
        repository.getAllTasks(callback);
    }

    public void getTasksByDate(String date, DatabaseListCallback<Task> callback) {
        repository.getTasksByDate(date, callback);
    }

    public void getUpcomingTasks(String currentDate, DatabaseListCallback<Task> callback) {
        repository.getUpcomingTasks(currentDate, callback);
    }

    public void getInProgressTasks(String currentDate, DatabaseListCallback<Task> callback) {
        repository.getInProgressTasks(currentDate, callback);
    }

    public void getCompletedTasks(DatabaseListCallback<Task> callback) {
        repository.getCompletedTasks(callback);
    }

    public void searchTasks(String searchQuery, DatabaseListCallback<Task> callback) {
        repository.searchTasks(searchQuery, callback);
    }

    public void getTaskCountByMonth(String monthYear, DatabaseCountCallback callback) {
        repository.getTaskCountByMonth(monthYear, callback);
    }

    public void getTodayTasksForWidget(String currentDate, DatabaseListCallback<Task> callback) {
        repository.getTodayTasksForWidget(currentDate, callback);
    }

    // Count methods for Monthly Preview
    public void getCompletedTasksCount(DatabaseCountCallback callback) {
        repository.getCompletedTasksCount(callback);
    }

    public void getUpcomingTasksCount(String currentDate, DatabaseCountCallback callback) {
        repository.getUpcomingTasksCount(currentDate, callback);
    }

    public void getInProgressTasksCount(String currentDate, DatabaseCountCallback callback) {
        repository.getInProgressTasksCount(currentDate, callback);
    }

    public void getTaskById(int taskId, DatabaseCallback<Task> callback) {
        repository.getTaskById(taskId, callback);
    }

    public void insert(Task task, DatabaseCallback<Long> callback) {
        // Calculate priority automatically
        int priority = PriorityUtils.calculatePriority(task.getDate());
        task.setPriority(priority);
        repository.insert(task, callback);
    }

    public void update(Task task, DatabaseCallback<Integer> callback) {
        // Recalculate priority
        int priority = PriorityUtils.calculatePriority(task.getDate());
        task.setPriority(priority);
        task.setUpdatedAt(System.currentTimeMillis());
        repository.update(task, callback);
    }

    public void delete(Task task, DatabaseCallback<Integer> callback) {
        repository.delete(task, callback);
    }

    // Category operations
    public void getAllCategories(DatabaseListCallback<Category> callback) {
        repository.getAllCategories(callback);
    }

    public void getCategoryById(int id, DatabaseCallback<Category> callback) {
        repository.getCategoryById(id, callback);
    }

    public void insert(Category category, DatabaseCallback<Long> callback) {
        repository.insert(category, callback);
    }
}