package com.emedrep.reportthat.Service;

import android.app.IntentService;
import android.content.Intent;
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
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.ReportPending;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eMedrep on 1/23/2018.
 */

public class PendingReportService extends IntentService {

    ReportPendingDataSource reportPendingDataSource;

    List<ReportPending> reportPendingList;

    public PendingReportService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        reportPendingDataSource=new ReportPendingDataSource(this);
        reportPendingList=reportPendingDataSource.getAllReportPending();
        if(reportPendingList==null){
            reportPendingList=new ArrayList<>();
        }

        if(reportPendingList.size()>0)
        {
            submitBulkReport(reportPendingList);
        }
    }

    public void submitBulkReport(List<ReportPending> reportPending){

        for(int i=0;i<reportPending.size();i++){
            postPendingReports(reportPending.get(i));
        }
    }
    private void postPendingReports(final ReportPending reportPending) {
        try {


            RequestQueue queue = Volley.newRequestQueue(this);
            final String requestBody = getItems(reportPending);
            final String URL = Constant.API_URL + "/Reports";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {
                        ReportDataSource sql=new ReportDataSource(PendingReportService.this);
                        sql.updateIsSynced(reportPending.reportId);
                        reportPendingDataSource.deleteReportPending(reportPending.reportpendingId);


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
    private String getItems(ReportPending rP) {


        try {
            File file = new File(rP.filePath);

            Bitmap photoBm = BitmapFactory.decodeFile(file.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //  Bitmap photoBm =BitmapFactory.decodeFile(b, options);
            //get its orginal dimensions
            int bmOriginalWidth = photoBm.getWidth();
            int bmOriginalHeight = photoBm.getHeight();
            double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
            double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;
            //choose a maximum height
            int maxHeight = 1024;
            //choose a max width
            int maxWidth = 1024;

            photoBm = Utilities.getScaledBitmap(photoBm, bmOriginalWidth, bmOriginalHeight,
                    originalWidthToHeightRatio, originalHeightToWidthRatio,
                    maxHeight, maxWidth);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photoBm.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            byte[] ba = bytes.toByteArray();
            String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
            JSONObject cartItemsObjedct = new JSONObject();

            cartItemsObjedct.putOpt("StateId",rP.stateId);
            cartItemsObjedct.putOpt("LGAId",rP.lgaId );
            cartItemsObjedct.putOpt("KnownName", rP.knownName);
            cartItemsObjedct.putOpt("Address", rP.address);

            cartItemsObjedct.putOpt("Latitude", rP.latitude);
            cartItemsObjedct.putOpt("Longitude", rP.longitude);
            cartItemsObjedct.putOpt("PremisesType", rP.premisesType);
            cartItemsObjedct.putOpt("Premises", rP.premises);
            cartItemsObjedct.putOpt("UserId",rP.userId);
            cartItemsObjedct.putOpt("SuspicionType", rP.suspicionType);
            cartItemsObjedct.putOpt("IsAnonymous",rP.isAnonymous);
            cartItemsObjedct.putOpt("Capture", ba1);
            cartItemsObjedct.putOpt("DrugName", rP.drugName);
            cartItemsObjedct.putOpt("OtherSuspicion",rP.otherSuspicion);
            //cartItemsObjedct.putOpt("DrugId ", "");
            cartItemsObjedct.putOpt("Country", rP.otherSuspicion);
            cartItemsObjedct.putOpt("PostalCode", rP.postalCodde);

            cartItemsObjedct.putOpt("RelativeAddress", rP.relativeAddress);
            cartItemsObjedct.putOpt("ThoroughFare", rP.thoroughFare);


            return cartItemsObjedct.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}
