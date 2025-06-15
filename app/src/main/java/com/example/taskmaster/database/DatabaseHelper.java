package com.example.taskmaster.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "taskmaster.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_CATEGORIES = "categories";

    // Tasks Table Columns
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_TITLE = "title";
    public static final String COLUMN_TASK_DESCRIPTION = "description";
    public static final String COLUMN_TASK_DATE = "date";
    public static final String COLUMN_TASK_START_TIME = "start_time";
    public static final String COLUMN_TASK_END_TIME = "end_time";
    public static final String COLUMN_TASK_CATEGORY_ID = "category_id";
    public static final String COLUMN_TASK_IS_COMPLETED = "is_completed";
    public static final String COLUMN_TASK_PRIORITY = "priority";
    public static final String COLUMN_TASK_CREATED_AT = "created_at";
    public static final String COLUMN_TASK_UPDATED_AT = "updated_at";

    // Categories Table Columns
    public static final String COLUMN_CATEGORY_ID = "id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_COLOR = "color";

    private static DatabaseHelper instance;
    private static final Object lock = new Object();

    public static DatabaseHelper getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new DatabaseHelper(context.getApplicationContext());
            }
            return instance;
        }
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create Categories Table
            String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES +
                    "(" +
                    COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CATEGORY_NAME + " TEXT NOT NULL," +
                    COLUMN_CATEGORY_COLOR + " TEXT NOT NULL" +
                    ")";

            // Create Tasks Table
            String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS +
                    "(" +
                    COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TASK_TITLE + " TEXT NOT NULL," +
                    COLUMN_TASK_DESCRIPTION + " TEXT," +
                    COLUMN_TASK_DATE + " TEXT NOT NULL," +
                    COLUMN_TASK_START_TIME + " TEXT NOT NULL," +
                    COLUMN_TASK_END_TIME + " TEXT NOT NULL," +
                    COLUMN_TASK_CATEGORY_ID + " INTEGER," +
                    COLUMN_TASK_IS_COMPLETED + " INTEGER DEFAULT 0," +
                    COLUMN_TASK_PRIORITY + " INTEGER DEFAULT 3," +
                    COLUMN_TASK_CREATED_AT + " INTEGER NOT NULL," +
                    COLUMN_TASK_UPDATED_AT + " INTEGER NOT NULL," +
                    "FOREIGN KEY(" + COLUMN_TASK_CATEGORY_ID + ") REFERENCES " +
                    TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ")" +
                    ")";

            db.execSQL(CREATE_CATEGORIES_TABLE);
            db.execSQL(CREATE_TASKS_TABLE);

            // Insert default categories
            insertDefaultCategories(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion != newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
                onCreate(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (db != null && db.isOpen()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        try {
            return super.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        try {
            return super.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized void close() {
        try {
            super.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        try {
            if (db != null && db.isOpen()) {
                db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + COLUMN_CATEGORY_NAME + ", " + COLUMN_CATEGORY_COLOR + ") VALUES ('Kuliah', '#2196F3')");
                db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + COLUMN_CATEGORY_NAME + ", " + COLUMN_CATEGORY_COLOR + ") VALUES ('Tugas', '#FF9800')");
                db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + COLUMN_CATEGORY_NAME + ", " + COLUMN_CATEGORY_COLOR + ") VALUES ('Pribadi', '#4CAF50')");
                db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + COLUMN_CATEGORY_NAME + ", " + COLUMN_CATEGORY_COLOR + ") VALUES ('Lainnya', '#9E9E9E')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to safely close database connection
     */
    public static void closeDatabase() {
        synchronized (lock) {
            if (instance != null) {
                try {
                    instance.close();
                    instance = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}