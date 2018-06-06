package com.profileownerdemo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.app.admin.DevicePolicyManager.FLAG_MANAGED_CAN_ACCESS_PARENT;
import static android.app.admin.DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED;
import static com.profileownerdemo.Util.ACCROS_INTENT;
import static com.profileownerdemo.Util.LAUNCH_MAIN_ACTIVITY;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SetProfileOwner extends AppCompatActivity implements View.OnClickListener {
    private static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "com.profileownerdemo", "com.profileownerdemo.SetProfileOwner");
    private Button setProfile;
    private Button startApp;
    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    private boolean multiUser = false;
    private DevicePolicyManager manager = null;
    private static LauncherApps launcherApps = null;
    private UserManager userManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        launcherApps = (LauncherApps) this.getSystemService(LAUNCHER_APPS_SERVICE);
        userManager = (UserManager) this.getSystemService(USER_SERVICE);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SEND);
//        filter.addDataType("text/plain");
//        filter.addDataType("image/jpeg");
//        filter.addDataType();
        // This is how you can register an IntentFilter as allowed pattern of Intent forwarding
        if(Process.myUserHandle().hashCode() !=0){
            manager.addCrossProfileIntentFilter(BasicDeviceAdminReceiver.getComponentName(this),
                    filter, FLAG_MANAGED_CAN_ACCESS_PARENT | FLAG_PARENT_CAN_ACCESS_MANAGED);
        }

        setProfile = findViewById(R.id.set_up_profile);
        setProfile.setOnClickListener(this);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (manager.isProfileOwnerApp(getApplicationContext().getPackageName())) {
//                startProfileOWnerDesktop();
//                Intent intent = new Intent(this, PermissionManager.class);
//                startActivity(intent);
            } else {
                if (!multiUser) {
                    provisionManagedProfile(this);
//                    startProfileOWnerDesktop();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_up_profile:
                UserHandle destUser;
                if (multiUser) {
                    Log.e("ggg", "disabled ");
                    Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, true);
                    for (UserHandle currentUser : userManager.getUserProfiles()) {
                        if (currentUser.hashCode() != 0) {
                            destUser = currentUser;
                            Intent intent = new Intent(ACCROS_INTENT);
                            intent.putExtra(LAUNCH_MAIN_ACTIVITY, true);
                            this.sendBroadcast(new Intent(ACCROS_INTENT));
                            Log.e("ggg", "send  " + currentUser.toString());
//                            Util.startLauncherActivity(launcherApps, this.getPackageName(), destUser);
                            return;
                        }
                    }
                    return;
                }
                provisionManagedProfile(this);
                break;
            case R.id.startApp:
                for (UserHandle currentUser : userManager.getUserProfiles()) {
                    if (currentUser.hashCode() != 0) {
                        destUser = currentUser;
                        Log.e("ggg", "startApp " + currentUser.toString());
                        Util.startLauncherActivity(launcherApps, this.getPackageName(), destUser);
                        return;
                    }
                }

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
