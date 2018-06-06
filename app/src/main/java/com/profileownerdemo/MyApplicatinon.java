package com.profileownerdemo;

import android.app.Application;
import android.content.IntentFilter;

import static com.profileownerdemo.Util.ACCROS_INTENT;

/**
 * Created by lyg on 2018/6/4.
 */
public class MyApplicatinon extends Application {
    private HiddenBroadcastReceiver hiddenBroadcastReceiver = new HiddenBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACCROS_INTENT);
        this.registerReceiver(hiddenBroadcastReceiver, intentFilter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.unregisterReceiver(hiddenBroadcastReceiver);
    }
}
