package com.emedrep.reportthat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emedrep.reportthat.Model.Visit;
import com.emedrep.watchdrug.R;

import java.util.List;

public class VisitAdapter2 extends ArrayAdapter<Visit> {
Context context;
int layoutResourceId;
List<Visit> items = null;
public VisitAdapter2(Context context, int layoutResourceId, List<Visit> items) {
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
return convertView;
}
@Override
public Visit getItem(int position) {
Visit visit = super.getItem(position);
return visit;
}
}
