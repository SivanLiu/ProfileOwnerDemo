package com.profileownerdemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class SetProfileOwner extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;

    private static final String ACCROS_INTENT = "com.disable.comp";
    private boolean multiUser = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACCROS_INTENT.equals(intent.getAction())) {
                if (Process.myUserHandle().hashCode() == 0) {
                    setDisableComponent(context, getClass(), true);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACCROS_INTENT);
        this.registerReceiver(broadcastReceiver, intentFilter);

        button = findViewById(R.id.setUp);
        button.setOnClickListener(this);

        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        LauncherApps launcherApps = (LauncherApps) this.getSystemService(LAUNCHER_APPS_SERVICE);
        UserManager userManager1 = (UserManager) this.getSystemService(USER_SERVICE);
        UserHandle myUser = Process.myUserHandle();


//        for (UserHandle uh : userManager1.getUserProfiles()) {
//            List list = launcherApps.getActivityList(getPackageName(), uh);
//            if (list.size() != 0) {
//                Log.e("ggg", "user = " + uh.hashCode() + "  comp = " + launcherApps.getActivityList(getPackageName(), uh).get(0).getComponentName());
//            }
//        }

        if (myUser.hashCode() == 0) {
            for (UserHandle uh : userManager1.getUserProfiles()) {
                if (uh.hashCode() != myUser.hashCode()) {
                    multiUser = false;
//                    launcherApps.startMainActivity(new ComponentName(this, getClass()), uh, null, null);
                    return;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (manager.isProfileOwnerApp(getApplicationContext().getPackageName())) {
                startProfileOWnerDesktop();
//                Intent intent = new Intent(this, PermissionManager.class);
//                startActivity(intent);
            } else {
                if (!multiUser) {
                    provisionManagedProfile(this);
                    startProfileOWnerDesktop();
                    try {
                        Thread.sleep(2000);
                        setDisableComponent(this, getClass(), true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_up_profile:
                if (multiUser) {
                    return;
                }
                provisionManagedProfile(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
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

    private void setDisableComponent(Context context, Class<?> cls, boolean disable) {
        int state = context.getPackageManager().getComponentEnabledSetting(new ComponentName(context, cls));
        if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == state) {
            return;
        }

        if (disable) {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    , PackageManager.DONT_KILL_APP);
        } else {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, cls),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    , PackageManager.DONT_KILL_APP);
        }
    }

    private void startProfileOWnerDesktop() {
        Intent intent = new Intent(this, ProfileOwnerDesktop.class);
        startActivity(intent);
    }

    public void getInstalledApps() {
        PackageManager pm = this.getPackageManager();
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.e("ggg", "packageNames = " + packageInfo.packageName + "\n" + "  " +
                    "label = " + pm.getApplicationLabel(packageInfo).toString() + " \n" + "");
        }
    }
}
