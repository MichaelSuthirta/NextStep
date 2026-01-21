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

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
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
    public User getUserByEmail(String email){
        if (email == null) return null;

        SQLiteDatabase db = dbConnector.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, COL_EMAIL + "=?",
                    new String[]{email}, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                User user = new User(
                        cursor.getString(cursor.getColumnIndex(COL_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COL_PASSWORD))
                );
                user.setId(Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_ID))));
                return user;
            }
        } catch (Exception ignored) {
            // fallthrough
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return null;
    }


    public User upsertByEmail(String username, String phone, String email, String password) {
        User existing = getUserByEmail(email);
        if (existing != null) {
            // Optional: keep username up-to-date for Google users.
            if (username != null && !username.trim().isEmpty() && !username.equals(existing.getUsername())) {
                updateUsername(existing.getId(), username);
                existing.setUsername(username);
            }
            return existing;
        }

        long id = addUser(new User(
                username == null || username.trim().isEmpty() ? email : username,
                phone == null ? "" : phone,
                email,
                password == null ? "" : password
        ));

        if (id <= 0) return getUserByEmail(email);

        User created = new User(
                username == null || username.trim().isEmpty() ? email : username,
                phone == null ? "" : phone,
                email,
                password == null ? "" : password
        );
        created.setId(Long.toString(id));
        return created;
    }

    public boolean updateUsername(String userId, String newUsername) {
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_USERNAME, newUsername);
            int rows = db.update(TABLE_NAME, cv, COL_ID + "=?", new String[]{userId});
            return rows > 0;
        } finally {
            db.close();
        }
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
            cursor.close();
        }
        return user;
    }
}
