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
import android.widget.Toast;

import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.tasks.AsyncTaskCallback;
import com.pinit.pinitmobile.tasks.user.GetUserDataTask;
import com.pinit.pinitmobile.tasks.login.LoginTask;
import com.pinit.pinitmobile.util.MD5Encryption;
import com.pinit.pinitmobile.util.Validation;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements AsyncTaskCallback {

    private static final String TAG = RegisterActivity.class.getName();
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private ProgressBar progressBar;
    private RegisterTask registerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailView = (EditText) findViewById(R.id.email_register);
        passwordView = (EditText) findViewById(R.id.password_register);
        confirmPasswordView = (EditText) findViewById(R.id.confirm_password_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_register);
        Button register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();
        if (checkCredentialsFormat(email, password, confirmPassword)) {
            progressBar.setVisibility(View.VISIBLE);
            registerTask = new RegisterTask(email, password);
            registerTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private boolean checkCredentialsFormat(String email, String password, String confirmPassword) {
        return checkEmailFormat(email) && checkPasswordFormat(password, confirmPassword);
    }

    private boolean checkEmailFormat(String email) {
        if (!Validation.isEmailFormatValid(email)) {
            emailView.setError(getString(R.string.wrong_email_format));
            emailView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkPasswordFormat(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            passwordView.setError(getString(R.string.diffrent_passwords));
            passwordView.requestFocus();
            return false;
        }
        if (!Validation.isPasswordFormatValid(password)) {
            passwordView.setError(getString(R.string.wrong_password_format));
            passwordView.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void afterExecute(int taskId, int httpStatusCode) {
        Log.d(TAG, "afterExecute " + taskId);
        if (taskId == LoginTask.TASK_ID) {
            if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
                GetUserDataTask getUserDataTask = new GetUserDataTask(progressBar, this, this);
                getUserDataTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        } else if (taskId == GetUserDataTask.TASK_ID) {
            if (httpStatusCode == Globals.EXECUTE_SUCCESS) {
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
            }
        }
    }

    public class RegisterTask extends AsyncTask<Void, Void, ResponseEntity> {
        private final String TAG = RegisterTask.class.getName();
        private Map<String, String> registrationData = new HashMap<>();

        public RegisterTask(String email, String password) {
            registrationData.put("email", email);
            registrationData.put("password", MD5Encryption.encrypt(password));
        }

        @Override
        protected ResponseEntity doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String uri = Globals.SERVER_URL + Globals.REGISTER_URL;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(registrationData, headers);

            ResponseEntity response;
            try {
                response = restTemplate.exchange(uri, HttpMethod.POST, entity, ResponseEntity.class);
            } catch (HttpClientErrorException e) {
                Log.d(TAG, e.getMessage());
                response = new ResponseEntity(e.getStatusCode());
            }
            return response;
        }

        @Override
        protected void onPostExecute(ResponseEntity response) {
            registerTask = null;
            progressBar.setVisibility(View.GONE);
            if (response != null) {
                HttpStatus code = response.getStatusCode();
                if (code.equals(HttpStatus.OK)) {
                    LoginTask loginTask = new LoginTask(progressBar, RegisterActivity.this, registrationData, RegisterActivity
                            .this);
                    loginTask.executeOnExecutor(SERIAL_EXECUTOR);
                } else if (code.equals(HttpStatus.CONFLICT)) {
                    Toast.makeText(getApplicationContext(), getApplication().getString(R.string.user_alredy_exist), Toast
                            .LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getApplication().getString(R.string.connection_error), Toast
                            .LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getApplication().getString(R.string.connection_error), Toast
                        .LENGTH_LONG).show();
            }
        }
    }

}
