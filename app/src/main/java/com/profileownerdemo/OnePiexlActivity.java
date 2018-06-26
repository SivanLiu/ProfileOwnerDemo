package com.profileownerdemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.profileownerdemo.Util.ACROSS_INTENT_ACTION;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;
import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

/**
 * Created by lyg on 2018/6/7.
 */
public class OnePiexlActivity extends Activity {
    private static final String TAG = "OnePiexlActivity";

    public static AtomicBoolean showHome = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        final WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        Intent intent = getIntent();
        Log.e(TAG, "receive intent " + (intent == null ? null : intent.getAction()));
        if (intent != null && ACROSS_INTENT_ACTION.equals(intent.getAction())) {
            String action = intent.getAction();
            Bundle receiveBundle = intent.getBundleExtra(PASS_DATA);
            String result = receiveBundle.getString(PASS_DATA_KEY);
            Log.e(TAG, "UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);

            Intent sendIntent = new Intent(this, MonitorService.class);
            sendIntent.setAction(PASS_INTENT_ACTION);
            sendIntent.putExtra(PASS_DATA, receiveBundle);
//            sendBroadcast(sendIntent);
            this.startService(sendIntent);
            Log.e(TAG, sendIntent + " sendBroadcast intent to ....");
        }

        if (showHome.get()) {
            openLauncherUi();
        } else {
            Intent intents = new Intent(this, SetProfileOwner.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intents);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onePixelActivity destroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onePixelActivity onResume");
//        showHome();
    }

    public static String getTopPacakge(Context mContext) {
        try {
            ActivityManager am = (ActivityManager) mContext
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean openLauncherUi() {
        ArrayList<String> list = getLauncherPkgToStart(this);
        if (list == null || list.size() < 1) {
            finish();
            return false;
        }
        String pkg = list.get(0);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setPackage(pkg);
        intent.addCategory(Intent.CATEGORY_HOME);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static ArrayList<String> getLauncherPkgToStart(Context context) {
        ArrayList<String> list = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> installedList = context.getApplicationContext()
                .getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : installedList) {
            String pkg = info.activityInfo.packageName;
            if (context.getPackageName().equals(pkg)) {
                continue;
            }
            list.add(pkg);
        }
        return list;
    }
}
