package com.pinit.pinitmobile.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
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
import android.widget.Toast;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.dao.UsersDao;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.group.GetSubmissionTask;
import com.pinit.pinitmobile.tasks.group.UpdateGroupTask;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GroupAdminActivity extends AppCompatActivity implements AsyncTaskCallback {

    private long groupId;
    private TextView infoNoNewEntries;
    private ListView usersListView;
    private List<User> usersList;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_admin);
        handleIntent();
        setTitle("Administrator - " + App.getGroupsDao().get(groupId).getName());
        infoNoNewEntries = (TextView) findViewById(R.id.info_no_entries);
        usersListView = (ListView) findViewById(R.id.users_list);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        usersList = UsersDao.getInstance().getUsers();
        usersAdapter = new UsersAdapter(UsersDao.getInstance().getUsers(), this);
        usersListView.setAdapter(usersAdapter);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupAdminActivity.this, AccountActivity.class);
                intent.putExtra("groupId", id);
                startActivity(intent);
            }
        });
        runGetSubmissionTask();
    }

    private void runGetSubmissionTask() {
        GetSubmissionTask getSubmissionTask = new GetSubmissionTask(progressBar, this, this, groupId);
        getSubmissionTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void handleIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupId = extras.getLong("groupId");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_name) {
            showEditNameDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoNoEntries() {
        infoNoNewEntries.setVisibility(View.VISIBLE);
    }

    private void showEditNameDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.change_group_name));
        final EditText input = new EditText(this);
        input.setTextColor(ContextCompat.getColor(this, R.color.white));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setMessage(getString(R.string.write_new_group_name)).setCancelable(false).setPositiveButton
                (getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String groupName = input.getText().toString();
                Group g = App.getGroupsDao().get(groupId);
                g.setName(groupName);
                UpdateGroupTask updateGroupTask = new UpdateGroupTask(progressBar, GroupAdminActivity.this, g,
                        GroupAdminActivity.this);
                updateGroupTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

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

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if (taskId == GetSubmissionTask.TASK_ID) {
            if (httpStatusCode == GetSubmissionTask.NO_USERS_CODE) {
                showInfoNoEntries();
            } else if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
                refreshData();
            }
        } else if (taskId == UpdateGroupTask.TASK_ID && httpStatusCode == Globals.EXECUTE_SUCCESS) {
            setTitle("Administrator - " + App.getGroupsDao().get(groupId).getName());
        }
    }

    private class UsersAdapter extends BaseAdapter {

        private List<User> users;
        private Context ctx;

        private UsersAdapter(List<User> users, Context ctx) {
            this.users = users;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public User getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getUserId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.user_list_item, parent, false);
            }
            final User u = getItem(position);
            TextView name = (TextView) v.findViewById(R.id.user_name);
            name.setText(getValidUsername(u));
            final ImageButton accept = (ImageButton) v.findViewById(R.id.accept_user);
            accept.setTag(R.id.accept_user, position);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AcceptUserTask acceptUserTask = new AcceptUserTask(progressBar, GroupAdminActivity.this, groupId, u
                            .getUserId(), position);
                    acceptUserTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                }
            });
            ImageButton decline = (ImageButton) v.findViewById(R.id.decline_user);
            decline.setTag(R.id.decline_user, position);
            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeclineUserTask declineUserTask = new DeclineUserTask(progressBar, GroupAdminActivity.this, groupId, u
                            .getUserId(), position);
                    declineUserTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
            });
            return v;
        }

        private String getValidUsername(User user) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) return user.getUsername();
            return user.getEmail();
        }
    }

    private void refreshData() {
        if (usersListView != null) {
            usersList.clear();
            usersList.addAll(UsersDao.getInstance().getUsers());
            usersAdapter.notifyDataSetChanged();
        }
    }

    private void removeListItem(int position) {
        usersList.remove(position);
        if (usersList.size() == 0) showInfoNoEntries();
        usersAdapter.notifyDataSetChanged();
    }

    public class AcceptUserTask extends AbstractAsyncTask<Void, Void> {

        int position;

        public AcceptUserTask(ProgressBar progressBar, FragmentActivity activity, long groupId, long userId, int position) {
            super(progressBar, activity, null, null, HttpMethod.PUT, Void.class);
            url = Globals.SERVER_URL + Globals.ACCEPT_TO_GROUP_URL_1 + groupId + "/" + Globals.ACCEPT_TO_GROUP_URL_2 + userId +
                    "/" + UserData.getToken().getToken();
            this.position = position;
        }

        @Override
        protected void doOnSuccess(ResponseEntity responseEntity) {
            removeListItem(position);
            Toast.makeText(activity, activity.getString(R.string.user_accepted), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void notifyRequester(int code) {
        }
    }

    public class DeclineUserTask extends AbstractAsyncTask<Void, Void> {

        int position;

        public DeclineUserTask(ProgressBar progressBar, FragmentActivity activity, long groupId, long userId, int position) {
            super(progressBar, activity, null, null, HttpMethod.PUT, Void.class);
            url = Globals.SERVER_URL + Globals.DECLINE_USER_URL_1 + groupId + "/" + Globals.DECLINE_USER_URL_2 + userId + "/" +
                    UserData.getToken().getToken();
            this.position = position;
        }

        @Override
        protected void doOnSuccess(ResponseEntity responseEntity) {
            removeListItem(position);
            Toast.makeText(activity, activity.getString(R.string.user_declined), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void notifyRequester(int code) {
        }
    }
}
