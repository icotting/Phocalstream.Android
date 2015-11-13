package com.plattebasintimelapse.phocalstream.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.plattebasintimelapse.phocalstream.fragments.UserSiteFragment;
import com.plattebasintimelapse.phocalstream.model.UserSite;

import java.util.ArrayList;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class UserSitePagerAdapter extends FragmentStatePagerAdapter {

    private final Gson gson;
    private ArrayList<UserSite> sites = new ArrayList<>();

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
    public int getItemPosition(Object object) {
        super.getItemPosition(object);
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sites.get(position).getName();
    }

    public ArrayList<UserSite> getSites() {
        return sites;
    }

    public void setSites(ArrayList<UserSite> sites) {
        this.sites = sites;
    }
}
