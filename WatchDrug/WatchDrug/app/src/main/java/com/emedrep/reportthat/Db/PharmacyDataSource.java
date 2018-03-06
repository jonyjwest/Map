package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.Pharmacy;

import java.util.ArrayList;
import java.util.List;
/**
* Created by John Opeyemi on10/25/2017 10:48:35 AM
*/

public class PharmacyDataSource {

 public static String TBL_PHARMACY = "Pharmacy";

 public static String COL_PHARMACY_ID = "_id";



 public static String COL_PREMISENAME = "premiseName";

 public static String COL_PREMISEADDRESS = "premiseAddress";

 public static String COL_PHONENUMBER = "phoneNumber";



 public static String COL_PREMISETYPE = "premiseType";

 public static String COL_PHARMACISTNAME = "pharmacistName";

 public static String COL_PHARMACISTPHONE = "pharmacistPhone";

 public static String COL_LATITUDE = "latitude";

 public static String COL_LONGITUDE = "longitude";

 public static String COL_DATECREATED = "dateCreated";



 public static final String PHARMACY_CREATE = "create table " + TBL_PHARMACY + "(" + COL_PHARMACY_ID + " integer primary key autoincrement," +  " integer," + COL_PREMISENAME + " text," + COL_PREMISEADDRESS + " text," + COL_PHONENUMBER + " text,"  + COL_PREMISETYPE + " text," + COL_PHARMACISTNAME + " text," + COL_PHARMACISTPHONE + " text," + COL_LATITUDE + " text," + COL_LONGITUDE + " text," + COL_DATECREATED + " text)";

 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_PHARMACY_ID, COL_PREMISENAME, COL_PREMISEADDRESS, COL_PHONENUMBER, COL_PREMISETYPE, COL_PHARMACISTNAME, COL_PHARMACISTPHONE, COL_LATITUDE, COL_LONGITUDE, COL_DATECREATED};

 public PharmacyDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createPharmacy(String premiseName, String premiseAddress, String phoneNumber,  String premiseType, String pharmacistName, String pharmacistPhone, String latitude, String longitude, String dateCreated) {
  ContentValues values = new ContentValues();

  values.put(COL_PREMISENAME, premiseName);
  values.put(COL_PREMISEADDRESS, premiseAddress);
  values.put(COL_PHONENUMBER, phoneNumber);

  values.put(COL_PREMISETYPE, premiseType);
  values.put(COL_PHARMACISTNAME, pharmacistName);
  values.put(COL_PHARMACISTPHONE, pharmacistPhone);
  values.put(COL_LATITUDE, latitude);
  values.put(COL_LONGITUDE, longitude);
  values.put(COL_DATECREATED, dateCreated);

  long insertId = database.insert(TBL_PHARMACY, null, values);
  return insertId;
 }

 public List<Pharmacy> getAllPharmacy(Context context) {
  try {
   List<Pharmacy> pharmacys = new ArrayList<>();
   Cursor cursor = database.query(TBL_PHARMACY, allColumns, null, null, null, null, null);
   cursor.moveToFirst();
   Location loc1=Utilities.getMyLocation(context);
    while (!cursor.isAfterLast()) {
     Pharmacy pharmacy = cursorToPharmacy(cursor);

     Location loc2=new Location(pharmacy.getPremiseName());
     loc2.setLatitude(Double.parseDouble(pharmacy.getLatitude()));
     loc2.setLongitude(Double.parseDouble(pharmacy.getLongitude()));
    // float ss=Utilities.getDistance(loc1.getLatitude(),loc1.getLongitude(),Double.parseDouble(pharmacy.getLatitude()),Double.parseDouble(pharmacy.getLongitude()));
     pharmacy.setDistance(loc1.distanceTo(loc2));
     pharmacys.add(pharmacy);
     cursor.moveToNext();
    }
    cursor.close();
   return pharmacys;
  }
  catch (Exception ex){
   return null;
  }
 }

 public void deleteAllPharmacys() {
  database.delete(TBL_PHARMACY, null, null);
 }

 public boolean pharmacyDuplicated(String premiseName) {
  Cursor cursor = database.query(TBL_PHARMACY, allColumns, COL_PREMISENAME + "=?", new String[]{premiseName}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public Pharmacy getPharmacyById(int pharmacyId) {
  try {
   String id = String.valueOf(pharmacyId);
   Cursor cursor = database.query(TBL_PHARMACY, allColumns, COL_PHARMACY_ID + "=?", new String[]{id}, null, null, null);
   cursor.moveToPosition(0);
   Pharmacy pharmacy = cursorToPharmacy(cursor);
   cursor.close();
   return pharmacy;
  } catch (Exception ex) {
   return null;
  }
 }

 private Pharmacy cursorToPharmacy(Cursor cursor) {
  Pharmacy pharmacy = new Pharmacy();
  pharmacy.setPharmacyId(cursor.getInt(0));

  pharmacy.setPremiseName(cursor.getString(1));
  pharmacy.setPremiseAddress(cursor.getString(2));
  pharmacy.setPhoneNumber(cursor.getString(3));
     pharmacy.setPremiseType(cursor.getString(4));
  pharmacy.setPharmacistName(cursor.getString(5));
  pharmacy.setPharmacistPhone(cursor.getString(6));
  pharmacy.setLatitude(cursor.getString(7));
  pharmacy.setLongitude(cursor.getString(8));
  pharmacy.setDateCreated(cursor.getString(9));

  return pharmacy;
 }
}