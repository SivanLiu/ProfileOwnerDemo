package com.profileownerdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;

/**
 * Created by lyg on 2018/6/4.
 */
public class HiddenBroadcastReceiver extends BroadcastReceiver {
    public static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "com.profileownerdemo", "com.profileownerdemo.SetProfileOwner");
    private static final String TAG = "HiddenBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        boolean startActivity = intent.getBooleanExtra(LAUNCH_MAIN_ACTIVITY, false);
//        Log.e("ggg", "intent = " + intent.getAction() + "  launch_main = " + startActivity);
//        Util.setDisableComponent(context, LAUNCHER_COMPONENT_NAME, false);

        String action = intent.getAction();
        String result = intent.getBundleExtra(PASS_DATA).getString(PASS_DATA_KEY);
        Log.e(TAG, "last receive UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);
    }
}
