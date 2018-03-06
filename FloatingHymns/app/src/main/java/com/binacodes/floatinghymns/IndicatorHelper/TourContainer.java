package com.binacodes.floatinghymns.IndicatorHelper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.binacodes.floatinghymns.Db.SongDataSource;
import com.binacodes.floatinghymns.ParentActivity;
import com.binacodes.floatinghymns.R;
import com.binacodes.floatinghymns.Util.AppController;
import com.binacodes.floatinghymns.Util.Constant;
import com.binacodes.floatinghymns.Util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by John on 21/11/2015.
 */
public class TourContainer extends FragmentActivity {

    CircleIndicatorAdapter mAdapter;
    ViewPager mPager;
    int pagerPosition=0;
    ProgressBar pgrGen;
    TextView btnLuunch;
    SongDataSource songDataSource;
    PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_container);
        songDataSource=new SongDataSource(this);
        mAdapter = new CircleIndicatorAdapter(getSupportFragmentManager());
        pgrGen=(ProgressBar)findViewById(R.id.pgrGen);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        String x = getIntent().getStringExtra("categoryId");
        final String position=getIntent().getStringExtra("position");
        if(position=="2"){
           mPager.setCurrentItem(2);
        }

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator = indicator;
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        indicator.setStrokeColor(0xFF000000);
        indicator.setStrokeWidth(0);
        btnLuunch=(TextView)findViewById(R.id.btnNext);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerPosition=position;
               if(position==2){
                 btnLuunch.setText("Launch");
               }
               else{
                   btnLuunch.setText("Next>>");
               }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        btnLuunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pagerPosition==2){
                   if(songDataSource.getAllSong().size()<1){
                        getSongs();
                    }
                    else {
                        Utilities.StartActivity(TourContainer.this, ParentActivity.class);
                    }

                }
                else {
                   mPager.setCurrentItem(pagerPosition+1);
                }

            }
        });
    }

    private void showpDialog() {
        pgrGen.setVisibility(View.VISIBLE);
        btnLuunch.setVisibility(View.GONE);


    }

    private void hidepDialog() {
        pgrGen.setVisibility(View.GONE);
        btnLuunch.setVisibility(View.VISIBLE);

    }
    private void getSongs() {

        showpDialog();
        try {

            JsonArrayRequest req = new JsonArrayRequest(Constant.API_URL + "/Songs",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            try {

                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String title = jsonObject.getString("Title");
                                     String file = jsonObject.getString("FileName");

                                    songDataSource.createSong(title,file,"");


                                }

                                Utilities.StartActivity(TourContainer.this, ParentActivity.class);

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
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });

            int socketTimeout = 50000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
           // queue.add(req);
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

}