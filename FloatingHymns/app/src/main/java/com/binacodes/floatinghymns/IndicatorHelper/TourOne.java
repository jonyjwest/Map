package com.binacodes.floatinghymns.IndicatorHelper;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binacodes.floatinghymns.R;


/**
 * Created by John on 27/10/2015.
 */
public class TourOne extends Fragment {

    SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

         final View v = inflater.inflate(R.layout.tour_one, container, false);

        return v;
    }


}
