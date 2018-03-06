package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.emedrep.reportthat.Model.Visit;

import java.util.List;

/**
 * Created by eMedrep Nigeria LTD on 1/2/2018.
 */

public class PlacesVirtualDb extends SQLiteOpenHelper {
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME="VVISIT";

    public static String COL_VISIT_ID = "_id";

    public static String COL_STATEID = "stateId";

    public static String COL_LGAID = "lgaId";

    public static String COL_TYPE = "type";

    public static String COL_NAME = "name";

    public static String COL_ADDRESS = "address";

    public static String COL_LATITUDE = "latitude";

    public static String COL_LONGITUDE = "longitude";

    public static String COL_DATE = "date";





    private final Context mContext;
    public PlacesVirtualDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    public Cursor getWordMatches(String query) {
        String selection = COL_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, null);
    }


    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        String sortOrder= COL_NAME
                + " ASC";
        Cursor cursor = builder.query(this.getReadableDatabase(), columns, selection, selectionArgs, null, null, sortOrder);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }





    private SQLiteDatabase mDatabase;

    private static final String FTS_TABLE_CREATE=  "CREATE VIRTUAL TABLE "+ FTS_VIRTUAL_TABLE +
            " USING fts3 ("+ COL_VISIT_ID+","+ COL_NAME+ ","+ COL_ADDRESS+","+ COL_DATE+","+ COL_LATITUDE+ ","+ COL_LONGITUDE+ ","+ COL_STATEID+ ","+ COL_LGAID+  ","+ COL_TYPE+ ")";




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mDatabase=sqLiteDatabase;
        mDatabase.execSQL(FTS_TABLE_CREATE);
        loadDictionary();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
        onCreate(db);}

    private void loadDictionary() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d("dico" ,"dictionary ");

                    loadVisit();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    private void loadVisit(){

        VisitDataSource manager=new VisitDataSource(mContext);
        List<Visit> visits=manager.getAllVisit();
        for(Visit sn:visits)   {
            addVisit(sn.getVisitId(),sn.getName(),sn.getAddress(),sn.getDate(),sn.getLatitude(),sn.getLongitude(),sn.getStateId(),sn.getLgaId(),sn.getType())  ;

        }
    }


    public long addVisit(int id,String name,String address,String date,String latitude,String longitude,int stateId,int lga,int type){
        ContentValues initialValues=new ContentValues();
        initialValues.put(COL_VISIT_ID,id);
        initialValues.put(COL_NAME,name);
        initialValues.put(COL_ADDRESS,address);
        initialValues.put(COL_DATE,date);
        initialValues.put(COL_LATITUDE,latitude);

        initialValues.put(COL_LONGITUDE,longitude);
        initialValues.put(COL_STATEID,stateId);
        initialValues.put(COL_LGAID,lga);
        initialValues.put(COL_TYPE,type);
        return mDatabase.insert(FTS_VIRTUAL_TABLE,null,initialValues);
    }
}




