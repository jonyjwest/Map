package com.emedrep.reportthat;

import android.content.Intent;
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
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.watchdrug.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;

    private ProgressBar pDialog;
    Button mEmailSignInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.email);

        pDialog = (ProgressBar)findViewById(R.id.pgrGen);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    private void attemptLogin() {

        email.setError(null);

        boolean cancel = false;
        View focusView = null;
        //  UserDataSource userDataSource = new UserDataSource(com.emedrep.reportthat.ForgotPassword.this);

        String memail = email.getText().toString();

        if (TextUtils.isEmpty(memail)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }


        if (cancel) {

            focusView.requestFocus();
        } else {

            postPassword();
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
                     // String  takeResponse=TextUtils.substring(response,1,5);
                        if(response.equals("26")){
                            Toast.makeText(ForgotPassword.this,"Email",Toast.LENGTH_LONG).show();
                            hidepDialog();


                        }
                        else if(response.equals("false")){
                            Toast.makeText(ForgotPassword.this,"Something went wrong",Toast.LENGTH_LONG).show();
                            hidepDialog();
                        }

                        else{
                            hidepDialog();
                            Intent catIntent = new Intent(ForgotPassword.this, ForgotPassword2.class);

                            catIntent.putExtra("useremail",email.getText().toString());

                            startActivity(catIntent);

                        }

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ForgotPassword.this,"Unable to connect to network",Toast.LENGTH_LONG).show();
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

            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}