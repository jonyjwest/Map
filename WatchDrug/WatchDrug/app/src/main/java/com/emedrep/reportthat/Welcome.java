package com.emedrep.reportthat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.watchdrug.R;

import java.util.ArrayList;


/**
 * Created by eMedrep Nigeria LTD on 10/17/2017.
 */

public class Welcome extends Activity  implements View.OnClickListener {

    Button btnLaunchReg;
    TextView txtRegister;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        btnLaunchReg = (Button) findViewById(R.id.btnLaunchReg);
        btnLaunchReg.setOnClickListener(this);
        txtRegister = (TextView) findViewById(R.id.sign_up);
        txtRegister.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        if (view == btnLaunchReg) {
            Utilities.StartActivity(Welcome.this, Login.class);
        }
        if (view == txtRegister) {
            Utilities.StartActivity(Welcome.this, Register.class);
        }
    }
}