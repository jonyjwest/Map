package com.emedrep.reportthat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.emedrep.reportthat.Db.ReportSql;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.reportthat.Model.NameValue;
import com.emedrep.reportthat.Model.Product;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.watchdrug.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.tooltip.Tooltip;

public class DrugReport extends AppCompatActivity implements View.OnClickListener{

    ImageView imgPreview;
    Spinner spnSuspicion;
    SharedPreferences pre;
    String b;
    String reportId;
    LinearLayout lnbtn;
    private ProgressBar pDialog;
    TextView myTextProgress,txt_skip;
    AutoCompleteTextView txtDrugName,txtManufacturer;
    Button btnReport,btnTryAgain;
    private NameValue selectedSuspicion;
    RelativeLayout lnInternetIssue;
    EditText txtSuspicious;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_report);
        lnInternetIssue = (RelativeLayout)findViewById(R.id.lnInternetIssue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pre=getSharedPreferences(Constant.PREFERENCE_NAME,0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("  Report Product");
        spnSuspicion=(Spinner)findViewById(R.id.spnSuspicion);
        imgPreview=(ImageView)findViewById(R.id.imgPreview);
        btnReport=(Button)findViewById(R.id.btnReport);
        btnTryAgain=(Button)findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(this);
        btnReport.setOnClickListener(this);

        txtDrugName=(AutoCompleteTextView) findViewById(R.id.txtDrugName);
        txtManufacturer=(AutoCompleteTextView) findViewById(R.id.txtManufacturer);


        Tooltip.make(this,
                new Tooltip.Builder(101)
                        .anchor(txtDrugName, Tooltip.Gravity.TOP)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 5000)
                        .activateDelay(900)
                        .showDelay(400)
                        .text("Type product or select from the suggestion list.")
                        .maxWidth(600)
                        .withArrow(true)
                        .withOverlay(true).build()
        ).show();
        //Type product or select from the suggestion list.

        txtSuspicious=(EditText)findViewById(R.id.txtSuspicious);

        List<Product> productList=loadDrugs();
        ArrayAdapter<Product> adapter = new ArrayAdapter<Product>
                (this,android.R.layout.simple_list_item_1,productList);
        txtDrugName.setAdapter(adapter);
        txtDrugName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product=(Product)parent.getItemAtPosition(position);

                txtManufacturer.setText(product.manufacturer);
            }
        });

        myTextProgress=(TextView)findViewById(R.id.myTextProgress);
        pDialog = (ProgressBar)findViewById(R.id.pgrGen);
        // TextView gpsSignal=(TextView)v.findViewById(R.id.gpsSignal);
        lnbtn=(LinearLayout)findViewById(R.id.lnbtn);

        Intent iin= getIntent();
         b = iin.getStringExtra("picName");
         reportId=iin.getStringExtra("reportId");

        //TextView rndName=(TextView) findViewById(R.id.txtName);
       // rndName.setText(pre.getString("LastName",null) +" "+pre.getString("FirstName",null));
        loadSuspicion();
        if(b!=null)
        {
           imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
           // Bitmap bmp = Utilities.loadBitmap(this,b);
            File f = new File(b);
          //  Picasso.with(this).load(f).into(imgPreview);


           // File f = new File(b);
         Picasso.with(this).load(f).resize(200, 147).centerCrop().into(imgPreview);


        }


    }

    private List<Product> loadDrugs() {

        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.drugs);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final List<Product> products = new ArrayList<Product>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equals("")){
                    continue;
                }
                String[] strings = TextUtils.split(line, "@");
                Product product=new Product();
                product.productId=strings[0];
                product.name=strings[1];
                product.activeIngredient=strings[2];
                if(product.manufacturer!=null){
                    if(product.manufacturer.trim().equals("SAME AS APPLICANT")){
                        product.manufacturer="";
                    }
                    else{
                        product.manufacturer=strings[3];
                    }
                }

                product.nafdac=strings[4];
                products.add(product);
            }


            return products;
        }
        catch (IOException ex){
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("done", "DONE loading words.");
        return null;
    }

    private void loadSuspicion(){

        try {

            List<NameValue> mList=new ArrayList<>();
            NameValue mV0=new NameValue();
            mV0.setName("-Suspicion-");
            mV0.setValue(0);
            mList.add(mV0);

            NameValue mV1=new NameValue();
            mV1.setName("Expired Product");
            mV1.setValue(1);
            mList.add(mV1);

            NameValue mV2=new NameValue();
            mV2.setName("Fake Product");
            mV2.setValue(2);
            mList.add(mV2);

            NameValue mV3=new NameValue();
            mV3.setName("Unlicensed Vendor");
            mV3.setValue(3);
            mList.add(mV3);

            NameValue mV4=new NameValue();
            mV4.setName("Others");
            mV4.setValue(4);
            mList.add(mV4);



            ArrayAdapter<NameValue> arrayAdapter = new ArrayAdapter<NameValue>(this,R.layout.spinner_item, mList);

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnSuspicion.setAdapter(arrayAdapter);
            spnSuspicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                    selectedSuspicion = (NameValue) parent.getItemAtPosition(pos);
                    if(selectedSuspicion.getValue()==4){
                        txtSuspicious.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtSuspicious.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
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
    private void hidepDialog() {
        pDialog.setVisibility(View.GONE);
        myTextProgress.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            lnbtn.setAlpha((float) 1);
        }
    }


    private void postReports(){
        try {

            showpDialog();
            showHideCtr(0);
            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems();
            final String URL = Constant.API_URL + "/Reports";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(response!=null){


                         hidepDialog();
                        ReportSql sql=new ReportSql(DrugReport.this);

                        sql.updateDetails(Long.parseLong(reportId));
                        showHideCtr(0);
                       // Library.StartActivity(DrugReport.this,MasterActivity.class);
                        Intent catIntent=new Intent(DrugReport.this,MasterActivity.class);

                        catIntent.putExtra("success","success");

                        startActivity(catIntent);

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DrugReport.this, error.getMessage(), Toast.LENGTH_LONG);
                    showHideCtr(1);
                    hidepDialog();

                    return;
                }
            })
            {

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
            Intent iin= getIntent();
            b = iin.getStringExtra("picName");
            reportId=iin.getStringExtra("reportId");
           // Bitmap photoBm = Utilities.resizeBitMapImage1(b,200,240);

            File file = new File(b);

            Bitmap photoBm = BitmapFactory.decodeFile(file.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
          //  Bitmap photoBm =BitmapFactory.decodeFile(b, options);
            //get its orginal dimensions
            int bmOriginalWidth = photoBm.getWidth();
            int bmOriginalHeight = photoBm.getHeight();
            double originalWidthToHeightRatio =  1.0 * bmOriginalWidth / bmOriginalHeight;
            double originalHeightToWidthRatio =  1.0 * bmOriginalHeight / bmOriginalWidth;
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

           // reportId
            ReportSql sql=new ReportSql(this);
            Report report=sql.getReportById(reportId);
            cartItemsObjedct.putOpt("Latitude",pre.getString("Latitude",null));
            cartItemsObjedct.putOpt("Longitude",pre.getString("Longitude",null));
            cartItemsObjedct.putOpt("ReporterId",pre.getString("ReporterId",null));
            cartItemsObjedct.putOpt("SuspicionType",selectedSuspicion.getValue());
            cartItemsObjedct.putOpt("Capture", ba1);
            cartItemsObjedct.putOpt("DrugName", txtDrugName.getText());
            cartItemsObjedct.putOpt("OtherSuspicion",txtSuspicious.getText());
            //cartItemsObjedct.putOpt("DrugId ", "");
            cartItemsObjedct.putOpt("State", report.state);

            cartItemsObjedct.putOpt("Address",report.address);
            cartItemsObjedct.putOpt("City", report.city);
            cartItemsObjedct.putOpt("Country", report.country);
            cartItemsObjedct.putOpt("PostalCode",report.postalCode);
            cartItemsObjedct.putOpt("KnownName", report.knownName);
            cartItemsObjedct.putOpt("RelativeAddress", report.relativeAddress);

            cartItemsObjedct.putOpt("Premise",report.premise);
            cartItemsObjedct.putOpt("ThoroughFare", report.thoroughFare);
            cartItemsObjedct.putOpt("SubThoroughFare", report.subThoroughFare);


            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }


    private boolean validate() {
        txtDrugName.setError(null);
        String drugName = txtDrugName.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(drugName)) {
            txtDrugName.setError("Drug Name is required");
            focusView = txtDrugName;
            cancel = true;
            return false;
        }

       if(selectedSuspicion.getValue()==0){
           new ViewDialog(this,"Please select suspicion type").show();
           return false;
       }

        if (cancel) {

            focusView.requestFocus();
        }
       return true;
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
        if(v==btnReport) {
            if (!validate()) {
                return;

            }
            else{
                Intent catIntent = new Intent(this, DrugReport2.class);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                catIntent.putExtra("DrugName",txtDrugName.getText().toString());
                catIntent.putExtra("OtherSuspicion",txtSuspicious.getText().toString());
                String dd=String.valueOf(selectedSuspicion.getValue());
                catIntent.putExtra("SuspicionType",dd);
                catIntent.putExtra("b",b);
                catIntent.putExtra("reportId",reportId);


                startActivity(catIntent);
                overridePendingTransition(R.anim.right_to_left, R.anim.right);


            }
        }

    }


}
