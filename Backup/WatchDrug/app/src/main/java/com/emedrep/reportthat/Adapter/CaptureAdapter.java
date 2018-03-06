package com.emedrep.reportthat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emedrep.reportthat.CaptureView;
import com.emedrep.reportthat.DrugReport;
import com.emedrep.reportthat.MapActivity;
import com.emedrep.reportthat.Model.Report;
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
    public void onBindViewHolder(CaptureAdapter.ViewHolder viewHolder, final int i) {
//
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemClick(galleryList.get(i),i);
                return false;
            }
        });

       // viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        String path;
//        if(galleryList.get(i).storageType.equals("SDCARD")){
//           path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WatchDrug";
//        }
//        else{
//            ContextWrapper cw=new ContextWrapper(context);
//             path=cw.getDir("ImageDir",Context.MODE_PRIVATE).getPath();
//        }


      // Bitmap bmp = BitmapFactory.decodeFile(galleryList.get(i).CaptureFile);
     //   Bitmap bmp=Utilities.loadBitmap(context,galleryList.get(i).CaptureFile);
        File f = new File(galleryList.get(i).CaptureFile);
        Picasso.with(context).load(f).resize(200, 270).centerCrop().into(viewHolder.img);
       // Picasso.with(context).load(f).into(viewHolder.img);
      //  viewHolder.img.setImageBitmap(Utilities.resizeBitMapImage1(galleryList.get(i).CaptureFile,100,100));
          //viewHolder.img.setImageBitmap(bmp);

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent catIntent=new Intent(context,CaptureView.class);

                catIntent.putExtra("picName",galleryList.get(i).CaptureFile);

                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(catIntent);
            }
        });

        viewHolder.txtDate.setText("Registered Date :"+galleryList.get(i).date);
        if(galleryList.get(i).isReported.equals("true")){
            viewHolder.txtReported.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.txtReported.setVisibility(View.GONE);
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
        
        viewHolder.reportDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent catIntent=new Intent(context,DrugReport.class);
              //  String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WatchDrug";
              //  ContextWrapper cw=new ContextWrapper(context);
              //  File path=cw.getDir("ImageDir",Context.MODE_PRIVATE);
                catIntent.putExtra("picName",galleryList.get(i).CaptureFile);
              //  int xx=galleryList.get(i).reportId;
                catIntent.putExtra("reportId",String.valueOf(galleryList.get(i).reportId));
                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(catIntent);
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

        //  bntView btnReport
        public ViewHolder(View view) {
            super(view);
            txtReported = (TextView) view.findViewById(R.id.txtReported);
            img = (ImageView) view.findViewById(R.id.img);
            viewLocation = (TextView) view.findViewById(R.id.bntView);
            reportDrug = (TextView) view.findViewById(R.id.btnReport);
            txtDate = (TextView) view.findViewById(R.id.date);

        }


    }
}