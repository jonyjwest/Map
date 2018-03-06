package com.emedrep.reportthat;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedrep.reportthat.Fragments.FragmentCaptures;
import com.emedrep.reportthat.Fragments.FragmentFinder;
import com.emedrep.reportthat.Fragments.FragmentLanding;
import com.emedrep.reportthat.Fragments.FragmentSearch;
import com.emedrep.reportthat.Fragments.FragmentVisitedLocation;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Fragments.FragmentAbout;

import com.emedrep.reportthat.Library.Library;
import com.emedrep.watchdrug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import it.sephiroth.android.library.tooltip.Tooltip;

public class MasterActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "LocationActivity";
    NavigationView navigationView;
    SharedPreferences settings;
    String firstname;
    String lastname;
    String email;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView txt_skip;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // setTitle(" Report That!");
        setContentView(R.layout.activity_master);
        Intent successIntent=getIntent();
        String success=successIntent.getStringExtra("success");
        if(success!=null){

            Toast.makeText(this,"Report sent successfully",Toast.LENGTH_LONG).show();
        }

        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        editor = prefs.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setLogo(R.drawable.actionlogo);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
               // setTitle(" Watch Drug");
                //getSupportActionBar().setLogo(R.drawable.wdsm);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView shortName = (TextView) header.findViewById(R.id.txtSName);
        TextView txtEmail = (TextView) header.findViewById(R.id.txtEmail);
        TextView masterName = (TextView) header.findViewById(R.id.masterName);
        firstname=prefs.getString("FirstName",null);
        lastname=prefs.getString("LastName",null);
        email=prefs.getString("Email",null);
        masterName.setText(lastname+" "+firstname);
        txtEmail.setText(email);
      // menuItem.setTitle(lastname+" "+firstname);

        if(firstname!=null && lastname!=null){
            String xx= firstname.substring(0,1);
            String yy= lastname.substring(0,1);
            shortName.setText(String.valueOf(yy+xx));

        }
        Class fragmentClass;
        String pvsuccess=successIntent.getStringExtra("pvsuccess");
        if(pvsuccess!=null){
            fragmentClass = FragmentVisitedLocation.class;
        }
        else if(success!=null){
            fragmentClass = FragmentCaptures.class;
        }
        else{
            fragmentClass = FragmentLanding.class;
        }


        try {

            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            drawer.closeDrawer(GravityCompat.START);


        } catch (Exception e) {
            e.printStackTrace();
        }


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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

            drawer.setFocusableInTouchMode(false);
        } else {

            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.getBackStackEntryCount()>0){
                fragmentManager.popBackStack();
            }
            else{
                //super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent loginscreen=new Intent(MasterActivity.this,Login.class);
//                            SharedPreferences settings =  getSharedPreferences(Constant.PREFERENCE_NAME, 0);
//                            settings.edit().clear().commit();
                            //startActivity(loginscreen);
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                            sendBroadcast(broadcastIntent);
                            finish();


                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        }

    }





    @Override
    public void onDestroy()
    {
        android.os.Process.killProcess(android.os.Process.myPid());

        System.exit(0);
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
//        MenuItem item = menu.findItem(R.id.settings);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.menu_logout){
            // if (prefs.getString("Email", null) != null && prefs.getString("FirstName", null) != null) {
//          editor.remove("Email");
//          editor.remove("FirstName");
            editor.putBoolean("LogOut",true);
            editor.commit();
            Intent catIntent = new Intent(MasterActivity.this,Login.class);
                         startActivity(catIntent);
            overridePendingTransition(R.anim.right_to_left, R.anim.right);
           // Library.StartActivity(Login.this,MasterActivity.class);
            return true;
        }
        Fragment fragment = null;
        Class fragmentClass = null;


        if (id == R.id.menu_gallery) {

            fragmentClass = FragmentCaptures.class;
        }
        else if (id == R.id.menu_location) {

            fragmentClass = FragmentVisitedLocation.class;
        }


        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.nav_report) {

            fragmentClass = FragmentLanding.class;
        }
        else if (id == R.id.nav_visit) {

            fragmentClass = FragmentVisitedLocation.class;
        }

        else if (id == R.id.nav_gallery) {

            fragmentClass = FragmentCaptures.class;
         }
        else if (id == R.id.nav_pharm_finder) {

            fragmentClass = FragmentFinder.class;

        }
        else if (id == R.id.nav_search) {

            fragmentClass = FragmentSearch.class;

        } else if (id == R.id.nav_about) {
            fragmentClass = FragmentAbout.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
//        Fragment fragment = null;
//        try {
//            fragment = FragmentCaptures.class.newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        fragment.onActivityResult(requestCode, resultCode, data);
    //}
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
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();

        }
    }
    @Override
    public void onConnected(Bundle bundle) {
       // Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
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





}
