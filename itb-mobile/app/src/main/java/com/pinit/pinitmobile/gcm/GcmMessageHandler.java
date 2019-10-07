package com.pinit.pinitmobile.gcm;


import android.app.IntentService;
import android.content.Intent;

public class GcmMessageHandler extends IntentService {
    private static final String TAG = GcmMessageHandler.class.getName();

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
