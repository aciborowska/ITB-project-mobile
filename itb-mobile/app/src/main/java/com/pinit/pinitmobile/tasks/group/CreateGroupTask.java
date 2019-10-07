package com.pinit.pinitmobile.tasks.group;


import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CreateGroupTask extends AbstractAsyncTask<Group, Long> {

    public static final int TASK_ID = 15;

    public CreateGroupTask(ProgressBar progressBar, FragmentActivity activity, Group objectToSend, AsyncTaskCallback requester) {
        super(progressBar, activity, objectToSend, requester, HttpMethod.POST, Long.class);
        url = Globals.SERVER_URL + Globals.CREATE_GROUP_URL + UserData.getToken().getToken();
    }

    @Override
    protected void onPostExecute(ResponseEntity<Long> responseEntity) {
        int code = -1;
        if (responseEntity != null) {
            code = responseEntity.getStatusCode().value();
            switch (responseEntity.getStatusCode()) {
                case CREATED: {
                    doOnSuccess(responseEntity);
                    break;
                }
                case FORBIDDEN: {
                    Toast.makeText(activity, R.string.token_expired, Toast.LENGTH_SHORT).show();
                    break;
                }
                default: {
                    handleStatusCodes(responseEntity.getStatusCode());
                }
            }
        } else {
            Toast.makeText(activity, R.string.connection_error, Toast.LENGTH_SHORT).show();
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        notifyRequester(code);
        Log.d(TAG, "finished " + url);
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Long id = (Long) responseEntity.getBody();
        objectToSend.setGroupId(id);
        App.getGroupsDao().save(objectToSend);
        Toast.makeText(activity, R.string.created_new_group, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(TASK_ID, code);
        }
    }

    @Override
    protected void handleStatusCodes(HttpStatus httpStatus) {
        if (httpStatus.equals(HttpStatus.CONFLICT)) {
            Toast.makeText(activity, activity.getString(R.string.group_with_name_already_exist), Toast.LENGTH_LONG).show();
        }
    }
}
