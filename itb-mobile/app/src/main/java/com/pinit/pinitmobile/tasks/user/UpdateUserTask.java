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

public class UpdateUserTask extends AbstractAsyncTask<User, Void> {

    public static final int TASK_ID = 12;

    public UpdateUserTask(ProgressBar progressBar, FragmentActivity activity, User user, AsyncTaskCallback requester) {
        super(progressBar, activity, user, requester, HttpMethod.PUT, Void.class);
        url = Globals.SERVER_URL + Globals.UPDATE_USER_URL + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        UserData.saveUser(objectToSend);
        Toast.makeText(activity, activity.getString(R.string.changes_saved), Toast.LENGTH_SHORT);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(TASK_ID, code);
        }
    }
}