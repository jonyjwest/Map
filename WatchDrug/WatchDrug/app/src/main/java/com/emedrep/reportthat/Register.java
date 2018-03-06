package com.emedrep.reportthat;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emedrep.reportthat.Db.PharmacyDataSource;
import com.emedrep.reportthat.Db.UserDataSource;
import com.emedrep.reportthat.Library.AppController;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.watchdrug.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private EditText firstname;
    private EditText lastname;

    private EditText password,confirmPassword, phone;
    private EditText email;

    private View mProgressView;
    private View mLoginFormView;

    LinearLayout lnbtn;

//    RelativeLayout lnInternetIssue;
//    Button btnTryAgain;
    Button btnRegister;
    TextView haveAcct;
    //CheckBox chkTc;

    PharmacyDataSource pharmacyDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        editor = prefs.edit();
        setContentView(R.layout.activity_register);
        //lnInternetIssue = (RelativeLayout)findViewById(R.id.lnInternetIssue);
         pharmacyDataSource = new PharmacyDataSource(Register.this);
        lnbtn=(LinearLayout)findViewById(R.id.lnbtn);
      //  btnTryAgain=(Button)findViewById(R.id.btnTryAgain);
        haveAcct=(TextView)findViewById(R.id.haveAcct);
        //chkTc=(CheckBox)findViewById(R.id.chkTc);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        password = (EditText) findViewById(R.id.password);
        confirmPassword=(EditText)findViewById(R.id.confirmPassword);
        email = (EditText) findViewById(R.id.email);
        phone=(EditText)findViewById(R.id.phone);
        if (getIntent().getStringExtra("Email") != null) {
            String vv = getIntent().getStringExtra("Email");
            email.setText(vv);
        }
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        Library.removeKeyboardDialog(this, firstname);


         btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();

            }
        });
        haveAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.StartActivity(Register.this, Login.class);
            }
        });
      //  btnTryAgain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validate();
//
//            }
//        });
    }

//    private void showHideCtr(int i) {
//        if (i == 0) {
//            lnInternetIssue.setVisibility(View.GONE);
//            lnbtn.setVisibility(View.VISIBLE);
//
//
//        } else {
//            lnInternetIssue.setVisibility(View.VISIBLE);
//            lnbtn.setVisibility(View.GONE);
//
//        }
//    }

    private void validate() {
        firstname.setError(null);
        lastname.setError(null);
        email.setError(null);
        phone.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        String eml = email.getText().toString();
        String fname = firstname.getText().toString();
        String lname = lastname.getText().toString();
        String psw=password.getText().toString();
        String confirmpsw=confirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(fname)) {
            firstname.setError("First name is required");
            focusView = firstname;
            cancel = true;
        }

        if (TextUtils.isEmpty(lname)) {
            lastname.setError("Last name is required");
            focusView = lastname;
            cancel = true;
        }
        int xx=phone.getText().length();
        if (xx == 0) {
            phone.setError("Phone is required");
            focusView = phone;
            cancel = true;
        }
        if(xx!=11){
            phone.setError("Invalid phone number");
            focusView = phone;
            cancel = true;
        }
        if (TextUtils.isEmpty(eml)) {
            email.setError("Email is required");
            focusView = email;
            cancel = true;
        }
       // String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
           String emailPattern="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (!String.valueOf(email.getText()).matches(emailPattern)) {
            email.setError("Invalid Email address ");
            focusView = email;
            cancel = true;

        }

        if (TextUtils.isEmpty(psw)) {
            password.setError("Password is required");
            focusView = password;
            cancel = true;
        }
        if(psw.length()<6){
            password.setError("Password lenght must be a minimum of 6 characters");
            focusView = password;
            cancel = true;
        }
        if (TextUtils.isEmpty(confirmpsw)) {
            confirmPassword.setError("Confirm Password is required");
            focusView = confirmPassword;
            cancel = true;
        }
        if(!psw.equals(confirmpsw)){
            confirmPassword.setError("A password mismatch has been detected");
            focusView = confirmPassword;
            cancel = true;
        }
//        if(!chkTc.isChecked())
//        {
//            new ViewDialog(this,"Please indicate that you agree to the T&C.").show();
//        }
        if (cancel) {

            focusView.requestFocus();
        } else {
//            if(pharmacyDataSource.getAllPharmacy(this).size()>0){
                postRegistration();
               // getPharmaciesRequest();

           // }
           // else{
                getPharmaciesRequest();
          //  }


        }

    }








    private void showpDialog() {
        mProgressView.setVisibility(View.VISIBLE);

        btnRegister.setVisibility(View.GONE);


    }
    private void hidepDialog() {
        mProgressView.setVisibility(View.GONE);
        btnRegister.setVisibility(View.VISIBLE);
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
                                postRegistration();
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

    private void postRegistration(){
        try {
            showpDialog();
           // showHideCtr(0);

            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems();
            final String URL = Constant.API_URL + "/Account/Register";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("24")) {
                        Toast.makeText(Register.this, "Email has been taken", Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                    else if(response.equals("23")) {
                        Toast.makeText(Register.this, "Invalid input", Toast.LENGTH_LONG).show();
                        hidepDialog();
                    }
                    else {

                        try {
                            JSONObject obj = new JSONObject(response);
                            editor.putString("Email", email.getText().toString());
                            editor.putString("FirstName",obj.getString("Firstname"));
                            editor.putString("LastName", obj.getString("Lastname"));

                            editor.putString("UserId",obj.getString("Id"));
                            editor.commit();
                            UserDataSource userDataSource = new UserDataSource(Register.this);
                            userDataSource.createUser(firstname.getText().toString(), lastname.getText().toString(), email.getText().toString(), password.getText().toString());
                            String xx = email.getText().toString() + "_" + password.getText().toString();
                            Toast.makeText(Register.this,"Welcome to Report That!",Toast.LENGTH_LONG).show();
                            Library.StartActivity(Register.this, MasterActivity.class, "credentials", xx);
                            hidepDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }






                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Register.this, "Unable to connect to network.", Toast.LENGTH_LONG).show();
                   // showHideCtr(1);
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
                        showpDialog();
                        return null;
                    }
                }
            };

            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
           // showHideCtr(1);
            hidepDialog();
            Toast.makeText(getApplicationContext(),
                    "Unable to connect to network", Toast.LENGTH_LONG).show();
        }
    }






    private String getItems() {


        try {
            JSONObject cartItemsObjedct = new JSONObject();
            cartItemsObjedct.putOpt("Firstname", firstname.getText().toString());
            cartItemsObjedct.putOpt("Lastname", lastname.getText().toString());
            cartItemsObjedct.putOpt("Email",email.getText().toString());
            cartItemsObjedct.putOpt("Phone", phone.getText().toString());
            cartItemsObjedct.putOpt("Password", password.getText().toString());

            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}
