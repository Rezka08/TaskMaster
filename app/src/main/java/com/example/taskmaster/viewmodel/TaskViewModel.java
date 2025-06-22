package com.example.taskmaster.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        // PERBAIKAN: Gunakan instance singleton dari TaskRepository
        taskRepository = TaskRepository.getInstance(application);
    }

    // ====================== TASK OPERATIONS ======================

    public void insert(Task task, DatabaseCallback<Long> callback) {
        taskRepository.insert(task, callback);
    }

    public void update(Task task, DatabaseCallback<Integer> callback) {
        taskRepository.update(task, callback);
    }

    public void delete(Task task, DatabaseCallback<Integer> callback) {
        taskRepository.delete(task, callback);
    }

    public void getTaskById(int taskId, DatabaseCallback<Task> callback) {
        taskRepository.getTaskById(taskId, callback);
    }

    public void getAllTasks(DatabaseListCallback<Task> callback) {
        taskRepository.getAllTasks(callback);
    }

    public void getTasksByDate(String date, DatabaseListCallback<Task> callback) {
        taskRepository.getTasksByDate(date, callback);
    }

    public void searchTasks(String query, DatabaseListCallback<Task> callback) {
        taskRepository.searchTasks(query, callback);
    }

    // ====================== TASK COUNTS ======================

    public void getCompletedAndOverdueTasksCount(String currentDate, DatabaseCountCallback callback) {
        taskRepository.getCompletedAndOverdueTasksCount(currentDate, callback);
    }

    public void getUpcomingTasksCount(String currentDate, DatabaseCountCallback callback) {
        taskRepository.getUpcomingTasksCount(currentDate, callback);
    }

    public void getInProgressTasksCount(String currentDate, DatabaseCountCallback callback) {
        taskRepository.getInProgressTasksCount(currentDate, callback);
    }

    // ====================== TASK LISTS ======================

    public void getUpcomingTasks(String currentDate, DatabaseListCallback<Task> callback) {
        taskRepository.getUpcomingTasks(currentDate, callback);
    }

    public void getInProgressTasks(String currentDate, DatabaseListCallback<Task> callback) {
        taskRepository.getInProgressTasks(currentDate, callback);
    }

    public void getCompletedTasks(DatabaseListCallback<Task> callback) {
        taskRepository.getCompletedTasks(callback);
    }

    // ====================== CATEGORY OPERATIONS ======================

    public void getAllCategories(DatabaseListCallback<Category> callback) {
        taskRepository.getAllCategories(callback);
    }

    public void getCategoryById(int categoryId, DatabaseCallback<Category> callback) {
        taskRepository.getCategoryById(categoryId, callback);
    }
}