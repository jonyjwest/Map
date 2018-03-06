package com.emedrep.farmmapper.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emedrep.farmmapper.Model.Land;

import java.util.ArrayList;
import java.util.List;
/**
* Created by John Opeyemi on2/26/2018 12:21:19 PM
*/

public class LandDataSource {

 public static String TBL_LAND = "Land";

 public static String COL_LAND_ID = "_id";


 public static String COL_NAME = "name";

 public static String COL_DATECREATED = "dateCreated";

 public static final String LAND_CREATE = "create table " + TBL_LAND + "(" + COL_LAND_ID + " integer primary key autoincrement,"  + COL_NAME + " text," + COL_DATECREATED + " text)";

 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_LAND_ID, COL_NAME, COL_DATECREATED};

 public LandDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createLand(String name, String dateCreated) {
  ContentValues values = new ContentValues();

  values.put(COL_NAME, name);
  values.put(COL_DATECREATED, dateCreated);
  long insertId = database.insert(TBL_LAND, null, values);
  return insertId;
 }

 public List<Land> getAllLand() {
  List<Land> lands = new ArrayList<Land>();
  Cursor cursor = database.query(TBL_LAND, allColumns, null, null, null, null, null);
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   Land land = cursorToLand(cursor);
   lands.add(land);
   cursor.moveToNext();
  }
  cursor.close();
  return lands;
 }

 public void deleteAllLands() {
  database.delete(TBL_LAND, null, null);
 }

 public boolean landDuplicated(String name) {
  Cursor cursor = database.query(TBL_LAND, allColumns, COL_NAME + "=?", new String[]{name}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public Land getLandById(int landId) {
  try {
   String id = String.valueOf(landId);
   Cursor cursor = database.query(TBL_LAND, allColumns, COL_LAND_ID + "=?", new String[]{id}, null, null, null);
   cursor.moveToPosition(0);
   Land land = cursorToLand(cursor);
   cursor.close();
   return land;
  } catch (Exception ex) {
   return null;
  }
 }

 private Land cursorToLand(Cursor cursor) {
  Land land = new Land();
  land.setLandId(cursor.getInt(0));

  land.setName(cursor.getString(1));
  land.setDateCreated(cursor.getString(2));
  return land;
 }
}