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
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
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

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Category category = cursorToCategory(cursor);
                    if (category != null) {
                        categories.add(category);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
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

            if (cursor != null && cursor.moveToFirst()) {
                return cursorToCategory(cursor);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private Category cursorToCategory(Cursor cursor) {
        try {
            if (cursor == null || cursor.isClosed()) {
                return null;
            }

            return new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR))
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}