package com.emedrep.reportthat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.emedrep.watchdrug.R;

/**
 * Created by John on 4/26/2017.
 */

public class InternetIssue extends Activity implements View.OnClickListener{

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_internet_issue);
        Button bntRetry=(Button)findViewById(R.id.btnRetry);
        bntRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent regIntent=new Intent(this,SplashScreen.class);
        startActivity(regIntent);
    }
}
