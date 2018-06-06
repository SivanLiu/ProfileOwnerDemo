package com.profileownerdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.profileownerdemo.Util.LAUNCH_MAIN_ACTIVITY;

/**
 * Created by lyg on 2018/6/4.
 */
public class HiddenBroadcastReceiver extends BroadcastReceiver {
    private static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "com.profileownerdemo", "com.profileownerdemo.SetProfileOwner");

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean startActivity = intent.getBooleanExtra(LAUNCH_MAIN_ACTIVITY, false);
        Log.e("ggg", "intent = " + intent.getAction() + "  launch_main = " + startActivity);
        Util.setDisableComponent(context, LAUNCHER_COMPONENT_NAME, false);

        if (startActivity) {
            Log.e("ggg", "start main");
            Intent start = new Intent();
            start.setComponent(LAUNCHER_COMPONENT_NAME);
            context.startActivity(start);
        }
    }
}
