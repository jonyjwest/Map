package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.emedrep.reportthat.Model.Report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
* Created by John Opeyemi on1/24/2018 10:37:00 AM
*/

public class ReportDataSource {

 public static String TBL_REPORT = "Report";

 public static String COL_REPORT_ID = "_id";

 public static String COL_STATEID = "stateId";

 public static String COL_LGAID = "lgaId";

 public static String COL_PREMISESTYPE = "premisesType";

 public static String COL_SUSPICIONTYPE = "suspicionType";

 public static String COL_KNOWNAME = "knowName";

 public static String COL_ADDRESS = "address";

 public static String COL_CITY = "city";

 public static String COL_LATITUDE = "latitude";

 public static String COL_LONGITUDE = "longitude";

 public static String COL_PREMISES = "premises";

 public static String COL_DATECREATED = "dateCreated";

 public static String COL_ISANONYMOUSE = "isAnonymouse";

 public static String COL_FILEPATH = "filePath";

 public static String COL_DRUGNAME = "drugName";

 public static String COL_DRUGID = "drugId";

 public static String COL_OTHERSUSPICION = "otherSuspicion";

 public static String COL_COUNTRY = "country";

 public static String COL_POSTALCODE = "postalCode";

 public static String COL_RELATIVEADDRESS = "relativeAddress";

 public static String COL_THOROUGHFARE = "thoroughFare";

 public static String COL_MANUFACTURER = "manufacturer";

 public static String COL_ISREPORTED = "isReported";

 public static String COL_ISSYNCED = "isSynced";

 public static final String REPORT_CREATE = "create table " + TBL_REPORT + "(" + COL_REPORT_ID + " integer primary key autoincrement," + COL_STATEID + " integer," + COL_LGAID + " integer," + COL_PREMISESTYPE + " integer," + COL_SUSPICIONTYPE + " text," + COL_KNOWNAME + " text," + COL_ADDRESS + " text," + COL_CITY + " text," + COL_LATITUDE + " text," + COL_LONGITUDE + " text," + COL_PREMISES + " text," + COL_DATECREATED + " text," + COL_ISANONYMOUSE + " text," + COL_FILEPATH + " text," + COL_DRUGNAME + " text," + COL_DRUGID + " text," + COL_OTHERSUSPICION + " text," + COL_COUNTRY + " text," + COL_POSTALCODE + " text," + COL_RELATIVEADDRESS + " text," + COL_THOROUGHFARE + " text," + COL_MANUFACTURER + " text," + COL_ISREPORTED + " text," + COL_ISSYNCED + " text)";

 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_REPORT_ID, COL_STATEID, COL_LGAID, COL_PREMISESTYPE, COL_SUSPICIONTYPE, COL_KNOWNAME, COL_ADDRESS, COL_CITY, COL_LATITUDE, COL_LONGITUDE, COL_PREMISES, COL_DATECREATED, COL_ISANONYMOUSE, COL_FILEPATH, COL_DRUGNAME, COL_DRUGID, COL_OTHERSUSPICION, COL_COUNTRY, COL_POSTALCODE, COL_RELATIVEADDRESS, COL_THOROUGHFARE, COL_MANUFACTURER, COL_ISREPORTED, COL_ISSYNCED};

 public ReportDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createReport(Report report) {
  ContentValues values = new ContentValues();
  values.put(COL_STATEID, report.stateId);
  values.put(COL_LGAID, report.lgaId);
  values.put(COL_PREMISESTYPE, report.premisesType);
  values.put(COL_SUSPICIONTYPE, report.suspicionType);
  values.put(COL_KNOWNAME, report.knowName);
  values.put(COL_ADDRESS, report.address);
  values.put(COL_CITY, report.city);
  values.put(COL_LATITUDE, report.latitude);
  values.put(COL_LONGITUDE, report.longitude);
  values.put(COL_PREMISES, report.premises);
  values.put(COL_DATECREATED, report.dateCreated);
  values.put(COL_ISANONYMOUSE, report.isAnonymouse);
  values.put(COL_FILEPATH, report.filePath);
  values.put(COL_DRUGNAME, report.drugName);
  values.put(COL_DRUGID, report.drugId);
  values.put(COL_OTHERSUSPICION, report.otherSuspicion);
  values.put(COL_COUNTRY, report.country);
  values.put(COL_POSTALCODE, report.postalCode);
  values.put(COL_RELATIVEADDRESS, report.relativeAddress);
  values.put(COL_THOROUGHFARE, report.thoroughFare);
  values.put(COL_MANUFACTURER, report.manufacturer);
  values.put(COL_ISREPORTED, report.isReported);
  values.put(COL_ISSYNCED, report.isSynced);
  long insertId = database.insert(TBL_REPORT, null, values);
  return insertId;
 }

 public List<Report> getAllReport() {
  List<Report> reports = new ArrayList<Report>();
  Cursor cursor = database.query(TBL_REPORT, allColumns, null, null, null, null, COL_REPORT_ID + " DESC");
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   Report report = cursorToReport(cursor);
   File file=new File(report.filePath);
   if(file.exists()){
    reports.add(report);

   }
   cursor.moveToNext();
  }
  cursor.close();
  return reports;
 }

 public void deleteAllReports() {
  database.delete(TBL_REPORT, null, null);
 }

 public void deleteReport(int reportId) {
  try {
   close();
   open();
   database.delete(TBL_REPORT, this.COL_REPORT_ID
           + " = " + reportId, null);

  } catch (Exception ex) {
   //new ViewDialog(this,"Process failed, Please try again!").show();

  }
 }
 public void close() {
  dbHelper.close();
 }

 public void open() {
  database = dbHelper.getWritableDatabase();
 }
 public boolean reportDuplicated(String suspicionType) {
  Cursor cursor = database.query(TBL_REPORT, allColumns, COL_SUSPICIONTYPE + "=?", new String[]{suspicionType}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public Report getReportById(String reportId) {
  try {

   Cursor cursor = database.query(TBL_REPORT, allColumns, COL_REPORT_ID + "=?", new String[]{reportId}, null, null, null);
   cursor.moveToPosition(0);
   Report report = cursorToReport(cursor);
   cursor.close();
   return report;
  } catch (Exception ex) {
   return null;
  }
 }

 private Report cursorToReport(Cursor cursor) {
  Report report = new Report();
  report.setReportId(cursor.getInt(0));
  report.setStateId(cursor.getInt(1));
  report.setLgaId(cursor.getInt(2));
  report.setPremisesType(cursor.getInt(3));
  report.setSuspicionType(cursor.getString(4));
  report.setKnowName(cursor.getString(5));
  report.setAddress(cursor.getString(6));
  report.setCity(cursor.getString(7));
  report.setLatitude(cursor.getString(8));
  report.setLongitude(cursor.getString(9));
  report.setPremises(cursor.getString(10));
  report.setDateCreated(cursor.getString(11));
  report.setIsAnonymouse(cursor.getString(12));
  report.setFilePath(cursor.getString(13));
  report.setDrugName(cursor.getString(14));
  report.setDrugId(cursor.getString(15));
  report.setOtherSuspicion(cursor.getString(16));
  report.setCountry(cursor.getString(17));
  report.setPostalCode(cursor.getString(18));
  report.setRelativeAddress(cursor.getString(19));
  report.setThoroughFare(cursor.getString(20));
  report.setManufacturer(cursor.getString(21));
  report.setIsReported(cursor.getString(22));
  report.setIsSynced(cursor.getString(23));
  return report;
 }


 public boolean updateDetails(long id, String premiseName,String drugName,String manufacturer,String isAnonymous,int stateId,int lga,String suspicionType,String address) {
  ContentValues args = new ContentValues();
  args.put(COL_ISREPORTED, "true");
  args.put(COL_PREMISES, premiseName);
  args.put(COL_DRUGNAME, drugName);
  args.put(COL_MANUFACTURER, manufacturer);
  args.put(COL_ISANONYMOUSE, isAnonymous);
  args.put(COL_STATEID, stateId);
  args.put(COL_LGAID,lga);

  args.put(COL_SUSPICIONTYPE, suspicionType);
  args.put(COL_ADDRESS, address);

  int i = database.update(TBL_REPORT, args, COL_REPORT_ID + "=" + id, null);
  return i > 0;
 }

 public boolean updateIsSynced(long rowId) {
  ContentValues args = new ContentValues();
  args.put(COL_ISSYNCED, "true");
  int i = database.update(TBL_REPORT, args, COL_REPORT_ID + "=" + rowId, null);
  return i > 0;
 }

 public Report getReport(int reportId) {

  String query = "SELECT * from  " + TBL_REPORT + " WHERE " + this.COL_REPORT_ID + "='" + reportId + "'  ";
  Log.d("MyQuery", query);
  Cursor cursor = database.rawQuery(query, null);
  cursor.moveToPosition(0);

  Report report = cursorToReport(cursor);


  cursor.close();

  return report;
 }
}