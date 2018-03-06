package com.binacodes.floatinghymns;

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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.binacodes.floatinghymns.IndicatorHelper.TourContainer;
import com.binacodes.floatinghymns.Util.AppController;
import com.binacodes.floatinghymns.Util.Constant;
import com.binacodes.floatinghymns.Util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



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
        showScreen();
           //StartAnimations();





    }


   


    private void showScreen() {
        Thread splashScreenStarter = new Thread() {
            public void run() {
                int i = 0;
                try {
                    int waitingTime = 500;
                    while (waitingTime < 500) {

                        sleep(100);
                        waitingTime = waitingTime + 10;

                    }
                    Utilities.StartActivity(SplashScreen.this, TourContainer.class);
                      } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    finish();

                }
            }

        };
        splashScreenStarter.start();
    }


    private void getPharmaciesRequest() {


        try {
            JsonArrayRequest req = new JsonArrayRequest(Constant.API_URL + "Songs",
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





                                }

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("dd", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Unable to connect to network", Toast.LENGTH_LONG).show();

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

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println(resultCode);

        if (resultCode == RESULT_OK) {


            showScreen();

        }
        else{
            finish();
            System.exit(0);
        }

    }


}




