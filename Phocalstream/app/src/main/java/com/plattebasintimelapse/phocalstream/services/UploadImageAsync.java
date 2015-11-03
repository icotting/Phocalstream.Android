package com.plattebasintimelapse.phocalstream.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.plattebasintimelapse.phocalstream.managers.RequestManager;

import java.util.HashMap;

/**
 * Created by ZachChristensen on 10/30/15.
 */
public class UploadImageAsync extends AsyncTask<Long, Integer, String[]> {

    private Activity activity;
    private ProgressBar progressBar;
    private String filePath;

    private RequestManager requestManager;

    public UploadImageAsync(Activity activity, ProgressBar progressBar, String filePath) {
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
            message = "Image uploaded successfully.";
        }
        else {
            message = result[1];
        }
        Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String[] doInBackground(Long... params) {
        String uploadUrl = String.format("http://images.plattebasintimelapse.org/api/upload/upload?selectedCollectionID=%d", params[0]);
        Log.d("Photo Upload", uploadUrl);
        String[] result = this.requestManager.uploadImage(uploadUrl, this.filePath);
        return result;
    }
}
