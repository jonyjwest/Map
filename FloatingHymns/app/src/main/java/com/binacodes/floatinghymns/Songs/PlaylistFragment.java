package com.binacodes.floatinghymns.Songs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binacodes.floatinghymns.R;


/**
 * Created by John on 1/21/2018.
 */

public class PlaylistFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_fragment, container, false);

       
        return view;
    }
}