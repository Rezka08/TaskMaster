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
    private TaskRepository taskRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
    }

    // ====================== TASK OPERATIONS ======================

    /**
     * Insert a new task
     */
    public void insert(Task task, DatabaseCallback<Long> callback) {
        if (taskRepository != null) {
            taskRepository.insert(task, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Update an existing task
     */
    public void update(Task task, DatabaseCallback<Integer> callback) {
        if (taskRepository != null) {
            taskRepository.update(task, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Delete a task
     */
    public void delete(Task task, DatabaseCallback<Integer> callback) {
        if (taskRepository != null) {
            taskRepository.delete(task, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get task by ID
     */
    public void getTaskById(int taskId, DatabaseCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getTaskById(taskId, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get all tasks
     */
    public void getAllTasks(DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getAllTasks(callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get tasks by date
     */
    public void getTasksByDate(String date, DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getTasksByDate(date, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Search tasks
     */
    public void searchTasks(String query, DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.searchTasks(query, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    // ====================== TASK COUNTS ======================

    /**
     * Get count of completed tasks only
     */
    public void getCompletedTasksCount(DatabaseCountCallback callback) {
        if (taskRepository != null) {
            taskRepository.getCompletedTasksCount(callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get count of completed and overdue tasks
     * This method properly counts both completed tasks and overdue tasks
     */
    public void getCompletedAndOverdueTasksCount(String currentDate, DatabaseCountCallback callback) {
        if (taskRepository != null) {
            taskRepository.getCompletedAndOverdueTasksCount(currentDate, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get count of upcoming tasks (future tasks that are not completed)
     */
    public void getUpcomingTasksCount(String currentDate, DatabaseCountCallback callback) {
        if (taskRepository != null) {
            taskRepository.getUpcomingTasksCount(currentDate, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get count of in progress tasks (today's tasks that are not completed)
     */
    public void getInProgressTasksCount(String currentDate, DatabaseCountCallback callback) {
        if (taskRepository != null) {
            taskRepository.getInProgressTasksCount(currentDate, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    // ====================== TASK LISTS ======================

    /**
     * Get list of upcoming tasks
     */
    public void getUpcomingTasks(String currentDate, DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getUpcomingTasks(currentDate, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get list of in progress tasks (today's tasks)
     */
    public void getInProgressTasks(String currentDate, DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getInProgressTasks(currentDate, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get list of completed tasks
     */
    public void getCompletedTasks(DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getCompletedTasks(callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get today's tasks for widget
     */
    public void getTodayTasksForWidget(String currentDate, DatabaseListCallback<Task> callback) {
        if (taskRepository != null) {
            taskRepository.getTodayTasksForWidget(currentDate, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    // ====================== CATEGORY OPERATIONS ======================

    /**
     * Get all categories
     */
    public void getAllCategories(DatabaseListCallback<Category> callback) {
        if (taskRepository != null) {
            taskRepository.getAllCategories(callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Get category by ID
     */
    public void getCategoryById(int categoryId, DatabaseCallback<Category> callback) {
        if (taskRepository != null) {
            taskRepository.getCategoryById(categoryId, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    /**
     * Insert a new category
     */
    public void insertCategory(Category category, DatabaseCallback<Long> callback) {
        if (taskRepository != null) {
            taskRepository.insertCategory(category, callback);
        } else {
            callback.onError("Repository not initialized");
        }
    }

    // ====================== CLEANUP ======================

    @Override
    protected void onCleared() {
        super.onCleared();
        if (taskRepository != null) {
            taskRepository.cleanup();
        }
    }
}