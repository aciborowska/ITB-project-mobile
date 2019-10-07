package com.pinit.pinitmobile.dao;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.database.DatabaseHelper;
import com.pinit.pinitmobile.database.EventTable;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.EventLocation;
import com.pinit.pinitmobile.model.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class EventsDao extends Observable {

    private static final String TAG = EventsDao.class.getName();
    private DatabaseHelper dbManager;
    private static EventsDao instance = null;

    public static EventsDao getInstance(DatabaseHelper db) {
        if (instance == null) {
            instance = new EventsDao(db);
        }
        return instance;
    }

    private EventsDao(DatabaseHelper db) {
        dbManager = db;
    }

    public long save(Event e) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventTable.EventColumns._ID, e.getEventId());
        values.put(EventTable.EventColumns.COLUMN_NAME, e.getName());
        values.put(EventTable.EventColumns.COLUMN_DESCRIPTION, e.getDescription());
        values.put(EventTable.EventColumns.COLUMN_START_DATE, e.getStartDate());
        values.put(EventTable.EventColumns.COLUMN_COMMENTS_AMOUNT, e.getCommentsAmount());
        values.put(EventTable.EventColumns.COLUMN_POSITIVES_AMOUNT, e.getPositivesAmount());
        values.put(EventTable.EventColumns.COLUMN_NEGATIVES_AMOUNT, e.getNegativesAmount());
        values.put(EventTable.EventColumns.COLUMN_PRIVATE, e.isisPrivate());
        values.put(EventTable.EventColumns.COLUMN_MARK, e.getMark());
        values.put(EventTable.EventColumns.COLUMN_EVENT_TYPE_ID, e.getEventTypeId());
        values.put(EventTable.EventColumns.COLUMN_GROUP_ID, e.getGroupId());
        values.put(EventTable.EventColumns.COLUMN_LOCATION_ID, e.getLocationId());
        long id = -1;
        try {
            id = db.insertOrThrow(EventTable.TABLE_NAME, null, values);
        } catch (SQLException ex) {
            Log.d(TAG,"SQL exception while inserting "+e.getName());
        }
        return id;
    }

    public void update(Event e) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventTable.EventColumns._ID, e.getEventId());
        values.put(EventTable.EventColumns.COLUMN_NAME, e.getName());
        values.put(EventTable.EventColumns.COLUMN_DESCRIPTION, e.getDescription());
        values.put(EventTable.EventColumns.COLUMN_START_DATE, e.getStartDate());
        values.put(EventTable.EventColumns.COLUMN_COMMENTS_AMOUNT, e.getCommentsAmount());
        values.put(EventTable.EventColumns.COLUMN_POSITIVES_AMOUNT, e.getPositivesAmount());
        values.put(EventTable.EventColumns.COLUMN_NEGATIVES_AMOUNT, e.getNegativesAmount());
        values.put(EventTable.EventColumns.COLUMN_PRIVATE, e.isisPrivate());
        values.put(EventTable.EventColumns.COLUMN_MARK, e.getMark());
        values.put(EventTable.EventColumns.COLUMN_EVENT_TYPE_ID, e.getEventTypeId());
        values.put(EventTable.EventColumns.COLUMN_GROUP_ID, e.getGroupId());
        values.put(EventTable.EventColumns.COLUMN_LOCATION_ID, e.getLocationId());
        db.update(EventTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(e.getEventId())});
    }

    public Event get(Long id) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Event e = null;
        Cursor c = db.query(EventTable.TABLE_NAME,
                EventTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if (c.moveToFirst()) {
            e = this.buildEventFromCursor(c);
        }
        if (!c.isClosed()) c.close();
        return e;
    }

    public List<Event> getAll() {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        List<Event> events = new ArrayList<>();
        Cursor c = db.query(EventTable.TABLE_NAME,
                EventTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                Event e = this.buildEventFromCursor(c);
                if (e != null) {
                    events.add(e);
                }
            } while (c.moveToNext());
        }
        if (!c.isClosed())
            c.close();
        return events;
    }

    private Event buildEventFromCursor(Cursor c) {
        Event e = null;
        if (c != null) {
            e = new Event();
            e.setEventId(c.getLong(0));
            e.setName(c.getString(1));
            e.setDescription(c.getString(2));
            e.setStartDate(c.getLong(3));
            e.setCommentsAmount(c.getInt(4));
            e.setPositivesAmount(c.getInt(5));
            e.setNegativesAmount(c.getInt(6));
            e.setisPrivate(c.getInt(7) == 1 ? true : false);
            e.setMark(c.getInt(8));
            e.setEventTypeId((long) c.getInt(9));
            e.setGroupId(c.getLong(10));
            e.setLocationId(c.getLong(11));
        }
        return e;
    }

    public void delete(Event e) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        if (e == null || e.getEventId() <= 0) return;
        App.getLocationDao().delete(e.getLocation());
        db.delete(EventTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(e.getEventId())});
    }

    public void deleteAll() {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.delete(EventTable.TABLE_NAME, null, null);
    }

    public void saveAll(List<Event> events) {
        for (Event e : events) {
            save(e);
        }
    }

    public void saveEventsWithLocations(Event[] events) {
        for (Event e : events)
            saveWithLocation(e);
    }

    public void saveWithLocation(Event e) {
        EventLocation l = e.getLocation();
        long locationId = App.getLocationDao().save(l);
        e.setLocationId(locationId);
        save(e);
    }

    public List<Event> getEventsByGroup(long groupId) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor c = db.query(EventTable.TABLE_NAME,
                EventTable.ALL_COLUMNS,
                EventTable.EventColumns.COLUMN_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)},
                null,
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                Event e = this.buildEventFromCursor(c);
                if (e != null) {
                    events.add(e);
                }
            } while (c.moveToNext());
        }
        if (!c.isClosed())
            c.close();
        return events;
    }

    public List<Event> filterByGroupAndType(List<String> types, List<String> groups) {
        List<Event> filterListTemp = getAll();
        if (types.size() > 0) {
            for (Event e : getAll()) {
                if (!types.contains(String.valueOf(e.getEventTypeId())))
                    filterListTemp.remove(e);
            }
        }

        List<Event> filterList = new ArrayList<>();
        filterList.addAll(filterListTemp);
        if (groups.size() > 0) {
            for (Event e : filterListTemp) {
                Group g = App.getGroupsDao().get(e.getGroupId());
                if (g != null) {
                    if (!groups.contains(String.valueOf(g.getGroupId())))
                        filterList.remove(e);
                } else {
                    filterList.remove(e);
                }
            }
        }
        return filterList;
    }


}
