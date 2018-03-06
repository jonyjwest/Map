package com.emedrep.farmmapper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emedrep.farmmapper.DB.LandDataSource;
import com.emedrep.farmmapper.Util.Library;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewFarmLand extends AppCompatActivity {

    EditText edtName;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_farm_land);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edtName = (EditText) findViewById(R.id.edtName);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        btnSave=(Button)findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validate()){
                    return;
                }
            }
        });
    }


    private boolean validate() {
        boolean cancel = false;
        View focusView = null;
        edtName.setError(null);

        String name = edtName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name field_is required");
            focusView = edtName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            LandDataSource landDataSource=new LandDataSource(this);
            long i=landDataSource.createLand(name,timeStamp);
            if(i>0){
                Library.StartActivity(this,MainActivity.class);
                Toast.makeText(this,"Land added successfully",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Unable to submit",Toast.LENGTH_LONG).show();
            }

        }
        return cancel;
    }



}
