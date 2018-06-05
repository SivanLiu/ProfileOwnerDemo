package com.profileownerdemo;

import android.app.Application;
import android.content.IntentFilter;

/**
 * Created by lyg on 2018/6/4.
 */
public class MyApplicatinon extends Application {
    private HiddenBroadcastReceiver hiddenBroadcastReceiver = new HiddenBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.disable.icon");
        this.registerReceiver(hiddenBroadcastReceiver, intentFilter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.unregisterReceiver(hiddenBroadcastReceiver);
    }
}
