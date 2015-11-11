package com.plattebasintimelapse.phocalstream.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.plattebasintimelapse.phocalstream.adapters.CameraSiteAdapter;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.model.CameraSite;
import com.plattebasintimelapse.phocalstream.services.CameraSiteAsync;

import java.util.ArrayList;


public class CameraSitesActivity extends Activity {

    public static final String CAMERA_SITES_CACHE = "camera_site_cache";
    public static final String CAMERA_SITES_DOWNLOAD_TIME = "camera_site_download_time";
    private static final long CAMERA_SITES_CACHE_LENGTH = 3600000; // 1000 * 60 * 60 = 1 hour

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_sites);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        CameraSiteAdapter mAdapter = new CameraSiteAdapter(this, new ArrayList<CameraSite>());
        mRecyclerView.setAdapter(mAdapter);

        // Check the cache
        SharedPreferences prefs = getPreferences(0);
        if (System.currentTimeMillis() - prefs.getLong(CameraSitesActivity.CAMERA_SITES_DOWNLOAD_TIME, 0) > CameraSitesActivity.CAMERA_SITES_CACHE_LENGTH) {
            CameraSiteAsync cameraSiteAsync = new CameraSiteAsync(this, prefs, progressBar, mAdapter);
            cameraSiteAsync.execute();
        }
        else {
            ArrayList<CameraSite> sites = new Gson().fromJson(
                    prefs.getString(CameraSitesActivity.CAMERA_SITES_CACHE, ""),
                    new TypeToken<ArrayList<CameraSite>>() {}.getType());

            mAdapter.setSites(sites);
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacesItemDecoration() {
        this.space = 40;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if(parent.getChildAdapterPosition(view) == 0)
            outRect.top = space;
    }
}
