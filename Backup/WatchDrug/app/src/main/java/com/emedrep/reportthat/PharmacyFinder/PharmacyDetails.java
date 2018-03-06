package com.emedrep.reportthat.PharmacyFinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emedrep.reportthat.Library.AppController;
import com.emedrep.reportthat.Library.GeoHelper;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.watchdrug.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class PharmacyDetails extends AppCompatActivity implements View.OnClickListener {
    TextView txtDistance;
    TextView txtTime1;
    TextView txtTime2;
    String latitude;
    String longitude;
    String returnedString;
    TextView txtViewOnMap;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
       // actionBar.setLogo(R.drawable.wdsm);
       // actionBar.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
         setTitle("  Pharmacy Details");
        TextView txtPremiseName=(TextView)findViewById(R.id.txtName);
        TextView txtAddress=(TextView)findViewById(R.id.txtAddress);
        TextView txtType=(TextView)findViewById(R.id.txtPremiseType);
        txtViewOnMap=(TextView)findViewById(R.id.txtViewOnMap);
        txtViewOnMap.setOnClickListener(this);
        txtDistance=(TextView)findViewById(R.id.txtDistance);
//        txtTime1=(TextView)findViewById(R.id.txtTime1);
//        txtTime2=(TextView)findViewById(R.id.txtTime2);

        name=getIntent().getStringExtra("name");
        String address=getIntent().getStringExtra("address");
        String premiseType=getIntent().getStringExtra("premiseType");
        txtPremiseName.setText(name);
        txtAddress.setText(address);
        txtType.setText(premiseType);
        latitude=getIntent().getStringExtra("latitude");
        longitude=getIntent().getStringExtra("longitude");

       // Location loc= Utilities.getMyLocation(this);
      //  getDistance(String.valueOf(loc.getLatitude()),String.valueOf(loc.getLongitude()).trim(),latitude,longitude.trim(),"driving");
    }



    public String makeHttpRequest(String originLat, String originLon, String destinationLat, String destinationLon,String mode) {
        InputStream is = null;
        String jsonString = "";
        try {

             String url= GeoHelper.getUrl(originLat,originLon,destinationLat,destinationLon,mode);
           // String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + originLat + "," + originLon + "&destinations=" + destinationLat + "," + destinationLon + "&mode=driving&language=en-EN&units=metric&key=AIzaSyAMl7qgaBaIIXQC0z470xG1iyQsjB5lcyM";

           // String url = MessageFormat.format("http://www.mychurchapps.net/Webservice/RegistrationKeyHub.ashx?operation_name={0}&key_gen={1}&ime={2}&details={3}", "validatekey", pin, ime, query);

            HttpParams httpParameters = new BasicHttpParams();

//            int timeoutConnection = 3000;
//            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//
//            int timeoutSocket = 5000;
//            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            //String paramString = URLEncodedUtils.format(params, "utf-8");
            // url += "?" + paramString;


            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpGet);

            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            Log.d("Error1", e.getMessage());
            return "Unable to cannot to the internet";
        } catch (ClientProtocolException e) {

            e.printStackTrace();
            Log.d("Error2", e.getMessage());
            return "Unable to cannot to the internet";
        } catch (IOException e) {

            e.printStackTrace();
            Log.d("Error3", e.getMessage());
            return "Unable to cannot to the internet";
        } catch (Exception e) {
            e.printStackTrace();

            // showAlertDialog("Unable to connect to the Internet");
            Log.d("Error", e.getMessage());
            return "Unable to cannot to the internet";
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();
        } catch (Exception e) {

            Log.e("Buffer Error", "Error converting result " + e.toString());
            return "Unable to cannot to the internet";
        }


        // return JSON String
        return jsonString.toString();

    }

    @Override
    public void onClick(View v) {
        try {

            String urlAddress = "http://maps.google.com/maps?q="+ latitude +"," + longitude +"("+ name + ")&iwloc=A&hl=es";

            Intent maps = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
            startActivity(maps);
        } catch (Exception e) {
          //  Log.e(TAG, e.toString());
        }
    }


    public class PinValidator extends AsyncTask<String, String, String> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        protected String doInBackground(String... args) {
            try {
                Location loc= Utilities.getMyLocation(PharmacyDetails.this);
                returnedString= makeHttpRequest(Double.toString(loc.getLatitude()),Double.toString(loc.getLongitude()),latitude,longitude,"walking");

            } catch (Exception e) {

                Log.e("Error:",e.getMessage());
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
           // pDialog.dismiss();
            // edActivationBox.setText("");
          String xx=returnedString;
            txtDistance.setText(xx);





//            Intent homepage = new Intent( ActivateGhs.this,   ViewPagerContainer.class);
//        homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(homepage);
        }
    }
    private String getDistance(String originLat, String originLon, String destinationLat, String destinationLon,String mode) {

        String getdistance = "";
      // String url= GeoHelper.getUrl(originLat,originLon,destinationLat,destinationLon,mode);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + originLat + "," + originLon + "&destinations=" + destinationLat + "," + destinationLon + "&mode=driving&language=en-EN&units=metric&key=AIzaSyAMl7qgaBaIIXQC0z470xG1iyQsjB5lcyM";
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String line = EntityUtils.toString(httpEntity);

            JSONObject rootObject = new JSONObject(line);
            JSONArray rows = rootObject.getJSONArray("rows"); // Get all JSONArray rows

            for (int i = 0; i < rows.length(); i++) { // Loop over each each row
                Log.d("loop!", "rows");
                JSONObject row = rows.getJSONObject(i); // Get row object
                JSONArray elements = row.getJSONArray("elements"); // Get all elements for each row as an array
                for (int j = 0; j < elements.length(); j++) { // Iterate each element in the elements array
                    Log.d("loop!", "elements");
                    JSONObject element = elements.getJSONObject(j); // Get the element object
                    JSONObject distance = element.getJSONObject("distance"); // Get distance sub object
                    getdistance = String.valueOf(distance.getInt("value")/1000);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getdistance;
    }
    private void getDistanceMatrix(String originLat, String originLon, String destinationLat, String destinationLon,String mode) {
        String url= GeoHelper.getUrl(originLat,originLon,destinationLat,destinationLon,mode);
        StringRequest request = new StringRequest(Request.Method.GET, url, new

                Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                         try {

                                 JSONObject result=new JSONObject(response);


//                             JSONObject elements=result.getJSONObject("elements");
//                             JSONObject duration=elements.getJSONObject("duration");
//                             String text=duration.getString("text");


                         }
                         catch (JSONException e) {

                             e.printStackTrace();
                         }
                    }
                },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", "");
            }
        });
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}
