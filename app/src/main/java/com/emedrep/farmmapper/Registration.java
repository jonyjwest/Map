package com.emedrep.farmmapper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.emedrep.farmmapper.Adapter.CoordinateAdapter;
import com.emedrep.farmmapper.DB.CoordinateDataSource;
import com.emedrep.farmmapper.Model.Coordinate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Registration extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LocationActivity";
    int num = 1;
    Button btnFusedLocation;
    TextView tvLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    TextView txtGps;

    LinearLayout lblSignal;
    ListView listViewCoordinate;
    Button btnSave;
    List<Coordinate> listCoordinate;
    CoordinateDataSource coordinateDataSource;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    EditText txtCurrentCordinate;
    private List<LinearLayout> linearlayoutList = new ArrayList<LinearLayout>();
    private List<EditText> editTextListStoreName = new ArrayList<EditText>();
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    int landId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocate);
        txtGps=(TextView)findViewById(R.id.gpsSignal);

        lblSignal=(LinearLayout)findViewById(R.id.lblSignal);
        txtGps.setText(0+"% (Weak)");
        txtGps.setTextColor(Color.RED);
        txtCurrentCordinate = (EditText) findViewById(R.id.txtCurrentCordinate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        listViewCoordinate=(ListView)findViewById(R.id.listViewCoordinate);
        coordinateDataSource =new CoordinateDataSource(this);
        Intent intent=getIntent();
         landId=intent.getIntExtra("landId",0);
        listCoordinate=coordinateDataSource.getAllCoordinateByLandId(landId);
        CoordinateAdapter coordinateAdapter=new CoordinateAdapter(this,R.layout.cell_coordinate_listview,listCoordinate);
        listViewCoordinate.setAdapter(coordinateAdapter);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    if( mCurrentLocation.getAccuracy()!=0 && mCurrentLocation.getAccuracy()>23 ){
                        Toast.makeText(Registration.this,"GPS signal is weak.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        if (null != mCurrentLocation) {
                            String lat = String.valueOf(mCurrentLocation.getLatitude());
                            String lng = String.valueOf(mCurrentLocation.getLongitude());
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                    .format(new Date());
                            coordinateDataSource.createCoordinate(landId,lat,lng,timeStamp );
                            listCoordinate=coordinateDataSource.getAllCoordinateByLandId(landId);
                            CoordinateAdapter coordinateAdapter=new CoordinateAdapter(Registration.this,R.layout.cell_coordinate_listview,listCoordinate);
                            listViewCoordinate.setAdapter(coordinateAdapter);
                        }
                    }

                }

            }
        });


    }

    private boolean validate() {
        boolean cancel = false;
        View focusView = null;
        txtCurrentCordinate.setError(null);
        String curentLocation = txtCurrentCordinate.getText().toString();
        if (TextUtils.isEmpty(curentLocation)) {
            txtCurrentCordinate.setError("Please make sure current location is shown before submitting");
            focusView = txtCurrentCordinate;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public int percentageAccuracy(float accuracy){
        if(accuracy>0 && accuracy<5){
            return 100;
        }
        if(accuracy>=5 && accuracy<7){
            return 95;
        }
        if(accuracy>=7 && accuracy<10){
            return 90;
        }
        if(accuracy>=10 && accuracy<13){
            return 85;
        }
        if(accuracy>=13 && accuracy<16){
            return 80;
        }
        if(accuracy>=16 && accuracy<20){
            return 75;
        }
        if(accuracy>=20 && accuracy<22){
            return 60;
        }
        if(accuracy>=22 && accuracy<23){
            return 55;
        }
        if(accuracy>=23 && accuracy<24){
            return 46;
        }
        if(accuracy>=24 && accuracy<26){
            return 40;
        }
        if(accuracy>=26 && accuracy<28){
            return 30;
        }
        if(accuracy>=28 && accuracy<35){
            return 20;
        }
        if(accuracy>=35 && accuracy<40){
            return 45;
        }
        if(accuracy>=40 && accuracy<60){
            return 40;
        }
        if(accuracy>=60 && accuracy<100){
            return 35;
        }
        if(accuracy>=100 && accuracy<200){
            return 30;
        }
        if(accuracy>=200 && accuracy<300){
            return 25;
        }
        if(accuracy>=300 && accuracy<500){
            return 20;
        }
        if(accuracy>=500 && accuracy<1000){
            return 15;
        }
        if(accuracy>=1000 && accuracy<2000){
            return 10;
        }
        return 0;
    }
    public void setGPS(float accuracy){
        if(accuracy!=0){
            float pAcc=percentageAccuracy(accuracy);
            if(accuracy<=15){
                txtGps.setText(pAcc+"% (Strong)");
                txtGps.setTextColor(Color.GREEN);


            }
            if(accuracy>15 && accuracy <=23){
                txtGps.setText(pAcc+"% (Medium)");
                txtGps.setTextColor(Color.BLUE);

            }
            if(accuracy>23){
                txtGps.setText(pAcc+"% (Weak)");
                txtGps.setTextColor(Color.RED);

            }

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
//        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {

        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            txtCurrentCordinate.setText(lat+", "+lng);
            setGPS(mCurrentLocation.getAccuracy());

    // Toast.makeText(this,"Testing: "+mCurrentLocation.getAccuracy()+" Provider: "+mCurrentLocation.getProvider()+" Accurate:"+prefs.getFloat("Accuracy",0),Toast.LENGTH_LONG).show();


        } else {
            Log.d(TAG, "location is null ...............");
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }


}


