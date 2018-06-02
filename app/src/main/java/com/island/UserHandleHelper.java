package com.island;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

/**
 * Created by lyg on 2018/5/31.
 */
public class UserHandleHelper extends MyContentProvider {
    public static UserHandle firstUserHandle;
    public static UserHandle secondUserHandle;

    private static final UserHandle myUserHandle = null;
    private static int myUserHandleId;
    private static int secondUserHandleId = -1;

    private MonitorUserHandleChange monitorUserHandleChange = new MonitorUserHandleChange(this);

    static {
        UserHandle myUserHandle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            myUserHandle = Process.myUserHandle();
        }
        myUserHandle = myUserHandle;
        myUserHandleId = myUserHandle.hashCode();
    }

    class MonitorUserHandleChange extends BroadcastReceiver {

        final UserHandleHelper userHandleHelper;

        MonitorUserHandleChange(UserHandleHelper userHandleHelper) {
            this.userHandleHelper = userHandleHelper;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("sivan", "Profile changed");
            userHandleChange(context);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        BroadcastReceiver broadcastReceiver = this.monitorUserHandleChange;
        String[] strArr = new String[]{"android.intent.action.MANAGED_PROFILE_ADDED", "android.intent.action.MANAGED_PROFILE_REMOVED"};
        IntentFilter intentFilter = new IntentFilter();
        for (int i = 0; i < 2; ++i) {
            intentFilter.addAction(strArr[i]);
        }

        intentFilter.setPriority(999);
        if (context == null) {
            return false;
        }
        context.registerReceiver(broadcastReceiver, intentFilter);
        userHandleChange(context);
        return true;
    }

    public static void userHandleChange(Context context) {
        UserHandle currentUserHandle = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (UserHandle userHandle : ((UserManager) context.getSystemService(Context.USER_SERVICE)).getUserProfiles()) {
                    if (userHandle.hashCode() <= 100) {
                        if (checkUserHandle(userHandle)) {
                            firstUserHandle = userHandle;
                        } else if (currentUserHandle == null) {
                            currentUserHandle = userHandle;
                        }
                    }
                }
            }
        }

        int i = -1;
        if (currentUserHandle != null) {
            i = currentUserHandle.hashCode();
        }

        secondUserHandleId = i;
        StringBuilder stringBuilder = new StringBuilder("Profile ID:");
        stringBuilder.append(secondUserHandleId);
        Log.i("sivan", stringBuilder.toString());
    }

    public static boolean getFirstUserHandle() {
        return firstUserHandle != null;
    }

    public static UserHandle getMyUserHandle() {
        return myUserHandle;
    }

    public static boolean checkUserHandle(UserHandle userHandle) {
        return userHandle.hashCode() == 0;
    }

    public static boolean checkUserHandleHashCodeSame() {
        return myUserHandleId == secondUserHandleId;
    }

    public static boolean checkUserHandleHashCode(UserHandle userHandle) {
        return userHandle.hashCode() == myUserHandleId;
    }

    public static boolean checkSameUserHandle(int i1, int i2) {
        return i1 % 100000 == i2 % 100000;
    }
}
