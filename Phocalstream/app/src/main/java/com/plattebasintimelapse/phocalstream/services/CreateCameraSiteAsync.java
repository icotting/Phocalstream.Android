package com.plattebasintimelapse.phocalstream.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.plattebasintimelapse.phocalstream.managers.RequestManager;

import java.util.HashMap;

public class CreateCameraSiteAsync extends AsyncTask<HashMap<String, String>, Integer, String[]> {

    private final Activity activity;
    private final ProgressBar progressBar;
    private final String filePath;

    private final RequestManager requestManager;

    public CreateCameraSiteAsync(Activity activity, ProgressBar progressBar, String filePath) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.filePath = filePath;

        this.requestManager = new RequestManager(this.activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        progressBar.setVisibility(View.INVISIBLE);

        String message;
        if (result[0].equals("200")) {
            message = "Site created and image upload.";
        }
        else {
            message = result[1];
        }
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show();
        this.activity.finish();
    }

    @SafeVarargs
    @Override
    protected final String[] doInBackground(HashMap<String, String>... params) {
        String url = "http://images.plattebasintimelapse.org/api/usercollection/CreateUserCameraSite";
        String[] result = this.requestManager.Post_Connection(url, params[0]);

        if(result[0].equals("200")) {
            long siteId = Long.parseLong(result[1]);
            String uploadUrl = String.format("http://images.plattebasintimelapse.org/api/upload/upload?selectedCollectionID=%d", siteId);
            result = this.requestManager.uploadImage(uploadUrl, this.filePath);
        }
        else {
            Log.d("Create Site", result[0] + ": " + result[1]);
        }
        return result;
    }
}
