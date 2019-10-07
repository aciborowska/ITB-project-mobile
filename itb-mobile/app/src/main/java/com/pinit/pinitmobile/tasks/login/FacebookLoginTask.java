package com.pinit.pinitmobile.tasks.login;

import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Token;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class FacebookLoginTask extends AbstractAsyncTask<Void, Token> {
    public static final int taskId = 3;

    public FacebookLoginTask(ProgressBar progressBar, FragmentActivity activity, String fbAccessToken, AsyncTaskCallback
            requester) {
        super(progressBar, activity, null, requester, HttpMethod.POST, Token.class);
        url = Globals.SERVER_URL + Globals.FACEBOOK_LOGIN_URL + fbAccessToken;
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Toast.makeText(activity, activity.getString(R.string.user_login), Toast.LENGTH_SHORT).show();
        Token token = (Token) responseEntity.getBody();
        UserData.saveToken(token);
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) {
            requester.afterExecute(taskId, code);
        }
    }
}
