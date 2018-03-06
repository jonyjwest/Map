package com.emedrep.farmmapper.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emedrep.farmmapper.Model.Coordinate;

import java.util.ArrayList;
import java.util.List;
/**
* Created by John Opeyemi on2/27/2018 3:53:42 PM
*/

public class CoordinateDataSource {

 public static String TBL_COORDINATE = "Coordinate";

 public static String COL_COORDINATE_ID = "_id";

 public static String COL_LANDID = "landId";

 public static String COL_LATITUDE = "latitude";

 public static String COL_LONGITUDE = "longitude";

 public static String COL_DATECREATED = "dateCreated";

 public static final String COORDINATE_CREATE = "create table " + TBL_COORDINATE + "(" + COL_COORDINATE_ID + " integer primary key autoincrement," + COL_LANDID + " integer," + COL_LATITUDE + " text," + COL_LONGITUDE + " text," + COL_DATECREATED + " text)";

 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_COORDINATE_ID, COL_LANDID, COL_LATITUDE, COL_LONGITUDE, COL_DATECREATED};

 public CoordinateDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createCoordinate(int landId, String latitude, String longitude, String dateCreated) {
  ContentValues values = new ContentValues();
  values.put(COL_LANDID, landId);
  values.put(COL_LATITUDE, latitude);
  values.put(COL_LONGITUDE, longitude);
  values.put(COL_DATECREATED, dateCreated);
  long insertId = database.insert(TBL_COORDINATE, null, values);
  return insertId;
 }

 public List<Coordinate> getAllCoordinate() {
  List<Coordinate> coordinates = new ArrayList<Coordinate>();
  Cursor cursor = database.query(TBL_COORDINATE, allColumns, null, null, null, null, null);
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   Coordinate coordinate = cursorToCoordinate(cursor);
   coordinates.add(coordinate);
   cursor.moveToNext();
  }
  cursor.close();
  return coordinates;
 }

 public List<Coordinate> getAllCoordinateByLandId(int landId) {
  List<Coordinate> coordinates = new ArrayList<Coordinate>();
  String id = String.valueOf(landId);
  Cursor cursor = database.query(TBL_COORDINATE, allColumns, COL_LANDID + "=?", new String[]{id}, null, null, null);
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   Coordinate coordinate = cursorToCoordinate(cursor);
   coordinates.add(coordinate);
   cursor.moveToNext();
  }
  cursor.close();
  return coordinates;
 }
 public void deleteAllCoordinates() {
  database.delete(TBL_COORDINATE, null, null);
 }

 public boolean coordinateDuplicated(String latitude) {
  Cursor cursor = database.query(TBL_COORDINATE, allColumns, COL_LATITUDE + "=?", new String[]{latitude}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public Coordinate getCoordinateById(int coordinateId) {
  try {
   String id = String.valueOf(coordinateId);
   Cursor cursor = database.query(TBL_COORDINATE, allColumns, COL_COORDINATE_ID + "=?", new String[]{id}, null, null, null);
   cursor.moveToPosition(0);
   Coordinate coordinate = cursorToCoordinate(cursor);
   cursor.close();
   return coordinate;
  } catch (Exception ex) {
   return null;
  }
 }

 private Coordinate cursorToCoordinate(Cursor cursor) {
  Coordinate coordinate = new Coordinate();
  coordinate.setCoordinateId(cursor.getInt(0));
  coordinate.setLandId(cursor.getInt(1));
  coordinate.setLatitude(cursor.getString(2));
  coordinate.setLongitude(cursor.getString(3));
  coordinate.setDateCreated(cursor.getString(4));
  return coordinate;
 }
}