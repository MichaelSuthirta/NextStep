package com.example.nextstep.data_access;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.UserProfile;

import java.util.ArrayList;

/**
 * Single source of truth for Profile data:
 * - role (header)
 * - about me (description)
 * - skills (stored as CSV)
 */
public class UserProfileDAO {
    public static final String TABLE_NAME = "user_profiles";

    private static final String COL_USER_ID = "user_id";
    private static final String COL_ROLE = "role";
    private static final String COL_ABOUT_ME = "about_me";
    private static final String COL_SKILLS = "skills"; // comma separated

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (" + COL_USER_ID + " INTEGER PRIMARY KEY, " +
            COL_ROLE + " TEXT, " +
            COL_ABOUT_ME + " TEXT, " +
            COL_SKILLS + " TEXT)";

    private final SQLiteConnector dbConnector;

    public UserProfileDAO(SQLiteConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    /** Ensure a profile row exists for a user. */
    public void ensureProfile(String userId) {
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_NAME, new String[]{COL_USER_ID}, COL_USER_ID + "=?",
                    new String[]{userId}, null, null, null);
            if (c == null || !c.moveToFirst()) {
                ContentValues cv = new ContentValues();
                cv.put(COL_USER_ID, Integer.parseInt(userId));
                cv.put(COL_ROLE, "");
                cv.put(COL_ABOUT_ME, "");
                cv.put(COL_SKILLS, "");
                db.insert(TABLE_NAME, null, cv);
            }
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    @SuppressLint("Range")
    public UserProfile getProfile(String userId) {
        ensureProfile(userId);

        SQLiteDatabase db = dbConnector.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, COL_USER_ID + "=?",
                    new String[]{userId}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String role = cursor.getString(cursor.getColumnIndex(COL_ROLE));
                String about = cursor.getString(cursor.getColumnIndex(COL_ABOUT_ME));
                String skillsCsv = cursor.getString(cursor.getColumnIndex(COL_SKILLS));
                return new UserProfile(userId, role, about, csvToList(skillsCsv));
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return new UserProfile(userId, "", "", new ArrayList<>());
    }

    public boolean updateRole(String userId, String role) {
        return updateField(userId, COL_ROLE, role == null ? "" : role);
    }

    public boolean updateAboutMe(String userId, String aboutMe) {
        return updateField(userId, COL_ABOUT_ME, aboutMe == null ? "" : aboutMe);
    }

    public boolean updateSkills(String userId, ArrayList<String> skills) {
        return updateField(userId, COL_SKILLS, listToCsv(skills));
    }

    private boolean updateField(String userId, String col, String value) {
        ensureProfile(userId);
        SQLiteDatabase db = dbConnector.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(col, value);
            int rows = db.update(TABLE_NAME, cv, COL_USER_ID + "=?", new String[]{userId});
            return rows > 0;
        } finally {
            db.close();
        }
    }

    public static String listToCsv(ArrayList<String> skills) {
        if (skills == null || skills.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : skills) {
            if (s == null) continue;
            String trimmed = s.trim();
            if (trimmed.isEmpty()) continue;
            if (sb.length() > 0) sb.append(",");
            sb.append(trimmed);
        }
        return sb.toString();
    }

    public static ArrayList<String> csvToList(String csv) {
        ArrayList<String> res = new ArrayList<>();
        if (csv == null) return res;
        String trimmed = csv.trim();
        if (trimmed.isEmpty()) return res;
        String[] parts = trimmed.split(",");
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) res.add(t);
        }
        return res;
    }
}
