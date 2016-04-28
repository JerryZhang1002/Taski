package com.veggies.android.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JerryCheung on 3/20/16.
 * Used for creating or updating the SQLite database.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "veggies_todolist_v3.0.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_LIST = "lists";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMPLETE = "complete";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_AUDIO = "audio_path";

    public static final String CREATE_TABLE_LIST = "CREATE TABLE " + TABLE_LIST + "(" + COLUMN_ID + " integer primary key autoincrement," + COLUMN_TITLE + " text," + COLUMN_DESC + " text," +
            COLUMN_DATE + " text," + COLUMN_TIME + " text," + COLUMN_COMPLETE + " integer," + COLUMN_TYPE + " integer," + COLUMN_AUDIO + " text)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
