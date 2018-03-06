package com.emedrep.reportthat.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emedrep.watchdrug.R;

/**
 * Created by eMedrep Nigeria LTD on 8/23/2017.
 */

public class FragmentAbout extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_about, container, false);
        getActivity().setTitle("About");
        TextView txtHlp = (TextView)v.findViewById(R.id.txtabout_text);
        String txt = " ";


        String text;
        text = "<html><body><p align=\"left\">";
        text += txt;
        text += "</p>" +
                " Use this app to send reports of incidents (expired drugs, fake drugs, unlicensed premises, etc) to NAFDAC privately, securely and safely. You can send in reports as anonymous . No one can see or access the information you send in except the approved officers of NAFDAC and it's treated with strict confidentiality. It might be you or a loved one or even a stranger that needs help, Use this app to send in the incident in a very quite and discrete manner without anyone knowing about it."+

                "</body></html>";


        txtHlp.setText(Html.fromHtml(text));
        return v;
    }
}
