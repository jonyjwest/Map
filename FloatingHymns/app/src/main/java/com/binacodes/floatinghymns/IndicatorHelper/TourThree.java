package com.binacodes.floatinghymns.IndicatorHelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.binacodes.floatinghymns.R;
import com.binacodes.floatinghymns.Util.Constant;


/**
 * Created by John on 21/11/2015.
 */
public class TourThree extends Fragment {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View v = inflater.inflate(R.layout.tour_three, container, false);
//        Button btnRegister=(Button)v.findViewById(R.id.register);
//        Button btnSignin=(Button)v.findViewById(R.id.signin);
//        Button btnDemo=(Button)v.findViewById(R.id.demo);
//        progressBar=(ProgressBar)v.findViewById(R.id.progress);
//        btnDemo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//              if(!Utilities.isDeviceOnline(getActivity())){
//                  new ViewDialog(getActivity(),"Unable to connect to network").show();
//
//                  return;
//              }


                prefs = getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, 0);
                editor=prefs.edit();
                editor.clear();
                editor.commit();
                editor.putString("Username", Constant.USERNAME);
                editor.putString("Password", Constant.PASSWORD);
                editor.putBoolean("rememberChecked", true);
                editor.commit();
                // Utilities.deleteJobs(getActivity());
               // Utilities.StartActivity(getActivity(), Landing.class);;
//            }
//        });
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "http://sajenwa.com/request-demo.aspx";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//               // Utilities.StartActivity(getActivity(),Register.class);
//
//            }
//        });
//        btnSignin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utilities.StartActivity(getActivity(),LoginActivity.class);
//            }
//        });


        return v;
    }





    }



