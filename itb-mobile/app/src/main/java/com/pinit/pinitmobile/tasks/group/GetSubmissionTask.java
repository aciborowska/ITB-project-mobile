package com.pinit.pinitmobile.tasks.group;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.dao.UsersDao;
import com.pinit.pinitmobile.model.User;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

public class GetSubmissionTask extends AbstractAsyncTask<Void, User[]> {
    public static final int TASK_ID = 13;
    public static final int NO_USERS_CODE = -100;
    private boolean isUserListEmpty = false;

    public GetSubmissionTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester, long groupId) {
        super(progressBar, activity, null, requester, HttpMethod.GET, User[].class);
        url = Globals.SERVER_URL + Globals.GET_USERS_LIST_URL_1 + groupId + "/" + Globals.GET_USERS_LIST_URL_2 +
                UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        User[] users = (User[]) responseEntity.getBody();
        if (users.length == 0) {
            isUserListEmpty = true;
        } else {
            UsersDao.getInstance().addAllUsers(new ArrayList<>(Arrays.asList(users)));
        }
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            if (isUserListEmpty) requester.afterExecute(TASK_ID, NO_USERS_CODE);
            else requester.afterExecute(TASK_ID, code);
        }
    }
}
