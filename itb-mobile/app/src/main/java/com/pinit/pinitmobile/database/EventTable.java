package com.pinit.pinitmobile.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class EventTable {
    public static final String TABLE_NAME = "EVENT_TABLE";
    public static String[] ALL_COLUMNS = {EventColumns._ID, EventColumns.COLUMN_NAME, EventColumns.COLUMN_DESCRIPTION,
            EventColumns.COLUMN_START_DATE, EventColumns.COLUMN_COMMENTS_AMOUNT, EventColumns
            .COLUMN_POSITIVES_AMOUNT, EventColumns.COLUMN_NEGATIVES_AMOUNT, EventColumns.COLUMN_PRIVATE, EventColumns
            .COLUMN_MARK, EventColumns.COLUMN_EVENT_TYPE_ID, EventColumns.COLUMN_GROUP_ID, EventColumns.COLUMN_LOCATION_ID};

    public interface EventColumns extends BaseColumns {
        String COLUMN_NAME = "NAME";
        String COLUMN_DESCRIPTION = "DESCRIPTION";
        String COLUMN_START_DATE = "START_DATE";
        String COLUMN_COMMENTS_AMOUNT = "COMMENTS_AMOUNT";
        String COLUMN_POSITIVES_AMOUNT = "POSITIVES_AMOUNT";
        String COLUMN_NEGATIVES_AMOUNT = "NEGATIVES_AMOUNT";
        String COLUMN_PRIVATE = "PRIVATE";
        String COLUMN_MARK = "MARK";
        String COLUMN_EVENT_TYPE_ID = "EVENT_TYPE_ID";
        String COLUMN_GROUP_ID = "GROUP_ID";
        String COLUMN_LOCATION_ID = "LOCATION_ID";
    }

    public static void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " ( ";
        sql += EventColumns._ID + " INTEGER NOT NULL PRIMARY KEY, ";
        sql += EventColumns.COLUMN_NAME + " text, ";
        sql += EventColumns.COLUMN_DESCRIPTION + " text, ";
        sql += EventColumns.COLUMN_START_DATE + " integer(10), ";
        sql += EventColumns.COLUMN_COMMENTS_AMOUNT + " integer(5), ";
        sql += EventColumns.COLUMN_POSITIVES_AMOUNT + " integer(5), ";
        sql += EventColumns.COLUMN_NEGATIVES_AMOUNT + " integer(5), ";
        sql += EventColumns.COLUMN_PRIVATE + " integer(1), ";
        sql += EventColumns.COLUMN_MARK + " integer(1), ";
        sql += EventColumns.COLUMN_EVENT_TYPE_ID + " integer(10), ";
        sql += EventColumns.COLUMN_GROUP_ID + " integer(10), ";
        sql += EventColumns.COLUMN_LOCATION_ID + " integer(10), ";
        sql += "FOREIGN KEY(" + EventColumns.COLUMN_EVENT_TYPE_ID + ") REFERENCES " + EventTypeTable.TABLE_NAME + " (_id), ";
        sql += "FOREIGN KEY(" + EventColumns.COLUMN_GROUP_ID + ") REFERENCES " + GroupTable.TABLE_NAME + " (_id), ";
        sql += "FOREIGN KEY(" + EventColumns.COLUMN_LOCATION_ID + ") REFERENCES " + LocationTable.TABLE_NAME + " (_id));";
        db.execSQL(sql);
    }
}
