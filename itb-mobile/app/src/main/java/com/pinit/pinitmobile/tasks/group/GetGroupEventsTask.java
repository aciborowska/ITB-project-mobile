package com.pinit.pinitmobile.tasks.group;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class GetGroupEventsTask extends AbstractAsyncTask<Void, Group[]> {
    public static final int TASK_ID = 6;

    public GetGroupEventsTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester) {
        super(progressBar, activity, null, requester, HttpMethod.GET, Group[].class);
        url = Globals.SERVER_URL + Globals.GET_USERS_GROUPS_WITH_EVENTS + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Group[] groups = (Group[]) responseEntity.getBody();
        App.getGroupsDao().saveGroupsWithEvents(groups);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}
