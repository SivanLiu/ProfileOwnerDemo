package com.profileownerdemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyg on 2018/6/7.
 */
public class OnePiexlActivity extends Activity {
    private static final String TAG = "OnePiexlActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Process.myUserHandle().hashCode() != 0) {
//            Log.e(TAG, "OnePiexlActivity return");
//            return;
//        }

        Log.e(TAG, "OnePiexlActivity start");
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        final WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

//        Intent intent = getIntent();
//        Log.e(TAG, "receive intent " + (intent == null ? null : intent.getAction()));
//        if (intent != null && ACROSS_INTENT_ACTION.equals(intent.getAction())) {
//            String action = intent.getAction();
//            Bundle receiveBundle = intent.getBundleExtra(PASS_DATA);
//            String result = receiveBundle.getString(PASS_DATA_KEY);
//            Log.e(TAG, "UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);
//
//            Intent sendIntent = new Intent(this, MonitorService.class);
//            sendIntent.setAction(PASS_INTENT_ACTION);
//            sendIntent.putExtra(PASS_DATA, receiveBundle);
////            sendBroadcast(sendIntent);
//            this.startService(sendIntent);
//            Log.e(TAG, sendIntent + " sendBroadcast intent to ....");
//        }

//        finish();
        List<String> pkgNamesT = new ArrayList<String>();
        List<String> actNamesT = new ArrayList<String>();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (int i = 0; i < resolveInfos.size(); i++) {
            Log.e("ggg", "resolveInfo = " + resolveInfos.get(i).activityInfo + " pkg = " + resolveInfos.get(i).activityInfo.packageName);
            String string = resolveInfos.get(i).activityInfo.packageName;
            if (!string.equals(this.getPackageName())) {
                //自己的launcher不要
                pkgNamesT.add(string);
                string = resolveInfos.get(i).activityInfo.name;
                actNamesT.add(string);
            }
        }

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.e("ggg", "name = " + name);
        if ("com.profileownerdemo.SetProfileOwner".equals(name)) {
            Intent intents = new Intent(this, SetProfileOwner.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
//        finish();
    }
}
