package com.profileownerdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import static com.profileownerdemo.SetProfileOwner.LAUNCHER_COMPONENT_NAME;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;
import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

public class MonitorService extends Service {
    private static final String TAG = "MonitorService";

    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            Log.e(TAG, "action is null");
            return START_STICKY;
        }

        if (PASS_INTENT_ACTION.equalsIgnoreCase(intent.getAction())) {
            Log.e(TAG, "onStartCommand enable componment");
            Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, false);
        }

        Bundle resultBundle = intent.getBundleExtra(PASS_DATA);
        if (resultBundle == null) {
            Log.e(TAG, "result bundle is null");
            return START_STICKY;
        }

        String result = resultBundle.getString(PASS_DATA_KEY);
        Log.e(TAG, "last receive UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
}
