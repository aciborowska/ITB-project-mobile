package com.pinit.pinitmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.util.UserData;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = WelcomeActivity.class.getName();
    private static final long GET_DATA_INTERVAL = 2000;
    int images[] = {R.drawable.test1, R.drawable.test2, R.drawable.test3};
    int index = 0;
    View img;
    Handler hand = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);

        if (!UserData.getToken().getToken().isEmpty()) {
            startMainActivity();
        }

        img = findViewById(R.id.background);
        hand.postDelayed(run, 0);
        setupLoginButton();
        setupRegisterButton();
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            img.setBackgroundResource(images[index++]);
            if (index == images.length) index = 0;
            hand.postDelayed(run, GET_DATA_INTERVAL);
        }
    };

    public static boolean isFbLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
    }


    private void setupLoginButton() {
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "token: " + UserData.getToken().getToken());
                if (!UserData.getToken().getToken().isEmpty()) {
                    Intent intent = new Intent(WelcomeActivity.this, StartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupRegisterButton() {
        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
