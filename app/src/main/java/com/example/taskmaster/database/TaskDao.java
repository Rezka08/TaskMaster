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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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

        long id = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public int update(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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

        int rowsAffected = db.update(DatabaseHelper.TABLE_TASKS, values,
                DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return rowsAffected;
    }

    public int delete(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_TASKS,
                DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return rowsDeleted;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_DATE + " ASC, " +
                DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public List<Task> getTasksByDate(String date) {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_DATE + " = ?" +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public List<Task> getUpcomingTasks(String currentDate) {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                " AND " + DatabaseHelper.COLUMN_TASK_DATE + " >= ?" +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_DATE + " ASC, " +
                DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{currentDate});

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public List<Task> getInProgressTasks(String currentDate) {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                " AND " + DatabaseHelper.COLUMN_TASK_DATE + " = ?" +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_START_TIME + " ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{currentDate});

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 1" +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_UPDATED_AT + " DESC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public List<Task> searchTasks(String searchQuery) {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_TITLE + " LIKE ? OR " +
                DatabaseHelper.COLUMN_TASK_DESCRIPTION + " LIKE ?" +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_DATE + " ASC";

        String searchPattern = "%" + searchQuery + "%";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public int getTaskCountByMonth(String monthYear) {
        String selectQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_DATE + " LIKE ?" +
                " AND " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{monthYear + "%"});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public List<Task> getTodayTasksForWidget(String currentDate) {
        List<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TASKS +
                " WHERE " + DatabaseHelper.COLUMN_TASK_DATE + " = ?" +
                " AND " + DatabaseHelper.COLUMN_TASK_IS_COMPLETED + " = 0" +
                " ORDER BY " + DatabaseHelper.COLUMN_TASK_PRIORITY + " ASC, " +
                DatabaseHelper.COLUMN_TASK_START_TIME + " ASC LIMIT 5";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{currentDate});

        if (cursor.moveToFirst()) {
            do {
                Task task = cursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
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
    }
}
