package com.profileownerdemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION;
import static android.app.admin.DevicePolicyManager.FLAG_MANAGED_CAN_ACCESS_PARENT;
import static android.app.admin.DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED;
import static com.profileownerdemo.Util.ACROSS_INTENT_ACTION;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;
import static com.profileownerdemo.Util.componentName;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SetProfileOwner extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SetProfileOwner";
    public static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "com.profileownerdemo", "com.profileownerdemo.SetProfileOwner");

    public static final ComponentName onePiexlActivity = new ComponentName("com.profileownerdemo", "com.profileownerdemo.ProfileOwnerActivity");
    private static final String FILE_PROVIDER_AUTHORITIES
            = "com.profileownerdemo.fileprovider";

    private static final String TYPE = "text";

    private Button setProfile;
    private Button bt_enable_componment;
    private Button bt_disable_componment;
    private Button bt_enable_across_intent;
    private Button bt_disable_across_intent;
    private Button bt_send_across_intent;
    private Button startApp;

    private Button createUser;
    private Button switchUser;
    private Button removeUser;

    private long newUserSerialNumber = 0;
    private long ownerSerialNumber = 0;


    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    private boolean multiUser = false;
    private DevicePolicyManager manager = null;
    private UserManager userManager;
    private static LauncherApps launcherApps = null;
    private UserManager userManager = null;

    IntentFilter intentFilter = new IntentFilter(ACROSS_INTENT_ACTION);
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        userManager = (UserManager) getSystemService(USER_SERVICE);
        if (Process.myUserHandle().hashCode() == 0) {
            ownerSerialNumber = userManager.getSerialNumberForUser(Process.myUserHandle());
        }

        launcherApps = (LauncherApps) this.getSystemService(LAUNCHER_APPS_SERVICE);
        userManager = (UserManager) this.getSystemService(USER_SERVICE);
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

        createUser = findViewById(R.id.createUser);
        switchUser = findViewById(R.id.switchUser);
        removeUser = findViewById(R.id.removeUser);

        createUser.setOnClickListener(this);
        switchUser.setOnClickListener(this);
        removeUser.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_up_profile:
                provisionManagedProfile(this);
                break;

            case R.id.enable_componment:
                Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, false);
                break;

            case R.id.disable_componment:
                Log.e(TAG, "disable_componment");
                Util.setDisableComponent(this, LAUNCHER_COMPONENT_NAME, true);
                break;

            case R.id.enable_across_intent:
                //只有在设备管理激活的情况下才可以使用该接口
                Log.e(TAG, "enable_across_intent admin = " + manager.isAdminActive(BasicDeviceAdminReceiver.getComponentName(this)));

                if (manager.isAdminActive(BasicDeviceAdminReceiver.getComponentName(this))) {
                    try {
                        intentFilter.addDataType("*/*");
                        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                    } catch (IntentFilter.MalformedMimeTypeException e) {
                        e.printStackTrace();
                    }
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
                Uri data;
                File file = new File(this.getFilesDir() + "/text", "hello.txt");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    data = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITIES, file);
                    // 给目标应用一个临时授权
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    data = Uri.fromFile(file);
                }
                intent.setData(data);

                try {
                    transferIntentToProfile(this, intent);
                    startActivity(intent);
                    Log.e(TAG, Process.myUserHandle().toString() + " send_across_intent...");
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.startApp:
//                Intent showApps = new Intent(this, AppShowActivity.class);
//                startActivity(showApps);

//                Intent intents = new Intent(this, ProfileOwnerActivity.class);
//                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                this.startActivity(intents);
//                Util.startLauncherActivity(launcherApps, this.getPackageName(), Util.getSecondeUserHandle(this));

                shareFile();
                break;

            case R.id.createUser:
                UserHandle userHandle = manager.createAndManageUser(componentName, "Owner", componentName, null, 0);
                newUserSerialNumber = userManager.getSerialNumberForUser(userHandle);
                break;
            case R.id.switchUser:
                if (Process.myUserHandle().hashCode() == 0) {
                    manager.switchUser(componentName, userManager.getUserForSerialNumber(newUserSerialNumber));
                } else {
                    manager.switchUser(componentName, userManager.getUserForSerialNumber(ownerSerialNumber));
                }
                break;
            case R.id.removeUser:
                manager.removeUser(componentName, userManager.getUserForSerialNumber(newUserSerialNumber));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (null == activityManager) {
            return;
        }

        activityManager.moveTaskToFront(getTaskId(), 0);
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
            intent.putExtra(EXTRA_PROVISIONING_SKIP_ENCRYPTION, true);
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_SKIP_USER_CONSENT, true);
        }

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
            activity.finish();
        } else {
            Toast.makeText(activity, "Device provisioning is not enabled. Stopping.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createDir() {
        File dir = new File(this.getExternalFilesDir(TYPE).getAbsolutePath());
        if (!dir.exists() || !dir.isDirectory()) {
            boolean mkdirs = dir.mkdirs();
            Log.d(TAG, "createDir: photoDir created: " + mkdirs);
        }
    }

    private void shareFile() {
        Log.d(TAG, "shareFile: ");
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.profileownerdemo",
                "com.profileownerdemo.SetProfileOwner");
        intent.setComponent(componentName);
        File file = new File(this.getFilesDir() + "/text", "hello.txt");
//        File file = new File(mContext.getExternalFilesDir(null) + "/text", "hello.txt");
//        File file = new File(Environment.getExternalStorageDirectory() + "/text", "hello.txt");
        Log.d(TAG, "shareFile: file.exists(): " + file.exists());
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITIES, file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, data);
        startActivity(intent);
    }

    private void createFile() {
        File dir = new File(this.getFilesDir(), "text");
//        File dir = new File(mContext.getExternalFilesDir(null), "text");
        if (!dir.exists()) {
            boolean dirResult = dir.mkdirs();
            Log.d(TAG, "createFile: dirResult: " + dirResult);
        }
        File file = new File(dir, "hello.txt");
        if (!file.exists()) {
            try {
                boolean fileResult = file.createNewFile();
                Log.d(TAG, "createFile: fileResult: " + fileResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write("Hello Worldsss 9999".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void transferIntentToProfile(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        ResolveInfo profileResolveInfo = null;
        for (ResolveInfo resolveInfo : info) {
            if ("android".equals(resolveInfo.activityInfo.packageName)) {
                profileResolveInfo = resolveInfo;
            }
        }

        if (null != profileResolveInfo) {
            intent.setComponent(new ComponentName(profileResolveInfo.activityInfo.packageName, profileResolveInfo.activityInfo.name));
        }
    }
}
