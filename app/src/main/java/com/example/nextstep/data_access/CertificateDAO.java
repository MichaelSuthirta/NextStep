package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.Certificate;

import java.util.ArrayList;

public class CertificateDAO {
    public static final String TABLE_NAME = "certificates";

    private static final String COL_POSTID = "cert_id";
    private static final String COL_USERID = "user_id";
    private static final String COL_TITLE = "title";
    private static final String COL_PUBLISH = "publish_date";
    private static final String COL_EXPIRE = "end_date";
    private static final String COL_PUBLISHER = "publisher";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (" + COL_POSTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USERID + " INTEGER, " +
            COL_TITLE + " TEXT, " +
            COL_PUBLISH + " TEXT, " +
            COL_EXPIRE + " TEXT, " +
            COL_PUBLISHER + " TEXT, " +
            "FOREIGN KEY (" + COL_USERID + ") REFERENCES " + UserDAO.TABLE_NAME + "(id)" +
            ")";

    private final SQLiteConnector dbConnector;

    public CertificateDAO(SQLiteConnector dbConn) {
        this.dbConnector = dbConn;
    }

    public long createPost(Certificate cert){
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        long result = -1;

        ContentValues cv = new ContentValues();

        cv.put(COL_USERID, cert.getUserId());
        cv.put(COL_TITLE, cert.getTitle());
        cv.put(COL_PUBLISH, cert.getPublishDate());
        cv.put(COL_EXPIRE, cert.getExpireDate());
        cv.put(COL_PUBLISHER, cert.getPublisher());

        result = db.insert(TABLE_NAME, null, cv);
        db.close();

        return result;
    }

    @SuppressLint("Range")
    public ArrayList<Certificate> getUserCertifs(String userID) {
        SQLiteDatabase db = dbConnector.getReadableDatabase();
        ArrayList<Certificate> postList = new ArrayList<>();

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
                Certificate post = new Certificate(
                        Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USERID))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PUBLISHER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PUBLISH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRE))
                );
                post.setPostId(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COL_POSTID))));
                postList.add(post);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return postList;
    }

    /**
     * Updates an existing certificate by its cert_id (postId).
     * @return number of rows affected.
     */
    public int updateCertificate(Certificate cert) {
        if (cert == null || cert.getPostId() == null) return 0;
        SQLiteDatabase db = dbConnector.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, cert.getTitle());
        cv.put(COL_PUBLISH, cert.getPublishDate());
        cv.put(COL_EXPIRE, cert.getExpireDate());
        cv.put(COL_PUBLISHER, cert.getPublisher());

        int rows = db.update(
                TABLE_NAME,
                cv,
                COL_POSTID + "=? AND " + COL_USERID + "=?",
                new String[]{cert.getPostId(), cert.getUserId()}
        );
        db.close();
        return rows;
    }
}
