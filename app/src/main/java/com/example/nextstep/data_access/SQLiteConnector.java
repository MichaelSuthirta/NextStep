package com.example.nextstep.data_access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnector extends SQLiteOpenHelper {
    private static final String DB_NAME = "nextstep_db";
    private static final int DB_VER = 4;

    private static SQLiteConnector instance;

    public SQLiteConnector(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VER);
    }

    public static SQLiteConnector getInstance(Context context){
        if(instance == null){
            instance = new SQLiteConnector(context, null, null, 4);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(ExperienceDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExperienceDAO.TABLE_NAME);
        onCreate(db);
    }
}
