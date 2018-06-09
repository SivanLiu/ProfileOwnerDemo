package com.profileownerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import static com.profileownerdemo.Util.ACROSS_INTENT_ACTION;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;
import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

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

        Intent intent = getIntent();
        Log.e(TAG, "receive intent " + (intent == null ? null : intent.getAction()));
        if (intent != null && ACROSS_INTENT_ACTION.equals(intent.getAction())) {
            String action = intent.getAction();
            Bundle receiveBundle = intent.getBundleExtra(PASS_DATA);
            String result = receiveBundle.getString(PASS_DATA_KEY);
            Log.e(TAG, "UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);

            Intent sendIntent = new Intent();
            intent.setAction(PASS_INTENT_ACTION);
            intent.putExtra(PASS_DATA, receiveBundle);
            this.sendBroadcast(sendIntent);
            Log.e(TAG, "sendBroadcast intent to ....");
        }

//        finish();
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
    }
}
