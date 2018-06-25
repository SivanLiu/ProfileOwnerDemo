package com.profileownerdemo;

import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

/**
 * Created by lyg on 2018/6/4.
 */
public class MyApplication extends Application {
    private HiddenBroadcastReceiver hiddenBroadcastReceiver = new HiddenBroadcastReceiver();

    private static Context context;

    class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        Log.e("ggg", "homeeeeee");
                        for (int j = 0; j < 10; j++) {
                            Intent setIntent = new Intent(context, SetProfileOwner.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(context, 0, setIntent, 0);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        Log.e("ggg", "rrrrrr");
//                        Toast.makeText(getApplicationContext(), "多任务键被监听", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private InnerRecevier innerRecevier = new InnerRecevier();

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACCROS_INTENT);
        intentFilter.addAction(PASS_INTENT_ACTION);
        this.registerReceiver(hiddenBroadcastReceiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        this.registerReceiver(innerRecevier, intentFilter1);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.unregisterReceiver(hiddenBroadcastReceiver);

        this.unregisterReceiver(innerRecevier);
        context = null;
    }

    public static Context getContext() {
        return context;
    }
}
