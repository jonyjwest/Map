package com.emedrep.farmmapper.DB;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.emedrep.farmmapper.Model.Coordinate;
import com.emedrep.farmmapper.Util.Constant;


public class DataBaseHandler extends SQLiteOpenHelper {


    public DataBaseHandler(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LandDataSource.LAND_CREATE);
        db.execSQL(CoordinateDataSource.COORDINATE_CREATE);



    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(DataBaseHandler.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion);
        db.execSQL("drop table if exist " + LandDataSource.TBL_LAND);
        db.execSQL("drop table if exist " + CoordinateDataSource.TBL_COORDINATE);

        onCreate(db);
        db.close();

    }

}


