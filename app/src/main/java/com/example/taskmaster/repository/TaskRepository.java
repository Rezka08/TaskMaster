package com.example.taskmaster.repository;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.database.CategoryDao;
import com.example.taskmaster.database.DatabaseHelper;
import com.example.taskmaster.database.TaskDao;
import com.example.taskmaster.model.Category;
import com.example.taskmaster.model.Task;

public class TaskRepository {
    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private ExecutorService executor;
    private DatabaseHelper databaseHelper;

    public TaskRepository(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
        taskDao = new TaskDao(databaseHelper);
        categoryDao = new CategoryDao(databaseHelper);
        executor = Executors.newSingleThreadExecutor(); // Use single thread to avoid conflicts
    }

    // ====================== TASK OPERATIONS ======================

    /**
     * Insert a new task
     */
    public void insert(Task task, DatabaseCallback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = taskDao.insert(task);
                callback.onSuccess(id);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Update an existing task
     */
    public void update(Task task, DatabaseCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rowsUpdated = taskDao.update(task);
                callback.onSuccess(rowsUpdated);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Delete a task
     */
    public void delete(Task task, DatabaseCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rowsDeleted = taskDao.delete(task);
                callback.onSuccess(rowsDeleted);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get task by ID
     */
    public void getTaskById(int taskId, DatabaseCallback<Task> callback) {
        executor.execute(() -> {
            try {
                Task task = taskDao.getTaskById(taskId);
                callback.onSuccess(task);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get all tasks
     */
    public void getAllTasks(DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.getAllTasks();
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get tasks by date
     */
    public void getTasksByDate(String date, DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.getTasksByDate(date);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Search tasks
     */
    public void searchTasks(String query, DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.searchTasks(query);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ====================== TASK COUNTS ======================

    /**
     * Get count of completed tasks only
     */
    public void getCompletedTasksCount(DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                int count = taskDao.getCompletedTasksCount();
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get count of completed and overdue tasks with callback
     */
    public void getCompletedAndOverdueTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                int count = taskDao.getCompletedAndOverdueTasksCount(currentDate);
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get count of upcoming tasks with callback
     */
    public void getUpcomingTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                int count = taskDao.getUpcomingTasksCount(currentDate);
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get count of in progress tasks with callback
     */
    public void getInProgressTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                int count = taskDao.getInProgressTasksCount(currentDate);
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ====================== TASK LISTS ======================

    /**
     * Get list of upcoming tasks with callback
     */
    public void getUpcomingTasks(String currentDate, DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.getUpcomingTasks(currentDate);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get list of in progress tasks with callback
     */
    public void getInProgressTasks(String currentDate, DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.getInProgressTasks(currentDate);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get list of completed tasks with callback
     */
    public void getCompletedTasks(DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.getCompletedTasks();
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get today's tasks for widget
     */
    public void getTodayTasksForWidget(String currentDate, DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.getTodayTasksForWidget(currentDate);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ====================== CATEGORY OPERATIONS ======================

    /**
     * Get all categories
     */
    public void getAllCategories(DatabaseListCallback<Category> callback) {
        executor.execute(() -> {
            try {
                List<Category> categories = categoryDao.getAllCategories();
                callback.onSuccess(categories);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Get category by ID
     */
    public void getCategoryById(int categoryId, DatabaseCallback<Category> callback) {
        executor.execute(() -> {
            try {
                Category category = categoryDao.getCategoryById(categoryId);
                callback.onSuccess(category);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Insert a new category
     */
    public void insertCategory(Category category, DatabaseCallback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = categoryDao.insert(category);
                callback.onSuccess(id);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ====================== CLEANUP ======================

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}