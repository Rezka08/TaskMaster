package com.example.taskmaster.repository;

import android.app.Application;
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
    private static volatile TaskRepository instance;
    private final TaskDao taskDao;
    private final CategoryDao categoryDao;
    private final ExecutorService executor;

    private TaskRepository(Application application) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(application);
        taskDao = new TaskDao(databaseHelper);
        categoryDao = new CategoryDao(databaseHelper);
        executor = Executors.newSingleThreadExecutor();
    }

    public static TaskRepository getInstance(Application application) {
        if (instance == null) {
            synchronized (TaskRepository.class) {
                if (instance == null) {
                    instance = new TaskRepository(application);
                }
            }
        }
        return instance;
    }


    // ====================== TASK OPERATIONS ======================

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

    public void getCompletedAndOverdueTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                long count = taskDao.getCompletedAndOverdueTasksCount(currentDate);
                callback.onSuccess((int) count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getUpcomingTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                long count = taskDao.getUpcomingTasksCount(currentDate);
                callback.onSuccess((int) count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getInProgressTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                long count = taskDao.getInProgressTasksCount(currentDate);
                callback.onSuccess((int) count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ====================== TASK LISTS ======================

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

    // ====================== CATEGORY OPERATIONS ======================

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
}