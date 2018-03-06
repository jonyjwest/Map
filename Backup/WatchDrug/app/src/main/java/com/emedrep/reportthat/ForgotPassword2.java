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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.watchdrug.R;

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
            Intent catIntent = new Intent(ForgotPassword2.this, CreatePassword.class);

            catIntent.putExtra("code",code.getText().toString());
            catIntent.putExtra("useremail",email);

            startActivity(catIntent);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}