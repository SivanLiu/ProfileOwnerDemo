package com.island;

import com.profileownerdemo.R;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class Main3Activity extends AppCompatActivity {

    private boolean isDeviceOwnerApp = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserHandleHelper.checkUserHandleHashCodeSame()) {

            //判断设备管理器是否激活
            if (!ComponentNameHelper.isAdminActive()) {
                //激活设备管理器
            } else {
                List queryBroadcastReceivers = getPackageManager().queryBroadcastReceivers(new Intent("android.intent.action.USER_INITIALIZE").setPackage("com.oasisfeng.island"), 0);
                if (queryBroadcastReceivers.isEmpty()) {
                    Log.w("sivan", "Manual provisioning is finished, but launcher activity is still left enabled. Disable it now.");
                    getPackageManager().setComponentEnabledSetting(new ComponentName(this, getClass()), 2, 1);
                } else {
                    Log.w("sivan", "Manual provisioning is pending, resume it now.");
                    ActivityInfo info = ((ResolveInfo) queryBroadcastReceivers.get(0)).activityInfo;
                    sendBroadcast(new Intent().setComponent(new ComponentName(info.packageName, info.name)));
                }
            }
            finish();
        }

        isDeviceOwnerApp = ComponentNameHelper.isDeviceOwnerApp();
        if (isDeviceOwnerApp) {
            deviceOwnerSet(this, savedInstanceState);
        }

        UserHandle userHandle = UserHandleHelper.firstUserHandle;
        if (userHandle == null) {
            //跳转到设置界面
//            startActivity(new Intent(this, SetupActivity.class));
            pendIntent(this, "overall_analytics");
            finish();
        }

//        if () {
//            LauncherApps launcherApps = (LauncherApps) getSystemService("launcherapps");
//            if (launcherApps != null) {
//                List activityList = launcherApps.getActivityList(getPackageName(), userHandle);
//                if (!activityList.isEmpty()) {
//                    launcherApps.startMainActivity(((LauncherActivityInfo) activityList.get(0)).getComponentName(), userHandle, null, null);
//                    showHome();
//                    return;
//                }
//            }
//            //            startActivity(new Intent(this, SetupActivity.class));
//            pendIntent(this, "overall_analytics");
//            showHome();
//        } else if (startLauncherActivity(this, getPackageName(), userHandle)) {
//            showHome();
//        } else {
//            pendIntent(this, "overall_analytics");
//            showHome();
//        }

        deviceOwnerSet(this, savedInstanceState);
    }

    private void deviceOwnerSet(Context context, Bundle bundle) {
        setContentView(R.layout.activity_main);
        if (bundle == null) {
//            getFragmentManager().beginTransaction().replace(C0384d.container, new arq()).commit();
            pendIntent(context, "overall_analytics");
        }
    }

    public final boolean pendIntent(Context context, String str) {
        Intent intent = startIntent(context, str);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 536870912);
        PendingIntent.getBroadcast(context, 0, intent, 134217728);
        return broadcast != null;
    }

    private Intent startIntent(Context context, String str) {
        StringBuilder stringBuilder = new StringBuilder("SCOPE:");
        stringBuilder.append(str);
        return new Intent(stringBuilder.toString()).setPackage(context.getPackageName());
    }

    public static boolean startLauncherActivity(Context context, String packageName, UserHandle userHandle) {
        LauncherApps launcherApps = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            launcherApps = (LauncherApps) context.getSystemService(LAUNCHER_APPS_SERVICE);
        }

        if (launcherApps != null) {
            List<LauncherActivityInfo> list = launcherApps.getActivityList(packageName, userHandle);

            if (null != list && !list.isEmpty()) {
                launcherApps.startMainActivity((list.get(0)).getComponentName(), userHandle, null, null);
                return true;
            }
        }
        return false;
    }
}
