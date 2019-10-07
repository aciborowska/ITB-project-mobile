package com.pinit.pinitmobile.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.login.FacebookLoginTask;
import com.pinit.pinitmobile.tasks.user.GetUserDataTask;
import com.pinit.pinitmobile.tasks.user.GetUserPhotoBigTask;
import com.pinit.pinitmobile.tasks.login.LoginTask;
import com.pinit.pinitmobile.util.MD5Encryption;
import com.pinit.pinitmobile.util.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements AsyncTaskCallback {

    public static final String TAG = LoginActivity.class.getName();
    private EditText emailView;
    private EditText passwordView;
    private ProgressBar progressBar;
    private CallbackManager callbackManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_login);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        setupFbLoginButton();
        setupLoginButton();
    }

    private void setupFbLoginButton() {
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook login successful");
                getFbUserEmail(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook login cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "Facebook login erorr: " + e.getMessage());
            }
        });
    }

    private void getFbUserEmail(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback
                () {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    String email = jsonObject.getString("email");
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    FacebookLoginTask facebookLoginTask = new FacebookLoginTask(progressBar, LoginActivity.this, accessToken
                            .getToken(), LoginActivity.this);
                    facebookLoginTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception " + e.getMessage());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setupLoginButton() {
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        if (checkCredentialsFormat(email, password)) {
            progressBar.setVisibility(View.VISIBLE);
            Map<String, String> registrationData = new HashMap<>();
            registrationData.put("email", email);
            registrationData.put("password", MD5Encryption.encrypt(password));
            LoginTask loginTask = new LoginTask(progressBar, this, registrationData, this);
            loginTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private boolean checkCredentialsFormat(String email, String password) {
        emailView.setError(null);
        passwordView.setError(null);
        return isEmailFormatCorrect(email) && isPasswordFormatValid(password);
    }

    private boolean isEmailFormatCorrect(String email) {
        View focusView;
        if (!Validation.isEmailFormatValid(email)) {
            emailView.setError(getString(R.string.wrong_email_format));
            focusView = emailView;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isPasswordFormatValid(String password) {
        View focusView;
        if (password.isEmpty()) {
            passwordView.setError(getString(R.string.field_cannot_be_empty));
            focusView = passwordView;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        Log.d(TAG, "afterExecute " + taskId);
        if (taskId == LoginTask.TASK_ID || taskId == FacebookLoginTask.taskId) {
            if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
                GetUserDataTask getUserDataTask = new GetUserDataTask(progressBar, this, this);
                getUserDataTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        } else if (taskId == GetUserDataTask.TASK_ID) {
            if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
                GetUserPhotoBigTask getUserPhotosTask = new GetUserPhotoBigTask(progressBar, this, this);
                getUserPhotosTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        } else if (taskId == GetUserPhotoBigTask.TASK_ID) {
            if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            }
        }
    }
}
