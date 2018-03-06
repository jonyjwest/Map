package com.emedrep.reportthat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.reportthat.Model.NameValue;
import com.emedrep.reportthat.Model.Product;
import com.emedrep.watchdrug.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;

public class DrugReport extends AppCompatActivity implements View.OnClickListener{

    ImageView imgPreview;
    Spinner spnSuspicion;
    SharedPreferences pre;
    String b;
    String reportId;
    LinearLayout lnbtn;
    private ProgressBar pDialog;
    TextView myTextProgress,txt_skip;
    AutoCompleteTextView txtDrugName,txtManufacturer;
    Button btnReport,btnTryAgain;
    private NameValue selectedSuspicion;
    RelativeLayout lnInternetIssue;
    EditText txtSuspicious;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_report);
        lnInternetIssue = (RelativeLayout)findViewById(R.id.lnInternetIssue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pre=getSharedPreferences(Constant.PREFERENCE_NAME,0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setTitle("Report Product");
        spnSuspicion=(Spinner)findViewById(R.id.spnSuspicion);
        imgPreview=(ImageView)findViewById(R.id.imgPreview);
        btnReport=(Button)findViewById(R.id.btnReport);
        btnTryAgain=(Button)findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(this);
        btnReport.setOnClickListener(this);

        txtDrugName=(AutoCompleteTextView) findViewById(R.id.txtDrugName);
        txtManufacturer=(AutoCompleteTextView) findViewById(R.id.txtManufacturer);

        if(pre.getString("txtDrug",null)!=null) {
            Tooltip.make(this,
                    new Tooltip.Builder(101)
                            .anchor(txtDrugName, Tooltip.Gravity.TOP)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 5000)
                            .activateDelay(900)
                            .showDelay(400)
                            .text("Type product or select from the suggestion list.")
                            .maxWidth(600)
                            .withArrow(true)
                            .withOverlay(true).build()
            ).show();
            Utilities.clearPreference(DrugReport.this,"txtDrug");
        }
        //Type product or select from the suggestion list.

        txtSuspicious=(EditText)findViewById(R.id.txtSuspicious);

        List<Product> productList=loadDrugs();
        ArrayAdapter<Product> adapter = new ArrayAdapter<Product>
                (this,android.R.layout.simple_list_item_1,productList);
        txtDrugName.setAdapter(adapter);
        txtDrugName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product=(Product)parent.getItemAtPosition(position);

                txtManufacturer.setText(product.manufacturer);

            }
        });

        myTextProgress=(TextView)findViewById(R.id.myTextProgress);
        pDialog = (ProgressBar)findViewById(R.id.pgrGen);
        // TextView gpsSignal=(TextView)v.findViewById(R.id.gpsSignal);
        lnbtn=(LinearLayout)findViewById(R.id.lnbtn);

        Intent iin= getIntent();
         b = iin.getStringExtra("picName");
         reportId=iin.getStringExtra("reportId");

        //TextView rndName=(TextView) findViewById(R.id.txtName);
       // rndName.setText(pre.getString("LastName",null) +" "+pre.getString("FirstName",null));
        loadSuspicion();
        if(b!=null)
        {
           imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
           // Bitmap bmp = Utilities.loadBitmap(this,b);
            File f = new File(b);
          //  Picasso.with(this).load(f).into(imgPreview);


           // File f = new File(b);
         Picasso.with(this).load(f).resize(200, 147).centerCrop().into(imgPreview);


        }


    }

    private List<Product> loadDrugs() {

        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.drugs);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final List<Product> products = new ArrayList<Product>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equals("")){
                    continue;
                }
                String[] strings = TextUtils.split(line, "@");
                Product product=new Product();
                product.productId=strings[0];
                product.name=strings[1];
                product.activeIngredient=strings[2];
                if(product.manufacturer!=null){
                    if(product.manufacturer.trim().equals("SAME AS APPLICANT")){
                        product.manufacturer="";
                    }
                    else{
                        product.manufacturer=strings[3];
                    }
                }

                product.nafdac=strings[4];
                products.add(product);
            }


            return products;
        }
        catch (IOException ex){
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("done", "DONE loading words.");
        return null;
    }

    private void loadSuspicion(){

        try {

            List<NameValue> mList=new ArrayList<>();
            NameValue mV0=new NameValue();
            mV0.setName("-Suspicion-");
            mV0.setValue(0);
            mList.add(mV0);

            NameValue mV1=new NameValue();
            mV1.setName("Expired Product");
            mV1.setValue(1);
            mList.add(mV1);

            NameValue mV2=new NameValue();
            mV2.setName("Fake Product");
            mV2.setValue(2);
            mList.add(mV2);

            NameValue mV3=new NameValue();
            mV3.setName("Unlicensed Vendor");
            mV3.setValue(3);
            mList.add(mV3);

            NameValue mV4=new NameValue();
            mV4.setName("Others");
            mV4.setValue(4);
            mList.add(mV4);



            ArrayAdapter<NameValue> arrayAdapter = new ArrayAdapter<NameValue>(this,R.layout.spinner_item, mList);

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnSuspicion.setAdapter(arrayAdapter);
            spnSuspicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                    selectedSuspicion = (NameValue) parent.getItemAtPosition(pos);
                    if(selectedSuspicion.getValue()==4){
                        txtSuspicious.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtSuspicious.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }








    private boolean validate() {
        txtDrugName.setError(null);
        String drugName = txtDrugName.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(drugName)) {
            txtDrugName.setError("Drug Name is required");
            focusView = txtDrugName;
            cancel = true;
            return false;
        }

       if(selectedSuspicion.getValue()==0){
           new ViewDialog(this,"Please select suspicion type").show();
           return false;
       }

        if (cancel) {

            focusView.requestFocus();
        }
       return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v==btnReport) {
            if (!validate()) {
                return;

            }
            else{
                Intent catIntent = new Intent(this, DrugReport2.class);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                catIntent.putExtra("DrugName",txtDrugName.getText().toString());
                catIntent.putExtra("Manufacturer",txtManufacturer.getText().toString());
                catIntent.putExtra("OtherSuspicion",txtSuspicious.getText().toString());
                String dd=String.valueOf(selectedSuspicion.getValue());
                catIntent.putExtra("SuspicionType",dd);
                catIntent.putExtra("b",b);
                catIntent.putExtra("reportId",reportId);


                startActivity(catIntent);
                overridePendingTransition(R.anim.right_to_left, R.anim.right);


            }
        }

    }


}
