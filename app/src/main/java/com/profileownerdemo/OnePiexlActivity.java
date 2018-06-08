package com.profileownerdemo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import static com.profileownerdemo.Util.PASS_DATA;

/**
 * Created by lyg on 2018/6/7.
 */
public class OnePiexlActivity extends Activity {
    ComponentName componentName = new ComponentName("com.profileownerdemo", "com.profileownerdemo.OnePiexlActivity");
    private BroadcastReceiver endReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent secondIntent = new Intent(context, PassActivity.class);
            String data = intent.getStringExtra(PASS_DATA);
            secondIntent.putExtra(PASS_DATA, data);
            Log.e("ggg", "intent " + intent.getAction() + "  data = " + data);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("ggg", "ActivityNotFoundException");
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("gggg", "OnePiexlActivity start");
        Intent intent = getIntent();
        Bundle result = intent.getExtras();
        Log.e("gggg", "OnePiexlActivity start  result = " + result.getString("bundle"));
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        final WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACCROS_INTENT);
//        registerReceiver(endReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ggg", "onePixelActivity destroy");
//        this.unregisterReceiver(endReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
