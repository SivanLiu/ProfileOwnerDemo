package com.profileownerdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
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
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            Log.e(TAG, "action is null");
            return;
        }

        Bundle resultBundle = intent.getBundleExtra(PASS_DATA);
        if (resultBundle == null) {
            Log.e(TAG, "result bundle is null");
            return;
        }

        String result = resultBundle.getString(PASS_DATA_KEY);
        Log.e(TAG, "last receive UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);
    }
}
