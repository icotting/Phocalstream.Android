package com.plattebasintimelapse.phocalstream.managers;

import android.content.Context;
import android.widget.ProgressBar;

import com.plattebasintimelapse.phocalstream.services.AuthenticateAsync;

/**
 * Created by ZachChristensen on 5/21/15.
 */
public class AuthenticationManager {

    private Context context;

    public AuthenticationManager(Context context) {
        this.context = context;
    }

    public void login(ProgressBar progressBar, String fbToken) {
        AuthenticateAsync authenticateAsync= new AuthenticateAsync(context, progressBar);
        authenticateAsync.execute(fbToken);
    }

 }
