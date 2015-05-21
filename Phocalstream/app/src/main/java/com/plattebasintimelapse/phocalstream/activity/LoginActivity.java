package com.plattebasintimelapse.phocalstream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.plattebasintimelapse.phocalstream.R;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        // TESTING
        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
    }

    public void cameraSites(View view) {
        Intent i = new Intent(LoginActivity.this, CameraSitesActivity.class);
        startActivity(i);
    }
}

