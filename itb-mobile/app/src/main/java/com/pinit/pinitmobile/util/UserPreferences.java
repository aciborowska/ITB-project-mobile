package com.pinit.pinitmobile.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserPreferences {

    public static void saveCollection(List<String> collection, String name) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.user_properties), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(collection);
        editor.putStringSet(name, set);
        editor.commit();
    }

    public static List<String> getCollection(String name) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.user_properties), Context.MODE_PRIVATE);
        Set<String> types = sharedpreferences.getStringSet(name, new HashSet<String>());
        List<String> typesList = new ArrayList<>();
        typesList.addAll(types);
        return typesList;
    }


    public static boolean isFilteringEnabled() {
        if(getCollection(Globals.SELECTED_GROUPS).size()>0) return true;
        if(getCollection(Globals.SELECTED_TYPES).size()>0) return true;
        return false;
    }
}