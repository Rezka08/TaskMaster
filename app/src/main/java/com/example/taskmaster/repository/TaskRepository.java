package com.example.taskmaster.repository;

import android.content.Context;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.example.taskmaster.callback.DatabaseCallback;
import com.example.taskmaster.callback.DatabaseListCallback;
import com.example.taskmaster.callback.DatabaseCountCallback;
import com.example.taskmaster.database.DatabaseHelper;
import com.example.taskmaster.database.TaskDao;
import com.example.taskmaster.database.CategoryDao;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.Category;
import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private CategoryDao categoryDao;
    private Executor executor;

    public TaskRepository(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        taskDao = new TaskDao(dbHelper);
        categoryDao = new CategoryDao(dbHelper);
        executor = Executors.newFixedThreadPool(4);
    }

    // Task operations
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

    public void searchTasks(String searchQuery, DatabaseListCallback<Task> callback) {
        executor.execute(() -> {
            try {
                List<Task> tasks = taskDao.searchTasks(searchQuery);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getTaskCountByMonth(String monthYear, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                Integer count = taskDao.getTaskCountByMonth(monthYear);
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

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

    // Count methods for Monthly Preview
    public void getCompletedTasksCount(DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                Integer count = taskDao.getCompletedTasksCount();
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // New method: Get count of completed tasks AND overdue tasks (tasks that passed their date)
    public void getCompletedAndOverdueTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                Integer count = taskDao.getCompletedAndOverdueTasksCount(currentDate);
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getUpcomingTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                Integer count = taskDao.getUpcomingTasksCount(currentDate);
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getInProgressTasksCount(String currentDate, DatabaseCountCallback callback) {
        executor.execute(() -> {
            try {
                Integer count = taskDao.getInProgressTasksCount(currentDate);
                callback.onSuccess(count);
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

    public void insert(Task task, DatabaseCallback<Long> callback) {
        executor.execute(() -> {
            try {
                Long id = taskDao.insert(task);
                callback.onSuccess(id);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void update(Task task, DatabaseCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                Integer rowsAffected = taskDao.update(task);
                callback.onSuccess(rowsAffected);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void delete(Task task, DatabaseCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                Integer rowsDeleted = taskDao.delete(task);
                callback.onSuccess(rowsDeleted);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // Category operations
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

    public void getCategoryById(int id, DatabaseCallback<Category> callback) {
        executor.execute(() -> {
            try {
                Category category = categoryDao.getCategoryById(id);
                callback.onSuccess(category);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void insert(Category category, DatabaseCallback<Long> callback) {
        executor.execute(() -> {
            try {
                Long id = categoryDao.insert(category);
                callback.onSuccess(id);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
}