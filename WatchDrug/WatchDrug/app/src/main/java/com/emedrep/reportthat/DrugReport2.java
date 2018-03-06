package com.emedrep.reportthat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.emedrep.reportthat.Adapter.VisitAdapter2;
import com.emedrep.reportthat.Db.PlacesVirtualDb;
import com.emedrep.reportthat.Db.ReportDataSource;
import com.emedrep.reportthat.Db.ReportPendingDataSource;
import com.emedrep.reportthat.Db.VendorDataSource;
import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.reportthat.Model.NameValue;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.reportthat.Model.ReportPending;
import com.emedrep.reportthat.Model.Visit;

import com.emedrep.reportthat.Service.VendorIntentService;
import com.emedrep.watchdrug.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrugReport2 extends AppCompatActivity  implements View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private static final String TAG = "LocationActivity";
    SharedPreferences pre;
    String b;
    String reportId;
    LinearLayout lnbtn,lnThere;
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
    String drugName, otherSuspicion, suspicionType,manufactuer;
    Visit visit;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    VisitDataSource visitDataSource;

    Spinner spnLocalGovt, spnState,spnType;
    final List<NameValue> lgaList=new ArrayList<NameValue>();
    private NameValue selectedState, selectedLGA,selectedType;


    EditText edtName;

    EditText edtDescription;

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
        setContentView(R.layout.activity_drug_report2);
        lnInternetIssue = (RelativeLayout) findViewById(R.id.lnInternetIssue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        drugName=intent.getStringExtra("DrugName");
        manufactuer=intent.getStringExtra("Manufacturer");
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
        lnThere=(LinearLayout)findViewById(R.id.lnThere);
        myTextProgress=(TextView)findViewById(R.id.myTextProgress);
        pDialog = (ProgressBar)findViewById(R.id.pgrGen);
        btnReport = (Button) findViewById(R.id.btnReport);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        edtAddress = (EditText) findViewById(R.id.edtAddress);
        spnType=(Spinner)findViewById(R.id.spnType);
        spnState = (Spinner) findViewById(R.id.spnState);
        spnLocalGovt = (Spinner) findViewById(R.id.spnLocalGovt);
        loadStateCapital();
        loadType();
        edtName = (EditText) findViewById(R.id.edtName);
        edtDescription = (EditText) findViewById(R.id.edtAddress2);
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
                lnThere.setVisibility(View.VISIBLE);
            }
        });

        chkMyPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkMyPlaces.setChecked(false);
                chkInLocation.setChecked(false);

                lnThere.setVisibility(View.GONE);
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
                        final ArrayAdapter<NameValue> arrayAdapterLGA = new ArrayAdapter<NameValue>(DrugReport2.this, R.layout.spinner_item, lgaList);
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
    private void hidepDialog() {
        pDialog.setVisibility(View.GONE);
        myTextProgress.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            lnbtn.setAlpha((float) 1);
        }
    }


    private void postReports() {
        try {
             if(!chkInLocation.isChecked() && !chkMyPlaces.isChecked() ){
                 new ViewDialog(this,"Kindly check location before submission").show();
                 return;
             }
             else if(chkInLocation.isChecked() && !validate()){
                 return;
             }

             else{
                 final ReportDataSource sql = new ReportDataSource(DrugReport2.this);
                 if(chkMyPlaces.isChecked()){
                     sql.updateDetails(Long.parseLong(reportId),visit.name,drugName,manufactuer,String.valueOf(chkAnonymous.isChecked()),visit.stateId,visit.lgaId,suspicionType,visit.address);
                 }
                 else {
                     sql.updateDetails(Long.parseLong(reportId),edtName.getText().toString(),drugName,manufactuer,String.valueOf(chkAnonymous.isChecked()),selectedState.getValue(),selectedLGA.getValue(),suspicionType,edtDescription.getText().toString());
                 }
                if(chkInLocation.isChecked()){
                     String timeStamp = new SimpleDateFormat("dd/MM/yyyy KK:mm a")
                             .format(new Date());
                   long success = visitDataSource.createVisit(selectedState.getValue(),selectedLGA.getValue(),selectedType.getValue(),edtName.getText().toString(), edtDescription.getText().toString(), latValue, longValue, timeStamp,0);
                   if(success>0) {
                       int visitId = Integer.parseInt(String.valueOf(success));
                       VendorDataSource vendorDataSource = new VendorDataSource(this);
                       long id = vendorDataSource.createVendor(visitId, selectedState.getValue(), selectedLGA.getValue(), selectedType.getValue(), edtName.getText().toString(), edtDescription.getText().toString(), latValue, longValue, timeStamp);
                       visitDataSource.updateVisit2(success,Integer.parseInt(String.valueOf(success)));
                       Intent mServiceIntent = new Intent(this, VendorIntentService.class);
                       startService(mServiceIntent);
                   }
                 }
                 showpDialog();

                 RequestQueue queue = Volley.newRequestQueue(this);
                 final String requestBody = getItems();
                 final String URL = Constant.API_URL + "/Reports";

                 StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         if (response != null) {


                             hidepDialog();

                             sql.updateIsSynced(Long.parseLong(reportId));



                             Toast.makeText(DrugReport2.this,"Report sent Successfully",Toast.LENGTH_LONG).show();
                             // Library.StartActivity(DrugReport.this,MasterActivity.class);
                             Intent catIntent = new Intent(DrugReport2.this, MasterActivity.class);
                             catIntent.putExtra("success", "success");
                             startActivity(catIntent);


                         }

                     }

                 }, new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(DrugReport2.this, error.getMessage(), Toast.LENGTH_LONG);
                        // showHideCtr(1);
                         savePendingReport();
                         hidepDialog();
                         Intent catIntent = new Intent(DrugReport2.this, MasterActivity.class);
                         catIntent.putExtra("success", "success");
                         startActivity(catIntent);
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

             }

        } catch (Exception e) {
            e.printStackTrace();
            savePendingReport();
            Intent catIntent = new Intent(DrugReport2.this, MasterActivity.class);
            catIntent.putExtra("success", "success");
            startActivity(catIntent);
            //showHideCtr(1);
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

            ReportDataSource sql = new ReportDataSource(this);
            Report report = sql.getReportById(reportId);

            ReportPendingDataSource reportPending=new ReportPendingDataSource(this);
            if(chkMyPlaces.isChecked()){
                cartItemsObjedct.putOpt("StateId",visit.stateId );
                cartItemsObjedct.putOpt("LGAId",visit.lgaId );
                cartItemsObjedct.putOpt("KnownName", visit.name);
                cartItemsObjedct.putOpt("Address", visit.address);

                cartItemsObjedct.putOpt("Latitude", visit.latitude);
                cartItemsObjedct.putOpt("Longitude", visit.longitude);
                cartItemsObjedct.putOpt("PremisesType", visit.type);
                cartItemsObjedct.putOpt("Premises", visit.name);


            }
            else {
                cartItemsObjedct.putOpt("StateId",selectedState.getValue() );
                cartItemsObjedct.putOpt("LGAId",selectedLGA.getValue() );
                cartItemsObjedct.putOpt("KnownName", report.knowName);
                cartItemsObjedct.putOpt("Address", edtDescription.getText());
                cartItemsObjedct.putOpt("City", report.city);
                cartItemsObjedct.putOpt("Latitude", report.latitude);
                cartItemsObjedct.putOpt("Longitude", report.longitude);
                cartItemsObjedct.putOpt("PremisesType", selectedType.getValue());
                cartItemsObjedct.putOpt("Premises", edtName.getText());
            }
           //cartItemsObjedct.putOpt("DateCreated", "2018-01-10T16:17:39.715132+01:00");

            cartItemsObjedct.putOpt("Manufacturer", manufactuer);
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
            cartItemsObjedct.putOpt("ThoroughFare", report.thoroughFare);


            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }


    private String savePendingReport() {


        try {
            Toast.makeText(DrugReport2.this,"Your report has been saved offline .We will send the report when we are able to reach the server",Toast.LENGTH_LONG).show();
           ReportDataSource sql = new ReportDataSource(this);
            Report report = sql.getReportById(reportId);
            ReportPending xx=new ReportPending();
            ReportPendingDataSource reportPending=new ReportPendingDataSource(this);
            if(chkMyPlaces.isChecked()){
                xx.stateId=visit.stateId;
                xx.lgaId=visit.lgaId;
                xx.address=visit.address;
                xx.latitude=visit.latitude;
                xx.longitude=visit.longitude;
                xx.premisesType=visit.type;
                xx.premises=visit.name;



            }
            else {
                xx.stateId=selectedState.getValue() ;
                xx.lgaId=selectedLGA.getValue();
                xx.knownName=report.knowName;
                xx.address=edtDescription.getText().toString();
                xx.city=report.city;

                xx.latitude=report.latitude;
                xx.longitude=report.longitude;
                xx.premisesType=selectedType.getValue();
                xx.premises=edtName.getText().toString();

            }
             xx.userId=pre.getString("UserId",null);
            xx.suspicionType=suspicionType;
            xx.isAnonymous=String.valueOf(chkAnonymous.isChecked());
            xx.filePath=report.filePath;
            xx.drugName=drugName;
            xx.otherSuspicion=otherSuspicion;
            xx.country=report.country;
            xx.postalCodde=report.postalCode;
            xx.relativeAddress=report.relativeAddress;
            xx.thoroughFare=report.thoroughFare;
            xx.reportId=report.reportId;
          long ss=  reportPending.createReportPending(xx);

        } catch (Exception e1) {
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
                edtAddress.setVisibility(View.VISIBLE);
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



