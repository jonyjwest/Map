package com.emedrep.reportthat.Fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.emedrep.reportthat.Db.VirtualDatabase;
import com.emedrep.reportthat.Model.Product;
import com.emedrep.watchdrug.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eMedrep Nigeria LTD on 8/15/2017.
 */


public class FragmentSearch extends Fragment {

    SharedPreferences pref;
    EditText txtSearch;
    List<Product> products;
    VirtualDatabase virtualDatabase;
    ArrayAdapter arrayAdapter;
    TextView txtManufacturer, txtProductName;
    TextView txtActiveIngredient;
    TextView txtNafdact;
    LinearLayout lnContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        getActivity().setTitle("Drug Information Portal");
        final ListView productList = (ListView) v.findViewById(R.id.listDrug);
        txtSearch=(EditText)v.findViewById(R.id.txtSearch);
        txtActiveIngredient=(TextView)v.findViewById(R.id.txtActiveIngredient);
        txtManufacturer=(TextView)v.findViewById(R.id.txtManufacturer);
        txtNafdact=(TextView)v.findViewById(R.id.txtNafdact);
        txtProductName=(TextView)v.findViewById(R.id.txtProductName);
        lnContent=(LinearLayout)v.findViewById(R.id.lnContent);
        products=loadDrugs();
        arrayAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,products);

        productList.setAdapter(arrayAdapter);


        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Product xx=(Product)adapterView.getItemAtPosition(i);

                txtNafdact.setText(xx.nafdac);
                if(xx.manufacturer!=null){
                    if(xx.manufacturer.trim().equals("SAME AS APPLICANT")){
                        txtManufacturer.setText("NOT AVAILABLE");
                    }
                    else{
                        txtManufacturer.setText(xx.manufacturer);
                    }
                }


                txtActiveIngredient.setText(xx.activeIngredient);

                txtProductName.setText(xx.name);
                lnContent.setVisibility(View.VISIBLE);
                productList.setVisibility(View.GONE);




            }
        });

        virtualDatabase=new VirtualDatabase(getActivity());
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                 lnContent.setVisibility(View.GONE);
                productList.setVisibility(View.VISIBLE);
                Log.d("Point of adding songs" ,"On text change");
                List searchList = new ArrayList<Product>();


                    Cursor cursor = virtualDatabase.getWordMatches(txtSearch.getText().toString());
                    if (cursor == null) {
                     if(txtSearch.getText().length()!=0) {
                         arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, searchList);
                         productList.setAdapter(arrayAdapter);
                     }
                        return;
                    }

                    if (cursor.moveToFirst()) {
                        do {
                            Product prd = new Product();
                            prd.setProductId(cursor.getString(0));
                            prd.setName(cursor.getString(1));
                            prd.setActiveIngredient(cursor.getString(2));
                            prd.setManufacturer(cursor.getString(3));
                            prd.setNafdac(cursor.getString(4));
                            searchList.add(prd);
                        }
                        while (cursor.moveToNext());

                        cursor.close();
                    }

                arrayAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,searchList);
                productList.setAdapter(arrayAdapter);
                productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Product xx=(Product)adapterView.getItemAtPosition(i);

                        txtNafdact.setText(xx.nafdac);
                        if(xx.manufacturer!=null){
                            if(xx.manufacturer.trim().equals("SAME AS APPLICANT")){
                                txtManufacturer.setText("NOT AVAILABLE");
                            }
                            else{
                                txtManufacturer.setText(xx.manufacturer);
                            }
                        }
                        txtActiveIngredient.setText(xx.activeIngredient);
                        txtProductName.setText(xx.name);
                        lnContent.setVisibility(View.VISIBLE);
                        productList.setVisibility(View.GONE);
                    }
                });
                }



            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    return v;
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
                product.manufacturer=strings[3];
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


}
