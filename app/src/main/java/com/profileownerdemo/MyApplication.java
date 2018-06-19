package com.profileownerdemo;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

/**
 * Created by lyg on 2018/6/4.
 */
public class MyApplication extends Application {
    private HiddenBroadcastReceiver hiddenBroadcastReceiver = new HiddenBroadcastReceiver();

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACCROS_INTENT);
        intentFilter.addAction(PASS_INTENT_ACTION);
        this.registerReceiver(hiddenBroadcastReceiver, intentFilter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.unregisterReceiver(hiddenBroadcastReceiver);
        context = null;
    }

    public static Context getContext() {
        return context;
    }
}
