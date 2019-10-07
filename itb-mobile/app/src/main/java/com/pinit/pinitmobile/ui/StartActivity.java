package com.pinit.pinitmobile.ui;


import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.login.LogoutTask;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity implements AsyncTaskCallback{

    public static final String TAG = StartActivity.class.getName();

    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private CustomDrawerAdapter adapter;
    private List<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_start);

        dataList = new ArrayList<>();
        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        dataList.add(new DrawerItem(getString(R.string.events), R.drawable.ic_map_white_24dp));
        dataList.add(new DrawerItem(getString(R.string.my_account), R.drawable.ic_account_circle_white_24dp));
        dataList.add(new DrawerItem(getString(R.string.groups), R.drawable.ic_group_white_24dp));
        dataList.add(new DrawerItem(getString(R.string.my_events), R.drawable.ic_beenhere_white_24dp));
        dataList.add(new DrawerItem(getString(R.string.logout), R.drawable.ic_power_settings_new_white_24dp));

        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
                dataList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        MapFragment.isNewlyCreated = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }


    public void selectItem(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, Fragment.instantiate(StartActivity.this, MapFragment.TAG))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, Fragment.instantiate(StartActivity.this, MyAccountFragment.TAG))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, Fragment.instantiate(StartActivity.this, GroupListFragment.TAG))
                        .commit();
                break;
            case 3:
                break;
            case 4:
                LogoutTask logoutTask = new LogoutTask(null,this,this);
                logoutTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                break;
            default:
        }

        listView.setItemChecked(position, true);
        setTitle(dataList.get(position).getItemName());
        drawerLayout.closeDrawer(listView);
    }


    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(this.title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        if(taskId==LogoutTask.TASK_ID){
            if(httpStatusCode == Globals.EXECUTE_SUCCESS){
                MapFragment.isNewlyCreated=true;
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                this.finish();
            }
        }
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }
}
