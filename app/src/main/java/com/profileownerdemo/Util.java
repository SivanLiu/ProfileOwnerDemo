package com.profileownerdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by lyg on 4/18/18.
 */
public class Util {
    public static final String ACCROS_INTENT = "com.disable.icon";
    public static final String LAUNCH_MAIN_ACTIVITY = "launch_main_activity";
    public static final String SECOND_INTENT = "com.second.intent";
    public static final String PASS_DATA = "data";
    public static final String PASS_DATA_KEY = "key";
    public static final String ACROSS_INTENT_ACTION = "com.intent.across";
    public static final String PASS_INTENT_ACTION = "com.intent.pass";
    public static ComponentName componentName = new ComponentName("com.profileownerdemo", "com.profileownerdemo.OnePiexlActivity");


    public synchronized ArrayList<String> getStorageDirectories(Activity activity) {
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        Log.e("ggg", "rawExternalStorage = " + rawExternalStorage);
        Log.e("ggg", "rawSecondaryStoragesStr = " + rawSecondaryStoragesStr);
        Log.e("ggg", "rawEmulatedStorageTarget = " + rawEmulatedStorageTarget);
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = (Pattern.compile("/")).split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }

            Log.e("ggg", "path = " + rawEmulatedStorageTarget + File.separator + rawUserId);
        }


        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }

        Log.e("ggg", "check = " + checkStoragePermission(activity));
        if (!checkStoragePermission(activity)) {
            requestStoragePermission(activity);
        }
        if (SDK_INT >= Build.VERSION_CODES.M && checkStoragePermission(activity))
            rv.clear();
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String strings[] = getExtSdCardPathsForActivity(activity);
            for (String s : strings) {
                File f = new File(s);
                if (!rv.contains(s) && canListFiles(f)) {
                    Log.e("ggg", "write = " + f.canWrite() + " execute = " + f.canExecute());
                    rv.add(s);
                    rv.add("/storage/sdcard0");
                }
            }
        }
        return rv;
    }

    public boolean checkStoragePermission(Activity activity) {
        // Verify that all required contact permissions have been granted.
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 77);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 77);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String[] getExtSdCardPathsForActivity(Context context) {
        List<String> paths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("ggg", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }

    public static boolean canListFiles(File f) {
        return f.canRead() && f.isDirectory();
    }

    public static void setDisableComponent(Context context, ComponentName componentName, boolean disable) {
        if (disable) {
            context.getPackageManager().setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    , PackageManager.DONT_KILL_APP);
        } else {
            context.getPackageManager().setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    , PackageManager.DONT_KILL_APP);
        }
    }

    public static boolean startLauncherActivity(LauncherApps launcherApps, String packageName, UserHandle userHandle) {
        if (launcherApps != null) {
            List<LauncherActivityInfo> list = launcherApps.getActivityList(packageName, userHandle);

            if (null != list && !list.isEmpty()) {
                launcherApps.startMainActivity((list.get(0)).getComponentName(), userHandle, null, null);
                return true;
            }
        }
        return false;
    }

    public static boolean isMainActivityAlive(Context context, String activityName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            // 注意这里的 topActivity 包含 packageName和className，可以打印出来看看
            if (info.topActivity.toString().equals(activityName) || info.baseActivity.toString().equals(activityName)) {
                Log.e("ggg", info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                return true;
            }
        }
        return false;
    }

    public static UserHandle getSecondeUserHandle(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (userManager == null) {
            return null;
        }
        for (UserHandle userHandle : userManager.getUserProfiles()) {
            if (userHandle.hashCode() != 0) {
                return userHandle;
            }
        }
        return null;
    }

    public static UserHandle getSystemUserHandler(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (userManager == null) {
            return null;
        }
        for (UserHandle userHandle : userManager.getUserProfiles()) {
            if (userHandle.hashCode() == 0) {
                return userHandle;
            }
        }
        return null;
    }

    public static List<String> getInstalledApps(Context context) {
        List<String> packageNames = new ArrayList<>();
//        List<ApplicationInfo> pkgs = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
//        for (ApplicationInfo applicationInfo : pkgs) {
//            if (!pkgs.contains(applicationInfo.packageName)) {
//                packageNames.add(applicationInfo.packageName);
//            }
//        }

        packageNames.add("com.android.contacts");
        packageNames.add("com.android.documentsui");
        return packageNames;
    }

    public static void setApplicationHidden(Context context, DevicePolicyManager policyManager, List<String> packageNames, boolean hidden) {
        if (packageNames == null || packageNames.size() == 0) {
            return;
        }
        for (String packageName : packageNames) {
            Log.e("sivan", "setApplicationHidden packageName = " + packageName);
            if (!context.getPackageName().equalsIgnoreCase(packageName)) {
                policyManager.setApplicationHidden(BasicDeviceAdminReceiver.getComponentName(context), packageName, hidden);
            }
        }
    }

    public static DevicePolicyManager getDeviceManager(Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (policyManager == null) {
            return null;
        }

        return policyManager;
    }

}
