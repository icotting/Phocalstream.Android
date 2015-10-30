package com.plattebasintimelapse.phocalstream.activity;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.plattebasintimelapse.phocalstream.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCameraSiteActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private Geocoder mGeocoder;
    private Map<String, String> stateAbbreviations;

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

        Log.d("Google Services", "getting ready to create client...");

        buildGoogleApiClient();
        this.mGeocoder = new Geocoder(this);
        this.stateAbbreviations = createStateAbbreviationHash();

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
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
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

    private void handleLocationUpdate() {
        this.siteLat = mLastLocation.getLatitude();
        this.siteLong = mLastLocation.getLongitude();
        this.siteLocationField.setText(String.format("( %f, %f )", this.siteLat, this.siteLong));

        try {
            List<Address> addressList = mGeocoder.getFromLocation(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude(), 1);

            this.siteCounty = addressList.get(0).getLocality();
            this.siteState = this.stateAbbreviations.containsKey(addressList.get(0).getAdminArea()) ? this.stateAbbreviations.get(addressList.get(0).getAdminArea()) : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(10000);
        this.mLocationRequest.setFastestInterval(5000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, this);
    }

    private Map<String, String> createStateAbbreviationHash() {
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","PQ");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");

        return states;
    }

    // MARK: Google Services Methods

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Google Services", "Connected...");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            handleLocationUpdate();
        }
        else {
            createLocationRequest();
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Google Services", connectionResult.toString());
    }

    // MARK: LocationListener

    @Override
    public void onLocationChanged(Location location) {
        this.mLastLocation = location;
        handleLocationUpdate();
    }




}
