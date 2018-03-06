package com.binacodes.floatinghymns.Songs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



/**
 * Created by eMedrep Nigeria LTD on 10/24/2017.
 */

public class SongFragmentPageAdapter extends FragmentPagerAdapter {
    private static final String TAG = SongFragmentPageAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 3;
    public SongFragmentPageAdapter(FragmentManager fm) {
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
                return "Tracks";
            case 1:
                return "Playlist";
            case 2:
                return "Recent Tracks";
        }
        return null;
    }
}