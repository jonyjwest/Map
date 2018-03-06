package com.binacodes.floatinghymns.Db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.binacodes.floatinghymns.Util.Constant;


public class DataBaseHandler extends SQLiteOpenHelper {


    public DataBaseHandler(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SongDataSource.SONG_CREATE);




    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(DataBaseHandler.class.getName(), "Upgrading from " + oldVersion + " to " + newVersion);
        db.execSQL("drop table if exist " + SongDataSource.TBL_SONG);

        onCreate(db);
        db.close();

    }

}


