package com.pinit.pinitmobile.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class GroupTable {
    public static final String TABLE_NAME = "GROUP_TABLE";

    public static String[] ALL_COLUMNS = {GroupColumns._ID, GroupColumns.COLUMN_NAME, GroupColumns.COLUMN_PEOPLE_AMOUNT,
            GroupColumns.COLUMN_ADMIN_ID};

    public interface GroupColumns extends BaseColumns {
        String COLUMN_NAME = "NAME";
        String COLUMN_PEOPLE_AMOUNT = "PEOPLE_AMOUNT";
        String COLUMN_ADMIN_ID = "ADMIN_ID";
    }

    public static void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "( ";
        sql += GroupColumns._ID + " INTEGER NOT NULL PRIMARY KEY, ";
        sql += GroupColumns.COLUMN_NAME + " text, ";
        sql += GroupColumns.COLUMN_PEOPLE_AMOUNT + " integer(5), ";
        sql += GroupColumns.COLUMN_ADMIN_ID + " integer(10)); ";
        db.execSQL(sql);
    }
}
