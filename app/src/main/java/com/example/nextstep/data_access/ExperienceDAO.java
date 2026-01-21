package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.Experience;
import com.example.nextstep.models.Post;

import java.util.ArrayList;

public class ExperienceDAO {

    public static final String TABLE_NAME = "experiences";

    private static final String COL_POSTID = "exp_id";
    private static final String COL_USERID = "user_id";
    private static final String COL_COMPANY = "company";
    private static final String COL_ROLE = "role";
    private static final String COL_START = "start_date";
    private static final String COL_END = "end_date";
    private static final String COL_LOCATION = "location";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (" + COL_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERID + " INTEGER, " +
            COL_COMPANY + " TEXT, " +
            COL_ROLE + " TEXT, " +
            COL_START + " TEXT, " +
            COL_END + " TEXT, " +
            COL_LOCATION + " TEXT, " +
            "FOREIGN KEY (" + COL_USERID + ") REFERENCES " + UserDAO.TABLE_NAME + "(id)" +
            ")";

    private final SQLiteConnector dbConnector;

    public ExperienceDAO(SQLiteConnector dbConn) {
        this.dbConnector = dbConn;
    }

    public long createPost(Experience exp){
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        long result = -1;

        ContentValues cv = new ContentValues();

        cv.put(COL_USERID, exp.getUserId());
        cv.put(COL_COMPANY, exp.getCompanyName());
        cv.put(COL_ROLE, exp.getRole());
        cv.put(COL_START, exp.getStart());
        cv.put(COL_END, exp.getFinish());
        cv.put(COL_LOCATION, exp.getLocation());

        result = db.insert(TABLE_NAME, null, cv);
        db.close();

        return result;
    }

    @SuppressLint("Range")
    public ArrayList<Experience> getUserExps(String userID){
        SQLiteDatabase db = dbConnector.getReadableDatabase();
        ArrayList<Experience> postList = new ArrayList<>();

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
                Experience post = new Experience(
                        Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USERID))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION))
                );
                post.setPostId(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_POSTID))));
                postList.add(post);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return postList;
    }

    public Experience findExpByID(String expID){
        SQLiteDatabase db = dbConnector.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COL_POSTID + "=?",
                new String[]{expID},
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            Experience post = new Experience(
                    Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USERID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_START)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_END)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION))
            );
            post.setPostId(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_POSTID))));
            return post;
        }

        db.close();
        cursor.close();

        return null;
    }

    public int editExperience(Experience experience){
        SQLiteDatabase db = dbConnector.getWritableDatabase();

        Experience post = findExpByID(experience.getPostId());

        ContentValues cv = new ContentValues();
        cv.put(COL_COMPANY, experience.getCompanyName());
        cv.put(COL_ROLE, experience.getRole());
        cv.put(COL_START, experience.getStart());
        cv.put(COL_END, experience.getFinish());
        cv.put(COL_LOCATION, experience.getLocation());

        int result = db.update(
                TABLE_NAME,
                cv,
                COL_POSTID + "=?",
                new String[]{experience.getPostId()}
        );

        db.close();
        return result;
    }

    public int deleteExp(String expId, String userId){
        SQLiteDatabase db = dbConnector.getWritableDatabase();

        int result = -1;

        try{
            result = db.delete(TABLE_NAME,
                    COL_POSTID + " = ? AND " +
                                COL_USERID + " = ?",
                    new String[]{expId, userId}
                    );
        }
        catch (Exception e){
            result = -1;
        }
        db.close();
        return result;
    }
}
