package com.emedrep.reportthat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emedrep.reportthat.CaptureView;
import com.emedrep.reportthat.Db.ReportDataSource;
import com.emedrep.reportthat.DrugReport;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.MapActivity;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.reportthat.ReportDetails;
import com.emedrep.watchdrug.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by eMedrep Nigeria LTD on 8/10/2017.
 */

public class CaptureAdapter extends RecyclerView.Adapter<CaptureAdapter.ViewHolder> {
    private List<Report> galleryList;
    private Context context;
    private  OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Report item, int i);
    }



    public CaptureAdapter(Context context,List<Report> items, OnItemClickListener listener) {
        this.galleryList = items;
        this.listener = listener;
        this.context = context;
    }
    public CaptureAdapter(Context context, List<Report> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public CaptureAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CaptureAdapter.ViewHolder viewHolder, final int i) {
//
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClick(galleryList.get(i),i);
                return false;
            }
        });

        File f = new File(galleryList.get(i).filePath);
        Picasso.with(context).load(f).resize(200, 270).centerCrop().into(viewHolder.img);

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent catIntent=new Intent(context,CaptureView.class);

                catIntent.putExtra("picName",galleryList.get(i).filePath);

                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(catIntent);
            }
        });

        viewHolder.txtDate.setText("Registered Date :"+galleryList.get(i).dateCreated);
        if(galleryList.get(i).isReported.equals("true")){
            viewHolder.txtReported.setVisibility(View.VISIBLE);
            viewHolder.reportDrug.setText("View Details");
            viewHolder.reportDrug.setTag("1");
            viewHolder.imgDeliverStatus.setImageResource(R.drawable.ic_schedule_black_24dp);
        }

        else
        {
            viewHolder.txtReported.setVisibility(View.GONE);
            viewHolder.reportDrug.setText("Report Product");
            viewHolder.reportDrug.setTag("0");
            viewHolder.imgDeliverStatus.setImageDrawable(null);

        }
        if(galleryList.get(i).isSynced.equals("true")){
            viewHolder.imgDeliverStatus.setImageResource(R.drawable.ic_done_all_black_24dp);
        }
        else if(galleryList.get(i).isReported.equals("true") && !galleryList.get(i).isSynced.equals("true")){
            viewHolder.imgDeliverStatus.setImageResource(R.drawable.ic_schedule_black_24dp);

        }
       else{
            viewHolder.imgDeliverStatus.setImageDrawable(null);
        }
        viewHolder.viewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent catIntent=new Intent(context,MapActivity.class);
                catIntent.putExtra("cordinateMeta",galleryList.get(i).latitude+"_"+galleryList.get(i).longitude);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(catIntent);

            }
        });
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to delete this?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ReportDataSource sdS=new ReportDataSource(context);
                                sdS.deleteReport(galleryList.get(i).reportId);

                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WatchDrug";
                                Utilities.deleteFromExternalStorage(galleryList.get(i).filePath,path);
                                galleryList.clear();

                                galleryList.addAll(sdS.getAllReport());
                                notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        viewHolder.reportDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.reportDrug.getTag().equals("1")){
                    Intent catIntent=new Intent(context,ReportDetails.class);
                    catIntent.putExtra("reportId",String.valueOf(galleryList.get(i).reportId));

                    context.startActivity(catIntent);
                }
                else{
                    Intent catIntent=new Intent(context,DrugReport.class);
                    catIntent.putExtra("picName",galleryList.get(i).filePath);

                    catIntent.putExtra("reportId",String.valueOf(galleryList.get(i).reportId));

                    context.startActivity(catIntent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtReported, txtDate;
        private ImageView img;
        private TextView viewLocation;
        private TextView reportDrug;
        private ImageView imgDeliverStatus,imgDelete;

        //  bntView btnReport
        public ViewHolder(View view) {
            super(view);
            txtReported = (TextView) view.findViewById(R.id.txtReported);
            img = (ImageView) view.findViewById(R.id.img);
            viewLocation = (TextView) view.findViewById(R.id.bntView);
            reportDrug = (TextView) view.findViewById(R.id.btnReport);
            txtDate = (TextView) view.findViewById(R.id.date);
            imgDeliverStatus=(ImageView)view.findViewById(R.id.imgDeliverStatus);
            imgDelete=(ImageView)view.findViewById(R.id.imgDelete);

        }


    }
}