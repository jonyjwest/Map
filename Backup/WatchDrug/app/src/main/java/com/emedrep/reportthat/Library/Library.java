package com.emedrep.reportthat.Library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;


import com.emedrep.watchdrug.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by OPEYEMI on 2015-08-14.
 */
public class Library {


    static String msg = "";


    public static void StartActivity(Activity context1, Class<?> context2) {
        Intent catIntent = new Intent(context1, context2);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.right_to_left, R.anim.right);
    }

    public static void StartActivity(Activity context1, Class<?> context2, String extra, String value) {
        Intent catIntent = new Intent(context1, context2);
        catIntent.putExtra(extra, value);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.right_to_left, R.anim.right);
    }
    public static void StartActivity(Activity context1, Class<?> context2, String extra, int value) {
        Intent catIntent = new Intent(context1, context2);
        catIntent.putExtra(extra, value);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.right_to_left, R.anim.right);
    }

    public static void StartActivityBackAnimation(Activity context1, Class<?> context2) {
        Intent catIntent = new Intent(context1, context2);

        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.back_in, R.anim.back_out);
    }

    public static void StartActivityBackAnimation(Activity context1, Class<?> context2, String extra, String value) {
        Intent catIntent = new Intent(context1, context2);
        catIntent.putExtra(extra, value);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        catIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(catIntent);
        context1.overridePendingTransition(R.anim.back_in, R.anim.back_out);
    }


    public static String NumberFormatter(String number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public static String getReferenceNo(int saleId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime + saleId;
    }

    public static void removeKeyboardDialog(Context context, EditText editText) {
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromInputMethod(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public static int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

//    public static List<SaleItem> getDataFromSharedPreferences(Context context) {
//        Gson gson = new Gson();
//        List<SaleItem> saleItemsFromShared = new ArrayList<SaleItem>();
//        SharedPreferences sharedPref = context.getSharedPreferences(Constant.PREFERENCE_NAME, 0);
//        String jsonPreferences = sharedPref.getString("saleItems", "");
//
//        Type type = new TypeToken<List<SaleItem>>() {
//        }.getType();
//        saleItemsFromShared = gson.fromJson(jsonPreferences, type);
//
//        return saleItemsFromShared;
//    }




    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void logError(Exception ex) {
        Log.e("Error", ex.getMessage());
    }







    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager cMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
        if (netInfo == null || netInfo.getState() == null)
            return false;
        return netInfo.getState().equals(NetworkInfo.State.CONNECTED);
    }







}