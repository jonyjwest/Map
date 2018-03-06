package com.emedrep.reportthat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emedrep.reportthat.PharmacyFinder.PharmacyDetails;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.Pharmacy;
import com.emedrep.watchdrug.R;

import java.util.List;
 public class PharmacyAdapter extends ArrayAdapter<Pharmacy> {
Context context;
int layoutResourceId;
List<Pharmacy> items = null;

public PharmacyAdapter(Context context, int layoutResourceId, List<Pharmacy> items) {
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

final Pharmacy pharmacy= items.get(position);
 TextView txtPremiseName=(TextView)convertView.findViewById(R.id.txtPremiseName);
 txtPremiseName.setText(pharmacy.getPremiseName());
 TextView txtPremiseAddress=(TextView)convertView.findViewById(R.id.txtPremiseAddress);
 txtPremiseAddress.setText(pharmacy.getPremiseAddress());
 TextView txtDistance=(TextView)convertView.findViewById(R.id.txtDistance);
 txtDistance.setText(Utilities.refineUnit(pharmacy.getDistance()));
    LinearLayout linearLayout=(LinearLayout)convertView.findViewById(R.id.listIem);
    linearLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             Activity contexx=(Activity)context;
            Intent catIntent = new Intent(contexx,PharmacyDetails.class);
            catIntent.putExtra("name", pharmacy.getPremiseName());
            catIntent.putExtra("address", pharmacy.getPremiseAddress());
            catIntent.putExtra("premiseType", Utilities.getPremiseType(pharmacy.getPremiseType()));
            catIntent.putExtra("latitude", pharmacy.getLatitude());
            catIntent.putExtra("longitude", pharmacy.getLongitude());
            catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contexx.startActivity(catIntent);
            contexx.overridePendingTransition(R.anim.right_to_left, R.anim.right);

        }
    });
    //TextView txtTime=(TextView)convertView.findViewById(R.id.txtTime);
// txtTime.setText(pharmacy.getTime());

 return convertView;
}




 @Override
 public Pharmacy getItem(int position) {
Pharmacy pharmacy = super.getItem(position);
return pharmacy;
 }
 }
