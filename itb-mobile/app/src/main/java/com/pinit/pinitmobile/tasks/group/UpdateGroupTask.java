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

public class UpdateGroupTask extends AbstractAsyncTask<Group,Void> {

    public static final int TASK_ID = 14;

    public UpdateGroupTask(ProgressBar progressBar, FragmentActivity activity, Group objectToSend, AsyncTaskCallback requester) {
        super(progressBar, activity, objectToSend, requester, HttpMethod.PUT, Void.class);
        url = Globals.SERVER_URL + Globals.UPDATE_GROUP_URL + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        App.getGroupsDao().update(objectToSend);
    }

    @Override
    protected void notifyRequester(int code) {
        if(requester!=null){
            requester.afterExecute(TASK_ID,code);
        }
    }
}
