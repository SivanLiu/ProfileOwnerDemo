package com.profileownerdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.profileownerdemo.Util.ACCROS_INTENT;


public class PassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        Log.e("ggg", "xxxx onCreate");
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.e("ggg", "action = " + action + "  type = " + type);

        Intent intent1 = new Intent();
        intent.setAction(ACCROS_INTENT);
        this.sendBroadcast(intent1);

        this.getPackageManager().setComponentEnabledSetting(SetProfileOwner.LAUNCHER_COMPONENT_NAME,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                , PackageManager.DONT_KILL_APP);
        Log.e("ggg", "enabled");
    }
}
