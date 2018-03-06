package com.emedrep.reportthat.PlaceVisited;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emedrep.reportthat.Db.VendorDataSource;
import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Fragments.FragmentMap;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.GPSTracker;
import com.emedrep.reportthat.MasterActivity;
import com.emedrep.reportthat.Model.NameValue;
import com.emedrep.reportthat.Service.VendorIntentService;
import com.emedrep.watchdrug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaceVisit extends AppCompatActivity implements View.OnClickListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    EditText edtName;
    VisitDataSource visitDataSource;
    EditText edtDescription;
    String latValue;
    String longValue;
    Button btnSave;
    ImageView imgRefresh;
    Spinner spnLocalGovt, spnState,spnType;
    private NameValue selectedState, selectedLGA,selectedType;

    private static final String TAG = "LocationActivity";

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView txtGps;
    final List<NameValue> lgaList=new ArrayList<NameValue>();

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
        visitDataSource = new VisitDataSource(this);
        setContentView(R.layout.activity_place_visit);
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
        imgRefresh.setOnClickListener(this);
        spnState = (Spinner) findViewById(R.id.spnState);
        spnLocalGovt = (Spinner) findViewById(R.id.spnLocalGovt);
        spnType=(Spinner)findViewById(R.id.spnType);
        edtName = (EditText) findViewById(R.id.edtName);
        edtDescription = (EditText) findViewById(R.id.edtAddress);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        loadStateCapital();
        loadType();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("  Add New Vendor");
       try {
           if (!isGooglePlayServicesAvailable()) {
               finish();
           }
           createLocationRequest();
           mGoogleApiClient = new GoogleApiClient.Builder(this)
                   .addApi(LocationServices.API)
                   .addConnectionCallbacks(this)
                   .addOnConnectionFailedListener(this)
                   .build();
           placeMap();
       }
       catch (Exception ex){

       }

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

    private void placeMap() {
        latValue = prefs.getString("Latitude", null);
        longValue = prefs.getString("Longitude", null);

        GPSTracker gpsTracker = new GPSTracker(this);
        if (!gpsTracker.getIsGPSTrackingEnabled()) {
            gpsTracker.showSettingsAlert();
        }
        if (longValue == null) {
            latValue = String.valueOf(gpsTracker.getLatitude());
            longValue = String.valueOf(gpsTracker.getLongitude());
        }
        if (longValue != null) {
            String b = latValue + "_" + longValue;
            Fragment xx = new FragmentMap();
            Bundle bundle = new Bundle();
            xx.setArguments(bundle);
            bundle.putString("cordinateMeta", b);
            getSupportFragmentManager().beginTransaction().add(R.id.flMapContent, xx).commit();

        } else {
            Toast.makeText(this, "Unable to get cordinate", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate() {
        boolean cancel = false;
        View focusView = null;
        edtName.setError(null);

        String name = edtName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name field is required");
            focusView = edtName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
        }
        return !cancel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == imgRefresh) {
            placeMap();
        } else if (v == btnSave) {
            if (!validate()) {
                return;
            } else {
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy KK:mm a")
                        .format(new Date());

                long success = visitDataSource.createVisit(selectedState.getValue(),selectedLGA.getValue(),selectedType.getValue(),edtName.getText().toString(), edtDescription.getText().toString(), latValue, longValue, timeStamp,0);

                if (success > 0) {
                    int visitId=Integer.parseInt(String.valueOf(success));
                    VendorDataSource vendorDataSource=new VendorDataSource(this);
                    long id= vendorDataSource.createVendor(visitId,selectedState.getValue(),selectedLGA.getValue(),selectedType.getValue(),edtName.getText().toString(),edtDescription.getText().toString(),latValue,longValue,timeStamp);

                   visitDataSource.updateVisit2(success,Integer.parseInt(String.valueOf(success)));
                    Intent mServiceIntent = new Intent(this, VendorIntentService.class);
                    startService(mServiceIntent);
                    Intent catIntent = new Intent(PlaceVisit.this, MasterActivity.class);

                    catIntent.putExtra("pvsuccess", "2");

                    startActivity(catIntent);

                }
            }


        }

    }

    private void loadStateCapital() {
        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.state_lga);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final List<NameValue> stateList = new ArrayList<NameValue>();
        NameValue mstate0 = new NameValue();
        mstate0.setName("-- Select State --");
        mstate0.setValue(0);
        stateList.add(mstate0);

        NameValue mlga = new NameValue();
        mlga.setName("-- Select Local Govt --");
        mlga.setValue(0);
        lgaList.add(mlga);
        final ArrayAdapter<NameValue> arrayAdapterLGA = new ArrayAdapter<NameValue>(this, R.layout.spinner_item, lgaList);
        arrayAdapterLGA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLocalGovt.setAdapter(arrayAdapterLGA);

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                String[] stateLGAArray = TextUtils.split(line, ";");

                String[] stateArray = TextUtils.split(stateLGAArray[0], ":");

                NameValue mstate = new NameValue();
                mstate.setName(stateArray[1]);
                mstate.setValue(Integer.parseInt(stateArray[0]));
                stateList.add(mstate);
            }
            ArrayAdapter<NameValue> arrayAdapter = new ArrayAdapter<NameValue>(this, R.layout.spinner_item, stateList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnState.setAdapter(arrayAdapter);
            spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                    selectedState = (NameValue) parent.getItemAtPosition(pos);
                    if(selectedState.getValue()>0){
                        loadLGA(String.valueOf(selectedState.getValue()));
                    }
                    else{
                        lgaList.clear();
                        NameValue mlga = new NameValue();
                        mlga.setName("-- Select Local Govt --");
                        mlga.setValue(0);
                        lgaList.add(mlga);
                        final ArrayAdapter<NameValue> arrayAdapterLGA = new ArrayAdapter<NameValue>(PlaceVisit.this, R.layout.spinner_item, lgaList);
                        arrayAdapterLGA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnLocalGovt.setAdapter(arrayAdapterLGA);
                    }





                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (IOException ex) {
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("done", "DONE loading words.");

    }

    public void loadLGA(String stateId) {
        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.state_lga);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
         //lgaList = new ArrayList<NameValue>();
        String line,selectedArr="";

        try {
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                String[] lineArr =TextUtils.split(line,":");
                String no=lineArr[0];
                if(no.equals(stateId)){
                    selectedArr=line;
                }

            }
                String[] stateLGAArray = TextUtils.split(selectedArr, ";");
                String[] lgaArray = TextUtils.split(stateLGAArray[1], ",");
                lgaList.clear();
               for (int i=0; i<lgaArray.length; i++){
                   String[] namevalue=TextUtils.split(lgaArray[i],":");
                   NameValue mstate = new NameValue();
                   mstate.setName(namevalue[1]);
                   mstate.setValue(Integer.parseInt(namevalue[0]));
                   lgaList.add(mstate);
               }

            ArrayAdapter<NameValue> arrayAdapter = new ArrayAdapter<NameValue>(this, R.layout.spinner_item, lgaList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLocalGovt.setAdapter(arrayAdapter);
            spnLocalGovt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   selectedLGA=(NameValue) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadType(){

        try {

            List<NameValue> mList=new ArrayList<>();
            NameValue mV0=new NameValue();
            mV0.setName("-Type-");
            mV0.setValue(0);
            mList.add(mV0);

            NameValue mV1=new NameValue();
            mV1.setName("Pharmacy");
            mV1.setValue(1);
            mList.add(mV1);

            NameValue mV2=new NameValue();
            mV2.setName("Patent Store");
            mV2.setValue(2);
            mList.add(mV2);

            NameValue mV3=new NameValue();
            mV3.setName("Super Market");
            mV3.setValue(3);
            mList.add(mV3);

            NameValue mV4=new NameValue();
            mV4.setName("Others");
            mV4.setValue(4);
            mList.add(mV4);

            ArrayAdapter<NameValue> arrayAdapter = new ArrayAdapter<NameValue>(this,R.layout.spinner_item, mList);

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnType.setAdapter(arrayAdapter);
            spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                    selectedType = (NameValue) parent.getItemAtPosition(pos);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        try {


        if (null != mCurrentLocation) {
            latValue = String.valueOf(mCurrentLocation.getLatitude());
            longValue = String.valueOf(mCurrentLocation.getLongitude());

            float accuracy=prefs.getFloat("Accuracy",0);
            if(accuracy==0){
                editor.putFloat("Accuracy",mCurrentLocation.getAccuracy());
                editor.putString("Latitude",latValue);
                editor.putString("Longitude",longValue);
                editor.commit();
            }
            else if(mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy()<accuracy){

                if(latValue!=null){
                    editor.putFloat("Accuracy",mCurrentLocation.getAccuracy());
                    editor.putString("Latitude",latValue);
                    editor.putString("Longitude",longValue);
                }


                editor.commit();
            }

            // Toast.makeText(this,"Testing: "+mCurrentLocation.getAccuracy()+" Provider: "+mCurrentLocation.getProvider()+" Accurate:"+prefs.getFloat("Accuracy",0),Toast.LENGTH_LONG).show();



        } else {
            //Log.d(TAG, "location is null ...............");
        } }
        catch (Exception ex){

        }
    }

}
