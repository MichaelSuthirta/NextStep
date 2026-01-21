package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.ExtraPost;

import java.util.ArrayList;

public class ExtraPostDAO {
    public static final String TABLE_NAME = "extra_post";

    private static final String COL_POSTID = "post_id";
    private static final String COL_USERID = "user_id";
    private static final String COL_TITLE = "title";
    private static final String COL_START = "start_date";
    private static final String COL_END = "end_date";
    private static final String COL_ORGANIZATION = "organization";
    private static final String COL_CATEGORY = "categoryID";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (" + COL_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERID + " INTEGER, " +
            COL_TITLE + " TEXT, " +
            COL_START + " TEXT, " +
            COL_END + " TEXT, " +
            COL_ORGANIZATION + " TEXT, " +
            COL_CATEGORY + " INTEGER, " +
            "FOREIGN KEY (" + COL_USERID + ") REFERENCES " + UserDAO.TABLE_NAME + "(id)" +
            "FOREIGN KEY (" + COL_CATEGORY + ") REFERENCES " + CategoryDAO.TABLE_NAME + "(category_id)" +
            ")";

    private final SQLiteConnector dbConnector;

    public ExtraPostDAO(SQLiteConnector dbConn) {
        this.dbConnector = dbConn;
    }

    public long createPost(ExtraPost post){
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        long result = -1;

        ContentValues cv = new ContentValues();

        cv.put(COL_USERID, post.getUserId());
        cv.put(COL_TITLE, post.getTitle());
        cv.put(COL_START, post.getStartDate());
        cv.put(COL_END, post.getEndDate());
        cv.put(COL_ORGANIZATION, post.getOrganization());
        cv.put(COL_CATEGORY, post.getCategoryID());

        result = db.insert(TABLE_NAME, null, cv);
        db.close();

        return result;
    }

    @SuppressLint("Range")
    public ArrayList<ExtraPost> getOtherPosts(String userID) {
        SQLiteDatabase db = dbConnector.getReadableDatabase();
        ArrayList<ExtraPost> postList = new ArrayList<>();

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
                ExtraPost post = new ExtraPost(
                        Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USERID))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ORGANIZATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_END)),
                        Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CATEGORY)))
                );
                post.setPostId(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_POSTID))));
                postList.add(post);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return postList;
    }

    public int deletePost(String postId, String userId){
        SQLiteDatabase db = dbConnector.getWritableDatabase();

        int result = -1;

        try{
            result = db.delete(TABLE_NAME,
                    COL_POSTID + " = ? AND " +
                            COL_USERID + " = ?",
                    new String[]{postId, userId}
            );
        }
        catch (Exception e){
            result = -1;
        }
        db.close();
        return result;
    }
}
