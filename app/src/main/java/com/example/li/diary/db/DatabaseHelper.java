package com.example.li.diary.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by li on 2016/5/21.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "notepad";
    public final static String DATABASE_TABLE = "diary";
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_CREATE = "create table " + DATABASE_TABLE + " ("
            + "_id integer primary key autoincrement, "
            +"title text not null, "
            +"body text not null, "
            +"created text not null)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
