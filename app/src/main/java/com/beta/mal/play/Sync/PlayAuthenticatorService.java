package com.beta.mal.play.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PlayAuthenticatorService extends Service{

    private playAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new playAuthenticator(this);
        Log.e("PLAY Authentication"," onCreate");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
