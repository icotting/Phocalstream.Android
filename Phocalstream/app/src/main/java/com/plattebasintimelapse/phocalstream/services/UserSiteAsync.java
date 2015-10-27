package com.plattebasintimelapse.phocalstream.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plattebasintimelapse.phocalstream.adapters.UserSitePagerAdapter;
import com.plattebasintimelapse.phocalstream.managers.RequestManager;
import com.plattebasintimelapse.phocalstream.model.UserSite;

import java.util.ArrayList;

/**
 * Created by ZachChristensen on 5/15/15.
 */
public class UserSiteAsync extends AsyncTask<Void, Integer, ArrayList<UserSite>> {

    private Context context;
    private ProgressBar progressBar;
    private UserSitePagerAdapter adapter;

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
        String url = "http://images.plattebasintimelapse.org/api/usercollection/usersites";
        ArrayList<UserSite> sites;

        String[] result = new RequestManager(this.context).Get_Connection(url);

        if(result[0].equals("200")) {
            Gson gson = new Gson();
            Log.d("Sites", result[1]);
            sites = gson.fromJson(result[1], new TypeToken<ArrayList<UserSite>>(){}.getType());
        }
        else {
            sites = new ArrayList<UserSite>();
            Log.d("Load sites", result[1]);
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
