package com.emedrep.reportthat.SyncHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by eMedrep on 1/23/2018.
 */

public class ReportSyncService extends Service {

    private static ReportSyncAdapter sSyncAdapter = null;
    public ReportSyncService() {
        super();
    }

    @Override
    public void onCreate() {
        if (sSyncAdapter == null)
            sSyncAdapter = new ReportSyncAdapter(getApplicationContext(), true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
