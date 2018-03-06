package com.emedrep.reportthat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emedrep.reportthat.Adapter.CaptureAdapter;
import com.emedrep.reportthat.Adapter.VisitAdapter2;
import com.emedrep.reportthat.Db.PlacesVirtualDb;
import com.emedrep.reportthat.Db.ReportSql;
import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.reportthat.Model.NameValue;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.reportthat.Model.Visit;

import com.emedrep.reportthat.PlaceVisited.PlaceVisit;
import com.emedrep.watchdrug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.emedrep.watchdrug.R.id.txtDrugName;

public class DrugReport2 extends AppCompatActivity  implements View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private static final String TAG = "LocationActivity";
    SharedPreferences pre;
    String b;
    String reportId;
    LinearLayout lnbtn;
    private ProgressBar pDialog;
    TextView myTextProgress,txtInfo;;
    Button btnBack, btnReport, btnTryAgain,btnAddPlaces;
    String latValue;
    String longValue;

    RelativeLayout lnInternetIssue;
    CheckBox chkName, chkAnonymous, chkInLocation, chkMyPlaces;
    List<Visit> visitList;
    EditText editText, edtAddress;
    ListView listViewPlaces;
    String drugName, otherSuspicion, suspicionType;
    Visit visit;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView txtGps;


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
        setContentView(R.layout.activity_drug_report2);
        lnInternetIssue = (RelativeLayout) findViewById(R.id.lnInternetIssue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        drugName=intent.getStringExtra("DrugName");
        otherSuspicion=intent.getStringExtra("OtherSuspicion");
        suspicionType=intent.getStringExtra("SuspicionType");
        b=intent.getStringExtra("b");
        reportId=intent.getStringExtra("reportId");

        pre = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("  Report Product");
        lnbtn=(LinearLayout)findViewById(R.id.lnbtn);
        myTextProgress=(TextView)findViewById(R.id.myTextProgress);
        pDialog = (ProgressBar)findViewById(R.id.pgrGen);
        btnReport = (Button) findViewById(R.id.btnReport);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        edtAddress = (EditText) findViewById(R.id.edtAddress);
        TextView rndName = (TextView) findViewById(R.id.txtName);
        rndName.setText(pre.getString("LastName", null) + " " + pre.getString("FirstName", null));
        chkName = (CheckBox) findViewById(R.id.chkName);
        chkAnonymous = (CheckBox) findViewById(R.id.chkAnonymous);
        chkName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkAnonymous.setChecked(false);
            }
        });

        chkAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkName.setChecked(false);
            }
        });


        chkInLocation = (CheckBox) findViewById(R.id.chkInLocation);
        chkMyPlaces = (CheckBox) findViewById(R.id.chkMyPlaces);


        chkInLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkMyPlaces.setChecked(false);
                edtAddress.setVisibility(View.GONE);
            }
        });

        chkMyPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkInLocation.setChecked(false);
                edtAddress.setVisibility(View.VISIBLE);
                alertDialog();
                // Utilities.StartActivity(DrugReport2.this, SearcheableVisit.class);
            }
        });

        btnTryAgain.setOnClickListener(this);
        btnReport.setOnClickListener(this);
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

        }
        catch (Exception ex){

        }


    }


    private void showpDialog() {
        pDialog.setVisibility(View.VISIBLE);
        myTextProgress.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            // lnbtn.setBackgroundColor(Color.WHITE);
            lnbtn.setAlpha((float) 0.1);
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

    private void hidepDialog() {
        pDialog.setVisibility(View.GONE);
        myTextProgress.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            lnbtn.setAlpha((float) 1);
        }
    }


    private void postReports() {
        try {

            showpDialog();
            showHideCtr(0);
            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems();
            final String URL = Constant.API_URL + "/Reports";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {


                        hidepDialog();
                        ReportSql sql = new ReportSql(DrugReport2.this);

                        sql.updateDetails(Long.parseLong(reportId));
                        showHideCtr(0);
                        // Library.StartActivity(DrugReport.this,MasterActivity.class);
                        Intent catIntent = new Intent(DrugReport2.this, MasterActivity.class);

                        catIntent.putExtra("success", "success");

                        startActivity(catIntent);

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DrugReport2.this, error.getMessage(), Toast.LENGTH_LONG);
                    showHideCtr(1);
                    hidepDialog();

                    return;
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("User-agent", "My useragent");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return String.format("application/json; charset=utf-8");
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        hidepDialog();
                        return null;
                    }
                }
            };

            int socketTimeout = 30000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            queue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
            showHideCtr(1);
            hidepDialog();
        }
    }

    private void showHideCtr(int i) {
        if (i == 0) {
            lnInternetIssue.setVisibility(View.GONE);
            lnbtn.setVisibility(View.VISIBLE);


        } else {
            lnInternetIssue.setVisibility(View.VISIBLE);
            lnbtn.setVisibility(View.GONE);

        }
    }

    private String getItems() {


        try {


            File file = new File(b);

            Bitmap photoBm = BitmapFactory.decodeFile(file.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //  Bitmap photoBm =BitmapFactory.decodeFile(b, options);
            //get its orginal dimensions
            int bmOriginalWidth = photoBm.getWidth();
            int bmOriginalHeight = photoBm.getHeight();
            double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
            double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;
            //choose a maximum height
            int maxHeight = 1024;
            //choose a max width
            int maxWidth = 1024;

            photoBm = Utilities.getScaledBitmap(photoBm, bmOriginalWidth, bmOriginalHeight,
                    originalWidthToHeightRatio, originalHeightToWidthRatio,
                    maxHeight, maxWidth);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photoBm.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            byte[] ba = bytes.toByteArray();
            String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);

            JSONObject cartItemsObjedct = new JSONObject();

            ReportSql sql = new ReportSql(this);
            Report report = sql.getReportById(reportId);
            if(chkMyPlaces.isChecked()){
                cartItemsObjedct.putOpt("StateId",visit.stateId );
                cartItemsObjedct.putOpt("KnownName", visit.name);
                cartItemsObjedct.putOpt("Address", visit.address);
                cartItemsObjedct.putOpt("City", visit.lgaId);
                cartItemsObjedct.putOpt("Latitude", visit.latitude);
                cartItemsObjedct.putOpt("Longitude", visit.longitude);
            }
            else {
               // cartItemsObjedct.putOpt("StateId",report.state );
                //cartItemsObjedct.putOpt("LGAId",report );
                cartItemsObjedct.putOpt("KnownName", report.knownName);
                cartItemsObjedct.putOpt("Address", report.address);
                cartItemsObjedct.putOpt("City", report.city);
                cartItemsObjedct.putOpt("Latitude", pre.getString("Latitude", null));
                cartItemsObjedct.putOpt("Longitude", pre.getString("Longitude", null));
            }
           String dd= pre.getString("Latitude", null);

             cartItemsObjedct.putOpt("DateCreated", "2018-01-10T16:17:39.715132+01:00");
            cartItemsObjedct.putOpt("UserId", pre.getString("UserId",null));
            cartItemsObjedct.putOpt("SuspicionType", suspicionType);
            cartItemsObjedct.putOpt("IsAnonymous",chkAnonymous.isChecked());

            cartItemsObjedct.putOpt("Capture", ba1);
             cartItemsObjedct.putOpt("DrugName", drugName);
            cartItemsObjedct.putOpt("OtherSuspicion",otherSuspicion);
            //cartItemsObjedct.putOpt("DrugId ", "");

            cartItemsObjedct.putOpt("Country", report.country);
            cartItemsObjedct.putOpt("PostalCode", report.postalCode);

            cartItemsObjedct.putOpt("RelativeAddress", report.relativeAddress);

            cartItemsObjedct.putOpt("Premises", report.premise);
            cartItemsObjedct.putOpt("PremisesType", "");
            cartItemsObjedct.putOpt("ThoroughFare", report.thoroughFare);
            cartItemsObjedct.putOpt("SubThoroughFare", report.subThoroughFare);


            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }




    public void alertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DrugReport2.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.seacheable, null);
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
        alertDialog.setView(convertView);

        listViewPlaces = (ListView) convertView.findViewById(R.id.listViewPlace);
        btnAddPlaces=(Button)convertView.findViewById(R.id.btnAddPlaces);
        txtInfo=(TextView)convertView.findViewById(R.id.txtInfo);
        editText = (EditText) convertView.findViewById(R.id.edtProduct);
        editText.addTextChangedListener(textWatcher);
        final AlertDialog alert = alertDialog.create();

        alert.setTitle(" Select Location...."); // Title
        VisitDataSource ss = new VisitDataSource(this);
        visitList = ss.getAllVisit();
        if(visitList.size()<1){
            btnAddPlaces.setVisibility(View.VISIBLE);
            txtInfo.setVisibility(View.VISIBLE);
        }
        else {
            btnAddPlaces.setVisibility(View.GONE);

            txtInfo.setVisibility(View.GONE);
        }

        btnAddPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent catIntent = new Intent(DrugReport2.this, MasterActivity.class);

                catIntent.putExtra("pvsuccess", "2");

                startActivity(catIntent);
            }
        });
        VisitAdapter2 myadapter = new VisitAdapter2(DrugReport2.this,

                R.layout.cell_visit_listview2, visitList);

        listViewPlaces.setAdapter(myadapter);

        listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 visit = (Visit) visitList.get(position);
                edtAddress.setText(visit.getName() + ", " + visit.getAddress());
                chkMyPlaces.setChecked(true);
                chkInLocation.setChecked(false);

                alert.cancel();

            }

        });

        // show dialog

        alert.show();

    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            PlacesVirtualDb virtualDatabase = new PlacesVirtualDb(DrugReport2.this);
            List searchList = new ArrayList<Visit>();
            Cursor cursor = virtualDatabase.getWordMatches(editText.getText().toString());

            if (cursor == null) {
                if (editText.getText().length() == 0) {
                    VisitAdapter2 inventoryAdapter = new VisitAdapter2(DrugReport2.this, R.layout.cell_visit_listview2, searchList);
                    listViewPlaces.setAdapter(inventoryAdapter);
                }
                return;
            }

            if (cursor.moveToFirst()) {
                do {
                    Visit prd = new Visit();
                    prd.visitId = cursor.getInt(0);
                    prd.name = cursor.getString(1);
                    prd.address = cursor.getString(2);
                    prd.date = cursor.getString(3);
                    prd.latitude = cursor.getString(4);
                    prd.longitude = cursor.getString(5);
                    prd.stateId = cursor.getInt(6);
                    prd.lgaId = cursor.getInt(7);
                    prd.type = cursor.getInt(8);

                    searchList.add(prd);
                }
                while (cursor.moveToNext());

                cursor.close();
            }

            VisitAdapter2 inventoryAdapter = new VisitAdapter2(DrugReport2.this, R.layout.cell_visit_listview2, searchList);
            listViewPlaces.setAdapter(inventoryAdapter);


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.back_in, R.anim.back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
           finish();
            overridePendingTransition(R.anim.back_in, R.anim.back_out);
            return;
        }
        if(v==btnTryAgain){
            postReports();
        }
        if (v == btnReport) {
//            if (!validate()) {
//                return;
//
//            }
            postReports();
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



