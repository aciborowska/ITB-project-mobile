package com.pinit.pinitmobile.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.AddressJr;
import com.pinit.pinitmobile.model.Token;
import com.pinit.pinitmobile.model.User;


public class UserData {
    public static void saveGcmDeviceId(String deviceId) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("deviceId", deviceId);
        editor.commit();
    }

    public static void saveUserLocation(LatLng cords) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat("latitude", (float) cords.latitude);
        editor.putFloat("longitude", (float) cords.longitude);
        editor.commit();
    }

    public static LatLng getUserLocation() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        float latitude = sharedpreferences.getFloat("latitude", (float) 51.108156);
        float longitude = sharedpreferences.getFloat("longitude", (float) 17.033088);
        return new LatLng(latitude, longitude);
    }

    public static void saveUser(User user) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("loggedUser", json);
        editor.commit();
    }

    public static User getUser() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString("loggedUser", "");
        User u = gson.fromJson(json, User.class);
        return u;
    }

    public static void saveToken(Token token) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token", token.getToken());
        editor.commit();
    }

    public static Token getToken() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        String value = sharedpreferences.getString("token", "");
        Token token = new Token();
        token.setToken(value);
        return token;
    }

    public static void cleanUserData() {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("loggedUser", null);
        editor.putString("token", null);
        editor.putStringSet(Globals.SELECTED_GROUPS, null);
        editor.putStringSet(Globals.SELECTED_TYPES, null);
        editor.commit();
        App.clearDatabase();
    }

    public static void saveLastKnownAddress(AddressJr addressJr) {
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(Globals.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("countryCode", addressJr.getCountryCode());
        editor.putString("city", addressJr.getCity());
        editor.commit();
    }

    public static AddressJr getLastKnownAddress() {
        AddressJr addressJr = new AddressJr();
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string
                .user_properties), Context.MODE_PRIVATE);
        addressJr.setCountryCode(sharedpreferences.getString("countryCode", ""));
        addressJr.setCity(sharedpreferences.getString("city", ""));
        return addressJr;
    }
}
