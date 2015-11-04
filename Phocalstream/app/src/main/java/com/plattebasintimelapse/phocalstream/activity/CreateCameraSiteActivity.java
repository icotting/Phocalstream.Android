package com.plattebasintimelapse.phocalstream.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.services.CreateCameraSiteAsync;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateCameraSiteActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private Geocoder mGeocoder;
    private Map<String, String> stateAbbreviations;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String photoPath;

    private EditText siteLocationField;
    private MenuItem create;

    private String siteName = "";
    private double siteLat = -1, siteLong = -1;
    private String siteCounty = "", siteState = "";
    private Bitmap siteImage;
    private ImageView siteImagePreview;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_camera_site);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        buildGoogleApiClient();
        this.mGeocoder = new Geocoder(this);
        this.stateAbbreviations = createStateAbbreviationHash();

        this.siteLocationField = (EditText) findViewById(R.id.siteLocationField);

        EditText siteNameField = (EditText) findViewById(R.id.siteNameField);
        siteNameField.addTextChangedListener(new TextWatcher() {
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

        this.siteImagePreview = (ImageView) findViewById(R.id.siteImagePreview);

        this.progressBar = (ProgressBar) findViewById(R.id.siteProgressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadImagePreview(this.photoPath);
        }
    }


    public void addFirstPhoto(View view) {
        dispatchTakePictureIntent();
    }

    private void createCameraSite() {
        CreateCameraSiteAsync createCameraSiteAsync = new CreateCameraSiteAsync(this, this.progressBar, this.photoPath);

        HashMap<String, String> values = new HashMap<>();
        values.put("SiteName", this.siteName);
        values.put("Latitude", String.valueOf(this.siteLat));
        values.put("Longitude", String.valueOf(this.siteLong));
        values.put("County", this.siteCounty);
        values.put("State", this.siteState);

        //noinspection unchecked
        createCameraSiteAsync.execute(values);
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
            List<Address> addressList = mGeocoder.getFromLocation(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude(), 5);

            for (Address address : addressList) {
                if (address.getSubAdminArea() != null) {
                    this.siteCounty = address.getSubAdminArea();
                    this.siteState = this.stateAbbreviations.containsKey(address.getAdminArea()) ? this.stateAbbreviations.get(address.getAdminArea()) : "";
                    break;
                }
            }

            this.create.setEnabled(this.isSiteValid());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(10000);
        this.mLocationRequest.setFastestInterval(5000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, this);
    }

    private Map<String, String> createStateAbbreviationHash() {
        Map<String, String> states = new HashMap<>();
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
        Toast.makeText(CreateCameraSiteActivity.this,
                "Unable to connect to Google Services: " + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    // MARK: LocationListener

    @Override
    public void onLocationChanged(Location location) {
        this.mLastLocation = location;
        handleLocationUpdate();
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

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void loadImagePreview(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        this.siteImage = BitmapFactory.decodeFile(photoPath, options);

        int width = 500;
        int height = 500;

        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        this.siteImage  = BitmapFactory.decodeFile(photoPath, options);

        if (this.siteImage  != null) {
            if (this.siteImage .getWidth() > this.siteImage .getHeight())
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                this.siteImage  = Bitmap.createBitmap(this.siteImage , 0, 0, this.siteImage .getWidth(), this.siteImage .getHeight(), matrix, true);
            }
            this.siteImagePreview.setImageBitmap(this.siteImage);
            this.create.setEnabled(this.isSiteValid());
        } else {
            Toast.makeText(CreateCameraSiteActivity.this, "Image not found. Whoops!", Toast.LENGTH_SHORT).show();
        }
    }




}
