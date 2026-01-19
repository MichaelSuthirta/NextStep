package com.example.nextstep.data_access;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnector extends SQLiteOpenHelper {
    private static final String DB_NAME = "nextstep_db";
    // Bump when schema changes.
    private static final int DB_VER = 9;

    private static SQLiteConnector instance;

    private SQLiteConnector(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, version);
    }

    public static SQLiteConnector getInstance(Context context){
        if(instance == null){
            instance = new SQLiteConnector(context, null, null, DB_VER);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(ExperienceDAO.CREATE_TABLE);
        db.execSQL(CertificateDAO.CREATE_TABLE);
        db.execSQL(UserProfileDAO.CREATE_TABLE);

        // Additional profile sections (Competitions, Volunteers, etc.)
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_SECTION);
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_ENTRY);
        db.execSQL(ExtraPostDAO.CREATE_TABLE);
        db.execSQL(CategoryDAO.CREATE_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(ExperienceDAO.CREATE_TABLE);
        db.execSQL(CertificateDAO.CREATE_TABLE);
        db.execSQL(UserProfileDAO.CREATE_TABLE);

        db.execSQL(ProfileSectionDAO.CREATE_TABLE_SECTION);
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_ENTRY);
        db.execSQL(ExtraPostDAO.CREATE_TABLE);
        db.execSQL(CategoryDAO.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Pastikan semua table ada
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(ExperienceDAO.CREATE_TABLE);
        db.execSQL(CertificateDAO.CREATE_TABLE);
        db.execSQL(UserProfileDAO.CREATE_TABLE);

        db.execSQL(ProfileSectionDAO.CREATE_TABLE_SECTION);
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_ENTRY);
        db.execSQL(ExtraPostDAO.CREATE_TABLE);
        db.execSQL(CategoryDAO.CREATE_TABLE);

        // --- MIGRASI experiences schema lama (title) -> schema baru (company, role) ---
        if (oldVersion < 9) {
            // kalau belum ada kolom company, tambahkan
            if (!hasColumn(db, ExperienceDAO.TABLE_NAME, "company")) {
                db.execSQL("ALTER TABLE " + ExperienceDAO.TABLE_NAME + " ADD COLUMN company TEXT");
                // kalau ada title, pindahkan isi title -> company
                if (hasColumn(db, ExperienceDAO.TABLE_NAME, "title")) {
                    db.execSQL("UPDATE " + ExperienceDAO.TABLE_NAME + " SET company = title WHERE company IS NULL");
                }
            }

            // kalau belum ada kolom role, tambahkan
            if (!hasColumn(db, ExperienceDAO.TABLE_NAME, "role")) {
                db.execSQL("ALTER TABLE " + ExperienceDAO.TABLE_NAME + " ADD COLUMN role TEXT");
            }
        }
    }

    // helper: cek kolom ada/tidak
    private boolean hasColumn(SQLiteDatabase db, String table, String col) {
        Cursor c = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        try {
            int nameIdx = c.getColumnIndexOrThrow("name");
            while (c.moveToNext()) {
                if (col.equals(c.getString(nameIdx))) return true;
            }
            return false;
        } finally {
            c.close();
        }
    }

}
