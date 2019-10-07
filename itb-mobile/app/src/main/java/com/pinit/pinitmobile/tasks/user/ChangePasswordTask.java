package com.pinit.pinitmobile.tasks.user;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ChangePasswordTask extends AbstractAsyncTask<String, User> {

    public static final int TASK_ID = 1;

    public ChangePasswordTask(ProgressBar progressBar, FragmentActivity activity, String objectToSend, AsyncTaskCallback
            requester) {
        super(progressBar, activity, objectToSend, requester, HttpMethod.PUT, User.class);
        url = Globals.SERVER_URL + Globals.CHANGE_PASSWORD_URL + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Toast.makeText(activity, activity.getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(TASK_ID, code);
        }
    }

}
