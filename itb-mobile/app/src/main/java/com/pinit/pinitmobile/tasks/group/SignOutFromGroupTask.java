package com.pinit.pinitmobile.tasks.group;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class SignOutFromGroupTask extends AbstractAsyncTask<Void, Void> {
    public static final int TASK_ID = 16;
    private long groupId;

    public SignOutFromGroupTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester, long groupId) {
        super(progressBar, activity, null, requester, HttpMethod.PUT, Void.class);
        this.groupId = groupId;
        url = Globals.SERVER_URL + Globals.SIGNOUT_FROM_GROUP_URL_1 + groupId + "/" + Globals.SIGNOUT_FROM_GROUP_URL_2 +
                UserData.getToken().getUserId() + "/" + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        App.getGroupsDao().delete(App.getGroupsDao().get(groupId));
        Toast.makeText(activity, R.string.user_signout_from_group, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(TASK_ID, code);
        }
    }
}
