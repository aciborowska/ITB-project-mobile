package com.pinit.pinitmobile.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class LocationTable {
    public static final String TABLE_NAME = "LOCATION_TABLE";
    public static String[] ALL_COLUMNS = {LocationColumns._ID, LocationColumns.COLUMN_LATITUDE, LocationColumns
            .COLUMN_LONGITUDE, LocationColumns.COLUMN_COUNTRY, LocationColumns.COLUMN_CITY, LocationColumns.COLUMN_STREET};

    public interface LocationColumns extends BaseColumns {
        String COLUMN_LATITUDE = "LATITUDE";
        String COLUMN_LONGITUDE = "LONGITUDE";
        String COLUMN_COUNTRY = "COUNTRY";
        String COLUMN_CITY = "CITY";
        String COLUMN_STREET = "STREET";
    }

    public static void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "( ";
        sql += LocationColumns._ID + " INTEGER NOT NULL PRIMARY KEY, ";
        sql += LocationColumns.COLUMN_LATITUDE + " real(10), ";
        sql += LocationColumns.COLUMN_LONGITUDE + " real(10), ";
        sql += LocationColumns.COLUMN_COUNTRY + " text, ";
        sql += LocationColumns.COLUMN_CITY + " text, ";
        sql += LocationColumns.COLUMN_STREET + " text) ";
        db.execSQL(sql);
    }
}
