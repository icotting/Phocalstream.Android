package com.plattebasintimelapse.phocalstream.services;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plattebasintimelapse.phocalstream.adapters.UserSitePagerAdapter;
import com.plattebasintimelapse.phocalstream.model.UserSite;

import java.util.ArrayList;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class UserSiteAsync extends AsyncTask<Void, Integer, ArrayList<UserSite>> {

    private ProgressBar progressBar;
    private UserSitePagerAdapter adapter;

    public UserSiteAsync(ProgressBar progressBar, UserSitePagerAdapter adapter) {
        this.progressBar = progressBar;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<UserSite> doInBackground(Void... voids) {
        String url = "http://images.plattebasintimelapse.com/api/sitecollection/list";
        ArrayList<UserSite> sites;

        String[] result = RequestManager.Get_Connection(url);

        if(result[0].equals("200")) {
            Gson gson = new Gson();
            sites = gson.fromJson(result[1], new TypeToken<ArrayList<UserSite>>(){}.getType());
        }
        else {
            sites = new ArrayList<UserSite>();
        }

        return sites;
    }

    @Override
    protected void onPostExecute(ArrayList<UserSite> results) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.setSites(results);
        adapter.notifyDataSetChanged();
    }

}
