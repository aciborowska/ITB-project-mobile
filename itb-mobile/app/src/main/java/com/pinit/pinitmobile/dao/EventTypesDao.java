package com.pinit.pinitmobile.dao;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.pinit.pinitmobile.database.DatabaseHelper;
import com.pinit.pinitmobile.database.EventTypeTable;
import com.pinit.pinitmobile.model.EventType;

import java.util.ArrayList;
import java.util.List;

public class EventTypesDao {

    private static final String TAG = EventTypesDao.class.getName();
    private static EventTypesDao instance = null;
    private DatabaseHelper dbManager = null;

    public static EventTypesDao getInstance(DatabaseHelper db) {
        if (instance == null) {
            instance = new EventTypesDao(db);
        }
        return instance;
    }

    private EventTypesDao(DatabaseHelper db) {
        dbManager = db;
    }

    public long save(EventType type) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventTypeTable.EventTypeColumns._ID, type.getId());
        values.put(EventTypeTable.EventTypeColumns.COLUMNT_EVENT_TYPE, type.getEventType());
        long id = -1;
        try {
            id = db.insertOrThrow(EventTypeTable.TABLE_NAME, null, values);
        } catch (SQLException ex) {
            Log.d(TAG, "SQL exception while inserting " + type.getEventType());
        }
        return id;
    }

    public void update(EventType type) {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventTypeTable.EventTypeColumns._ID, type.getId());
        values.put(EventTypeTable.EventTypeColumns.COLUMNT_EVENT_TYPE, type.getEventType());
        db.update(EventTypeTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(type.getId())});
    }

    public EventType get(Long id) {
        SQLiteDatabase db = this.dbManager.getReadableDatabase();
        EventType type = null;
        Cursor c = db.query(EventTypeTable.TABLE_NAME,
                EventTypeTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if (c.moveToFirst()) {
            type = this.buildFromCursor(c);
        }
        if (!c.isClosed()) c.close();
        return type;
    }

    public List<EventType> getAll() {
        SQLiteDatabase db = this.dbManager.getReadableDatabase();
        List<EventType> types = new ArrayList<>();
        Cursor c = db.query(EventTypeTable.TABLE_NAME,
                EventTypeTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                EventType type = this.buildFromCursor(c);
                if (type != null) {
                    types.add(type);
                }
            } while (c.moveToNext());
        }
        if (!c.isClosed())
            c.close();
        return types;
    }

    private EventType buildFromCursor(Cursor c) {
        EventType type = null;
        if (c != null) {
            type = new EventType();
            type.setId(c.getLong(0));
            type.setEventType(c.getString(1));
        }
        return type;
    }

    public void delete(EventType type) {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        if (type == null || type.getId() <= 0) return;
        db.delete(EventTypeTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(type.getId())});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        db.delete(EventTypeTable.TABLE_NAME, null, null);
    }

    public void saveAll(EventType[] types) {
        for (EventType type : types) {
            save(type);
        }
    }
}
