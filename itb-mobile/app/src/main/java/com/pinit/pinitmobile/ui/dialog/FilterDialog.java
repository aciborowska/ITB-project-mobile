package com.pinit.pinitmobile.ui.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.EventType;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.ui.MapFragment;
import com.pinit.pinitmobile.util.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class FilterDialog extends Dialog {

    private final static String TAG = FilterDialog.class.getName();
    private Activity activity;
    private ListView groupList;
    private ListView typeList;
    private ArrayAdapter<Group> groupAdapter;
    private ArrayAdapter<EventType> typeAdapter;
    private EventFilter eventFilter;

    public FilterDialog(Activity activity, MapFragment fragment, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
        eventFilter = fragment;
        View view = getLayoutInflater().inflate(R.layout.filter_dialog, null);
        setupDialog(view);
    }

    private void setupDialog(View view) {
        setContentView(view);
        setCancelable(true);
        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.RIGHT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setupLists(view);
        setupButtons(view);
    }

    private void setupLists(View view) {
        setupGroupList(view);
        setupTypeList(view);
    }

    private void setupGroupList(View view) {
        groupList = (ListView) view.findViewById(R.id.groupListView);
        groupList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        groupList.setItemsCanFocus(false);
        groupAdapter = new ArrayAdapter<>(activity, R.layout.checkbox_textview, App.getGroupsDao().getAll());
        groupList.setAdapter(groupAdapter);
        setSelectedGroups();
    }

    private void setupTypeList(View view) {
        typeList = (ListView) view.findViewById(R.id.typeListView);
        typeList.setItemsCanFocus(false);
        typeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        typeAdapter = new ArrayAdapter<>(activity, R.layout.checkbox_textview, App.getEventTypesDao().getAll());
        typeList.setAdapter(typeAdapter);
        setSelectedTypes();
    }

    private void setSelectedGroups() {
        List<String> ids = UserPreferences.getCollection(Globals.SELECTED_GROUPS);
        for (int i = 0; i < groupAdapter.getCount(); i++) {
            if (ids.contains(String.valueOf(groupAdapter.getItem(i).getGroupId()))) {
                groupList.setItemChecked(i, true);
            }
        }
        groupAdapter.notifyDataSetChanged();
    }

    private void setSelectedTypes() {
        List<String> ids = UserPreferences.getCollection(Globals.SELECTED_TYPES);
        for (int i = 0; i < typeAdapter.getCount(); i++) {
            if (ids.contains(String.valueOf(typeAdapter.getItem(i).getId()))) {
                typeList.setItemChecked(i, true);
            }
        }
        typeAdapter.notifyDataSetChanged();
    }


    private void setupButtons(View view) {
        Button ok = (Button) view.findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPreferences.saveCollection(getSelectedTypes(), Globals.SELECTED_TYPES);
                UserPreferences.saveCollection(getSelectedGroups(), Globals.SELECTED_GROUPS);
                eventFilter.refreshMap();
                dismiss();
            }
        });
    }

    private List<String> getSelectedGroups() {
        List<String> selectedGroups = new ArrayList<>();
        SparseBooleanArray sparseBooleanArray = groupList.getCheckedItemPositions();
        for (int i = 0; i < groupList.getCount(); i++) {
            if (sparseBooleanArray.get(i) == true) {
                Group g = (Group) groupList.getItemAtPosition(i);
                selectedGroups.add(String.valueOf(g.getGroupId()));
            }
        }
        return selectedGroups;
    }

    private List<String> getSelectedTypes() {
        List<String> selectedTypes = new ArrayList<>();
        SparseBooleanArray sparseBooleanArray = typeList.getCheckedItemPositions();
        for (int i = 0; i < typeList.getCount(); i++) {
            if (sparseBooleanArray.get(i) == true) {
                EventType type = (EventType) typeList.getItemAtPosition(i);
                selectedTypes.add(String.valueOf(type.getId()));
            }
        }
        return selectedTypes;
    }

    public interface EventFilter{
        void refreshMap();
    }
}
