package com.example.taskmaster.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.taskmaster.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    private DatabaseHelper dbHelper;

    public CategoryDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Category category) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
            values.put(DatabaseHelper.COLUMN_CATEGORY_COLOR, category.getColor());

            return db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES +
                    " ORDER BY " + DatabaseHelper.COLUMN_CATEGORY_NAME + " ASC";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Category category = cursorToCategory(cursor);
                    categories.add(category);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES +
                    " WHERE " + DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";

            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                return cursorToCategory(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private Category cursorToCategory(Cursor cursor) {
        return new Category(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR))
        );
    }
}