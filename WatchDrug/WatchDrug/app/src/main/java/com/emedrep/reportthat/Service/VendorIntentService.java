package com.emedrep.reportthat.Service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Base64;

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
import com.emedrep.reportthat.Db.ReportDataSource;
import com.emedrep.reportthat.Db.ReportPendingDataSource;
import com.emedrep.reportthat.Db.VendorDataSource;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.ReportPending;
import com.emedrep.reportthat.Model.Visit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VendorIntentService extends IntentService {


    public VendorIntentService() {
        super(null);
    }

    VendorDataSource vendorDataSource;

    List<Visit> vendorList;


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        vendorDataSource = new VendorDataSource(this);
        vendorList = vendorDataSource.getAllVisit();
        if (vendorList == null) {
            vendorList = new ArrayList<>();
        }

        if (vendorList.size() > 0) {
            submitBulkReport(vendorList);
        }
    }

    public void submitBulkReport(List<Visit> vendorList) {

        for (int i = 0; i < vendorList.size(); i++) {
            postVendor(vendorList.get(i));
        }
    }

    private void postVendor(final Visit vendor) {
        try {


            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems(vendor);
            final String URL = Constant.API_URL + "/UserLocations";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {
                        VendorDataSource sql = new VendorDataSource(VendorIntentService.this);

                        sql.deleteVendor(vendor.visitId);

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

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

        }
    }

    private String getItems(Visit rP) {


        try {

            JSONObject cartItemsObjedct = new JSONObject();
            cartItemsObjedct.putOpt("Premises", rP.name);
            cartItemsObjedct.putOpt("LocationType", rP.type);

            cartItemsObjedct.putOpt("LocationAddress", rP.address);

            cartItemsObjedct.putOpt("State", rP.stateId);
            cartItemsObjedct.putOpt("Lga", rP.lgaId);

            cartItemsObjedct.putOpt("Latitude", rP.latitude);
            cartItemsObjedct.putOpt("Longitude", rP.longitude);
            SharedPreferences pre=getSharedPreferences(Constant.PREFERENCE_NAME,0);
            String userId=pre.getString("UserId",null);
            cartItemsObjedct.putOpt("UserId", userId);
            cartItemsObjedct.putOpt("Id", rP.visitId);
            cartItemsObjedct.putOpt("DateCreated", rP.date);
            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}