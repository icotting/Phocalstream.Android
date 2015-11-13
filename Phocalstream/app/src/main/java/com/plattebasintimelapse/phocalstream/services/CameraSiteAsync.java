package com.plattebasintimelapse.phocalstream.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plattebasintimelapse.phocalstream.activity.CameraSitesActivity;
import com.plattebasintimelapse.phocalstream.adapters.CameraSiteAdapter;
import com.plattebasintimelapse.phocalstream.managers.RequestManager;
import com.plattebasintimelapse.phocalstream.model.CameraSite;

import java.util.ArrayList;

public class CameraSiteAsync extends AsyncTask<Void, Integer, ArrayList<CameraSite>> {

    private final Context context;
    private final SharedPreferences prefs;
    private final ProgressBar progressBar;
    private final CameraSiteAdapter adapter;

    public CameraSiteAsync(Context context, SharedPreferences prefs, ProgressBar progressBar, CameraSiteAdapter adapter) {
        this.context = context;
        this.prefs = prefs;
        this.progressBar = progressBar;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<CameraSite> doInBackground(Void... voids) {
        ArrayList<CameraSite> sites = new ArrayList<>();

        String[] result = new RequestManager(this.context).Get_Connection("http://images.plattebasintimelapse.com/api/sitecollection/list");

        if(result[0].equals("200")) {
            sites = new Gson().fromJson(result[1], new TypeToken<ArrayList<CameraSite>>() {
            }.getType());

            this.prefs.edit()
                    .putString(CameraSitesActivity.CAMERA_SITES_CACHE, result[1])
                    .putLong(CameraSitesActivity.CAMERA_SITES_DOWNLOAD_TIME, System.currentTimeMillis())
                    .apply();
        }

        return sites;
    }

    @Override
    protected void onPostExecute(ArrayList<CameraSite> results) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.setSites(results);
        adapter.notifyDataSetChanged();
    }

}
