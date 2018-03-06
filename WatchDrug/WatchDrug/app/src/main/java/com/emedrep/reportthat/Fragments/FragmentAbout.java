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
        text = "<html><body><p align=\"center\">";
        text += txt;
        text += "" +
                " Have you ever bought a drug, cream, soap, toothpaste or even water that turned out to be fake, counterfeit, or not what it was supposed to be? </br><br>" +
                "" +
                 "Well, now you can send a report directly to <strong> regulatory agencies </strong>with your mobile phone!" +

                "<br>" +
                "Register your vendors when you are within their shops, and if they sell you what they shouldn't, make a report by taking a picture of the product, select your complaint, tag the manufacturer then send. \n" +

                "<br>" +
                "Regulatory agencies will receive a copy of that report and act on it promptly. " +
                "<br>" +

                "Join the fight against fake products! </p>"+

                "</body></html>";


        txtHlp.setText(Html.fromHtml(text));
        return v;
    }
}
