package com.emedrep.reportthat.Library;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.emedrep.watchdrug.R;


/**
 * Created by John on 15/12/2015.
 */
public class ViewDialog extends Dialog implements
    View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
       TextView msgBox;
       String msg;
         public ViewDialog(){
             super(null);


         }
        public ViewDialog(Activity a, String msg) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.msg=msg;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.alert_box);
            yes = (Button) findViewById(R.id.btn_dialog);
           // no = (Button) findViewById(R.id.btn_no);
            msgBox=(TextView)findViewById(R.id.text_dialog);
            msgBox.setText(msg);
            yes.setOnClickListener(this);
          //  no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            dismiss();
        }
    }

