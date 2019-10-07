package com.pinit.pinitmobile.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.AddressJr;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.EventLocation;
import com.pinit.pinitmobile.service.FetchAddressIntentService;
import com.pinit.pinitmobile.service.GeocoderConstants;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.event.GetEventTypesTask;
import com.pinit.pinitmobile.tasks.group.GetGroupEventsTask;
import com.pinit.pinitmobile.tasks.event.GetPublicEventsTask;
import com.pinit.pinitmobile.ui.dialog.BottomSheetDialog;
import com.pinit.pinitmobile.ui.dialog.FilterDialog;
import com.pinit.pinitmobile.ui.dialog.TaskProgressDialog;
import com.pinit.pinitmobile.util.UserData;
import com.pinit.pinitmobile.util.UserPreferences;

import java.util.List;

public class MapFragment extends SupportMapFragment implements GoogleMap.OnMapLongClickListener, GoogleMap
        .OnMyLocationChangeListener, ClusterManager.OnClusterItemClickListener<MarkerClusterItem>, FilterDialog.EventFilter,
        AsyncTaskCallback {

    public static final String TAG = MapFragment.class.getName();
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_GROUP_ID = "groupId";
    private AddressResultReceiver resultReceiver;
    private GoogleMap googleMap;
    private LatLng eventPosition = null;
    private long groupId = -1;
    private ClusterManager<MarkerClusterItem> clusterManager;
    public static boolean isNewlyCreated = false;
    private ProgressDialog progressDialog;
    private GetPublicEventsTask getPublicEventsTask;
    private GetGroupEventsTask getGroupEventsTask;

    public MapFragment() {
    }

    public static MapFragment newInstance(double latitude, double longitude, long groupId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
        resultReceiver = new AddressResultReceiver(new Handler());
        if (getArguments() != null) {
            eventPosition = new LatLng(getArguments().getDouble(ARG_LATITUDE), getArguments().getDouble(ARG_LONGITUDE));
            groupId = getArguments().getLong(ARG_GROUP_ID, -1);
            if (groupId != -1)
                Toast.makeText(getActivity(), getString(R.string.choose_group_event_location), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup arg1, Bundle arg2) {
        Log.d(TAG, "onCreateView");
        getActivity().setTitle(getString(R.string.title_map_fragment));
        return super.onCreateView(mInflater, arg1, arg2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        setupMap();
        setupClusterManager();
        setupUserCurrentLocation();
        refreshMap();
        if (eventPosition != null) {
            Log.d(TAG, String.valueOf(eventPosition.latitude) + ", " + String.valueOf(eventPosition.longitude));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventPosition, 15));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_map_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        if (UserPreferences.isFilteringEnabled()) {
            filterItem.setIcon(R.drawable.ic_search_green_36dp);
        } else {
            filterItem.setIcon(R.drawable.ic_search_white_48dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                AddressJr addressJr = UserData.getLastKnownAddress();
                App.clearDatabase();
                if (!addressJr.getCountryCode().isEmpty() && !addressJr.getCity().isEmpty()) {
                    startDownloadingEventData(addressJr);
                } else {
                    LatLng userLastLocation = UserData.getUserLocation();
                    Location location = new Location("userPosition");
                    location.setLatitude(userLastLocation.latitude);
                    location.setLongitude(userLastLocation.longitude);
                    startGeoCoderService(location);
                }
                break;
            case R.id.action_filter:
                FilterDialog filterDialog = new FilterDialog(getActivity(), this, R.style.FilterDialog);
                filterDialog.show();
                break;
            case R.id.action_download_event_for_map_position:
                LatLng cameraPosition = googleMap.getCameraPosition().target;
                Location location = new Location("reverseGeocoding");
                location.setLatitude(cameraPosition.latitude);
                location.setLongitude(cameraPosition.longitude);
                startGeoCoderService(location);
                break;
            case R.id.action_add_event:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, Fragment.instantiate(getActivity(),
                        AddEventFragment.TAG)).addToBackStack("Map").commit();
                break;
            default:
                Log.e(TAG, "Brak akcji dla elementu menu");
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupMap() {
        googleMap = getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMyLocationChangeListener(this);
    }

    private void setupClusterManager() {
        clusterManager = new ClusterManager<>(getActivity(), googleMap);
        clusterManager.setRenderer(new EventRenderer(getActivity(), googleMap, clusterManager));
        clusterManager.setOnClusterItemClickListener(this);
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnCameraChangeListener(clusterManager);
    }

    private void setupUserCurrentLocation() {
        LocationManager locationmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        String bestLocationProvider = locationmanager.getBestProvider(cri, true);
        Location myLocation = locationmanager.getLastKnownLocation(bestLocationProvider);
        if (myLocation != null) {
            double lat = myLocation.getLatitude();
            double lon = myLocation.getLongitude();
            LatLng position = new LatLng(lat, lon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    @Override
    public void refreshMap() {
        clusterManager.clearItems();
        googleMap.clear();
        getActivity().invalidateOptionsMenu();
        setupEvents();
        clusterManager.cluster();
    }

    private void setupEvents() {
        Log.d(TAG, "setupEvents");
        List<Event> events = App.getEventsDao().filterByGroupAndType(UserPreferences.getCollection(Globals.SELECTED_TYPES),
                UserPreferences.getCollection(Globals.SELECTED_GROUPS));
        for (Event e : events) {
            EventLocation l = App.getLocationDao().get(e.getLocationId());
            if (l != null) {
                MarkerClusterItem item = new MarkerClusterItem(new LatLng(l.getLatitude(), l.getLongitude()), e.getEventId(), e
                        .getCommentsAmount());
                clusterManager.addItem(item);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        AddEventFragment addEventFragment = AddEventFragment.newInstance(latLng.latitude, latLng.longitude, groupId);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, addEventFragment).addToBackStack("Map").commit();
    }


    @Override
    public void onMyLocationChange(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        UserData.saveUserLocation(loc);
        if (isNewlyCreated) {
            isNewlyCreated = false;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
            startGeoCoderService(location);
        }
    }

    @Override
    public boolean onClusterItemClick(MarkerClusterItem markerClusterItem) {
        Event event = App.getEventsDao().get(markerClusterItem.getEventId());
        if (event != null) {
            LatLng position = new LatLng(markerClusterItem.getPosition().latitude - 0.005, markerClusterItem.getPosition()
                    .longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 14.0f));
            BottomSheetDialog bottomSheet = new BottomSheetDialog(getActivity(), R.style.MaterialDialogSheet, event);
            bottomSheet.show();
        } else {
            Log.e(TAG, "No event for selected marker! eventId " + event.getEventId());
        }
        return true;
    }

    private void startGeoCoderService(Location location) {
        progressDialog = TaskProgressDialog.show(getActivity(), getString(R.string.downloading), getString(R.string
                .downloading_events));
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(GeocoderConstants.RECEIVER, resultReceiver);
        intent.putExtra(GeocoderConstants.LOCATION_DATA_EXTRA, location);
        getActivity().startService(intent);
    }

    private void startDownloadingEventData(AddressJr address) {
        if (progressDialog == null) {
            progressDialog = TaskProgressDialog.show(getActivity(), getString(R.string.downloading), getString(R.string
                    .downloading_events));
        }
        GetEventTypesTask eventTypesTask = new GetEventTypesTask(null, getActivity(), this);
        eventTypesTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        getPublicEventsTask = new GetPublicEventsTask(null, getActivity(), this, address.getCountryCode(), address.getCity());
        getGroupEventsTask = new GetGroupEventsTask(null, getActivity(), this);

    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
            if (taskId == GetEventTypesTask.TASK_ID) {
                getPublicEventsTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else if (taskId == GetPublicEventsTask.TASK_ID) {
                getGroupEventsTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else if (taskId == GetGroupEventsTask.TASK_ID) {
                refreshMap();
                progressDialog.dismiss();
            }
        } else {
            progressDialog.dismiss();
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            AddressJr address = resultData.getParcelable(GeocoderConstants.RESULT_DATA_KEY);
            if (address != null) {
                UserData.saveLastKnownAddress(address);
                startDownloadingEventData(address);
            } else if (resultCode == GeocoderConstants.FAILURE_RESULT) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
