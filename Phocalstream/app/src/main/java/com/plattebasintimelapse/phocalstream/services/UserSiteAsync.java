package com.plattebasintimelapse.phocalstream.services;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plattebasintimelapse.phocalstream.adapters.UserSitePagerAdapter;
import com.plattebasintimelapse.phocalstream.managers.RequestManager;
import com.plattebasintimelapse.phocalstream.model.UserSite;

import java.util.ArrayList;

public class UserSiteAsync extends AsyncTask<Void, Integer, ArrayList<UserSite>> {

    private final Context context;
    private final ProgressBar progressBar;
    private final UserSitePagerAdapter adapter;

    public UserSiteAsync(Context context, ProgressBar progressBar, UserSitePagerAdapter adapter) {
        this.context = context;
        this.progressBar = progressBar;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<UserSite> doInBackground(Void... voids) {
        ArrayList<UserSite> sites = new ArrayList<>();

        String[] result = new RequestManager(this.context).Get_Connection("http://images.plattebasintimelapse.org/api/usercollection/usersites");

        if(result[0].equals("200")) {
            sites = new Gson().fromJson(result[1], new TypeToken<ArrayList<UserSite>>() {}.getType());
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
