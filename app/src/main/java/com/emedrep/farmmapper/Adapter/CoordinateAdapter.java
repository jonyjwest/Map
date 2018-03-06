package com.emedrep.farmmapper.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emedrep.farmmapper.Model.Coordinate;
import com.emedrep.farmmapper.R;

import java.util.List;

public class CoordinateAdapter extends ArrayAdapter<Coordinate> {
 Context context;
 int layoutResourceId;
 List<Coordinate> items = null;

 public CoordinateAdapter(Context context, int layoutResourceId, List<Coordinate> items) {
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
  final Coordinate coordinate = items.get(position);
  TextView txtLatitude = (TextView) convertView.findViewById(R.id.txtLatitude);
  txtLatitude.setText(coordinate.getLatitude());
  TextView txtLongitude = (TextView) convertView.findViewById(R.id.txtLongitude);
  txtLongitude.setText(coordinate.getLongitude());
  TextView txtDateCreated = (TextView) convertView.findViewById(R.id.txtDateCreated);
  txtDateCreated.setText(coordinate.getDateCreated());
  return convertView;
 }

 @Override
 public Coordinate getItem(int position) {
  Coordinate coordinate = super.getItem(position);
  return coordinate;
 }
}