package com.pinit.pinitmobile.dao;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.database.DatabaseHelper;
import com.pinit.pinitmobile.database.GroupTable;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupsDao {

    private static GroupsDao instance = null;
    private DatabaseHelper dbManager = null;
    private static final String TAG = GroupsDao.class.getName();

    public static GroupsDao getInstance(DatabaseHelper db) {
        if (instance == null)
            instance = new GroupsDao(db);
        return instance;
    }

    private GroupsDao(DatabaseHelper db) {
        dbManager = db;
    }

    public long save(Group g) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GroupTable.GroupColumns._ID, g.getGroupId());
        values.put(GroupTable.GroupColumns.COLUMN_NAME, g.getName());
        values.put(GroupTable.GroupColumns.COLUMN_PEOPLE_AMOUNT, g.getPeopleAmount());
        values.put(GroupTable.GroupColumns.COLUMN_ADMIN_ID, g.getAdminId());
        long id = -1;
        try {
            id = db.insertOrThrow(GroupTable.TABLE_NAME, null, values);
        } catch (SQLException ex) {
            Log.d(TAG, "SQL exception while inserting " + g.getName());
        }
        return id;
    }

    public void update(Group g) {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GroupTable.GroupColumns._ID, g.getGroupId());
        values.put(GroupTable.GroupColumns.COLUMN_NAME, g.getName());
        values.put(GroupTable.GroupColumns.COLUMN_PEOPLE_AMOUNT, g.getPeopleAmount());
        values.put(GroupTable.GroupColumns.COLUMN_ADMIN_ID, g.getAdminId());
        db.update(GroupTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(g.getGroupId())});
    }

    public Group get(Long id) {
        SQLiteDatabase db = this.dbManager.getReadableDatabase();
        Group group = null;
        Cursor c = db.query(GroupTable.TABLE_NAME,
                GroupTable.ALL_COLUMNS,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1");
        if (c.moveToFirst()) {
            group = this.buildGroupFromCursor(c);
        }
        if (!c.isClosed()) c.close();
        return group;
    }

    public List<Group> getAll() {
        SQLiteDatabase db = this.dbManager.getReadableDatabase();
        List<Group> groups = new ArrayList<>();
        Cursor c = db.query(GroupTable.TABLE_NAME,
                GroupTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                Group group = this.buildGroupFromCursor(c);
                if (group != null) {
                    groups.add(group);
                }
            } while (c.moveToNext());
        }
        if (!c.isClosed())
            c.close();
        return groups;
    }

    private Group buildGroupFromCursor(Cursor c) {
        Group group = null;
        if (c != null) {
            group = new Group();
            group.setGroupId(c.getLong(0));
            group.setName(c.getString(1));
            group.setPeopleAmount(c.getInt(2));
            group.setAdminId(c.getLong(3));
        }
        return group;
    }

    public void delete(Group g) {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        if (g == null || g.getGroupId() <= 0) return;
        List<Event> events = App.getEventsDao().getEventsByGroup(g.getGroupId());
        for (Event e : events) {
            if (e.isisPrivate())
                App.getEventsDao().delete(e);
        }
        db.delete(GroupTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(g.getGroupId())});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.dbManager.getWritableDatabase();
        db.delete(GroupTable.TABLE_NAME, null, null);
    }

    public void saveAll(List<Group> groups) {
        for (Group g : groups) {
            save(g);
        }
    }

    public void saveGroupsWithEvents(Group[] groups) {
        for (Group g : groups)
            saveWithEvents(g);
    }

    public void saveWithEvents(Group g) {
        long groupId = save(g);
        for (Event e : g.getEvents()) {
            e.setGroupId(groupId);
            if (e.getLocation() != null)
                App.getEventsDao().saveWithLocation(e);
            else {
                App.getEventsDao().save(e);
                Log.e(TAG, "Event without localization! Something went wrong!");
            }
        }
    }
}
