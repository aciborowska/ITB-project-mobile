package com.pinit.pinitmobile.tasks.login;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.Token;
import com.pinit.pinitmobile.tasks.AbstractAsyncTask;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.util.Credentials;
import com.pinit.pinitmobile.util.UserData;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class LoginTask extends AbstractAsyncTask<Map<String, String>, Token> {

    public static final int TASK_ID = 9;

    public LoginTask(ProgressBar progressBar, FragmentActivity activity, Map<String, String> objectToSend, AsyncTaskCallback
            requester) {
        super(progressBar, activity, objectToSend, requester, HttpMethod.POST, Token.class);
        url = Globals.SERVER_URL + Globals.INTERNAL_LOGIN_URL;
    }

    @Override
    protected void onPostExecute(ResponseEntity<Token> responseEntity) {
        progressBar.setVisibility(View.GONE);
        int code = -1;
        if (responseEntity != null) {
            code = responseEntity.getStatusCode().value();
            switch (responseEntity.getStatusCode()) {
                case OK:
                    doOnSuccess(responseEntity);
                    break;
                case FORBIDDEN:
                    Toast.makeText(activity, activity.getString(R.string.incorrect_email_passwd), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(activity, activity.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, activity.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
        notifyRequester(code);
    }

    @Override
    protected void doOnSuccess(ResponseEntity responseEntity) {
        Credentials credentials = new Credentials(objectToSend.get("email"), objectToSend.get("password"));
        credentials.saveCredentials();
        Token token = (Token) responseEntity.getBody();
        UserData.saveToken(token);
        Toast.makeText(activity, activity.getString(R.string.user_login), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyRequester(int code) {
        if (requester != null) requester.afterExecute(TASK_ID, code);
    }
}