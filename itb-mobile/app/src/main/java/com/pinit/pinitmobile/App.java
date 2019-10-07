package com.pinit.pinitmobile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.pinit.pinitmobile.dao.CommentsDao;
import com.pinit.pinitmobile.dao.EventTypesDao;
import com.pinit.pinitmobile.dao.EventsDao;
import com.pinit.pinitmobile.dao.GroupsDao;
import com.pinit.pinitmobile.dao.LocationDao;
import com.pinit.pinitmobile.database.DatabaseHelper;
import com.pinit.pinitmobile.util.Mock;
import com.pinit.pinitmobile.util.UserData;

public class App extends Application {

    private static final String TAG = App.class.getName();
    private DatabaseHelper db;
    private static EventsDao eventsDao = null;
    private static GroupsDao groupsDao = null;
    private static LocationDao locationDao = null;
    private static CommentsDao commentsDao = null;

    private static EventTypesDao eventTypesDao = null;
    private static Context ctx = null;

    public static EventsDao getEventsDao() {
        return eventsDao;
    }

    public static GroupsDao getGroupsDao() {
        return groupsDao;
    }

    public static LocationDao getLocationDao() {
        return locationDao;
    }

    public static EventTypesDao getEventTypesDao() {
        return eventTypesDao;
    }

    public static CommentsDao getCommentsDao() {
        return commentsDao;
    }

    public static Context getCtx() {
        return ctx;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        ctx = getApplicationContext();
        db = new DatabaseHelper(ctx);
        eventsDao = EventsDao.getInstance(db);
        locationDao = LocationDao.getInstance(db);
        groupsDao = GroupsDao.getInstance(db);
        eventTypesDao = EventTypesDao.getInstance(db);
        commentsDao = CommentsDao.getInstance();
    }

    public static void clearDatabase() {
        eventsDao.deleteAll();
        eventTypesDao.deleteAll();
        groupsDao.deleteAll();
        locationDao.deleteAll();
    }
}
