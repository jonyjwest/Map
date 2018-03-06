package com.emedrep.reportthat.SyncHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by eMedrep on 1/19/2018.
 */

public class ReportProvider extends ContentProvider {

    public static final String LOG_TAG=ReportProvider.class.getSimpleName();

    private static final int REPORTS=100;

    private static final int REPORT_ID=101;


    private static final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {

        uriMatcher.addURI(ReportContract.CONTENT_AUTHORITY,ReportContract.PATH_REPORT,REPORTS);
        uriMatcher.addURI(ReportContract.CONTENT_AUTHORITY, ReportContract.PATH_REPORT + "/#", REPORT_ID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
