package com.emedrep.reportthat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emedrep.reportthat.Db.VisitDataSource;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.MasterActivity;
import com.emedrep.reportthat.Model.Visit;
import com.emedrep.reportthat.PlaceVisited.EditPlaceVisited;
import com.emedrep.reportthat.PlaceVisited.PlaceVisit;
import com.emedrep.watchdrug.R;

import java.util.List;
 public class VisitAdapter extends ArrayAdapter<Visit> {
Context context;
int layoutResourceId;
List<Visit> items = null;
  OnVisitDeleteListener mCallback;
public VisitAdapter(Context context, int layoutResourceId, List<Visit> items) {
 super(context, layoutResourceId, items);
this.context = context;
this.layoutResourceId = layoutResourceId;
this.items = items;
 }

  public interface  OnVisitDeleteListener{
   public void OnVisitDeleteListener(int position);
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

     mCallback.OnVisitDeleteListener(visit.getVisitId());
     VisitDataSource visitDataSource=new VisitDataSource(context);
     visitDataSource.deleteVisit(visit.getVisitId());
     Toast.makeText(context,"My Places deleted successfully",Toast.LENGTH_LONG).show();
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
