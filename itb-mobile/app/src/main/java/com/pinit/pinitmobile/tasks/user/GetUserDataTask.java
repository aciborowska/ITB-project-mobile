package com.pinit.pinitmobile.tasks.user;


import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class GetUserDataTask extends AbstractAsyncTask<Void, User> {

    public static final int TASK_ID = 5;

    public GetUserDataTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester) {
        super(progressBar, activity, null, requester, HttpMethod.GET, User.class);
        url = Globals.SERVER_URL + Globals.GET_USER_BASIC_DATA_URL + UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        User u = (User) responseEntity.getBody();
        UserData.saveUser(u);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}