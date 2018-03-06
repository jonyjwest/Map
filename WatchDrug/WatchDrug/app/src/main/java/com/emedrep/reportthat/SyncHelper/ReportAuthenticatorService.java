package com.emedrep.reportthat.SyncHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by eMedrep on 1/19/2018.
 */

public class ReportAuthenticatorService extends Service {

    ReportAuthenticator mAuthenticcator;

    @Override
    public void onCreate(){
        mAuthenticcator=new ReportAuthenticator(this);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticcator.getIBinder();
    }
}
