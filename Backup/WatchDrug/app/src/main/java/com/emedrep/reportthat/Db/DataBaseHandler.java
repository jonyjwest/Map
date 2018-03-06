package com.emedrep.reportthat.Db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.emedrep.reportthat.Library.Constant;


public class DataBaseHandler extends SQLiteOpenHelper {


    public DataBaseHandler(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ReportSql.REPORT_CREATE);
        db.execSQL(PharmacyDataSource.PHARMACY_CREATE);
        db.execSQL(VisitDataSource.VISIT_CREATE);
        db.execSQL(UserDataSource.USER_CREATE);



    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(DataBaseHandler.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion);
        db.execSQL("drop table if exist " + DrugSql.TBL_DRUG);
        db.execSQL("drop table if exist " + PharmacyDataSource.TBL_PHARMACY);
        db.execSQL("drop table if exist " + VisitDataSource.TBL_VISIT);
        db.execSQL("drop table if exist " + UserDataSource.TBL_USER);

        onCreate(db);
        db.close();

    }

}


