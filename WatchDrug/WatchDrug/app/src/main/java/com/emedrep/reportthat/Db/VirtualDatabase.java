package com.emedrep.reportthat.Db;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.emedrep.watchdrug.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Developer
 * Date: 3/27/14
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualDatabase extends SQLiteOpenHelper{
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME="VDRUG";
    private static final String PRODUCT_ID = "ID";
    private static final String PRODUCT = "PRODUCT";
    private static final String ACTIVEINGREDIENT = "ACTIVEINGREDIENT";
    private static final String MANUFACTURER= "MANUFACTURER";
    private static final String NAFDAC= "NAFDAC";


   private final Context mContext;
     public VirtualDatabase(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
         mContext = context;


    }

    public Cursor getWordMatches(String query) {
        String selection = PRODUCT + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, null);
    }




    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        String sortOrder= PRODUCT
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
                " USING fts3 ("+ PRODUCT_ID+","+ PRODUCT+ ","+ ACTIVEINGREDIENT+ ","+ MANUFACTURER+","+ NAFDAC+")";




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

                        loadDrugs();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
               }
            }).start();
        }


        private void loadDrugs(){
            Log.d("Point of adding songs" ,"any ");

                final Resources resources = mContext.getResources();
                InputStream inputStream = resources.openRawResource(R.raw.drugs);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                    String line;

                        while ((line = reader.readLine()) != null) {
                            if(line.equals("")){
                                continue;
                            }
                            String[] strings = TextUtils.split(line, "@");

                            Log.d("Point of adding songs" ,line);

                        addProduct(strings[0],strings[1],strings[2],strings[3],strings[4])  ;
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                     Log.d("error",e.getMessage());
                    }
                    finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("error",e.getMessage());
                        }
                    }



         }




    public long addProduct(String productId,String productName,String activeIngredient,String manufacturer,String nafdac){
            ContentValues initialValues=new ContentValues();
            initialValues.put(PRODUCT_ID,productId);
            initialValues.put(PRODUCT,productName);
            initialValues.put(MANUFACTURER,manufacturer);
            initialValues.put(NAFDAC,nafdac);
            initialValues.put(ACTIVEINGREDIENT,activeIngredient);
            return mDatabase.insert(FTS_VIRTUAL_TABLE,null,initialValues);
        }
    }




