package com.island;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by lyg on 2018/6/1.
 */
public class ComponentNameHelper {
    private static ComponentName componentName;

    public static ComponentName setComponentName(Context context) {
        if (componentName == null) {
            componentName = getComponentName(context);
        }

        return componentName;
    }

    private static ComponentName getComponentName(Context context) {
        List<ComponentName> activeAdmins = ((DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE)).getActiveAdmins();
        if (!(activeAdmins == null || activeAdmins.isEmpty())) {
            for (ComponentName componentName : activeAdmins) {
                if ("com.profileownerdemo".equals(componentName.getPackageName())) {
                    return componentName;
                }
            }
        }

        try {
            Intent intent = new Intent();
            intent.setAction("android.app.action.DEVICE_ADMIN_ENABLED");
            intent.setPackage(context.getPackageName());
            List queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 512);

            if (queryBroadcastReceivers.size() != 1) {
                StringBuilder stringBuilder = new StringBuilder("Engine module is not correctly installed: ");
                stringBuilder.append(queryBroadcastReceivers);
                throw new IllegalStateException(stringBuilder.toString());
            }
            ResolveInfo resolveInfo = (ResolveInfo) queryBroadcastReceivers.get(0);

            componentName = new ComponentName("com.profileownerdemo", ((ResolveInfo) queryBroadcastReceivers.get(0)).activityInfo.name);
            if (!resolveInfo.activityInfo.enabled) {
                context.getPackageManager().setComponentEnabledSetting(componentName, 1, 1);
            }
            return componentName;
        } catch (Throwable e) {
            return new ComponentName(context.getPackageName(), "com.profileownerdemo.BasicDeviceAdminReceiver");
        }
    }

    public static boolean isAdminActive(){
        return false;
    }

    public static boolean isDeviceOwnerApp(){
        return false;
    }
}
