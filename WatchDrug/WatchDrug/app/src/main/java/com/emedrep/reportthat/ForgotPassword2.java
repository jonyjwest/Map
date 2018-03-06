package com.emedrep.reportthat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emedrep.reportthat.Db.PharmacyDataSource;
import com.emedrep.reportthat.Library.AppController;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.watchdrug.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword2 extends AppCompatActivity {

    TextView txtSend, txtEmailToSend;
    Button btnContinue;
    private EditText code;
    String email;
    PharmacyDataSource pharmacyDataSource;
    private ProgressBar pDialog;
    Button mEmailSignInButton;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password2);
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        editor = prefs.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        pharmacyDataSource = new PharmacyDataSource(ForgotPassword2.this);
        email = intent.getStringExtra("useremail");
        txtEmailToSend = (TextView) findViewById(R.id.txtEmailToSend);
        txtEmailToSend.setText(email);
        txtSend = (TextView) findViewById(R.id.txtSend);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        code = (EditText) findViewById(R.id.code);

        pDialog = (ProgressBar) findViewById(R.id.pgrGen);

        mEmailSignInButton = (Button) findViewById(R.id.btnContinue);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });
        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               postPassword();
            }
        });

    }

    private void showpDialog() {
        pDialog.setVisibility(View.VISIBLE);
        mEmailSignInButton.setVisibility(View.GONE);


    }

    private void hidepDialog() {
        pDialog.setVisibility(View.GONE);
        mEmailSignInButton.setVisibility(View.VISIBLE);

    }
    private void attemptLogin() {

        code.setError(null);

        boolean cancel = false;
        View focusView = null;
        //  UserDataSource userDataSource = new UserDataSource(com.emedrep.reportthat.ForgotPassword.this);

        String memail = code.getText().toString();

        if (TextUtils.isEmpty(memail)) {
            code.setError(getString(R.string.error_field_required));
            focusView = code;
            cancel = true;
        }


        if (cancel) {

            focusView.requestFocus();
        } else {
           postLogin();

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }




    private void postPassword() {
        try {

            showpDialog();

            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems();
            final String URL = Constant.API_URL + "/Account/ForgotPassword";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {

                        if(!response.equals("26") && !response.equals("false") ){
                            Toast.makeText(ForgotPassword2.this,"Email resent successful",Toast.LENGTH_LONG).show();
                            hidepDialog();


                        }


                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ForgotPassword2.this,"Unable to connect to network",Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,"Unable to connect to network",Toast.LENGTH_LONG);
            hidepDialog();
        }
    }



    private String getItems() {


        try {

            JSONObject cartItemsObjedct = new JSONObject();
            cartItemsObjedct.putOpt("Email",email);

            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }
    private String getLoginItems() {


        try {

            JSONObject cartItemsObjedct = new JSONObject();
            cartItemsObjedct.putOpt("Email",email);
            cartItemsObjedct.putOpt("Password",code.getText().toString());

            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    private void getPharmaciesRequest() {

        showpDialog();
        try {
            JsonArrayRequest req = new JsonArrayRequest(Constant.API_URL_PHARMAMAP + "Pharmacies",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            try {
                                // Loop through the array elements
                                for (int i = 0; i < response.length(); i++) {
                                    // Get current json object
                                    JSONObject jsonObject = response.getJSONObject(i);


                                    String jsonObjectLatitude = jsonObject.getString("Latitude");
                                    String jsonObjectLongitude = jsonObject.getString("Longitude");
                                    String jsonObjectPremAddress = jsonObject.getString("PremiseAddress");

                                    String jsonObjectPhN = jsonObject.getString("PhoneNumber");
                                    String jsonObjectPrName = jsonObject.getString("PremiseName");
                                    String jsonObjectPName = jsonObject.getString("PharmacistName");
                                    String jsonObjectPType = jsonObject.getString("PremiseTypeId");


                                    //createPharmacy(String premiseName, String premiseAddress, String phoneNumber,  String premiseType, String pharmacistName, String pharmacistPhone, String latitude, String longitude, String dateCreated)
                                    pharmacyDataSource.createPharmacy(jsonObjectPrName, jsonObjectPremAddress, jsonObjectPhN, jsonObjectPType, jsonObjectPName, jsonObjectPhN, jsonObjectLatitude, jsonObjectLongitude, "");


                                }
                                postLogin();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // showHideCtr(1);
                                hidepDialog();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("dd", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Unable to connect to network", Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });

            int socketTimeout = 50000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(req);
        }
        catch (Exception ex){
            ex.printStackTrace();
            //showHideCtr(1);
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to network", Toast.LENGTH_LONG).show();
            hidepDialog();
        }
    }
    private void postLogin() {
        try {

            showpDialog();

            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getLoginItems();
            final String URL = Constant.API_URL + "/Account/Login";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {


                        if(response.equals("false")){
                            Toast.makeText(ForgotPassword2.this,"Invalid Code",Toast.LENGTH_LONG).show();
                            hidepDialog();
                        }

                        else{


//                            if(pharmacyDataSource.getAllPharmacy(ForgotPassword2.this).size()<1){
//
//                                getPharmaciesRequest();
//                            }

                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                                editor.putString("Email", email);
                                editor.putString("FirstName",obj.getString("Firstname"));
                                editor.putString("LastName", obj.getString("Lastname"));

                                editor.putString("UserId",obj.getString("Id"));

                                editor.putBoolean("LogOut",false);
                                Intent catIntent = new Intent(ForgotPassword2.this, CreatePassword.class);

                                catIntent.putExtra("code",code.getText().toString());
                                catIntent.putExtra("useremail",email);

                                startActivity(catIntent);
                                editor.commit();


                               // Library.StartActivity(ForgotPassword2.this,CreatePassword.class);
                                // Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();
                                hidepDialog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(ForgotPassword2.this,"Unable to connect to network",Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,"Unable to connect to network",Toast.LENGTH_LONG);
            hidepDialog();
        }
    }
}