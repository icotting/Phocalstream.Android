package com.plattebasintimelapse.phocalstream.activity;

import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.model.CameraSite;
import com.plattebasintimelapse.phocalstream.model.UserSite;
import com.plattebasintimelapse.phocalstream.services.FetchImageAsync;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CameraSiteDetails extends Activity {

    public static final String ARG_SITE = "site";
    private CameraSite site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_site_details);

        this.site = new Gson().fromJson(getIntent().getStringExtra(ARG_SITE), CameraSite.class);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle(String.format("%s Details", this.site.getDetails().getSiteName()));
        }

        // Image
        ImageView thumbnail = (ImageView) findViewById(R.id.camera_details_image);
        new FetchImageAsync(thumbnail).execute(site.getDetails().getCoverPhotoID());

        // Name and Details
        DateFormat dateFormat = DateFormat.getDateInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,###,###");

        Button onlineButton = (Button) findViewById(R.id.camera_details_button_online);
        TextView photoCount = (TextView) findViewById(R.id.camera_details_count);
        TextView photoDates = (TextView) findViewById(R.id.camera_details_date);

        onlineButton.setText(String.format("Browse %s Online", site.getDetails().getSiteName()));

        if (site.getDetails().getPhotoCount() == 1) {
            photoCount.setText(String.format("%,d Photo", site.getDetails().getPhotoCount()));
        }
        else {
            photoCount.setText(String.format("%,d Photos", site.getDetails().getPhotoCount()));
        }

        try {
            photoDates.setText(String.format("%s to %s",
                            dateFormat.format(simpleDateFormat.parse(site.getDetails().getFirst().split("T")[0])),
                            dateFormat.format(simpleDateFormat.parse(site.getDetails().getLast().split("T")[0]))
                    )
            );
        } catch (ParseException e) {
            photoDates.setText("");
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

    public void browseOnline(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse(String.format("http://images.plattebasintimelapse.com/photo/sitedashboard?siteId=%d", CameraSiteDetails.this.site.getDetails().getSiteID()))
        ));
    }

}
