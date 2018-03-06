package com.emedrep.reportthat.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emedrep.reportthat.Db.ReportSql;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.GPSTracker;
import com.emedrep.reportthat.Library.ViewDialog;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.watchdrug.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.tooltip.Tooltip;

import static android.app.Activity.RESULT_OK;

/**
 * Created by eMedrep Nigeria LTD on 12/12/2017.
 */

public class FragmentLanding extends Fragment implements View.OnClickListener {

    Button btnReport;
    String mCurrentPhotoPath;
    static SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.landing, container, false);
        prefs =getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        btnReport=(Button)v.findViewById(R.id.btnReport);

        Tooltip.make(getActivity(),
                new Tooltip.Builder(101)
                        .anchor(btnReport, Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 5000)
                        .activateDelay(900)
                        .showDelay(400)
                        .text("Take a picture of the product with the receipt by clicking on the button.")
                        .maxWidth(600)
                        .withArrow(true)
                        .withOverlay(true).build()
        ).show();
        getActivity().setTitle("  Report That!");
       // getActivity().setTitle("");
        btnReport.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
             captureImage();
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

        String TimeStamp = new SimpleDateFormat("yyyyMMDdd_HHmmss", Locale.getDefault()).format(new Date());
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
            GPSTracker gpsTracker = new GPSTracker(getActivity());
            if (!gpsTracker.getIsGPSTrackingEnabled()) {
                gpsTracker.showSettingsAlert();
            }
            if (latValue == null) {
                latValue = String.valueOf(gpsTracker.getLatitude());
                longValue = String.valueOf(gpsTracker.getLongitude());
            }

            String timeStamp = new SimpleDateFormat("HHmmSS")
                    .format(new Date());
            File mediaFile;
            //mediaFile = new File(mediaStorageDir.getPath() + File.separator
            //   + "IMG_"+latValue+"_"+longValue+"_" +timeStamp+ ".jpg");
            //  String picName = "IMG_" + latValue + "_" + longValue + "_" + timeStamp + ".jpg";
            //  Bitmap bitmap = BitmapFactory.decodeByteArray(m, 0, m.length);

            //Utilities.saveFle(getActivity(), bitmap, picName);
            Geocoder geocoder;
            List<Address> addresses;

            Report report = new Report();
            try {
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                addresses = geocoder.getFromLocation(Double.parseDouble(latValue), Double.parseDouble(longValue), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String relativeAddress = addresses.get(0).getSubAdminArea();
                String premise = addresses.get(0).getSubAdminArea();
                String subThoroughFare = addresses.get(0).getSubThoroughfare();
                String thoroughFare = addresses.get(0).getThoroughfare();
                report.subThoroughFare = subThoroughFare;
                report.relativeAddress = relativeAddress;
                report.thoroughFare = thoroughFare;
                report.premise = premise;
                report.address = address;
                report.country = country;
                report.city = city;
                report.knownName = knownName;
                report.state = state;
                report.postalCode = postalCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {


                report.CaptureFile = path;

                report.latitude = latValue;
                report.longitude = longValue;
                report.date= new SimpleDateFormat("dd/MM/yyyy KK:mm a")
                        .format(new Date());
                report.storageType = memoryType;
                report.isReported = "0";

                ReportSql sql = new ReportSql(getActivity());

                long createIndex = sql.createReport(report);

            }
        }catch (Exception ex){
            Log.d("Storage error",ex.getMessage());
        }
        // return mediaFile;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setPic();
        Fragment fragment = null;
        if (requestCode == 100 && resultCode == RESULT_OK) {
            try {
               Class fragmentClass = FragmentCaptures.class;
               fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
