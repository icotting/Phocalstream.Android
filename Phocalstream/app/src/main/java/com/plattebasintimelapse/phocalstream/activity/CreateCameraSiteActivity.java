package com.plattebasintimelapse.phocalstream.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.plattebasintimelapse.phocalstream.R;

public class CreateCameraSiteActivity extends Activity {

    private EditText siteNameField, siteLocationField;
    private MenuItem create;

    private String siteName;
    private double siteLat = -1, siteLong = -1;
    private String siteCounty, siteState;
    private Bitmap siteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_camera_site);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        this.siteLocationField = (EditText) findViewById(R.id.siteLocationField);
        this.siteNameField = (EditText) findViewById(R.id.siteNameField);

        this.siteNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CreateCameraSiteActivity.this.siteName = s.toString();
                CreateCameraSiteActivity.this.create.setEnabled(isSiteValid());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addsite, menu);

        this.create = menu.findItem(R.id.action_create_site);
        this.create.setEnabled(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_site:
                createCameraSite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFirstPhoto(View view) {
        Toast.makeText(CreateCameraSiteActivity.this, "AddFirstPhoto", Toast.LENGTH_SHORT).show();
    }

    public void createCameraSite() {
        Toast.makeText(CreateCameraSiteActivity.this, "Create Camera Site", Toast.LENGTH_SHORT).show();

    }

    // MARK: Functions
    private boolean isSiteValid() {
        return (!this.siteName.isEmpty() && this.siteLat != -1 && this.siteLong != -1 &&
                !this.siteCounty.isEmpty() && !this.siteState.isEmpty() && this.siteImage != null);
    }

}
