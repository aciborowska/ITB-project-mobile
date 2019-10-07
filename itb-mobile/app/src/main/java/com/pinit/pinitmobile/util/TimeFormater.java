package com.pinit.pinitmobile.util;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeFormater {
    private static final String TAG = TimeFormater.class.getName();

    public static String longToDateString(long miliseconds){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        Date date = new Date(miliseconds);
        return dateFormat.format(date);
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        long milliseconds = c.getTimeInMillis();
        return longToDateString(milliseconds);
    }

    public static String timeToDateString(int hour, int minute){
        Calendar c = Calendar.getInstance();
        if(hour<c.get(Calendar.HOUR_OF_DAY) || (c.get(Calendar.HOUR_OF_DAY)>=hour && c.get(Calendar.MINUTE)<minute))
            c.add(Calendar.DAY_OF_MONTH,1);

        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);
        return longToDateString(c.getTimeInMillis());
    }

    public static long stringDateToLong(String date){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        Date d;
        try {
            d = f.parse(date+"-"+String.valueOf(currentYear));
        } catch (ParseException e) {
            Log.e(TAG,"Date parsing exception "+e.getMessage());
            d = new Date();
        }
        return d.getTime();
    }



}
