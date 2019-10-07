package com.pinit.pinitmobile.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.pinit.pinitmobile.database.DatabaseHelper;
import com.pinit.pinitmobile.database.LocationTable;
import com.pinit.pinitmobile.model.EventLocation;

import java.util.ArrayList;
import java.util.List;

public class LocationDao {

    private static final String TAG = LocationDao.class.getName();
    private static LocationDao instance = null;
    private DatabaseHelper dbManager;

    public static LocationDao getInstance(DatabaseHelper db) {
        if (instance == null) {
            instance = new LocationDao(db);
        }
        return instance;
    }

    private LocationDao(DatabaseHelper db) {
        dbManager = db;
    }

    public long save(EventLocation l) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationTable.LocationColumns._ID, l.getLocationId());
        values.put(LocationTable.LocationColumns.COLUMN_LATITUDE, l.getLatitude());
        values.put(LocationTable.LocationColumns.COLUMN_LONGITUDE, l.getLongitude());
        values.put(LocationTable.LocationColumns.COLUMN_COUNTRY, l.getCountry());
        values.put(LocationTable.LocationColumns.COLUMN_CITY, l.getCity());
        values.put(LocationTable.LocationColumns.COLUMN_STREET, l.getStreet());
        long id = -1;
        try {
            id = db.insertOrThrow(LocationTable.TABLE_NAME, null, values);
        } catch (SQLException ex) {
            Log.d(TAG, "SQL exception while inserting " + l.getLocationId());
        }
        return id;
    }

    public void update(EventLocation l) {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationTable.LocationColumns._ID, l.getLocationId());
        values.put(LocationTable.LocationColumns.COLUMN_LATITUDE, l.getLatitude());
        values.put(LocationTable.LocationColumns.COLUMN_LONGITUDE, l.getLongitude());
        values.put(LocationTable.LocationColumns.COLUMN_COUNTRY, l.getCountry());
        values.put(LocationTable.LocationColumns.COLUMN_CITY, l.getCity());
        values.put(LocationTable.LocationColumns.COLUMN_STREET, l.getStreet());
        db.update(LocationTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(l.getLocationId())});
    }

    public EventLocation get(Long id) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        EventLocation l = null;
        Cursor c = db.query(LocationTable.TABLE_NAME,
                LocationTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if (c.moveToFirst()) {
            l = this.buildGroupFromCursor(c);
        }
        if (!c.isClosed()) c.close();
        return l;
    }

    public List<EventLocation> getAll() {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        List<EventLocation> locations = new ArrayList<>();
        Cursor c = db.query(LocationTable.TABLE_NAME,
                LocationTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                EventLocation l = this.buildGroupFromCursor(c);
                if (l != null) {
                    locations.add(l);
                }
            } while (c.moveToNext());
        }
        if (!c.isClosed())
            c.close();
        return locations;
    }

    private EventLocation buildGroupFromCursor(Cursor c) {
        EventLocation l = null;
        if (c != null) {
            l = new EventLocation();
            l.setLocationId(c.getLong(0));
            l.setLatitude(c.getFloat(1));
            l.setLongitude(c.getFloat(2));
            l.setCountry(c.getString(3));
            l.setCity(c.getString(4));
            l.setStreet(c.getString(5));
        }
        return l;
    }

    public void delete(EventLocation l) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        if (l == null || l.getLocationId() <= 0) return;
        db.delete(LocationTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(l.getLocationId())});
    }

    public void deleteAll() {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.delete(LocationTable.TABLE_NAME, null, null);
    }

    public void saveAll(List<EventLocation> groups) {
        for (EventLocation l : groups) {
            save(l);
        }
    }
}
