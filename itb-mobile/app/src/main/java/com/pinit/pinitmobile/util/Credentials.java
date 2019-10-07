package com.pinit.pinitmobile.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.R;

public class Credentials {

    private String email;
    private String password;

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void saveCredentials(){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.user_properties), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    public static Credentials getCredentials(){
        SharedPreferences sharedpreferences = App.getCtx().getSharedPreferences(App.getCtx().getResources().getString(R.string.user_properties), Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("email", "");
        String password = sharedpreferences.getString("password", "");
        return new Credentials(email,password);
    }
}
