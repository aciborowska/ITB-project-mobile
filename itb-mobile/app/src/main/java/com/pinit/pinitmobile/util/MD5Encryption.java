package com.pinit.pinitmobile.util;


import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encryption {

    public static final String TAG = MD5Encryption.class.getName();

    public static String encrypt(String encTarget) {
        if(encTarget==null || encTarget.isEmpty()) return null;
        MessageDigest md5password = null;
        try {
            md5password = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "brak algorytmu MD5");
        }
        assert md5password != null;
        md5password.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, md5password.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }
}
