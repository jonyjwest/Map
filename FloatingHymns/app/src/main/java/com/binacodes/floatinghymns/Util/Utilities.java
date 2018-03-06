package com.binacodes.floatinghymns.Util;

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

import com.binacodes.floatinghymns.R;

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
