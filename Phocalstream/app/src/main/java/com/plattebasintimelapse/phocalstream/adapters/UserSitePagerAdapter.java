package com.plattebasintimelapse.phocalstream.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.gson.Gson;
import com.plattebasintimelapse.phocalstream.fragments.UserSiteFragment;
import com.plattebasintimelapse.phocalstream.model.UserSite;

import java.util.ArrayList;

/**
 * Created by ZachChristensen on 5/20/15.
 */
// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class UserSitePagerAdapter extends FragmentPagerAdapter {

    private Gson gson;
    private ArrayList<UserSite> sites = new ArrayList<UserSite>();

    public UserSitePagerAdapter(FragmentManager fm) {
        super(fm);
        gson = new Gson();
    }

    @Override
    public Fragment getItem(int i) {
        return UserSiteFragment.newInstance(i, gson.toJson(sites.get(i), UserSite.class));
    }

    @Override
    public int getCount() {
        return sites.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sites.get(position).getDetails().getSiteName();
    }

    public ArrayList<UserSite> getSites() {
        return sites;
    }

    public void setSites(ArrayList<UserSite> sites) {
        this.sites = sites;
    }
}