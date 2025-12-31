package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public static final String TABLE_NAME = "posts";

    private static final String COL_POSTID = "post_id";
    private static final String COL_USERID = "user_id";
    private static final String COL_CONTENT = "content";
    private static final String COL_DATETIME = "datetime";
    private static final String COL_IMG = "imagePath";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + COL_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERID + " INTEGER, " +
            COL_CONTENT + " TEXT, " +
            COL_DATETIME + " TIMESTAMP, " +
            COL_IMG + " TEXT, " +
            "FOREIGN KEY (" + COL_USERID + " REFERENCES " + UserDAO.TABLE_NAME + "("
            + COL_USERID + ")" +
            ")";

    private SQLiteConnector dbConnector;

    public PostDAO(SQLiteConnector dbConn) {
        this.dbConnector = dbConn;
    }

    public long createPost(Post post){
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        long result = -1;

        ContentValues cv = new ContentValues();

        cv.put(COL_USERID, post.getUserId());
        cv.put(COL_CONTENT, post.getContent());
        cv.put(COL_DATETIME, post.getPostDate());
        cv.put(COL_IMG, post.getImgPath());

        result = db.insert(TABLE_NAME, null, cv);
        db.close();

        return result;
    }

    @SuppressLint("Range")
    public ArrayList<Post> getUserPosts(String userID){
        SQLiteDatabase db = dbConnector.getReadableDatabase();

        int id = Integer.parseInt(userID);
        ArrayList<Post> postList = new ArrayList<>();

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
        cursor.moveToFirst();

        while(cursor.moveToNext()){
             Post post = new Post(
                     Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_POSTID))),
                     Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_USERID))),
                     cursor.getString(cursor.getColumnIndex(COL_CONTENT)),
                     cursor.getString(cursor.getColumnIndex(COL_IMG)),
                     cursor.getString(cursor.getColumnIndex(COL_DATETIME))
             );
             postList.add(post);
        }

        db.close();

        return postList;
    }
}
