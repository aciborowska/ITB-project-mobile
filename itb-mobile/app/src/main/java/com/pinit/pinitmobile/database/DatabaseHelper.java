package com.pinit.pinitmobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pinitmobile.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context cnt){
        super(cnt, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        EventTypeTable.onCreate(db);
        GroupTable.onCreate(db);
        LocationTable.onCreate(db);
        EventTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
