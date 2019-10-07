package com.pinit.pinitmobile.util;


import android.text.TextUtils;

public class Validation {
    public static boolean isEmailFormatValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordFormatValid(String password) {
        return password.length() >= 8;
    }
}
