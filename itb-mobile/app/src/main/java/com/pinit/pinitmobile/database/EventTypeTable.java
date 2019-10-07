package com.pinit.pinitmobile.database;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class EventTypeTable {

    public static final String TABLE_NAME = "EVENT_TYPE_TABLE";

    public static String[] ALL_COLUMNS = {EventTypeColumns._ID, EventTypeColumns.COLUMNT_EVENT_TYPE};

    public interface EventTypeColumns extends BaseColumns{
        String COLUMNT_EVENT_TYPE = "EVENT_TYPE";
    }



    public static void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "( ";
        sql += EventTypeColumns._ID + " INTEGER NOT NULL PRIMARY KEY, ";
        sql += EventTypeColumns.COLUMNT_EVENT_TYPE + " text);";
        db.execSQL(sql);
    }
}
