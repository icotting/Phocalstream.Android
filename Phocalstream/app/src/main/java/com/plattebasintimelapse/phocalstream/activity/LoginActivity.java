package com.plattebasintimelapse.phocalstream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.managers.AuthenticationManager;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    CallbackManager callbackManager;
    private ProgressBar progressBar;
    private AuthenticationManager authenticationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        authenticationManager = new AuthenticationManager(LoginActivity.this);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            authenticationManager.login(progressBar, accessToken.getToken());
        }

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                authenticationManager.login(progressBar, loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Facebook login cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Facebook Error: " + exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cameraSites(View view) {
        Intent i = new Intent(LoginActivity.this, CameraSitesActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

