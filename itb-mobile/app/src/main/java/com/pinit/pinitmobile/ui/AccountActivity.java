package com.pinit.pinitmobile.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.pinit.pinitmobile.R;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        long groupId = getGroupId();
        if(groupId!=-1) {
            MyAccountFragment fragment = MyAccountFragment.newInstance(groupId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.account_frame, fragment).commit();
        }
        else{
            finish();
        }
    }

    private long getGroupId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getLong("groupId");
        }
        return -1;
    }

}
