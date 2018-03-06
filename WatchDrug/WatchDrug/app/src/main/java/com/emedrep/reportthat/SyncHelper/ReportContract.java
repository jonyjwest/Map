package com.emedrep.reportthat.SyncHelper;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by eMedrep on 1/19/2018.
 */

public class ReportContract {


    public static final String CONTENT_AUTHORITY="com.emedrep.reportthat";

    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_REPORT="report.path";


    public static class ReportEntry implements BaseColumns{

        public static  final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_REPORT);

        public  static  final  String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+ "/"+PATH_REPORT;

        public  static  final  String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/"+PATH_REPORT;

        public final static String TABLE_NAME="report";

        public final static String _ID=BaseColumns._ID;


        public static String TBL_DRUG = "Report";



            public static String COLUMN_CAPTURE= "Capture";
            public static String COLUMN_LATITUDE= "Latitude";
        public static String COLUMN_LONGITUDE= "Longitude";
        public static String COLUMN_SUSPICION_TYPE= "SuspicionType";
        public static String COLUMN_OTHER_SUSPICION= "OtherSuspicion";
        public static String COLUMN_DRUG_ID= "DrugId";
        public static String COLUMN_DRUG_NAME= "DrugName";
        public static String COLUMN_MANUFACTURER= "Manufacturer";
        public static String COLUMN_COUNTRY= "Country";
        public static String COLUMN_PREMISES= "Premises";
        public static String COLUMN_PREMISE_TYPE= "PremisesType";
        public static String COLUMN_OTHER_TYPE= "OtherType";

        public static String COLUMN_STATE_ID= "StateId";
        public static String COLUMN_LGA= "LGAId";
        public static String COLUMN_ADDRESS= "Address";
        public static String COLUMN_CITY= "City";
        public static String COLUMN_USER_ID= "UserId";
        public static String COLUMN_IS_ANONYMOUS= "IsAnonymous";


    }
}
