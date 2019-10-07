package com.pinit.pinitmobile.tasks.login;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.ui.dialog.TaskProgressDialog;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LogoutTask extends AbstractAsyncTask<Void, Void> {

    public static final int TASK_ID = 9;
    private ProgressDialog progressDialog;

    public LogoutTask(ProgressBar progressBar, FragmentActivity activity, AsyncTaskCallback requester) {
        super(progressBar, activity, null, requester, HttpMethod.PUT, Void.class);
        url = Globals.SERVER_URL + Globals.LOGOUT_URL + UserData.getToken().getToken();
    }

    @Override
    protected void onPreExecute() {
        progressDialog = TaskProgressDialog.show(activity, activity.getString(R.string.logout), activity.getString(R.string
                .logout_in_progress));
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        UserData.cleanUserData();
        LoginManager.getInstance().logOut();
        Toast.makeText(activity, activity.getString(R.string.user_logout), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void handleStatusCodes(HttpStatus httpStatus) {
        if (httpStatus.equals(HttpStatus.NOT_MODIFIED)) {
            Toast.makeText(activity, activity.getString(R.string.logout_failure), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void notifyRequester(int code) {
        progressDialog.dismiss();
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}
