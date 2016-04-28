package com.veggies.android.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.veggies.android.custom.ToDoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JerryCheung on 3/20/16.
 * Providing api for operating on the database
 */
public class DBManager {
    private static SQLiteDatabase db;
    //list TYPE_STRING
    public static final int LIST_TYPE_DEFAULT = 0;
    public static final int LIST_TYPE_PERSONAL = 1;
    public static final int LIST_TYPE_SHOPPING = 2;
    public static final int LIST_TYPE_WISH = 3;
    public static final int LIST_TYPE_WORK = 4;
    public static final int LIST_TYPE_ALL = 5;
    public static final int LIST_TYPE_UNFINISHED = 6;

    public static void getDB(Context context) {
        if (db == null) {
            db = new DBHelper(context).getWritableDatabase();
        }
    }

    public static void closeDB() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    /**
     * Add new todolist
     */
    public static void addToDoList(Context context, ToDoItem todo) {
        getDB(context);
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, todo.getTitle());
        values.put(DBHelper.COLUMN_DESC, todo.getDescription());
        values.put(DBHelper.COLUMN_DATE, todo.getDate());
        values.put(DBHelper.COLUMN_TIME, Long.toString(todo.getTimeMillis()));
        values.put(DBHelper.COLUMN_COMPLETE, todo.getComplete());
        values.put(DBHelper.COLUMN_TYPE, todo.getType());
        values.put(DBHelper.COLUMN_AUDIO, todo.getAudioPath());
        db.insert(DBHelper.TABLE_LIST, null, values);
        closeDB();
    }

    /**
     * Update existing todoList
     */
    public static void updateToDoList(Context context, ToDoItem todo) {
        getDB(context);
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, todo.getTitle());
        values.put(DBHelper.COLUMN_DESC, todo.getDescription());
        values.put(DBHelper.COLUMN_DATE, todo.getDate());
        values.put(DBHelper.COLUMN_TIME, Long.toString(todo.getTimeMillis()));
        values.put(DBHelper.COLUMN_COMPLETE, todo.getComplete());
        values.put(DBHelper.COLUMN_TYPE, todo.getType());
        values.put(DBHelper.COLUMN_AUDIO, todo.getAudioPath());
        db.update(DBHelper.TABLE_LIST, values, DBHelper.COLUMN_ID + "=?", new String[]{todo.getId() + ""});
    }
    /**
     * Delete todolist
     */
    public static void deleteToDoList(Context context, ToDoItem todo) {
        getDB(context);
        db.delete(DBHelper.TABLE_LIST, DBHelper.COLUMN_ID + "=?", new String[]{todo.getId() + ""});
        closeDB();
    }

    /**
     * Delete todolist
     */
    public static void deleteToDoList(Context context, int id) {
        getDB(context);
        db.delete(DBHelper.TABLE_LIST, DBHelper.COLUMN_ID + "=?", new String[]{id + ""});
    }

    /**
     * query todoList by fuzzy
     */
    public static List<ToDoItem> queryToDoListByFuzzy(Context context, String str) {
        String where = str;
        if (str == null) {
            where = "";
        }
        getDB(context);
        List<ToDoItem> list = new ArrayList<>();
        Cursor cur = db.query(DBHelper.TABLE_LIST, null, DBHelper.COLUMN_TITLE + " like ?", new String[]{"%" + where + "%"}, null, null, null);
        while (cur.moveToNext()) {
            list.add(new ToDoItem(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), Long.parseLong(cur.getString(4)), cur.getInt(5), cur.getInt(6), cur.getString(7)));
        }
        cur.close();
        closeDB();
        return list;
    }

    /**
     * query todoList by ID
     */

    public static ToDoItem queryToDoListById(Context context, String id) {
        if (id == null) {
            return null;
        }
        getDB(context);
        ToDoItem toDoItem = new ToDoItem();
        Cursor cur = db.query(DBHelper.TABLE_LIST, null, DBHelper.COLUMN_ID + "=?", new String[]{id}, null, null, null);
        while (cur.moveToNext()) {
            toDoItem = new ToDoItem(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), Long.parseLong(cur.getString(4)), cur.getInt(5), cur.getInt(6), cur.getString(7));
        }
        cur.close();
        closeDB();
        return toDoItem;
    }

    /**
     * query todoList by Type
     */

    public static List<ToDoItem> queryToDoListByType(Context context, int typeId) {
        String id = typeId + "";
        getDB(context);
        List<ToDoItem> list = new ArrayList<>();
        Cursor cur = db.query(DBHelper.TABLE_LIST, null, DBHelper.COLUMN_TYPE + "=?", new String[]{id}, null, null, null);
        while (cur.moveToNext()) {
            list.add(new ToDoItem(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), Long.parseLong(cur.getString(4)), cur.getInt(5), cur.getInt(6), cur.getString(7)));
        }
        cur.close();
        closeDB();
        return list;
    }

    /**
     * query todoList by Complete
     */
    public static List<ToDoItem> queryToDoListByComplete(Context context, int compParam) {
        String comp = compParam + "";
        getDB(context);
        List<ToDoItem> list = new ArrayList<>();
        Cursor cur = db.query(DBHelper.TABLE_LIST, null, DBHelper.COLUMN_COMPLETE + "=?", new String[]{comp}, null, null, null);
        while (cur.moveToNext()) {
            list.add(new ToDoItem(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), Long.parseLong(cur.getString(4)), cur.getInt(5), cur.getInt(6), cur.getString(7)));
        }
        cur.close();
        closeDB();
        return list;
    }
}
