package com.pinit.pinitmobile.tasks.group;


import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SignToGroupTask extends AbstractAsyncTask<Void, Void> {

    public static final int TASK_ID = 10;

    public SignToGroupTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester, String groupName) {
        super(progressBar, activity, null, requester, HttpMethod.PUT, Void.class);
        url = Globals.SERVER_URL + Globals.SIGNIN_TO_GROUP_URL_1 + groupName + "/" + Globals.SIGNIN_TO_GROUP_URL_2 +
                UserData.getToken().getToken();
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Toast.makeText(activity, activity.getString(R.string.submissionToGroupAdded), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }

    @Override
    protected void handleStatusCodes(HttpStatus httpStatus) {
        if (httpStatus.equals(HttpStatus.NOT_MODIFIED)) {
            Toast.makeText(activity, activity.getString(R.string.group_with_given_name_does_not_exist), Toast.LENGTH_SHORT)
                    .show();
        } else if (httpStatus.equals(HttpStatus.CONFLICT)) {
            Toast.makeText(activity, activity.getString(R.string.user_alredy_in_group), Toast.LENGTH_SHORT).show();
        }
    }
}
