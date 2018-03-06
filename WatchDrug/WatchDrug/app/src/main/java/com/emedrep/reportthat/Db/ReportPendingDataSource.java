package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emedrep.reportthat.Model.ReportPending;

import java.util.ArrayList;

import java.util.List;


/**
* Created by John Opeyemi on1/23/2018 11:00:50 AM
*/

public class ReportPendingDataSource {

 public static String TBL_REPORTPENDING = "ReportPending";

 public static String COL_REPORTPENDING_ID = "_id";

 public static String COL_STATEID = "stateId";

 public static String COL_LGAID = "lgaId";

 public static String COL_PREMISESTYPE = "premisesType";

 public static String COL_SUSPICIONTYPE = "suspicionType";

 public static String COL_KNOWNNAME = "knownName";

 public static String COL_ADDRESS = "address";

 public static String COL_CITY = "city";

 public static String COL_LATITUDE = "latitude";

 public static String COL_LONGITUDE = "longitude";

 public static String COL_PREMISES = "premises";

 public static String COL_DATECREATED = "dateCreated";

 public static String COL_USERID = "userId";

 public static String COL_ISANONYMOUS = "isAnonymous";

 public static String COL_FILEPATH = "filePath";

 public static String COL_DRUGNAME = "drugName";

 public static String COL_DRUGID = "drugId";

 public static String COL_OTHERSUSPICION = "otherSuspicion";

 public static String COL_COUNTRY = "country";

 public static String COL_POSTALCODDE = "postalCodde";

 public static String COL_RELATIVEADDRESS = "relativeAddress";

 public static String COL_THOROUGHFARE = "thoroughFare";

 public static String COL_REPORT_ID = "reportId";


 public static final String REPORTPENDING_CREATE = "create table " + TBL_REPORTPENDING + "(" + COL_REPORTPENDING_ID + " integer primary key autoincrement," + COL_STATEID + " integer," + COL_LGAID + " integer," + COL_PREMISESTYPE + " integer," + COL_SUSPICIONTYPE + " text," + COL_KNOWNNAME + " text," + COL_ADDRESS + " text," + COL_CITY + " text," + COL_LATITUDE + " text," + COL_LONGITUDE + " text," + COL_PREMISES + " text," + COL_DATECREATED + " text," + COL_USERID + " text," + COL_ISANONYMOUS + " text," + COL_FILEPATH + " text," + COL_DRUGNAME + " text," + COL_DRUGID + " text," + COL_OTHERSUSPICION + " text," + COL_COUNTRY + " text," + COL_POSTALCODDE + " text," + COL_RELATIVEADDRESS + " text," + COL_THOROUGHFARE + " text ," + COL_REPORT_ID + " integer)";

 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_REPORTPENDING_ID, COL_STATEID, COL_LGAID, COL_PREMISESTYPE, COL_SUSPICIONTYPE, COL_KNOWNNAME, COL_ADDRESS, COL_CITY, COL_LATITUDE, COL_LONGITUDE, COL_PREMISES, COL_DATECREATED, COL_USERID, COL_ISANONYMOUS, COL_FILEPATH, COL_DRUGNAME, COL_DRUGID, COL_OTHERSUSPICION, COL_COUNTRY, COL_POSTALCODDE, COL_RELATIVEADDRESS, COL_THOROUGHFARE,COL_REPORT_ID};

 public ReportPendingDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createReportPending(ReportPending reportPending) {
  ContentValues values = new ContentValues();
  values.put(COL_STATEID, reportPending.stateId);
  values.put(COL_LGAID, reportPending.lgaId);
  values.put(COL_PREMISESTYPE, reportPending.premisesType);
  values.put(COL_SUSPICIONTYPE, reportPending.suspicionType);
  values.put(COL_KNOWNNAME, reportPending.knownName);
  values.put(COL_ADDRESS, reportPending.address);
  values.put(COL_CITY, reportPending.city);
  values.put(COL_LATITUDE, reportPending.latitude);
  values.put(COL_LONGITUDE, reportPending.longitude);
  values.put(COL_PREMISES, reportPending.premises);
  values.put(COL_DATECREATED, reportPending.dateCreated);
  values.put(COL_USERID, reportPending.userId);
  values.put(COL_ISANONYMOUS, reportPending.isAnonymous);
  values.put(COL_FILEPATH, reportPending.filePath);
  values.put(COL_DRUGNAME, reportPending.drugName);
  values.put(COL_DRUGID, reportPending.drugId);
  values.put(COL_OTHERSUSPICION, reportPending.otherSuspicion);
  values.put(COL_COUNTRY, reportPending.country);
  values.put(COL_POSTALCODDE, reportPending.postalCodde);
  values.put(COL_RELATIVEADDRESS, reportPending.relativeAddress);
  values.put(COL_THOROUGHFARE, reportPending.thoroughFare);
  values.put(COL_REPORT_ID, reportPending.reportId);
  long insertId = database.insert(TBL_REPORTPENDING, null, values);
  return insertId;
 }

 public List<ReportPending> getAllReportPending() {
  List<ReportPending> reportpendings = new ArrayList<ReportPending>();
  Cursor cursor = database.query(TBL_REPORTPENDING, allColumns, null, null, null, null, null);
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   ReportPending reportpending = cursorToReportPending(cursor);
   reportpendings.add(reportpending);
   cursor.moveToNext();
  }
  cursor.close();
  return reportpendings;
 }



 public void deleteAllReportPendings() {
  database.delete(TBL_REPORTPENDING, null, null);
 }

 public boolean reportpendingDuplicated(String knownName) {
  Cursor cursor = database.query(TBL_REPORTPENDING, allColumns, COL_KNOWNNAME + "=?", new String[]{knownName}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public ReportPending getReportPendingById(int reportpendingId) {
  try {
   String id = String.valueOf(reportpendingId);
   Cursor cursor = database.query(TBL_REPORTPENDING, allColumns, COL_REPORTPENDING_ID + "=?", new String[]{id}, null, null, null);
   cursor.moveToPosition(0);
   ReportPending reportpending = cursorToReportPending(cursor);
   cursor.close();
   return reportpending;
  } catch (Exception ex) {
   return null;
  }
 }

 private ReportPending cursorToReportPending(Cursor cursor) {
  ReportPending reportpending = new ReportPending();
  reportpending.setReportPendingId(cursor.getInt(0));
  reportpending.setStateId(cursor.getInt(1));
  reportpending.setLgaId(cursor.getInt(2));
  reportpending.setPremisesType(cursor.getInt(3));
  reportpending.setSuspicionType(cursor.getString(4));
  reportpending.setKnownName(cursor.getString(5));
  reportpending.setAddress(cursor.getString(6));
  reportpending.setCity(cursor.getString(7));
  reportpending.setLatitude(cursor.getString(8));
  reportpending.setLongitude(cursor.getString(9));
  reportpending.setPremises(cursor.getString(10));
  reportpending.setDateCreated(cursor.getString(11));
  reportpending.setUserId(cursor.getString(12));
  reportpending.setIsAnonymous(cursor.getString(13));
  reportpending.setFilePath(cursor.getString(14));
  reportpending.setDrugName(cursor.getString(15));
  reportpending.setDrugId(cursor.getString(16));
  reportpending.setOtherSuspicion(cursor.getString(17));
  reportpending.setCountry(cursor.getString(18));
  reportpending.setPostalCodde(cursor.getString(19));
  reportpending.setRelativeAddress(cursor.getString(20));
  reportpending.setThoroughFare(cursor.getString(21));
  reportpending.setReportId(cursor.getInt(22));
  return reportpending;
 }


 public long deleteReportPending(int reportPendingId) {
  try {


   return database.delete(TBL_REPORTPENDING, COL_REPORTPENDING_ID
           + " = " + reportPendingId, null);

  } catch (Exception ex) {
   return -1;
  }
 }



}