package com.emedrep.reportthat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Adapter.VisitAdapter;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.reportthat.MasterActivity;
import com.emedrep.reportthat.Model.Visit;
import com.emedrep.reportthat.PlaceVisited.EditPlaceVisited;
import com.emedrep.reportthat.PlaceVisited.PlaceVisit;
import com.emedrep.watchdrug.R;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Created by eMedrep Nigeria LTD on 12/18/2017.
 */

public class FragmentVisitedLocation extends Fragment{

    ListView listViewPlaces;
    TextView txtInfo;
    List<Visit> visitList;
    VisitAdapter visitAdapter;
    VisitDataSource ss;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_visited_location, container, false);

            listViewPlaces = (ListView) v.findViewById(R.id.listViewPlace);
            txtInfo=(TextView)v.findViewById(R.id.txtInfo);

            getActivity().setTitle("Vendors");

             ss=new VisitDataSource(getActivity());
            visitList = ss.getAllVisit();

            if(visitList==null){
                visitList=new ArrayList<>();
            }
            if(visitList.size()==0){
                txtInfo.setVisibility(View.VISIBLE);
            }
            else{
                txtInfo.setVisibility(View.GONE);
            }

            visitAdapter=new VisitAdapter(getActivity(),R.layout.cell_visit_listview,visitList);

            listViewPlaces.setAdapter(visitAdapter);
            FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                     //Library.StartActivity(getActivity(), PlaceVisit.class);
                    Intent catIntent = new Intent(getActivity(), PlaceVisit.class);
                     startActivity(catIntent);
                   // catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   // catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(catIntent);
                }
            });
            Tooltip.make(getActivity(),
                    new Tooltip.Builder(101)
                            .anchor(fab, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 5000)
                            .activateDelay(900)
                            .showDelay(300)
                            .text("When you are with vendor's  premises, click her to register the place.")
                            .maxWidth(600)
                            .withArrow(true)
                            .withOverlay(true).build()
            ).show();
            return v;
        }

    class VisitAdapter extends ArrayAdapter<Visit> {
        Context context;
        int layoutResourceId;
        List<Visit> items = null;
        com.emedrep.reportthat.Adapter.VisitAdapter.OnVisitDeleteListener mCallback;
        public VisitAdapter(Context context, int layoutResourceId, List<Visit> items) {
            super(context, layoutResourceId, items);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.items = items;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            final Visit visit= items.get(position);
            TextView txtName=(TextView)convertView.findViewById(R.id.txtName);
            txtName.setText(visit.getName());
            TextView txtAddress=(TextView)convertView.findViewById(R.id.txtAddress);
            txtAddress.setText(visit.getAddress());

            TextView txtDate=(TextView)convertView.findViewById(R.id.txtDate);
            txtDate.setText(visit.getDate());

            Button btnEdit=(Button)convertView.findViewById(R.id.btnEdit);
            Button btnDelete=(Button)convertView.findViewById(R.id.btnDelete);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent catIntent = new Intent(context, EditPlaceVisited.class);

                    catIntent.putExtra("visitId", String.valueOf(visit.getVisitId()));

                    context.startActivity(catIntent);
                    //  Utilities.StartActivity((Activity)context, EditPlaceVisited.class,"visitId",String.valueOf(visit.getVisitId()));
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to delete?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            VisitDataSource visitDataSource=new VisitDataSource(context);
                            visitDataSource.deleteVisit(visit.getVisitId());

                            visitList = ss.getAllVisit();

                            if(visitList==null){
                                visitList=new ArrayList<>();
                            }
                            if(visitList.size()==0){
                                txtInfo.setVisibility(View.VISIBLE);
                            }
                            else{
                                txtInfo.setVisibility(View.GONE);
                            }

                            visitAdapter=new VisitAdapter(getActivity(),R.layout.cell_visit_listview,visitList);

                            listViewPlaces.setAdapter(visitAdapter);
                            Toast.makeText(context," Place deleted successfully",Toast.LENGTH_LONG).show();



                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            return convertView;
        }
        @Override
        public Visit getItem(int position) {
            Visit visit = super.getItem(position);
            return visit;
        }
    }
}
