package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emedrep.reportthat.Model.Visit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eMedrep on 2/15/2018.
 */

public class VendorDataSource { public static String TBL_VENDOR = "Vendor";

    public static String COL_VENDOR_ID = "_id";

    public static String COL_STATEID = "stateId";

    public static String COL_LGAID = "lgaId";

    public static String COL_TYPE = "type";

    public static String COL_NAME = "name";

    public static String COL_ADDRESS = "address";

    public static String COL_LATITUDE = "latitude";

    public static String COL_LONGITUDE = "longitude";

    public static String COL_DATE = "date";

    public static final String VENDOR_CREATE = "create table " + TBL_VENDOR + "(" + COL_VENDOR_ID + " integer ," + COL_STATEID + " integer," + COL_LGAID + " integer," + COL_TYPE + " integer," + COL_NAME + " text," + COL_ADDRESS + " text," + COL_LATITUDE + " text," + COL_LONGITUDE + " text," + COL_DATE + " text)";

    private SQLiteDatabase database;
    private DataBaseHandler dbHelper;

    String[] allColumns = {COL_VENDOR_ID, COL_STATEID, COL_LGAID, COL_TYPE, COL_NAME, COL_ADDRESS, COL_LATITUDE, COL_LONGITUDE, COL_DATE};

    public VendorDataSource(Context context) {
        dbHelper = new DataBaseHandler(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createVendor(int vendorId,int stateId, int lgaId, int type, String name, String address, String latitude, String longitude, String date) {
        ContentValues values = new ContentValues();
        values.put(COL_VENDOR_ID, vendorId);
        values.put(COL_STATEID, stateId);
        values.put(COL_LGAID, lgaId);
        values.put(COL_TYPE, type);
        values.put(COL_NAME, name);
        values.put(COL_ADDRESS, address);
        values.put(COL_LATITUDE, latitude);
        values.put(COL_LONGITUDE, longitude);
        values.put(COL_DATE, date);
        long insertId = database.insert(TBL_VENDOR, null, values);
        return insertId;
    }

    public long updateVendor(long id,Visit visit) {
        ContentValues values = new ContentValues();
        values.put(COL_STATEID, visit.stateId);
        values.put(COL_LGAID,  visit.lgaId);
        values.put(COL_TYPE,  visit.type);
        values.put(COL_NAME,  visit.name);
        values.put(COL_ADDRESS,  visit.address);
        values.put(COL_LATITUDE,  visit.latitude);
        values.put(COL_LONGITUDE,  visit.longitude);
        values.put(COL_DATE,  visit.date);
        return database.update(TBL_VENDOR, values, "_id ='" + id + "'",null);

    }
    public List<Visit> getAllVisit() {
        List<Visit> visits = new ArrayList<Visit>();
        Cursor cursor = database.query(TBL_VENDOR, allColumns, null, null, null, null, COL_VENDOR_ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Visit visit = cursorToVisit(cursor);
            visits.add(visit);
            cursor.moveToNext();
        }
        cursor.close();
        return visits;
    }

    public void deleteAllVisits() {
        database.delete(TBL_VENDOR, null, null);
    }

    public void deleteVendor(int id){
        database.delete(TBL_VENDOR, "_id ='" + id + "'",null);
    }

    public boolean visitDuplicated(String name) {
        Cursor cursor = database.query(TBL_VENDOR, allColumns, COL_NAME + "=?", new String[]{name}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public Visit getVisitById(int visitId) {
        try {
            String id = String.valueOf(visitId);
            Cursor cursor = database.query(TBL_VENDOR, allColumns, COL_VENDOR_ID + "=?", new String[]{id}, null, null, null);
            cursor.moveToPosition(0);
            Visit visit = cursorToVisit(cursor);
            cursor.close();
            return visit;
        } catch (Exception ex) {
            return null;
        }
    }

    private Visit cursorToVisit(Cursor cursor) {
        Visit visit = new Visit();
        visit.setVisitId(cursor.getInt(0));
        visit.setStateId(cursor.getInt(1));
        visit.setLgaId(cursor.getInt(2));
        visit.setType(cursor.getInt(3));
        visit.setName(cursor.getString(4));
        visit.setAddress(cursor.getString(5));
        visit.setLatitude(cursor.getString(6));
        visit.setLongitude(cursor.getString(7));
        visit.setDate(cursor.getString(8));
        return visit;
    }
}