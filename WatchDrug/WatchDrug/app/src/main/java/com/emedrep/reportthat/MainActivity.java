package com.emedrep.reportthat;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emedrep.reportthat.Fragments.FragmentCaptures;
import com.emedrep.reportthat.Fragments.FragmentSearch;
import com.emedrep.reportthat.Fragments.FragmentCamera;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.watchdrug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity  implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
    {


        LocationRequest mLocationRequest;
        GoogleApiClient mGoogleApiClient;
        Location mCurrentLocation;
        String mLastUpdateTime;

        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        TextView txtGps;

        LinearLayout lblSignal;
        int progressStatus = 0;
        private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
        private long FASTEST_INTERVAL = 2000; /* 2 sec */

        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Class fragmentClass = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_home:

                   fragmentClass = FragmentCamera.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                    return true;
                case R.id.navigation_dashboard:

                    fragmentClass = FragmentCaptures.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentClass = FragmentSearch.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                     } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
//        }
      //  setTitle("  Watch Drug");
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        editor = prefs.edit();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Fragment fragment = null;
        Class fragmentClass = null;

        fragmentClass = FragmentCamera.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }                    FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

        protected void startLocationUpdates() {

            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);
            // Request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

        @Override
        public void onStart() {
            super.onStart();
           // Log.d(TAG, "onStart fired ..............");
            mGoogleApiClient.connect();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();

            }
        }
        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
         //   Log.d(TAG, "Firing onLocationChanged..............................................");
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        private void updateUI() {
          //  Log.d(TAG, "UI update initiated .............");
            if (null != mCurrentLocation) {
                String lat = String.valueOf(mCurrentLocation.getLatitude());
                String lng = String.valueOf(mCurrentLocation.getLongitude());

                float accuracy=prefs.getFloat("Accuracy",0);
                if(accuracy==0){
                    editor.putFloat("Accuracy",mCurrentLocation.getAccuracy());
                    editor.putString("Latitude",lat);
                    editor.putString("Longitude",lng);
                    editor.commit();
                }
                else if(mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy()<accuracy){
                    editor.putFloat("Accuracy",mCurrentLocation.getAccuracy());
                    editor.putString("Latitude",lat);
                    editor.putString("Longitude",lng);

                    editor.commit();
                }

                // Toast.makeText(this,"Testing: "+mCurrentLocation.getAccuracy()+" Provider: "+mCurrentLocation.getProvider()+" Accurate:"+prefs.getFloat("Accuracy",0),Toast.LENGTH_LONG).show();



            } else {
                //Log.d(TAG, "location is null ...............");
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
          //  Log.d(TAG, "Location update stopped .......................");
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

    }
