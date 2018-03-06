package com.binacodes.floatinghymns.Downloads;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.binacodes.floatinghymns.Songs.PlaylistFragment;
import com.binacodes.floatinghymns.Songs.RecentTrackFragment;
import com.binacodes.floatinghymns.Songs.TrackFragment;


/**
 * Created by eMedrep Nigeria LTD on 10/24/2017.
 */

public class DownloadFragmentPageAdapter extends FragmentPagerAdapter {
    private static final String TAG = DownloadFragmentPageAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 2;
    public DownloadFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TrackFragment();

            case 1:
                return new PlaylistFragment();
            case 2:
                return new RecentTrackFragment();
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
                return "Paid";
            case 1:
                return "Free";
            case 2:
                return "Recent Tracks";
        }
        return null;
    }
}