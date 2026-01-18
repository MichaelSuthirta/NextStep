package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.Certificate;
import com.example.nextstep.models.PostCategory;

import java.util.ArrayList;

public class CategoryDAO {
    public static final String TABLE_NAME = "categories";

    private static final String COL_CATEGORYID = "category_id";
    private static final String COL_USERID = "user_id";
    private static final String COL_NAME = "name";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (" + COL_CATEGORYID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERID + " INTEGER, " +
            COL_NAME + " TEXT, " +
            "FOREIGN KEY (" + COL_USERID + ") REFERENCES " + UserDAO.TABLE_NAME + "(id)" +
            ")";

    private final SQLiteConnector dbConnector;

    public CategoryDAO(SQLiteConnector dbConn) {
        this.dbConnector = dbConn;
    }

    public long createCategory(PostCategory category){
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        long result = -1;

        ContentValues cv = new ContentValues();

        cv.put(COL_USERID, category.getUserID());
        cv.put(COL_NAME, category.getCategoryName());

        result = db.insert(TABLE_NAME, null, cv);
        db.close();

        return result;
    }

    @SuppressLint("Range")
    public ArrayList<PostCategory> getAllCategories(String userID) {
        SQLiteDatabase db = dbConnector.getReadableDatabase();
        ArrayList<PostCategory> categoryList = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COL_USERID + "=?",
                new String[]{userID},
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            do {
                PostCategory category = new PostCategory(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USERID)))
                );
                category.setCategoryID(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CATEGORYID))));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return categoryList;
    }
}
