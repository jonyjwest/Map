package com.emedrep.reportthat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.emedrep.reportthat.Fragments.FragmentMap;
import com.emedrep.watchdrug.R;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
     //  actionBar.setLogo(R.drawable.actionlogo);
        actionBar.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("  Product Location");
        Intent iin= getIntent();
        String b = iin.getStringExtra("cordinateMeta");

        if(b!=null)
        {
            Fragment xx=new FragmentMap();
            Bundle bundle = new Bundle();
            xx.setArguments(bundle);
            bundle.putString("cordinateMeta", b );
            getSupportFragmentManager ().beginTransaction().add(R.id.flMapContent,xx).commit();

        }
        else{
            Toast.makeText(this,"Unable to get cordinate",Toast.LENGTH_LONG).show();
        }
       // String cordinateMeta=get();
       // if(getIntent()) cordinateMeta


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
