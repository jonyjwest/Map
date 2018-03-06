package com.emedrep.reportthat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.emedrep.watchdrug.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class CaptureView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView imgPreview=(ImageView)findViewById(R.id.imgPreview);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setLogo(R.drawable.wdsm);
        //actionBar.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("  Image wide view");
        Intent iin= getIntent();
        String b = iin.getStringExtra("picName");

        if(b!=null)
        {
            imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
           //``  Bitmap bmp = Utilities.loadBitmap(this,b);
           // imgPreview.setImageBitmap(bmp);
            File f = new File(b);
            Picasso.with(this).load(f).resize(250,250).centerCrop().into(imgPreview);
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
