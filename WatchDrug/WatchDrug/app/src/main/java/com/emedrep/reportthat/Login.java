package com.emedrep.reportthat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import com.emedrep.reportthat.Db.UserDataSource;
import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Library.AppController;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.watchdrug.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends Activity implements OnClickListener {


    private EditText email;
    private EditText password;
    private TextView txtCreate,txtForgetPassword;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private ProgressBar pDialog;
    Button mEmailSignInButton;
    PharmacyDataSource pharmacyDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pharmacyDataSource = new PharmacyDataSource(Login.this);
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        editor = prefs.edit();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        txtCreate=(TextView)findViewById(R.id.txtCreate);
        txtCreate.setOnClickListener(this);
        pDialog = (ProgressBar)findViewById(R.id.pgrGen);
        txtForgetPassword=(TextView)findViewById(R.id.txtForgetPassword);
        txtForgetPassword.setOnClickListener(this);
        Intent intent=getIntent();
        String cre=intent.getStringExtra("credentials");
        if(cre!=null){
            String [] crearr=cre.split("_");
            email.setText(crearr[0]);
            password.setText(crearr[1]);
        }
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });


    }



    private void attemptLogin() {

        email.setError(null);
        password.setError(null);
        boolean cancel = false;
        View focusView = null;
        UserDataSource userDataSource=new UserDataSource(Login.this);

        String memail=email.getText().toString();
        String mpassword=password.getText().toString();

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(email.getText())) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }
      //  String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (!String.valueOf(email.getText()).matches(emailPattern)) {
            email.setError("Invalid Email address ");
            focusView = email;
            cancel = true;

        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(password.getText())) {
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

           boolean succes= userDataSource.allowLogin(memail,mpassword);
            if(succes){
               // editor.putString("LogOut","true");
                //editor.commit();
                editor.putString("Email",email.getText().toString());
                editor.putBoolean("LogOut",false);
                editor.commit();
                Library.StartActivity(Login.this,MasterActivity.class);
            }
            else {
                postLogin();



            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v==txtForgetPassword){
           Library.StartActivity(this,ForgotPassword.class);
        }
        else if(v==txtCreate) {
            Library.StartActivity(this,Register.class);
        }

    }



    private void showpDialog() {
        pDialog.setVisibility(View.VISIBLE);
        mEmailSignInButton.setVisibility(View.GONE);


    }

    private void hidepDialog() {
        pDialog.setVisibility(View.GONE);
        mEmailSignInButton.setVisibility(View.VISIBLE);

    }


    private void postLogin() {
        try {

            showpDialog();

            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems();
            final String URL = Constant.API_URL + "/Account/Login";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {


                      if(response.equals("false")){
                            Toast.makeText(Login.this,"Invalid login",Toast.LENGTH_LONG).show();
                            hidepDialog();
                        }

                        else{


//                          if(pharmacyDataSource.getAllPharmacy(Login.this).size()<1) {
//                              getPharmaciesRequest(response);
//
//                          }
//                          else{

                               getGetStoredVendors(response);




                        //  }


                        }

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Login.this,"Unable to connect to network",Toast.LENGTH_LONG).show();
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


    private void getPharmaciesRequest(final String loginResponse) {

        //showpDialog();
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


                                    getGetStoredVendors(loginResponse);


                                }



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



    private void getGetStoredVendors(String response) {

        showpDialog();
        JSONObject obj1 = null;
        String userId = null;
      try {


          obj1 = new JSONObject(response);
          userId=obj1.getString("Id");

         }catch (JSONException ex){
              hidepDialog();
          }
        try {
            final JSONObject finalObj = obj1;
            JsonArrayRequest req = new JsonArrayRequest(Constant.API_URL + "/UserLocationsByUserId?userId="+userId,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            try {
                                // Loop through the array elements
                                for (int i = 0; i < response.length(); i++) {
                                    // Get current json object
                                    JSONObject jsonObject = response.getJSONObject(i);


                                    String jsonPremises = jsonObject.getString("Premises");
                                    int jsonObjectLocationType = jsonObject.getInt("LocationType");
                                    String jsonObjectLocationAddress = jsonObject.getString("LocationAddress");

                                    int jsonObjectState = jsonObject.getInt("State");
                                    int jsonObjectLga = jsonObject.getInt("Lga");
                                    String jsonObjectLatitude = jsonObject.getString("Latitude");
                                    String jsonObjectLongitude = jsonObject.getString("Longitude");
                                    String jsonObjectiD = jsonObject.getString("Id");
                                    int id = jsonObject.getInt("Id");
                                    String jsonDate=jsonObject.getString("DateCreated");
                                    VisitDataSource visitDataSource=new VisitDataSource(Login.this);
                                    if(!visitDataSource.visitDuplicatedById(jsonObjectiD)){
                                        long xx=visitDataSource.createVisit(jsonObjectState,jsonObjectLga,jsonObjectLocationType,jsonPremises,jsonObjectLocationAddress,jsonObjectLatitude,jsonObjectLongitude,jsonDate,id);
                                    }





                                }
                                editor.putString("Email", email.getText().toString());
                                editor.putString("FirstName", finalObj.getString("Firstname"));
                                editor.putString("LastName", finalObj.getString("Lastname"));

                                editor.putString("UserId", finalObj.getString("Id"));
                                editor.putString("Email", email.getText().toString());
                                editor.putBoolean("LogOut", false);
                                editor.putString("Email", email.getText().toString());
                                editor.commit();

                                Intent masterIntent=new Intent(Login.this,MasterActivity.class);
                                 startActivity(masterIntent);
                               // Library.StartActivity(Login.this,MasterActivity.class);
                                // Toast.makeText(Login.this,response,Toast.LENGTH_LONG).show();
                               // hidepDialog();

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getItems() {


        try {

            JSONObject cartItemsObjedct = new JSONObject();
            cartItemsObjedct.putOpt("Email",email.getText().toString());
            cartItemsObjedct.putOpt("Password",password.getText().toString());

            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}

