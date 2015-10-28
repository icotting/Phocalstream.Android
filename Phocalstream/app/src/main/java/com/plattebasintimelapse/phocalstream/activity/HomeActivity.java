package com.plattebasintimelapse.phocalstream.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.adapters.UserSitePagerAdapter;
import com.plattebasintimelapse.phocalstream.services.CameraSiteAsync;
import com.plattebasintimelapse.phocalstream.services.UserSiteAsync;

public class HomeActivity extends FragmentActivity {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    UserSitePagerAdapter userSitePagerAdapter;
    ViewPager mViewPager;

    private ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar actionBar = getActionBar();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        userSitePagerAdapter = new UserSitePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(userSitePagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                actionBar.setTitle(userSitePagerAdapter.getPageTitle(position));
            }
        });

        UserSiteAsync userSiteAsync = new UserSiteAsync(this, progressBar, userSitePagerAdapter);
        userSiteAsync.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_photo:
                handleUploadNewImage();
                return true;
            case R.id.action_add_site:
                handleNewSite();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void uploadNewImage(View view) {
        handleUploadNewImage();
    }

    private void handleUploadNewImage() {
        Toast.makeText(HomeActivity.this, "Upload new image for " + userSitePagerAdapter.getSites().get(mViewPager.getCurrentItem()).getName(), Toast.LENGTH_SHORT).show();
    }

    private void handleNewSite() {
        startActivity(new Intent(this, CreateCameraSiteActivity.class));
    }

    private void logout() {
        LoginManager.getInstance().logOut();
        HomeActivity.this.finish();
    }
}

