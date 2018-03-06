package com.emedrep.reportthat.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.emedrep.reportthat.Db.ReportDataSource;
import com.emedrep.reportthat.Db.ReportPendingDataSource;
import com.emedrep.reportthat.Model.Photo;
import com.emedrep.reportthat.Adapter.CaptureAdapter;
import com.emedrep.reportthat.CaptureView;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.GPSTracker;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.reportthat.Model.ReportPending;
import com.emedrep.reportthat.Service.PendingReportService;
import com.emedrep.watchdrug.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.tooltip.Tooltip;

import static android.app.Activity.RESULT_OK;

/**
 * Created by eMedrep Nigeria LTD on 8/10/2017.
 */

public class FragmentCaptures extends Fragment{

     int reportId, position;
    String picName;
    ReportDataSource ss;
    RecyclerView recyclerView;
    List<Report> createLists;
    CaptureAdapter captureAdapter;
    String mCurrentPhotoPath;
    static SharedPreferences prefs;
    TextView txtInfo;
    ReportPendingDataSource reportPendingDataSource;
    List<ReportPending> reportPendingList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        reportPendingDataSource=new ReportPendingDataSource(getActivity());
        reportPendingList=reportPendingDataSource.getAllReportPending();
        if(reportPendingList==null){
            reportPendingList=new ArrayList<>();
        }

        if(reportPendingList.size()>0)
        {
            Intent mServiceIntent = new Intent(getActivity(), PendingReportService.class);
            getActivity().startService(mServiceIntent);
       }
        View v = inflater.inflate(R.layout.captures, container, false);
        prefs =getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        recyclerView = (RecyclerView)v.findViewById(R.id.imagegallery);
         txtInfo=(TextView)v.findViewById(R.id.txtInfo);
        recyclerView.setHasFixedSize(true);

         recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),0));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(),1);
        getActivity().setTitle("Gallery");
        recyclerView.setLayoutManager(layoutManager);
        ss=new ReportDataSource(getActivity());

         createLists = ss.getAllReport();

        if(createLists==null){
            createLists=new ArrayList<>();
        }
        if(createLists.size()==0){
            txtInfo.setVisibility(View.VISIBLE);
        }
        else{
            txtInfo.setVisibility(View.GONE);
        }

        registerForContextMenu(recyclerView);
       captureAdapter=new CaptureAdapter(getActivity(),createLists, new CaptureAdapter.OnItemClickListener() {
           @Override public void onItemClick(Report item, int i) {
               reportId=item.reportId;
               position=i;
               picName=item.filePath;


           }
       });
        recyclerView.setAdapter(captureAdapter);

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);

        if(prefs.getString("fabCapture",null)!=null) {
            Tooltip.make(getActivity(),
                    new Tooltip.Builder(101)
                            .anchor(fab, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 5000)
                            .activateDelay(900)
                            .showDelay(400)
                            .text("Take a picture of the product with the receipt to send a report.")
                            .maxWidth(600)
                            .withArrow(true)
                            .withOverlay(true).build()
            ).show();
            Utilities.clearPreference(getActivity(),"fabCapture");
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();

             //  Library.StartActivity(getActivity(), Camera.class);
            }
        });
        return v;
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, 100);
            }
        }
    }





    private File createImageFile() throws IOException {
        // TODO Auto-generated method stub
        //String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        String TimeStamp = new SimpleDateFormat("yyyyMMDdd_HHmmss",Locale.getDefault()).format(new Date());
        String ImageFile = "JPEG_" + TimeStamp + "_.jpg";

        File StorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        new File(StorageDir + "/WatchDrug").mkdirs();
        File outputfile=new File(StorageDir+"/WatchDrug/", ImageFile);

        mCurrentPhotoPath = outputfile.getAbsolutePath();

       return outputfile;

    }
    private void setPic() {
        try {
               // Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
         //   Bitmap bm = Utilities.resizeBitMapImage1(mCurrentPhotoPath,100,100);
            //Utilities.saveImageToExternalStorage(bm);
             // ByteArrayOutputStream bao = new ByteArrayOutputStream();

                //bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
               // byte[] ba = bao.toByteArray();
          getOutputMediaFile(mCurrentPhotoPath);
        }
        catch (Exception ex){
          new ViewDialog(getActivity(),"Image capturing failed. Please try again").show();
            return ;
        }
    }


    private void getOutputMediaFile(String path) {
        try {
            String memoryType = "";
            String latValue = prefs.getString("Latitude", null);
            String longValue = prefs.getString("Longitude", null);
            GPSTracker  gpsTracker = new GPSTracker(getActivity());
            if (!gpsTracker.getIsGPSTrackingEnabled()) {
                gpsTracker.showSettingsAlert();
            }
            if (latValue == null) {
                latValue = String.valueOf(gpsTracker.getLatitude());
                longValue = String.valueOf(gpsTracker.getLongitude());
            }

            String timeStamp = new SimpleDateFormat("HHmmSS")
                    .format(new Date());

            Geocoder geocoder;
            List<Address> addresses;

            Report report = new Report();
            try {
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                addresses = geocoder.getFromLocation(Double.parseDouble(latValue), Double.parseDouble(longValue), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String relativeAddress = addresses.get(0).getSubAdminArea();
                String premise = addresses.get(0).getSubAdminArea();

                String thoroughFare = addresses.get(0).getThoroughfare();
                report.relativeAddress = relativeAddress;
                report.thoroughFare = thoroughFare;
                report.premises = premise;
                report.address = address;
                report.country = country;
                report.city = city;
                report.knowName = knownName;

                report.postalCode = postalCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {


                report.filePath = path;

                report.latitude = latValue;
                report.longitude = longValue;
                report.dateCreated= new SimpleDateFormat("dd/MM/yyyy KK:mm a")
                        .format(new Date());

                report.isReported = "0";
                report.isSynced="false";

                ReportDataSource sql = new ReportDataSource(getActivity());

                long createIndex = sql.createReport(report);
                createLists = ss.getAllReport();
                captureAdapter=new CaptureAdapter(getActivity(),createLists, new CaptureAdapter.OnItemClickListener() {
                    @Override public void onItemClick(Report item, int i) {
                        reportId=item.reportId;
                        position=i;
                        picName=item.filePath;


                    }
                });
                recyclerView.setAdapter(captureAdapter);
                if(createLists.size()==0){
                    txtInfo.setVisibility(View.VISIBLE);
                }
                else{
                    txtInfo.setVisibility(View.GONE);
                }
            }
        }catch (Exception ex){
            Log.d("Storage error",ex.getMessage());
        }
        // return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
           // mCurrentPhotoPath
            setPic();

        }
    }


    private ArrayList<Photo> prepareData() {


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WatchDrug";
        File f = new File(path);
        File file[] = f.listFiles();

        if (file != null) {
            Arrays.sort(file, Collections.reverseOrder());
            ArrayList<Photo> theimage = new ArrayList<>();

            for (int i = 0; i < file.length; i++) {
                Photo createList = new Photo();
                createList.setImage_title(file[i].getName());
                createList.setLocation(path + "/" + file[i].getName());
                theimage.add(createList);
            }
            return theimage;
        }
        return new ArrayList<>();
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.menu_delete:
          new AlertDialog.Builder(getActivity())
                        .setMessage("Are you sure you want to delete this?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ss.deleteReport(reportId);
                                createLists.remove(position);
                                captureAdapter.notifyDataSetChanged();
                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/WatchDrug";
                                Utilities.deleteFromExternalStorage(picName,path);
                                if(createLists.size()==0){
                                    txtInfo.setVisibility(View.VISIBLE);
                                }
                                else{
                                    txtInfo.setVisibility(View.GONE);
                                }
                       }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            case R.id.menu_view:
                Intent catIntent=new Intent(getActivity(),CaptureView.class);

                catIntent.putExtra("picName",picName);

                catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(catIntent);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.menu_2, menu);
    }
}
