package com.profileownerdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import static com.profileownerdemo.Util.ACCROS_INTENT;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.SECOND_INTENT;

/**
 * Created by lyg on 2018/6/7.
 */
public class OnePiexlActivity extends AppCompatActivity {
    private ComponentName componentName = new ComponentName("com.profileownerdemo.OnePiexlActivity",
            "com.profileownerdemo.SetProfileOwner");

    private BroadcastReceiver endReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent secondIntent = new Intent();
            secondIntent.setComponent(componentName);
            secondIntent.setAction(SECOND_INTENT);
            String data = intent.getStringExtra(PASS_DATA);
            secondIntent.putExtra(PASS_DATA, data);
            Log.e("ggg", "intent " + intent.getAction() + "  data = " + data);
            context.startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("gggg", "OnePiexlActivity");
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        final WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACCROS_INTENT);
        registerReceiver(endReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
