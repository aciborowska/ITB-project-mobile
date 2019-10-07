package com.pinit.pinitmobile.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.AddressJr;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.EventLocation;
import com.pinit.pinitmobile.model.EventType;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.service.FetchAddressIntentService;
import com.pinit.pinitmobile.service.GeocoderConstants;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.event.CreateEventTask;
import com.pinit.pinitmobile.util.TimeFormater;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEventFragment extends Fragment implements AsyncTaskCallback {

    public static final String TAG = AddEventFragment.class.getName();
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_GROUP_ID = "groupId";
    private Location location = null;
    private AddressResultReceiver resultReceiver;
    private AddressJr address;
    private static TextView startTime;
    private TextView addressTextView;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private Spinner typeSpinner;
    private Spinner groupSpinner;
    private CheckBox isPrivateEvent;
    private long groupId;
    private ProgressBar progressBar;

    public static AddEventFragment newInstance(double latitude, double longitude, long groupId) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        Log.d(TAG, latitude + " " + longitude);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    public AddEventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultReceiver = new AddressResultReceiver(new Handler());
        if (getArguments() != null) {
            location = new Location("reverseGeocoding");
            location.setLatitude(getArguments().getDouble(ARG_LATITUDE));
            location.setLongitude(getArguments().getDouble(ARG_LONGITUDE));
            groupId = getArguments().getLong(ARG_GROUP_ID, -1);
        }
        if (location != null) startIntentService();
        getActivity().setTitle(getString(R.string.title_add_event_fragment));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageButton timePicker = (ImageButton) getView().findViewById(R.id.time_picker_button);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        startTime = (TextView) getView().findViewById(R.id.time_start);
        startTime.setText(TimeFormater.getCurrentTime());
        addressTextView = (TextView) getView().findViewById(R.id.address_text_view);
        descriptionTextView = (TextView) getView().findViewById(R.id.event_description);
        nameTextView = (TextView) getView().findViewById(R.id.event_name);
        isPrivateEvent = (CheckBox) view.findViewById(R.id.is_private_event);
        if (groupId != -1) isPrivateEvent.setChecked(true);
        setupButtons();
        setupSpinners();
    }

    private void setupButtons() {
        setupAddEventButton();
        setupSearchButton();
    }

    private void setupAddEventButton() {
        Button add = (Button) getView().findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setError(null);
                addressTextView.setError(null);
                String name = nameTextView.getText().toString();
                if (name.isEmpty()) {
                    nameTextView.setError(getString(R.string.field_cannot_be_empty));
                    return;
                }
                String description = descriptionTextView.getText().toString();
                if (description.isEmpty()) description = getString(R.string.no_description);
                if (address == null) {
                    addressTextView.setError(getString(R.string.field_cannot_be_empty));
                    return;
                }
                EventLocation eventLocation = createEventLocation();
                Event event = new Event();
                event.setLocation(eventLocation);
                event.setName(name);
                event.setDescription(description);
                event.setStartDate(TimeFormater.stringDateToLong(startTime.getText().toString()));
                EventType type = (EventType) typeSpinner.getSelectedItem();
                event.setEventTypeId(type.getId());
                event.setAdminId(UserData.getToken().getUserId());
                Group g = (Group) groupSpinner.getSelectedItem();
                if (g.getGroupId() != -1) {
                    event.setGroupId(g.getGroupId());
                    if (isPrivateEvent.isChecked()) event.setisPrivate(true);
                } else event.setGroupId(null);
                CreateEventTask createEventTask = new CreateEventTask(progressBar, getActivity(), event, AddEventFragment.this,
                        HttpMethod.POST, Long.class);
                createEventTask.execute();
            }
        });
    }

    private EventLocation createEventLocation() {
        EventLocation eventLocation = new EventLocation();
        eventLocation.setLatitude((float) address.getLatitude());
        eventLocation.setLongitude((float) address.getLongitude());
        eventLocation.setCountry(address.getCountryCode());
        eventLocation.setCity(address.getCity());
        String street = address.getStreetName();
        if (address.getDisplayName() != null) street += address.getDisplayName();
        eventLocation.setStreet(street);
        return eventLocation;
    }

    private void setupSearchButton() {
        ImageButton searchLocation = (ImageButton) getView().findViewById(R.id.search_location_button);
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchLocationActivity.class);
                startActivityForResult(intent, SearchLocationActivity.SEARCH_LOCATION_ID);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == SearchLocationActivity.SEARCH_LOCATION_ID && resultCode == Activity.RESULT_OK) {
            address = data.getParcelableExtra(SearchLocationActivity.ADDRESS_JR_OBJECT);
            addressTextView.setText(address.toString());
        }
    }

    private void setupSpinners() {
        setupTypesSpinner();
        setupGroupSpinner();
    }

    private void setupTypesSpinner() {
        typeSpinner = (Spinner) getActivity().findViewById(R.id.type_spinner);
        ArrayAdapter<EventType> typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, App
                .getEventTypesDao().getAll());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    private void setupGroupSpinner() {
        groupSpinner = (Spinner) getActivity().findViewById(R.id.group_spinner);
        List<Group> groups = new ArrayList<>();
        Group group = new Group();
        group.setName(getString(R.string.no_group));
        group.setGroupId((long) -1);
        groups.add(group);
        groups.addAll(App.getGroupsDao().getAll());
        ArrayAdapter<Group> groupsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, groups);
        groupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupsAdapter);
        if (groupId != -1) {
            Group selectedGroup = new Group();
            selectedGroup.setGroupId(groupId);
            int position = groups.indexOf(selectedGroup);
            if (position != -1) {
                groupSpinner.setSelection(position);
            }
        }
    }

    private void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(GeocoderConstants.RECEIVER, resultReceiver);
        intent.putExtra(GeocoderConstants.LOCATION_DATA_EXTRA, location);
        getActivity().startService(intent);
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if (taskId == CreateEventTask.TASK_ID && httpStatusCode == Globals.EXECUTE_SUCCESS) {
            getFragmentManager().popBackStack();
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            address = resultData.getParcelable(GeocoderConstants.RESULT_DATA_KEY);
            if (address != null) addressTextView.setText(address.toString());
        }
    }


    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTime.setText(TimeFormater.timeToDateString(hourOfDay, minute));
        }
    }
}
