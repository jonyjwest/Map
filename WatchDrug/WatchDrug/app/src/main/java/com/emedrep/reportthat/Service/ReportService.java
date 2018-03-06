package com.emedrep.reportthat.Service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

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
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.reportthat.Model.ReportPending;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eMedrep on 1/23/2018.
 */

public class ReportService extends IntentService {


    public ReportService(){
        super(null);



    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    private void postReports(String postItem, final long reportId) {
        try {

                RequestQueue queue = Volley.newRequestQueue(this);
                final String requestBody = postItem;
                final String URL = Constant.API_URL + "/Reports";
            final ReportDataSource sql = new ReportDataSource(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {

                            sql.updateIsSynced(reportId);


                        }

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                       // savePendingReport();

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
                       ;
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
         //   savePendingReport();

        }
    }


    private String savePendingReport(String userId,String reportId,String item) {


        try {
            ReportDataSource sql = new ReportDataSource(this);
            Report report = sql.getReportById(reportId);
            ReportPending xx=new ReportPending();
            ReportPendingDataSource reportPending=new ReportPendingDataSource(this);

            JSONObject jo=new JSONObject(item);

                xx.stateId=Integer.valueOf(jo.getString("StateId"));
                xx.lgaId=Integer.valueOf(jo.getString("LGAId"));;
                xx.address=jo.getString("Address");
                xx.latitude=jo.getString("Latitude");;
                xx.longitude=jo.getString("Longitude");;
                xx.premisesType=Integer.valueOf(jo.getString("PremisesType"));;
                xx.premises=jo.getString("Premises");;
                xx.userId=userId;

                xx.country=report.country;
                xx.postalCodde=report.postalCode;
                xx.relativeAddress=report.relativeAddress;
                xx.thoroughFare=report.thoroughFare;

            reportPending.createReportPending(xx);
            } catch (JSONException e) {
            e.printStackTrace();
        }





        return null;
    }
}
