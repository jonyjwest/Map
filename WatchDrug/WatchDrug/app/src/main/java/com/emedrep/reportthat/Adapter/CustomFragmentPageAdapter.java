package com.emedrep.reportthat.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.emedrep.reportthat.PharmacyFinder.NearestPharmacyMap;
import com.emedrep.reportthat.PharmacyFinder.NearestPharmacy;

/**
 * Created by eMedrep Nigeria LTD on 10/24/2017.
 */

public class CustomFragmentPageAdapter extends FragmentPagerAdapter {
    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 2;
    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NearestPharmacy();

            case 1:
                return new NearestPharmacyMap();
        }
        return null;
    }
    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Pharmacies";
            case 1:
                return "Map";

        }
        return null;
    }
}