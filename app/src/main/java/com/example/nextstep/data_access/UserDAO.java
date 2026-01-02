package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.User;

public class UserDAO {
    public static final String TABLE_NAME = "users";

    private static final String COL_ID = "id";
    private static final String COL_USERNAME= "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    private SQLiteConnector dbConnector;

    public UserDAO(SQLiteConnector db){
        this.dbConnector = db;
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERNAME + " TEXT, " +
            COL_PHONE + " TEXT, " +
            COL_EMAIL + " TEXT, " +
            COL_PASSWORD + " TEXT)";

    public long addUser(User user){
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        long resultCode = -1;
        ContentValues cv = new ContentValues();

        cv.put(COL_USERNAME, user.getUsername());
        cv.put(COL_PHONE, user.getPhone());
        cv.put(COL_EMAIL, user.getEmail());
        cv.put(COL_PASSWORD, user.getPassword());

        resultCode = db.insert(TABLE_NAME, null, cv);
        db.close();

        return resultCode;
    }

    @SuppressLint("Range")
    public User getUserByUsername(String username){
        SQLiteDatabase db = dbConnector.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, COL_USERNAME + "=?",
                new String[]{username}, null, null, null, null);

        cursor.moveToFirst();
        User user;
        try{
            if(cursor.getInt(cursor.getColumnIndex(COL_ID)) > 0){
                user = new User(
//                        Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_ID))),
                        cursor.getString(cursor.getColumnIndex(COL_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COL_PASSWORD))
                );
                user.setId(Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_ID))));
            }
            else {
                user = null;
            }
        }
        catch (Exception e){
            user = null;
        }
        finally {
            db.close();
        }
        return user;
    }
}
