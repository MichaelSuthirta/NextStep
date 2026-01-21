package com.example.nextstep.data_access;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nextstep.models.ProfileSection;
import com.example.nextstep.models.ProfileSectionEntry;

import java.util.ArrayList;

/**
 * Generic “additional sections” under profile (e.g. Competitions, Volunteers).
 * Each section contains entries that look like experience/certificate rows.
 */
public class ProfileSectionDAO {

    // ==== TABLE: profile_sections ====
    public static final String TABLE_SECTION = "profile_sections";
    public static final String COL_SECTION_ID = "section_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_SECTION_NAME = "section_name";

    public static final String CREATE_TABLE_SECTION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SECTION + " (" +
                    COL_SECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USER_ID + " INTEGER, " +
                    COL_SECTION_NAME + " TEXT" +
                    ")";

    // ==== TABLE: profile_section_entries ====
    public static final String TABLE_ENTRY = "profile_section_entries";
    public static final String COL_ENTRY_ID = "entry_id";
    public static final String COL_SECTION_REF = "section_id";
    public static final String COL_COMPANY = "company_name";
    public static final String COL_ROLE = "role";
    public static final String COL_START = "start_date";
    public static final String COL_END = "end_date";
    public static final String COL_IS_CURRENT = "is_current";

    public static final String CREATE_TABLE_ENTRY =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRY + " (" +
                    COL_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SECTION_REF + " INTEGER, " +
                    COL_COMPANY + " TEXT, " +
                    COL_ROLE + " TEXT, " +
                    COL_START + " TEXT, " +
                    COL_END + " TEXT, " +
                    COL_IS_CURRENT + " INTEGER DEFAULT 0" +
                    ")";

    private final SQLiteConnector connector;

    public ProfileSectionDAO(SQLiteConnector connector) {
        this.connector = connector;
    }

    public long createSection(ProfileSection section) {
        SQLiteDatabase db = connector.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, section.getUserId());
        values.put(COL_SECTION_NAME, section.getName());
        return db.insert(TABLE_SECTION, null, values);
    }

    public long createEntry(ProfileSectionEntry entry) {
        SQLiteDatabase db = connector.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SECTION_REF, entry.getSectionId());
        values.put(COL_COMPANY, entry.getCompanyName());
        values.put(COL_ROLE, entry.getRole());
        values.put(COL_START, entry.getStartDate());
        values.put(COL_END, entry.getEndDate());
        values.put(COL_IS_CURRENT, entry.isCurrent() ? 1 : 0);
        return db.insert(TABLE_ENTRY, null, values);
    }

    public boolean updateEntry(int entryId, String company, String role, String start, String end, boolean isCurrent) {
        SQLiteDatabase db = connector.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMPANY, company);
        values.put(COL_ROLE, role);
        values.put(COL_START, start);
        values.put(COL_END, end);
        values.put(COL_IS_CURRENT, isCurrent ? 1 : 0);
        int rows = db.update(TABLE_ENTRY, values, COL_ENTRY_ID + "=?", new String[]{String.valueOf(entryId)});
        return rows > 0;
    }

    public boolean deleteEntry(int entryId) {
        SQLiteDatabase db = connector.getWritableDatabase();
        int rows = db.delete(TABLE_ENTRY, COL_ENTRY_ID + "=?", new String[]{String.valueOf(entryId)});
        return rows > 0;
    }

    public ArrayList<ProfileSection> getUserSections(int userId) {
        ArrayList<ProfileSection> out = new ArrayList<>();
        SQLiteDatabase db = connector.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_SECTION_ID + "," + COL_USER_ID + "," + COL_SECTION_NAME +
                        " FROM " + TABLE_SECTION +
                        " WHERE " + COL_USER_ID + "=?" +
                        " ORDER BY " + COL_SECTION_ID + " ASC",
                new String[]{String.valueOf(userId)}
        );
        try {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                int uid = c.getInt(1);
                String name = c.getString(2);
                out.add(new ProfileSection(id, uid, name));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public ArrayList<ProfileSectionEntry> getSectionEntries(int sectionId) {
        ArrayList<ProfileSectionEntry> out = new ArrayList<>();
        SQLiteDatabase db = connector.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_ENTRY_ID + "," + COL_SECTION_REF + "," + COL_COMPANY + "," + COL_ROLE + "," +
                        COL_START + "," + COL_END + "," + COL_IS_CURRENT +
                        " FROM " + TABLE_ENTRY +
                        " WHERE " + COL_SECTION_REF + "=?" +
                        " ORDER BY " + COL_ENTRY_ID + " ASC",
                new String[]{String.valueOf(sectionId)}
        );
        try {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                int sid = c.getInt(1);
                String company = c.getString(2);
                String role = c.getString(3);
                String start = c.getString(4);
                String end = c.getString(5);
                boolean isCurrent = c.getInt(6) == 1;
                out.add(new ProfileSectionEntry(id, sid, company, role, start, end, isCurrent));
            }
        } finally {
            c.close();
        }
        return out;
    }

    public ProfileSectionEntry getEntryById(int entryId) {
        SQLiteDatabase db = connector.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_ENTRY_ID + "," + COL_SECTION_REF + "," + COL_COMPANY + "," + COL_ROLE + "," +
                        COL_START + "," + COL_END + "," + COL_IS_CURRENT +
                        " FROM " + TABLE_ENTRY +
                        " WHERE " + COL_ENTRY_ID + "=?" +
                        " LIMIT 1",
                new String[]{String.valueOf(entryId)}
        );
        try {
            if (c.moveToFirst()) {
                return new ProfileSectionEntry(
                        c.getInt(0),
                        c.getInt(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getInt(6) == 1
                );
            }
        } finally {
            c.close();
        }
        return null;
    }
}
