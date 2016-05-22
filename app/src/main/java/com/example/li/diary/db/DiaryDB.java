package com.example.li.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.security.Key;
import java.util.Calendar;

/**
 * Created by li on 2016/5/21.
 */
public class DiaryDB {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ID = "_id";
    public static final String KEY_CREATED = "created";

    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DiaryDB(Context context) {
        this.context = context;
    }

    public void open(){
        databaseHelper = new DatabaseHelper(context);
        try{
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        }catch (SQLiteException e){
            sqLiteDatabase = databaseHelper.getReadableDatabase();
        }
    }

    public void close(){
        sqLiteDatabase.close();
    }

    public long createDiary(String title, String body){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_BODY, body);
        Calendar calendar = Calendar.getInstance();
        String created = calendar.get(Calendar.YEAR) + "/"
                + calendar.get(Calendar.MONTH) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH) + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE);
        contentValues.put(KEY_CREATED, created);
        return  sqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE, null, contentValues);
    }

    /**
     * 删除某一日记
     * @param rowId
     * @return
     */
    public boolean deleteDiary(long rowId){
        String whereString = KEY_ID + "=" + rowId;
        return sqLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE,whereString,null) > 0;
    }

    /**
     * 得到全部日记
     * @return
     */
    public Cursor getAllnotes(){
        String[] searchResult = {KEY_ID, KEY_TITLE, KEY_BODY, KEY_CREATED};
        return sqLiteDatabase.query(DatabaseHelper.DATABASE_TABLE,searchResult,null,null,null,null,null);
    }

    /**
     * 获得某一日记
     * @param rowId
     * @return
     */
    public Cursor getDiary(long rowId){
        String[] searchResult = {KEY_ID, KEY_TITLE, KEY_BODY, KEY_CREATED};
        String whereString = KEY_ID + "=" + rowId;
        Cursor mcursor = sqLiteDatabase.query(true, DatabaseHelper.DATABASE_TABLE, searchResult, whereString, null, null, null, null, null);
        if(mcursor != null){
            mcursor.moveToNext();
        }
        return mcursor;
    }

    /**
     * 修改某一个日记
     */
    public boolean updateDiary(long rowId, String title, String body){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID, title);
        contentValues.put(KEY_BODY, body);
        Calendar calendar = Calendar.getInstance();
        String created = calendar.get(Calendar.YEAR) + "/"
                + calendar.get(Calendar.MONTH) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH) + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE);
        contentValues.put(KEY_CREATED, created);
        String whereString = KEY_ID + "=" + rowId;
        return sqLiteDatabase.update(DatabaseHelper.DATABASE_TABLE, contentValues, whereString, null) > 0;
    }

}
