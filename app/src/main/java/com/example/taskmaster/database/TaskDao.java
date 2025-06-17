package com.example.taskmaster.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.taskmaster.model.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private DatabaseHelper dbHelper;

    public TaskDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Task task) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_TASK_TITLE, task.getTitle());
            values.put(DatabaseHelper.COLUMN_TASK_DESCRIPTION, task.getDescription());
            values.put(DatabaseHelper.COLUMN_TASK_DATE, task.getDate());
            values.put(DatabaseHelper.COLUMN_TASK_START_TIME, task.getStartTime());
            values.put(DatabaseHelper.COLUMN_TASK_END_TIME, task.getEndTime());
            values.put(DatabaseHelper.COLUMN_TASK_CATEGORY_ID, task.getCategoryId());
            values.put(DatabaseHelper.COLUMN_TASK_IS_COMPLETED, task.isCompleted() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_TASK_PRIORITY, task.getPriority());
            values.put(DatabaseHelper.COLUMN_TASK_CREATED_AT, task.getCreatedAt());
            values.put(DatabaseHelper.COLUMN_TASK_UPDATED_AT, task.getUpdatedAt());

            return db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            // Safe closing - only close if db is open and not the same instance
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public int update(Task task) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_TASK_TITLE, task.getTitle());
            values.put(DatabaseHelper.COLUMN_TASK_DESCRIPTION, task.getDescription());
            values.put(DatabaseHelper.COLUMN_TASK_DATE, task.getDate());
            values.put(DatabaseHelper.COLUMN_TASK_START_TIME, task.getStartTime());
            values.put(DatabaseHelper.COLUMN_TASK_END_TIME, task.getEndTime());
            values.put(DatabaseHelper.COLUMN_TASK_CATEGORY_ID, task.getCategoryId());
            values.put(DatabaseHelper.COLUMN_TASK_IS_COMPLETED, task.isCompleted() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_TASK_PRIORITY, task.getPriority());
            values.put(DatabaseHelper.COLUMN_TASK_UPDATED_AT, task.getUpdatedAt());

            return db.update(DatabaseHelper.TABLE_TASKS, values,
                    DatabaseHelper.COLUMN_TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getId())});
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public int delete(Task task) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            return db.delete(DatabaseHelper.TABLE_TASKS,
                    DatabaseHelper.COLUMN_TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getId())});
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public Task getTaskById(int taskId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_ID + " = ?";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(taskId)});

            if (cursor != null && cursor.moveToFirst()) {
                return cursorToTask(cursor);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_DATE + " ASC, " +
                    DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    public List<Task> getTasksByDate(String date) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_DATE + " = ?" +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{date});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    public List<Task> getUpcomingTasks(String currentDate) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                    " AND " + DatabaseHelper.COLUMN_TASK_DATE + " > ?" +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_DATE + " ASC, " +
                    DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{currentDate});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    public List<Task> getInProgressTasks(String currentDate) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                    " AND " + DatabaseHelper.COLUMN_TASK_DATE + " = ?" +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{currentDate});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 1" +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_UPDATED_AT + " DESC";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    public List<Task> searchTasks(String searchQuery) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_TITLE + " LIKE ? OR " +
                    DatabaseHelper.COLUMN_TASK_DESCRIPTION + " LIKE ?" +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_DATE + " ASC";

            String searchPattern = "%" + searchQuery + "%";
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{searchPattern, searchPattern});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    public int getTaskCountByMonth(String monthYear) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_DATE + " LIKE ?" +
                    " AND " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{monthYear + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    // Count methods for Monthly Preview
    public int getCompletedTasksCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 1";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    /**
     * NEW METHOD: Get count of completed tasks AND overdue tasks (tasks that passed their date)
     * This includes:
     * 1. Tasks that are marked as completed (is_completed = 1)
     * 2. Tasks that are overdue (date < current date AND is_completed = 0)
     */
    public int getCompletedAndOverdueTasksCount(String currentDate) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            // Query to get count of completed tasks OR overdue tasks
            String selectQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 1" +
                    " OR (" + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                    " AND " + DatabaseHelper.COLUMN_TASK_DATE + " < ?)";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{currentDate});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public int getUpcomingTasksCount(String currentDate) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                    " AND " + DatabaseHelper.COLUMN_TASK_DATE + " > ?";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{currentDate});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public int getInProgressTasksCount(String currentDate) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                    " AND " + DatabaseHelper.COLUMN_TASK_DATE + " = ?";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{currentDate});

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
    }

    public List<Task> getTodayTasksForWidget(String currentDate) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_DATE + " = ?" +
                    " AND " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                    " ORDER BY " + DatabaseHelper.COLUMN_TASK_PRIORITY + " ASC, " +
                    DatabaseHelper.COLUMN_TASK_START_TIME + " ASC LIMIT 5";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{currentDate});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = cursorToTask(cursor);
                    if (task != null) {
                        tasks.add(task);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                // Ignore closing errors
            }
        }
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        try {
            if (cursor == null || cursor.isClosed()) {
                return null;
            }

            return new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_START_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_END_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_IS_COMPLETED)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_PRIORITY)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_CREATED_AT)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_UPDATED_AT))
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}