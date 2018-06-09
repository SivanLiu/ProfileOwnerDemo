package com.profileownerdemo;

import android.app.Application;
import android.content.IntentFilter;

import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

/**
 * Created by lyg on 2018/6/4.
 */
public class MyApplicatinon extends Application {
    private HiddenBroadcastReceiver hiddenBroadcastReceiver = new HiddenBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACCROS_INTENT);
        intentFilter.addAction(PASS_INTENT_ACTION);
        this.registerReceiver(hiddenBroadcastReceiver, intentFilter);

//        Intent intent = new Intent(this, OnePiexlActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.unregisterReceiver(hiddenBroadcastReceiver);
    }
}
