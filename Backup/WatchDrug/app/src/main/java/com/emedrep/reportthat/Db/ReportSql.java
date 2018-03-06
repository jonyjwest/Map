package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.emedrep.reportthat.Model.Report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eMedrep Nigeria LTD on 8/29/2017.
 */

public class ReportSql {

    Report report;

    public static String TBL_DRUG = "Report";

    public static String captureFile="capureFile";
    public static String latitude="latitude";
    public static String longitude="longitude";

    public static String state="state";
    public static String address="address";
    public static String city ="city";
    public static String country="country" ;
    public static String postalCode="postalCode";
    public static String knownName="knownName";
    public static String relativeAddress="relativeAddress";
    public static String premise="premise";
    public static String thoroughFare="thoroughfare" ;
    public static String subThoroughFare="subThoroughFare";

    public static String reportId="reportId";

    public static String storageType="storageType";

    public static String isReported="isReported";

    public static String date="date";

    public  static final String REPORT_CREATE = "create table "
            + TBL_DRUG + "(" + reportId
            + " integer primary key autoincrement,"+address+"" +
            " text,"+captureFile+" text,"+ city+" text,"+country+"" +
            " text,"+" text,"+knownName+" text,"+latitude+" text," +
            ""+longitude+" text,"+state+" text,"+postalCode+" text," +
            ""+premise+" text,"+relativeAddress+" text," +
            ""+subThoroughFare+" text,"+thoroughFare+" text,"+isReported+" text, "+storageType+" text, "+date+" text)";
    private SQLiteDatabase database;
    private DataBaseHandler dbHelper;

    public long createReport(Report report) {
        ContentValues values = new ContentValues();

        values.put(address, report.address);
        values.put(captureFile, report.CaptureFile);
        values.put(city, report.city);
        values.put(country,report.country);
        values.put(knownName, report.knownName);
        values.put(latitude, report.latitude);
        values.put(longitude,report.longitude);
        values.put(state,report.state);
        values.put(postalCode, report.postalCode);
        values.put(premise, report.premise);
        values.put(relativeAddress,report.relativeAddress);
        values.put(subThoroughFare, report.subThoroughFare);
        values.put(thoroughFare, report.thoroughFare);
        values.put(isReported,report.isReported);
        values.put(storageType,report.storageType);
        values.put(date,report.date);
       long insertId = database.insert(TBL_DRUG, null,
                values);
        return insertId;
    }
    private String[] allColumns = {reportId,address,captureFile,city,country,knownName,latitude, longitude,state,postalCode,premise,relativeAddress,subThoroughFare,thoroughFare,isReported,storageType,date};

    public Report cursorToReport(Cursor cursor) {
        Report report = new Report();
        report.reportId=cursor.getInt(0);
        report.address=cursor.getString(1);
        report.CaptureFile=cursor.getString(2);
        report.city=cursor.getString(3);
        report.country=cursor.getString(4);
        report.knownName=cursor.getString(5);
        report.latitude=cursor.getString(6);
        report.longitude=cursor.getString(7);
        report.state=cursor.getString(8);
        report.postalCode=cursor.getString(9);
        report.premise=cursor.getString(10);
        report.relativeAddress=cursor.getString(11);
        report.subThoroughFare=cursor.getString(12);
        report.thoroughFare=cursor.getString(13);
        report.isReported=cursor.getString(14);
        report.storageType=cursor.getString(15);
        report.date=cursor.getString(16);

  return report;
    }

    public void close() {
        dbHelper.close();
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void deleteReport(int reportId) {
        try {
            close();
            open();
            database.delete(TBL_DRUG, this.reportId
                    + " = " + reportId, null);

        } catch (Exception ex) {
            //new ViewDialog(this,"Process failed, Please try again!").show();

        }
    }
    public ReportSql(Context context) {
        report=new Report();
        dbHelper = new DataBaseHandler(context);
        database = dbHelper.getWritableDatabase();
    }

    public List<Report> getAllReports() {
        try {
            List<Report> reportList = new ArrayList<Report>();

            Cursor cursor = database.query(TBL_DRUG, allColumns, null, null, null, null, reportId + " DESC");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Report report = cursorToReport(cursor);
                File file=new File(report.CaptureFile);
                if(file.exists()) {
                    reportList.add(report);
                }
                cursor.moveToNext();
            }
            cursor.close();

            return reportList;
        } catch (Exception ex) {
            Log.d("dd",ex.getMessage());
            return null;
        }
    }


    public Report getReportById(String reportId) {
        try {

            Cursor cursor = database.query(TBL_DRUG, allColumns, this.reportId + "=?", new String[]{reportId},
                    null, null, null);

            cursor.moveToFirst();
            cursor.moveToPosition(0);
            Report report = cursorToReport(cursor);
            cursor.close();

            return report;
        } catch (Exception ex) {


            return null;
        }

    }

    public boolean updateDetails(long rowId)
    {
        ContentValues args = new ContentValues();
       args.put(isReported, "true");
        int i =  database.update(TBL_DRUG, args, reportId + "=" + rowId, null);
        return i > 0;
    }
    public Report getReport(int reportId) {

        String query = "SELECT * from  " + TBL_DRUG + " WHERE " + this.reportId + "='" + reportId + "'  ";
        Log.d("MyQuery", query);
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToPosition(0);

            Report report = cursorToReport(cursor);



        cursor.close();

        return report;
    }




}
