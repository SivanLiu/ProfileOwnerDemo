package com.profileownerdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by lyg on 2018/6/4.
 */
public class HiddenBroadcastReceiver extends BroadcastReceiver {
    private static final String ACCROS_INTENT = "com.disable.icon";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ggg", "intent = "+intent.getAction());
        if (ACCROS_INTENT.equals(intent.getAction())) {
            try {
                setDisableComponent(context, Class.forName("com.profileownerdemo.SetProfileOwner"), false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDisableComponent(Context context, Class<?> cls, boolean disable) {
        int state = context.getPackageManager().getComponentEnabledSetting(new ComponentName(context, cls));
        if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == state) {
            return;
        }

        if (disable) {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    , PackageManager.DONT_KILL_APP);
        } else {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    , PackageManager.DONT_KILL_APP);
        }
    }
}
