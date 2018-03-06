package com.emedrep.farmmapper.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.emedrep.farmmapper.Model.Land;
import com.emedrep.farmmapper.PolylineDisplay;
import com.emedrep.farmmapper.R;
import com.emedrep.farmmapper.Registration;
import com.emedrep.farmmapper.Util.Library;

import java.util.List;

public class LandAdapter extends ArrayAdapter<Land> {
 Context context;
 int layoutResourceId;
 List<Land> items = null;

 public LandAdapter(Context context, int layoutResourceId, List<Land> items) {
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
  final Land land = items.get(position);
  TextView txtName = (TextView) convertView.findViewById(R.id.txtName);
  txtName.setText(land.getName());
  TextView txtDateCreated=(TextView)convertView.findViewById(R.id.txtDateCreated);
  txtDateCreated.setText(land.getDateCreated());
  TextView btnGeolocate = (TextView) convertView.findViewById(R.id.btnGeolocate);
  TextView btnView=(TextView)convertView.findViewById(R.id.btnView);
  btnView.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
    Library.StartActivity((Activity)context, PolylineDisplay.class,"landId",land.landId);
   }

  });
  btnGeolocate.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
    Library.StartActivity((Activity)context, Registration.class,"landId",land.landId);
   }
  });

  return convertView;
 }

 @Override
 public Land getItem(int position) {
  Land land = super.getItem(position);
  return land;
 }
}