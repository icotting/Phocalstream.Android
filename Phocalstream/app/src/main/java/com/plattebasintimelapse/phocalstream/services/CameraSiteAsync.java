package com.plattebasintimelapse.phocalstream.services;

import android.graphics.Camera;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plattebasintimelapse.phocalstream.CameraSiteAdapter;
import com.plattebasintimelapse.phocalstream.model.CameraSite;

import java.util.ArrayList;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class CameraSiteAsync extends AsyncTask<Void, Integer, ArrayList<CameraSite>> {

    private ProgressBar progressBar;
    private CameraSiteAdapter adapter;

    public CameraSiteAsync(ProgressBar progressBar, CameraSiteAdapter adapter) {
        this.progressBar = progressBar;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<CameraSite> doInBackground(Void... voids) {
        String url = "http://images.plattebasintimelapse.com/api/sitecollection/list";
        ArrayList<CameraSite> sites;

        String[] result = RequestManager.Get_Connection(url);

        if(result[0].equals("200")) {
            Gson gson = new Gson();
            sites = gson.fromJson(result[1], new TypeToken<ArrayList<CameraSite>>(){}.getType());
        }
        else {
            sites = new ArrayList<CameraSite>();
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
