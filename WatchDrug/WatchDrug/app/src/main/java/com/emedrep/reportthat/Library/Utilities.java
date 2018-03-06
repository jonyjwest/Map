package com.emedrep.reportthat.Library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.emedrep.reportthat.Db.PharmacyDataSource;
import com.emedrep.reportthat.Model.Pharmacy;
import com.emedrep.watchdrug.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Created by John on 21/11/2015.
 */
public class Utilities {

    public static void StartActivityBackAnimation(Activity context1, Class<?> context2) {
        Intent catIntent = new Intent(context1, context2);

        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.back_in, R.anim.back_out);
    }
    public static void StartActivity(Activity context1, Class<?> context2) {
        Intent catIntent = new Intent(context1, context2);
       // catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.right_to_left, R.anim.right);
    }

    public  static void saveFle(Context context,Bitmap b,String picName){
        FileOutputStream fos=null;
        try{
            fos=context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG,100,fos);
        }catch (FileNotFoundException e){
            Log.d("File Not found","File not found");
            e.printStackTrace();
        }
        catch (IOException e){
            Log.d("io exception","Io exception");
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFromExternalStorage(String fileName,String path) {

        try
        {
            File file = new File(path, fileName);
            if(file.exists())
                file.delete();
        }
        catch (Exception e)
        {
            Log.e("App", "Exception while deleting file " + e.getMessage());
        }
    }
    public  static void deleteFle(Context context,String picName){
        FileOutputStream fos=null;
        try{
            fos=context.openFileOutput(picName, Context.MODE_PRIVATE);
           File f = new File(System.getProperty("user.dir"),picName);
            fos.close();

            f.delete();
        }catch (FileNotFoundException e){
            Log.d("File Not found","File not found");
            e.printStackTrace();
        }
        catch (IOException e){
            Log.d("io exception","Io exception");
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static Bitmap resizeBitMapImage1(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitMapImage = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            double sampleSize = 0;
            Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth
                    - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize = scaleByHeight ? options.outHeight / targetHeight : options.outWidth / targetWidth;
                sampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
            }
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[128];
            while (true) {
                try {
                    options.inSampleSize = (int) sampleSize;
                    bitMapImage = BitmapFactory.decodeFile(filePath, options);
                    break;
                } catch (Exception ex) {
                    try {
                        sampleSize = sampleSize * 2;
                    } catch (Exception ex1) {

                    }
                }
            }
        } catch (Exception ex) {

        }
        return bitMapImage;
    }
    public static Location getMyLocation(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        String latValue = prefs.getString("Latitude", null);
        String longValue = prefs.getString("Longitude", null);
        GPSTracker  gpsTracker = new GPSTracker(context);
        if (!gpsTracker.getIsGPSTrackingEnabled()) {
            gpsTracker.showSettingsAlert();
        }
        if (latValue == null) {
            latValue = String.valueOf(gpsTracker.getLatitude());
            longValue = String.valueOf(gpsTracker.getLongitude());
        }
        Location loc1 = new Location("My Location");
        loc1.setLatitude(Double.parseDouble(latValue));
        loc1.setLongitude(Double.parseDouble(longValue));
        return  loc1;
    }

    public static Comparator<String> StringAscComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;

            return stringName1.compareToIgnoreCase(stringName2);
        }
    };

    public static List<Pharmacy> getClosestPharmacies(Context context){
       PharmacyDataSource pharmacyDataSource=new PharmacyDataSource(context);
        List<Pharmacy> pharmacyList=pharmacyDataSource.getAllPharmacy(context);
        Comparator <Pharmacy> com=new Comparator<Pharmacy>() {
            @Override
            public int compare(Pharmacy o1, Pharmacy o2) {
                return  o1.getDistance()<o2.getDistance()?-1 :(o1.getDistance()==o2.getDistance()?0:1);

            }
        };
        List<Pharmacy> pharmacies=new ArrayList<>();
        if(pharmacyList!=null) {
            Collections.sort(pharmacyList, com);
             for(int i=0; i<10; i++){
                pharmacies.add(pharmacyList.get(i));
            }
        }

            return pharmacies;

    }

    public static float getDistance(double lat1, double lon1, double lat2, double lon2) {
        String result_in_kms = "";
        String url = "http://maps.google.com/maps/api/directions/xml?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric";
        String tag[] = {"text"};
        HttpResponse response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost, localContext);
            InputStream is = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            if (doc != null) {
                NodeList nl;
                ArrayList args = new ArrayList();
                for (String s : tag) {
                    nl = doc.getElementsByTagName(s);
                    if (nl.getLength() > 0) {
                        Node node = nl.item(nl.getLength() - 1);
                        args.add(node.getTextContent());
                    } else {
                        args.add(" - ");
                    }
                }
                result_in_kms =String.valueOf( args.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Float f=Float.valueOf(result_in_kms);
        return f*1000;
    }
    public static Bitmap loadBitmap(Context context,String picName){
       Bitmap b=null;
        FileInputStream fis=null;
        try{
            fis=context.openFileInput(picName);
            b= BitmapFactory.decodeStream(fis);
        }
        catch (FileNotFoundException e){

        }

        finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public static void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }




    }
   public static String getPremiseType(String xx){
       switch (xx){
           case "1":{
               return "Wholesaler";
           }
           case "2":{
               return "Retailer";
           }
           case "3":{
               return "Distributor                                       ";
           }
       }
       return "";
   }

    public static String getSuspicionType(String xx){
try {
    switch (xx) {
        case "1": {
            return "Expired Product";
        }
        case "2": {
            return "Fake Product";
        }
        case "3": {
            return "Unlicensed Vendor";
        }
        case "4": {
            return "Others ";
        }
        default: {
            return "Not Available";
        }
    }
}
catch (Exception ex){
    return null;
}
    }


    public static void setPreference(Context context,String key,String value){
       SharedPreferences prefs = context.getSharedPreferences(Constant.PREFERENCE_NAME, 0);
       SharedPreferences.Editor editor = prefs.edit();
       editor.putString(key,value);
       editor.commit();
    }
    public static void clearPreference(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
    public static void StartActivity(Activity context1, Class<?> context2, String extra, String value) {
        Intent catIntent = new Intent(context1, context2);
        catIntent.putExtra(extra, value);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.right_to_left, R.anim.right);
    }
    public static Bitmap getScaledBitmap(Bitmap bm, int bmOriginalWidth, int bmOriginalHeight, double originalWidthToHeightRatio, double originalHeightToWidthRatio, int maxHeight, int maxWidth) {
        if(bmOriginalWidth > maxWidth || bmOriginalHeight > maxHeight) {
            //  Log.v(TAG, format("RESIZING bitmap FROM %sx%s ", bmOriginalWidth, bmOriginalHeight));

            if(bmOriginalWidth > bmOriginalHeight) {
                bm = scaleDeminsFromWidth(bm, maxWidth, bmOriginalHeight, originalHeightToWidthRatio);
            } else if (bmOriginalHeight > bmOriginalWidth){
                bm = scaleDeminsFromHeight(bm, maxHeight, bmOriginalHeight, originalWidthToHeightRatio);
            }

            // Log.v(TAG, format("RESIZED bitmap TO %sx%s ", bm.getWidth(), bm.getHeight()));
        }
        return bm;
    }

    private static Bitmap scaleDeminsFromHeight(Bitmap bm, int maxHeight, int bmOriginalHeight, double originalWidthToHeightRatio) {
        int newHeight = (int) Math.max(maxHeight, bmOriginalHeight * .55);
        int newWidth = (int) (newHeight * originalWidthToHeightRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    private static Bitmap scaleDeminsFromWidth(Bitmap bm, int maxWidth, int bmOriginalWidth, double originalHeightToWidthRatio) {
        //scale the width
        int newWidth = (int) Math.max(maxWidth, bmOriginalWidth * .75);
        int newHeight = (int) (newWidth * originalHeightToWidthRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }
    public static void alertBoxError(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Error!");
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager cMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
        if (netInfo == null || netInfo.getState() == null)
            return false;
        return netInfo.getState().equals(NetworkInfo.State.CONNECTED);
    }

    public static String refineUnit(float value){
        if(value<1000){
            return String.format("%.1f", value) +"m";
        }
        else{
           float xx= value/1000;
            String yy=String.format("%.1f", xx);
            return yy+"km";
        }

    }
}
