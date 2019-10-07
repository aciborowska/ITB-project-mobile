package com.pinit.pinitmobile.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.AddressJr;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    public static final String TAG = FetchAddressIntentService.class.getName();
    private static final String serviceName = "Geocoder";
    private ResultReceiver receiver;

    public FetchAddressIntentService() {
        super(serviceName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";

        Location location = intent.getParcelableExtra(GeocoderConstants.LOCATION_DATA_EXTRA);
        receiver = intent.getParcelableExtra(GeocoderConstants.RECEIVER);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(GeocoderConstants.FAILURE_RESULT, null);
        } else {
            for (int i = 0; i < addresses.size(); i++) {
                Log.d(TAG, addresses.get(i).toString());
            }
            AddressJr address = new AddressJr(addresses.get(0));

            deliverResultToReceiver(GeocoderConstants.SUCCESS_RESULT,address);
            stopSelf();
        }
    }

    private void deliverResultToReceiver(int resultCode, AddressJr address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GeocoderConstants.RESULT_DATA_KEY, address);
        receiver.send(resultCode, bundle);
    }
}
