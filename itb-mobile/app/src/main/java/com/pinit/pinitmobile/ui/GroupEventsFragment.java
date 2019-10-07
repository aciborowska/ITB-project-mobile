package com.pinit.pinitmobile.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.EventLocation;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.util.UserData;

import java.util.List;


public class GroupEventsFragment extends Fragment {

    private static final String TAG = GroupEventsFragment.class.getName();
    private long groupId;
    private static final String GROUP_ID = "groupId";

    public GroupEventsFragment() {
        // Required empty public constructor
    }

    public static GroupEventsFragment newInstance(Long id) {
        GroupEventsFragment fragment = new GroupEventsFragment();
        Bundle args = new Bundle();
        args.putLong(GROUP_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getLong(GROUP_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Group group = App.getGroupsDao().get(groupId);
        getActivity().setTitle(getString(R.string.events_for) + " " + group.getName());
        View v = inflater.inflate(R.layout.fragment_group_events, container, false);
        ListView eventsList = (ListView) v.findViewById(R.id.event_list);
        List<Event> events = App.getEventsDao()
                .getEventsByGroup(groupId);
        ArrayAdapter<Event> adapterEvent = new ArrayAdapter<>(getActivity(), R.layout.event_list_item,events);
        if (events.isEmpty()) {
            TextView noGroupEvents = (TextView) v.findViewById(R.id.no_group_events);
            noGroupEvents.setVisibility(View.VISIBLE);
        }
        eventsList.setAdapter(adapterEvent);
        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = (Event) parent.getItemAtPosition(position);
                EventLocation l = App.getLocationDao().get(e.getLocationId());
                MapFragment addEventFragment = MapFragment.newInstance(l.getLatitude(), l.getLongitude(), -1);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, addEventFragment).addToBackStack("EventList")
                        .commit();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_group_add_event, menu);
        if (App.getGroupsDao().get(groupId).getAdminId().equals(UserData.getToken().getUserId()))
            getActivity().getMenuInflater().inflate(R.menu.menu_group_admin, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin) {
            Intent intent = new Intent(getActivity(), GroupAdminActivity.class);
            intent.putExtra("groupId", groupId);
            getActivity().startActivity(intent);
        } else if (id == R.id.action_add_new_event_for_group) {
            LatLng lastKnownLocation = UserData.getUserLocation();
            MapFragment groupEventsFragment = MapFragment.newInstance(lastKnownLocation.latitude, lastKnownLocation.longitude,
                    groupId);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, groupEventsFragment).addToBackStack("GroupList")
                    .commit();
        } else Log.e(TAG, "No action for menu element");
        return super.onOptionsItemSelected(item);
    }
}
