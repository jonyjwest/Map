package com.emedrep.reportthat.SyncHelper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

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
 * Created by eMedrep on 1/19/2018.
 */

public class ReportSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG=ReportSyncAdapter.class.getSimpleName();
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
   // public static final int SYNC_INTERVAL=9;
    public static final long SYNC_FLEXTIME=SYNC_INTERVAL/3;
    ContentResolver mContentResolver;
    ReportPendingDataSource reportPendingDataSource;
 int count=0;
    List<ReportPending> reportPendingList;
    public ReportSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver=context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        SyncResult result = new SyncResult();
        try {
            Log.d(LOG_TAG,"Sync Started");
            reportPendingDataSource=new ReportPendingDataSource(getContext());
            reportPendingList=reportPendingDataSource.getAllReportPending();
            if(reportPendingList==null){
                reportPendingList=new ArrayList<>();
            }

            if(reportPendingList.size()>0)
            {
                submitBulkReport(reportPendingList);
            }
        } catch (Exception e) {
            syncResult.hasHardError();
        }
    }


    public void submitBulkReport(List<ReportPending> reportPending){

        for(int i=0;i<reportPending.size();i++){
            postPendingReports(reportPending.get(i));
        }
    }
    private void postPendingReports(final ReportPending reportPending) {
        try {


            RequestQueue queue = Volley.newRequestQueue(getContext());
            final String requestBody = getItems(reportPending);
            final String URL = Constant.API_URL + "/Reports";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {
                        ReportDataSource sql=new ReportDataSource(getContext());
                        sql.updateIsSynced(reportPending.reportId);
                        reportPendingDataSource.deleteReportPending(reportPending.reportpendingId);
                        count++;
                        Log.d(LOG_TAG,count+" row synced");
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

    public static void configurePeriodictSynce(Context context,long syncInterval,long flexTime){
        Account account=getSyncAccount(context);
        String authority="com.emedrep.reportthat";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            SyncRequest request=new SyncRequest.Builder().syncPeriodic(syncInterval,flexTime).setSyncAdapter(account,authority).setExtras(new Bundle()).build();
       ContentResolver.requestSync(request);
        }
        else {
            ContentResolver.addPeriodicSync(account,authority,new Bundle(),syncInterval);
        }
    }

    private static Account getSyncAccount(Context context) {


        AccountManager accountManager=(AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount=new Account("reportthat!","reportthat.emedrep.com");
        if(null==accountManager.getPassword(newAccount)){
            if(!accountManager.addAccountExplicitly(newAccount,"",null)){
                return null;
            }
            onAccountCreated(newAccount,context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        ReportSyncAdapter.configurePeriodictSynce(context,SYNC_INTERVAL,SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,"com.emedrep.reportthat",true);
        syncImmediateLy(context);

    }

    private static void syncImmediateLy(Context context) {

        Bundle bundle=new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        ContentResolver.requestSync(getSyncAccount(context),"com.emedrep.reportthat",bundle);
    }

    public static void initializeSynAdapter(Context context){
        getSyncAccount(context);
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
