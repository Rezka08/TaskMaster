package com.example.taskmaster.repository;

import android.content.Context;
import android.os.AsyncTask;
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

    public TaskRepository(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        taskDao = new TaskDao(dbHelper);
        categoryDao = new CategoryDao(dbHelper);
    }

    // Task operations
    public void getAllTasks(DatabaseListCallback<Task> callback) {
        new AsyncTask<Void, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(Void... voids) {
                try {
                    return taskDao.getAllTasks();
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute();
    }

    public void getTasksByDate(String date, DatabaseListCallback<Task> callback) {
        new AsyncTask<String, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(String... params) {
                try {
                    return taskDao.getTasksByDate(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute(date);
    }

    public void getUpcomingTasks(String currentDate, DatabaseListCallback<Task> callback) {
        new AsyncTask<String, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(String... params) {
                try {
                    return taskDao.getUpcomingTasks(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute(currentDate);
    }

    public void getInProgressTasks(String currentDate, DatabaseListCallback<Task> callback) {
        new AsyncTask<String, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(String... params) {
                try {
                    return taskDao.getInProgressTasks(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute(currentDate);
    }

    public void getCompletedTasks(DatabaseListCallback<Task> callback) {
        new AsyncTask<Void, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(Void... voids) {
                try {
                    return taskDao.getCompletedTasks();
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute();
    }

    public void searchTasks(String searchQuery, DatabaseListCallback<Task> callback) {
        new AsyncTask<String, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(String... params) {
                try {
                    return taskDao.searchTasks(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute(searchQuery);
    }

    public void getTaskCountByMonth(String monthYear, DatabaseCountCallback callback) {
        new AsyncTask<String, Void, Integer>() {
            private String error;

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    return taskDao.getTaskCountByMonth(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer count) {
                if (count != null) {
                    callback.onSuccess(count);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute(monthYear);
    }

    public void getTodayTasksForWidget(String currentDate, DatabaseListCallback<Task> callback) {
        new AsyncTask<String, Void, List<Task>>() {
            private String error;

            @Override
            protected List<Task> doInBackground(String... params) {
                try {
                    return taskDao.getTodayTasksForWidget(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                if (tasks != null) {
                    callback.onSuccess(tasks);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute(currentDate);
    }

    public void insert(Task task, DatabaseCallback<Long> callback) {
        new AsyncTask<Task, Void, Long>() {
            private String error;

            @Override
            protected Long doInBackground(Task... params) {
                try {
                    return taskDao.insert(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Long id) {
                if (id != null && id > 0) {
                    callback.onSuccess(id);
                } else {
                    callback.onError(error != null ? error : "Failed to insert task");
                }
            }
        }.execute(task);
    }

    public void update(Task task, DatabaseCallback<Integer> callback) {
        new AsyncTask<Task, Void, Integer>() {
            private String error;

            @Override
            protected Integer doInBackground(Task... params) {
                try {
                    return taskDao.update(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer rowsAffected) {
                if (rowsAffected != null && rowsAffected > 0) {
                    callback.onSuccess(rowsAffected);
                } else {
                    callback.onError(error != null ? error : "Failed to update task");
                }
            }
        }.execute(task);
    }

    public void delete(Task task, DatabaseCallback<Integer> callback) {
        new AsyncTask<Task, Void, Integer>() {
            private String error;

            @Override
            protected Integer doInBackground(Task... params) {
                try {
                    return taskDao.delete(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer rowsDeleted) {
                if (rowsDeleted != null && rowsDeleted > 0) {
                    callback.onSuccess(rowsDeleted);
                } else {
                    callback.onError(error != null ? error : "Failed to delete task");
                }
            }
        }.execute(task);
    }

    // Category operations
    public void getAllCategories(DatabaseListCallback<Category> callback) {
        new AsyncTask<Void, Void, List<Category>>() {
            private String error;

            @Override
            protected List<Category> doInBackground(Void... voids) {
                try {
                    return categoryDao.getAllCategories();
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Category> categories) {
                if (categories != null) {
                    callback.onSuccess(categories);
                } else {
                    callback.onError(error != null ? error : "Unknown error occurred");
                }
            }
        }.execute();
    }

    public void getCategoryById(int id, DatabaseCallback<Category> callback) {
        new AsyncTask<Integer, Void, Category>() {
            private String error;

            @Override
            protected Category doInBackground(Integer... params) {
                try {
                    return categoryDao.getCategoryById(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Category category) {
                if (category != null) {
                    callback.onSuccess(category);
                } else {
                    callback.onError(error != null ? error : "Category not found");
                }
            }
        }.execute(id);
    }

    public void insert(Category category, DatabaseCallback<Long> callback) {
        new AsyncTask<Category, Void, Long>() {
            private String error;

            @Override
            protected Long doInBackground(Category... params) {
                try {
                    return categoryDao.insert(params[0]);
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Long id) {
                if (id != null && id > 0) {
                    callback.onSuccess(id);
                } else {
                    callback.onError(error != null ? error : "Failed to insert category");
                }
            }
        }.execute(category);
    }
}