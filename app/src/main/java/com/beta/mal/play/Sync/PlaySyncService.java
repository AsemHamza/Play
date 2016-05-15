package com.beta.mal.play.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PlaySyncService extends Service {

    private static final Object sAdapterLock = new Object();
    private static PlaySyncAdapter playSyncAdapter = null;

    ///////////////////////////////////////////////////////////////////
    @Override
    public void onCreate() {
        Log.d("PlaySyncService", "Sync Service onCreate");
        synchronized (sAdapterLock) {
            if (playSyncAdapter == null) {
                playSyncAdapter = new PlaySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public IBinder onBind(Intent intent) {
        return playSyncAdapter.getSyncAdapterBinder();
    }
}
