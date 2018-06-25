package com.profileownerdemo;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherApps;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.app.admin.DevicePolicyManager.FLAG_MANAGED_CAN_ACCESS_PARENT;
import static android.app.admin.DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED;
import static com.profileownerdemo.Util.ACROSS_INTENT_ACTION;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SetProfileOwner extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SetProfileOwner";
    public static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "com.profileownerdemo", "com.profileownerdemo.SetProfileOwner");

    public static final ComponentName onePiexlActivity = new ComponentName("com.profileownerdemo", "com.profileownerdemo.OnePiexlActivity");

    private Button setProfile;
    private Button bt_enable_componment;
    private Button bt_disable_componment;
    private Button bt_enable_across_intent;
    private Button bt_disable_across_intent;
    private Button bt_send_across_intent;
    private Button startApp;
    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    private boolean multiUser = false;
    private DevicePolicyManager manager = null;
    private static LauncherApps launcherApps = null;
    private UserManager userManager = null;

    IntentFilter intentFilter = new IntentFilter(ACROSS_INTENT_ACTION);

    private HomeKeyLocker locker;
    private Activity activity;

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        if (!Util.isAppUsageStatAccessPermitted(this)) {
            Intent usage = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(usage);
        }

        Log.e("ggg", "top package : " + Util.getTopPkgByUsage(this) + " intent = "+getIntent());
        if (!this.getPackageName().equals(Util.getTopPkgByUsage(this))) {
            finish();
//            openLauncherUi();
        } else {
            openLauncherUi();
            Log.e("ggggggg", "");
        }
        manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        launcherApps = (LauncherApps) this.getSystemService(LAUNCHER_APPS_SERVICE);
        userManager = (UserManager) this.getSystemService(USER_SERVICE);

        Log.e("gggg 1", "accounts = " + userManager.getUserProfiles().size());

        AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);

        Log.e("gggg 2", "accounts = " + accountManager.getAccounts().length);

        setProfile = findViewById(R.id.set_up_profile);
        setProfile.setOnClickListener(this);
        bt_enable_componment = findViewById(R.id.enable_componment);
        bt_enable_componment.setOnClickListener(this);
        bt_disable_componment = findViewById(R.id.disable_componment);
        bt_disable_componment.setOnClickListener(this);
        bt_enable_across_intent = findViewById(R.id.enable_across_intent);
        bt_enable_across_intent.setOnClickListener(this);
        bt_disable_across_intent = findViewById(R.id.disable_across_intent);
        bt_disable_across_intent.setOnClickListener(this);
        bt_send_across_intent = findViewById(R.id.send_across_intent);
        bt_send_across_intent.setOnClickListener(this);
        startApp = findViewById(R.id.startApp);
        startApp.setOnClickListener(this);

        UserHandle primaryUser = Process.myUserHandle();

        for (UserHandle uh : userManager.getUserProfiles()) {
            if (uh.hashCode() != primaryUser.hashCode()) {
                multiUser = true;
//                launcherApps.startMainActivity(LAUNCHER_COMPONENT_NAME, uh, null, null);
                return;
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (manager.isProfileOwnerApp(getApplicationContext().getPackageName())) {
////                startProfileOWnerDesktop();
////                Intent intent = new Intent(this, PermissionManager.class);
////                startActivity(intent);
//            } else {
//                if (!multiUser) {
//                    provisionManagedProfile(this);
////                    startProfileOWnerDesktop();
//                }
//            }
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean openLauncherUi() {
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
        Log.e("gggg", "pkg = " + pkg);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_up_profile:
                provisionManagedProfile(this);
                break;

            case R.id.enable_componment:
                Intent ss = new Intent(this, OnePiexlActivity.class);
                startActivity(ss);
//                Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, false);
                break;

            case R.id.disable_componment:
                Log.e(TAG, "disable_componment");
                Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, true);
                break;

            case R.id.enable_across_intent:
                //只有在设备管理激活的情况下才可以使用该接口
                Log.e(TAG, "enable_across_intent admin = " + manager.isAdminActive(BasicDeviceAdminReceiver.getComponentName(this)));

                if (manager.isAdminActive(BasicDeviceAdminReceiver.getComponentName(this))) {
                    manager.addCrossProfileIntentFilter(BasicDeviceAdminReceiver.getComponentName(this),
                            intentFilter, FLAG_MANAGED_CAN_ACCESS_PARENT | FLAG_PARENT_CAN_ACCESS_MANAGED);
                    Log.e(TAG, "enable_across_intent");
                }
                break;

            case R.id.disable_across_intent:
                if (manager.isAdminActive(BasicDeviceAdminReceiver.getComponentName(this))) {
                    manager.clearCrossProfileIntentFilters(BasicDeviceAdminReceiver.getComponentName(this));
                    Log.e(TAG, "disable_across_intent");
                }
                break;

            case R.id.send_across_intent:
                Intent intent = new Intent(ACROSS_INTENT_ACTION);
                Bundle bundle = new Bundle();
                bundle.putString(PASS_DATA_KEY, "value....");
                intent.putExtra(PASS_DATA, bundle);
                try {
                    Util.setDisableComponent(this, new ComponentName(getPackageName(), OnePiexlActivity.class.getName()), true);
                    startActivity(intent);
                    if (Process.myUserHandle().hashCode() != 0) {
                        Util.setApplicationHidden(this, manager, Util.getInstalledApps(this), true);
                    }

                    Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, true);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Util.setDisableComponent(this, new ComponentName(getPackageName(), OnePiexlActivity.class.getName()), false);
                    finish();
                    Log.e(TAG, Process.myUserHandle().toString() + " send_across_intent...");
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.startApp:
//                Intent showApps = new Intent(this, AppShowActivity.class);
//                startActivity(showApps);

                Intent intents = new Intent(this, OnePiexlActivity.class);
                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intents);
//                Util.startLauncherActivity(launcherApps, this.getPackageName(), Util.getSecondeUserHandle(this));
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Provisioning done.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Provisioning failed.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public void onBackPressed() {
//        Log.e("ggg", "onBackPressed");
//        super.onBackPressed();
//    }

    @Override
    protected void onPause() {
        Log.e("ggg", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void provisionManagedProfile(Activity activity) {
        if (null == activity) {
            return;
        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);

        // Use a different intent extra below M to configure the admin component.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //noinspection deprecation
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                    activity.getApplicationContext().getPackageName());
        } else {
            final ComponentName component = new ComponentName(activity,
                    BasicDeviceAdminReceiver.class.getName());
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                    component);
        }

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
            activity.finish();
        } else {
            Toast.makeText(activity, "Device provisioning is not enabled. Stopping.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
