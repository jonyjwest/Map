package com.emedrep.reportthat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.GPSTracker;
import com.emedrep.reportthat.Library.Utilities;

import com.emedrep.watchdrug.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class SplashScreen extends Activity {
    LinearLayout mainLayout;
    ImageView img;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    String returnedString;



    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);


    }


//    private void GetProducts() {
//
//
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, Constant.API_URL + "/products?num=10",
//                new JSONArray(), new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//
//                DrugSql sql = new DrugSql(SplashScreen.this);
//                List<Product> productList = sql.getAllProduct();
//                if (productList.size() < 1) {
//
//
//                    if (response != null) {
//
//                        try {
//
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject obj = (JSONObject) response.get(i);
//                                Product prne = new Product();
//                                prne.productId = obj.getString("ProductId");
//                                prne.name = obj.getString("ProductName");
//                                prne.manufacturer = obj.getString("Manufacturer");
//                                prne.nafdac = obj.getString("NAFDACREGNO");
//                                prne.activeIngredient = obj.getString("ActiveIngridient");
//
//                                sql.createProduct(prne);
//                            }
//                            showScreen();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
//
//
//                    }
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                noInternet();
//            }
//        });
//        int socketTimeout = 10000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsonObjectRequest.setRetryPolicy(policy);
//        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
//    }

    private void noInternet() {
        Intent regIntent = new Intent(SplashScreen.this, InternetIssue.class);
        regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(regIntent);
        finish();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        editor = prefs.edit();
        editor.remove("Latitude");
        editor.remove("Longitude");
        editor.putString("btnReport","btnReport");
        editor.putString("fabVisitedLocation","fabVisitedLocation");
        editor.putString("fabCapture","fabCapture");
        editor.putString("txtDrug","txtDrug");
        editor.commit();
        editor.commit();
        setContentView(R.layout.splash_screen);
        GPSTracker gpsTracker = new GPSTracker(this);
        if (!gpsTracker.getIsGPSTrackingEnabled()) {

            displayLocationSettingsRequest(this);
        } else {

            StartAnimations();
            showScreen();

        }


    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("Loc", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("settings", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SplashScreen.this, 1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("ds", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("ds", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    private void showScreen() {
        Thread splashScreenStarter = new Thread() {
            public void run() {
                int i = 0;
                try {
                    int waitingTime = 100;
                    while (waitingTime < 500) {

                        sleep(100);
                        waitingTime = waitingTime + 10;

                    }
                   // Utilities.StartActivity(SplashScreen.this, MasterActivity.class);
                    if( prefs.getBoolean("LogOut",false)==true && prefs.getString("Email", null) != null  ){
                        Utilities.StartActivity(SplashScreen.this, Login.class);
                    }
                    else if ( prefs.getBoolean("LogOut",false)==false && prefs.getString("Email", null)  != null ) {

                            Utilities.StartActivity(SplashScreen.this, MasterActivity.class);
                        } else {
                            Utilities.StartActivity(SplashScreen.this, Welcome.class);

                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    finish();

                }
            }

        };
        splashScreenStarter.start();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println(resultCode);

        if (resultCode == RESULT_OK) {

            StartAnimations();
            showScreen();

        }
        else{
            finish();
            System.exit(0);
        }

    }


}




