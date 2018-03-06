package com.emedrep.reportthat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.emedrep.reportthat.Db.ReportDataSource;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.watchdrug.R;

public class ReportDetails extends AppCompatActivity {

    TextView   txtDrugName, txtManufacturer, txtSuspicious, txtPremiseName,txtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtDrugName=(TextView)findViewById(R.id.txtDrugName);
        txtManufacturer=(TextView)findViewById(R.id.txtManufacturer);
        txtSuspicious=(TextView)findViewById(R.id.txtSuspicious);
        txtPremiseName=(TextView)findViewById(R.id.txtPremiseName);
        txtAddress=(TextView)findViewById(R.id.txtAddress);

        Intent strIntent=getIntent();
        String reportId=strIntent.getStringExtra("reportId");
        ReportDataSource reportDataSource=new ReportDataSource(this);
        Report report=reportDataSource.getReport(Integer.parseInt(reportId));
        txtDrugName.setText(report.drugName);
        txtManufacturer.setText(report.manufacturer);
        String dd=Utilities.getSuspicionType(report.suspicionType);
        if(dd!=null){
            txtSuspicious.setText(dd);
        }

        txtPremiseName.setText(report.premises);
        txtAddress.setText(report.address);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("Report Details");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

