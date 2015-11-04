package com.plattebasintimelapse.phocalstream.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.adapters.UserSitePagerAdapter;
import com.plattebasintimelapse.phocalstream.model.UserSite;
import com.plattebasintimelapse.phocalstream.services.UploadImageAsync;
import com.plattebasintimelapse.phocalstream.services.UserSiteAsync;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends FragmentActivity {

    private UserSiteAsync userSiteAsync;
    private UserSitePagerAdapter userSitePagerAdapter;
    private ViewPager mViewPager;

    private ProgressBar progressBar;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String photoPath;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        userSitePagerAdapter = new UserSitePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(userSitePagerAdapter);

        this.userSiteAsync = new UserSiteAsync(this, progressBar, userSitePagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            UserSite site = HomeActivity.this.userSitePagerAdapter.getSites().get(this.mViewPager.getCurrentItem());
            UploadImageAsync uploadImageAsync = new UploadImageAsync(this, this.progressBar, this.photoPath);
            uploadImageAsync.execute(site.getCollectionID());
        }
    }

    private void handleUploadNewImage() {
        dispatchTakePictureIntent();
    }

    private void handleNewSite() {
        startActivity(new Intent(this, CreateCameraSiteActivity.class));
    }

    private void logout() {
        LoginManager.getInstance().logOut();
        HomeActivity.this.finish();
    }

    // MARK: Camera Functionality
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile;
            try {
                photoFile = this.createImageFile();

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
            catch (IOException ex) {
                Log.d("Photo bug: ", ex.toString());
                Toast.makeText(this, "Error accessing camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        this.photoPath = image.getAbsolutePath();
        return image;
    }


}

