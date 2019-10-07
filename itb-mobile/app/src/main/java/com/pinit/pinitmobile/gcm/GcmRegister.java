package com.pinit.pinitmobile.gcm;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.util.UserData;

import java.io.IOException;

public class GcmRegister extends AsyncTask<Void, Void, Integer>{

    private static final String TAG = GcmRegister.class.getName();
    private GoogleCloudMessaging gcm;
    private String regid;
    @Override
    protected Integer doInBackground(Void... params) {
       Integer result=-1;
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(App.getCtx());
            }
            //TODO: uzupełnić numer projektu po rejestracji serwera w Google Developers Console
            regid = gcm.register("nr projektu");
            result=1;
            Log.i(TAG, "Device registered, registration ID=" + regid);

        } catch (IOException ex) {
            Log.e(TAG, "Error :" + ex.getMessage());

        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result==1)
            UserData.saveGcmDeviceId(regid);
    }
}
