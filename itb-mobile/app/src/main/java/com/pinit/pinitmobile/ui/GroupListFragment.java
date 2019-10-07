package com.pinit.pinitmobile.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.group.CreateGroupTask;
import com.pinit.pinitmobile.tasks.group.SignOutFromGroupTask;
import com.pinit.pinitmobile.tasks.group.SignToGroupTask;
import com.pinit.pinitmobile.util.UserData;

import java.util.List;

public class GroupListFragment extends Fragment implements AsyncTaskCallback {

    public static final String TAG = GroupListFragment.class.getName();
    private List<Group> groupList;
    private ListView groupListView;
    private GroupAdapter groupAdapter;
    private ProgressBar progressBar;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.groups);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        groupList = App.getGroupsDao().getAll();
        groupListView = (ListView) v.findViewById(R.id.group_list);
        groupAdapter = new GroupAdapter(groupList, getActivity());
        groupListView.setAdapter(groupAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupEventsFragment groupEventsFragment = GroupEventsFragment.newInstance(id);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, groupEventsFragment).addToBackStack("GroupList")
                        .commit();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_groups, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_group:
                createInputDialog(getString(R.string.create_group), getString(R.string.write_group_name), R.id
                        .action_create_group);
                break;
            case R.id.action_signin:
                createInputDialog(getString(R.string.signin_to_group), getString(R.string.write_group_name), R.id.action_signin);
                break;
            default:
                Log.e(TAG, "Brak akcji dla elementu menu.");
        }

        return super.onOptionsItemSelected(item);
    }

    private void createInputDialog(String title, String message, final int actionId) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        final EditText input = new EditText(getActivity());
        input.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setMessage(message).setCancelable(false).setPositiveButton(getString(R.string.ok), new
                DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String groupName = input.getText().toString();
                if (actionId == R.id.action_create_group) {
                    runCreateGroupTask(groupName);
                } else if (actionId == R.id.action_signin) {
                    runSignToGroupTask(groupName);
                }
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog.findViewById(titleDividerId);
        if (titleDivider != null) titleDivider.setBackgroundColor(getResources().getColor(R.color.dark_green));

    }

    private void runSignToGroupTask(String groupName) {
        SignToGroupTask signToGroupTask = new SignToGroupTask(progressBar, getActivity(), this, groupName);
        signToGroupTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void runCreateGroupTask(String groupName) {
        Group g = new Group();
        g.setName(groupName);
        g.setAdminId(UserData.getToken().getUserId());
        g.setCreatedDate(System.currentTimeMillis());
        CreateGroupTask createGroupTask = new CreateGroupTask(progressBar, getActivity(), g, this);
        createGroupTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void showConfirmationDialog(final long groupId) {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getActivity()).setTitle(getString(R.string
                .leave_group)).setMessage(getString(R.string.do_you_really_want_to_leave_group)).setIcon(R.drawable
                .ic_clear_green_36dp).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                runSignOutFromGroupTask(groupId);
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        dialog.show();
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null) titleDivider.setBackgroundColor(getResources().getColor(R.color.dark_green));

    }

    private void runSignOutFromGroupTask(long groupId) {
        SignOutFromGroupTask signOutFromGroupTask = new SignOutFromGroupTask(progressBar, getActivity(), this, groupId);
        signOutFromGroupTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void refreshData() {
        if (groupListView != null) groupList.clear();
        groupList.addAll(App.getGroupsDao().getAll());
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if ((taskId == CreateGroupTask.TASK_ID && httpStatusCode == Globals.EXECUTE_CREATED) || (taskId == SignOutFromGroupTask
                .TASK_ID && httpStatusCode == Globals.EXECUTE_SUCCESS)) {
            refreshData();
        }
    }


    private class GroupAdapter extends BaseAdapter implements View.OnClickListener {
        private final List<Group> groups;
        private final Context ctx;

        private GroupAdapter(List<Group> groups, Context ctx) {
            this.groups = groups;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return groups.size();
        }

        @Override
        public Group getItem(int position) {
            return groups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getGroupId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.group_list_item, parent, false);
            }
            TextView teamName = (TextView) v.findViewById(R.id.group_name);
            ImageButton signoutButton = (ImageButton) v.findViewById(R.id.group_signout);

            signoutButton.setOnClickListener(this);
            signoutButton.setTag(R.id.group_signout, position);
            Group group = getItem(position);
            teamName.setText(group.getName());
            return v;
        }

        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag(R.id.group_signout);
            showConfirmationDialog(groups.get(position).getGroupId());
        }
    }
}


