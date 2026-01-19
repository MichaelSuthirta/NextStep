package com.example.nextstep.data_access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnector extends SQLiteOpenHelper {
    private static final String DB_NAME = "nextstep_db";
    // Bump when schema changes.
    private static final int DB_VER = 8;

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
<<<<<<< HEAD
        db.execSQL(UserProfileDAO.CREATE_TABLE);

        // Additional profile sections (Competitions, Volunteers, etc.)
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_SECTION);
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_ENTRY);
=======
        db.execSQL(ExtraPostDAO.CREATE_TABLE);
        db.execSQL(CategoryDAO.CREATE_TABLE);
>>>>>>> 793d372f17d0007b3e857c4d7901fe67849bcdc6
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(ExperienceDAO.CREATE_TABLE);
        db.execSQL(CertificateDAO.CREATE_TABLE);
<<<<<<< HEAD
        db.execSQL(UserProfileDAO.CREATE_TABLE);

        db.execSQL(ProfileSectionDAO.CREATE_TABLE_SECTION);
        db.execSQL(ProfileSectionDAO.CREATE_TABLE_ENTRY);
=======
        db.execSQL(ExtraPostDAO.CREATE_TABLE);
        db.execSQL(CategoryDAO.CREATE_TABLE);
>>>>>>> 793d372f17d0007b3e857c4d7901fe67849bcdc6
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
<<<<<<< HEAD
        // Keep existing data when possible; only add new tables/columns.
        if (oldVersion < 7) {
            db.execSQL(UserProfileDAO.CREATE_TABLE);
        }

        if (oldVersion < 8) {
            db.execSQL(ProfileSectionDAO.CREATE_TABLE_SECTION);
            db.execSQL(ProfileSectionDAO.CREATE_TABLE_ENTRY);
        }
=======
        db.execSQL("DROP TABLE IF EXISTS " + CertificateDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExperienceDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CertificateDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExtraPostDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryDAO.TABLE_NAME);
        onCreate(db);
>>>>>>> 793d372f17d0007b3e857c4d7901fe67849bcdc6
    }
}
