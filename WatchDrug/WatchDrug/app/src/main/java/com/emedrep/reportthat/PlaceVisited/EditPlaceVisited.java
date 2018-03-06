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

import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Fragments.FragmentMap;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.MasterActivity;
import com.emedrep.reportthat.Model.NameValue;
import com.emedrep.reportthat.Model.Visit;
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

public class EditPlaceVisited extends AppCompatActivity implements View.OnClickListener
       {
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
    Visit visitItem;

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
        setContentView(R.layout.activity_edit_place_visited);


        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
     //   imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
     //   imgRefresh.setOnClickListener(this);
        spnState = (Spinner) findViewById(R.id.spnState);
        spnLocalGovt = (Spinner) findViewById(R.id.spnLocalGovt);
        spnType=(Spinner)findViewById(R.id.spnType);
        edtName = (EditText) findViewById(R.id.edtName);
        edtDescription = (EditText) findViewById(R.id.edtAddress);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        visitDataSource = new VisitDataSource(this);
        Intent intent=getIntent();
        String visitId=intent.getStringExtra("visitId");
        if(visitId!=null){
            int visit=Integer.parseInt(visitId);
            visitItem=visitDataSource.getVisitById(visit);
            edtName.setText(visitItem.name);
            edtDescription.setText(visitItem.address);

        }
        loadStateCapital();
        loadType();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("  Edit this place");


        placeMap();


    }



    private void placeMap() {
        latValue = visitItem.getLatitude();
        longValue = visitItem.getLongitude();



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
       if (v == btnSave) {
            if (!validate()) {
                return;
            } else {
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy KK:mm a")
                        .format(new Date());
                Visit visit=new Visit();
                visit.stateId=selectedState.getValue();
                visit.lgaId=selectedLGA.getValue();
                visit.type=selectedType.getValue();
                visit.name=edtName.getText().toString();
                visit.address=edtDescription.getText().toString();
                visit.latitude=latValue;
                visit.longitude=longValue;
                visit.date=timeStamp;
               long success = visitDataSource.updateVisit(visitItem.visitId,visit);

                if (success > 0) {
                    Intent catIntent = new Intent(EditPlaceVisited.this, MasterActivity.class);
                  //  Intent catIntent = new Intent(PlaceVisit.this, MasterActivity.class);

                    catIntent.putExtra("pvsuccess", "2");

                    startActivity(catIntent);
//                    catIntent.putExtra("pvsuccess", "2");
                    catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(catIntent);
//                    overridePendingTransition(R.anim.right_to_left, R.anim.right);
                    Toast.makeText(this,"Vendor updated successfully",Toast.LENGTH_LONG).show();

                }
            }


        }

    }

    private void loadStateCapital() {
        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.state_lga);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final List<NameValue> stateList = new ArrayList<NameValue>();
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
                    loadLGA(String.valueOf(selectedState.getValue()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spnState.setSelection(getIndex(spnState,String.valueOf(visitItem.stateId)));
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
        final List<NameValue> lgaList = new ArrayList<NameValue>();
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
            spnLocalGovt.setSelection(getIndex(spnLocalGovt,String.valueOf(visitItem.lgaId)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            NameValue dss=(NameValue)spinner.getItemAtPosition(i);
            if (String.valueOf(dss.getValue()).equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
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
            spnType.setSelection(getIndex(spnType,String.valueOf(visitItem.type)));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





}
