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
    private static final String COL_TITLE = "title";
    private static final String COL_START = "start";
    private static final String COL_END = "end";
    private static final String COL_LOCATION = "location";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + COL_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERID + " INTEGER, " +
            COL_TITLE + " TEXT, " +
            COL_START + " TEXT, " +
            COL_END + " TEXT, " +
            COL_LOCATION + " TEXT, " +
            "FOREIGN KEY (" + COL_USERID + ") REFERENCES " + UserDAO.TABLE_NAME + "("
            + COL_USERID + ")" +
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
        cv.put(COL_TITLE, exp.getTitle());
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

        int id = Integer.parseInt(userID);
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
        cursor.moveToFirst();

        while(cursor.moveToNext()){
             Experience post = new Experience(
                     Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_POSTID))),
                     Integer.toString(cursor.getInt(cursor.getColumnIndex(COL_USERID))),
                     cursor.getString(cursor.getColumnIndex(COL_TITLE)),
                     cursor.getString(cursor.getColumnIndex(COL_START)),
                     cursor.getString(cursor.getColumnIndex(COL_END)),
                     cursor.getString(cursor.getColumnIndex(COL_LOCATION))
             );
             postList.add(post);
        }

        db.close();

        return postList;
    }
}
