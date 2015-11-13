package com.plattebasintimelapse.phocalstream.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.plattebasintimelapse.phocalstream.activity.HomeActivity;
import com.plattebasintimelapse.phocalstream.managers.RequestManager;

public class AuthenticateAsync extends AsyncTask<String, Void, String[]> {

    private final Context context;
    private final ProgressBar progressBar;

    public AuthenticateAsync(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);

        progressBar.setVisibility(View.GONE);

        String title = null;
        String message = null;
        switch (Integer.parseInt(result[0])) {
            case 200:
                Intent i = new Intent(context, HomeActivity.class);
                context.startActivity(i);
                break;
            case 403:
                title = "Invalid Facebook access token";
                message = "Facebook failed to validate your login. Please try again.";
                break;
            case 401:
                title = "No Account";
                message = "You do not have a Phocalstream account. Visit the Phocalstream site to create one.";
                break;
            default:
                title = "Authentication error";
                message = "An error occurred verifying your account with Phocalstream.";
                break;
        }

        if (title != null) {
            showDialog(title, message);
        }
    }

    @Override
    protected String[] doInBackground(String... params) {
        String url = String.format("http://images.plattebasintimelapse.org/api/mobileclient/authenticate?fbToken=%s", params[0]);
        RequestManager requestManager = new RequestManager(this.context);
        return requestManager.Login(url);
    }


    private void showDialog(String title, String message) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        if (title.equals("No Account")) {
            builder.setNeutralButton("Create account", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://images.plattebasintimelapse.com/account/login")));
                }
            });
        }

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
