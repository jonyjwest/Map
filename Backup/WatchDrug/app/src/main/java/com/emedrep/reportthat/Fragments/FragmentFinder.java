package com.emedrep.reportthat.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emedrep.reportthat.Adapter.CustomFragmentPageAdapter;
import com.emedrep.watchdrug.R;

/**
 * Created by eMedrep Nigeria LTD on 10/24/2017.
 */

public class FragmentFinder extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public FragmentFinder() {
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new CustomFragmentPageAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
       getActivity().setTitle("Find a Pharmacy");
        return view;
    }
}