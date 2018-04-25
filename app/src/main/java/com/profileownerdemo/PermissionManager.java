package com.profileownerdemo;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED;
import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;
import static android.content.Intent.ACTION_VIEW;

/**
 * @author sivan
 */
public class PermissionManager extends AppCompatActivity implements View.OnClickListener {
    private Button disableCmaera;
    private DevicePolicyManager mDpm;
    private ComponentName mAdminComponentName;
    private PackageManager packageManager;
    private static final String pkgName = "com.tencent.mm";
    private static final String permission = "android.permission.CAMERA";
    private static final String permission_write = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String permission_read = "android.permission.READ_EXTERNAL_STORAGE";
    private boolean disable = true;
    private Button enableCamera;
    private Button install;
    private Button read_write;
    private Button setVpn;
    private EditText inputVpn;

    private boolean installed = false;

    private File wxFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_manager);
        enableCamera = findViewById(R.id.enable_camera);
        disableCmaera = findViewById(R.id.disable_camera);
        install = findViewById(R.id.install);
        read_write = findViewById(R.id.read_write);
        setVpn = findViewById(R.id.set_vpn);
        inputVpn = findViewById(R.id.input_pkg_vpn);
        enableCamera.setOnClickListener(this);
        disableCmaera.setOnClickListener(this);
        install.setOnClickListener(this);
        read_write.setOnClickListener(this);
        setVpn.setOnClickListener(this);
        inputVpn.setOnClickListener(this);

        mDpm = (DevicePolicyManager) this.getSystemService(DEVICE_POLICY_SERVICE);
        mAdminComponentName = BasicDeviceAdminReceiver.getComponentName(this);

        /**
         * 允许安装应用
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDpm.clearUserRestriction(mAdminComponentName, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
            Bundle bundle = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                bundle = mDpm.getUserRestrictions(mAdminComponentName);
            }
            Log.d("sivan", "bundle = " + bundle.get(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES));

            mDpm.setSecureSetting(mAdminComponentName,
                    Settings.Secure.INSTALL_NON_MARKET_APPS, "1");
        }

        packageManager = getPackageManager();
        List<ApplicationInfo> pkgs = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : pkgs) {
            if (pkgName.equals(applicationInfo.packageName)) {
                installed = true;
            }
        }

        wxFile = copyAssetFileToSdcard("wx.apk", "wx.apk");
    }

    private File copyAssetFileToSdcard(String fileName, String destFileName) {
        File wxFile = new File(Environment.getExternalStorageDirectory(), destFileName);
        try {
            boolean create = false;
            if (wxFile.exists()) {
                wxFile.delete();
            }
            create = wxFile.createNewFile();
            Log.d("sivan", "create = " + create);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = this.getAssets().open(fileName);
            outputStream = new FileOutputStream(wxFile);
            IoUtil.write(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.closeQuietly(outputStream);
            IoUtil.closeQuietly(inputStream);
        }
        Log.d("sivan", "wx apk = " + wxFile.getAbsolutePath() + " exist = " + wxFile.exists() + " size = " + wxFile.length());
        return wxFile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disable_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDpm.setPermissionGrantState(mAdminComponentName,
                            pkgName, permission
                            , PERMISSION_GRANT_STATE_DENIED);

                    int state = mDpm.getPermissionGrantState(mAdminComponentName,
                            pkgName,
                            permission);

                    mDpm.setPermissionGrantState(mAdminComponentName,
                            getPackageName(), permission_read
                            , PERMISSION_GRANT_STATE_DENIED);

                    mDpm.setPermissionGrantState(mAdminComponentName,
                            getPackageName(), permission_write
                            , PERMISSION_GRANT_STATE_DENIED);
                    Log.d("sivan", "camera state = " + state);
                }
                break;
            case R.id.enable_camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDpm.setPermissionGrantState(mAdminComponentName,
                            pkgName, permission
                            , PERMISSION_GRANT_STATE_GRANTED);
                    int state = mDpm.getPermissionGrantState(mAdminComponentName,
                            pkgName,
                            permission);
                    Log.d("sivan", "camera state  = " + state);
                }
                break;
            case R.id.install:
                if (wxFile.exists()) {
                    Log.d("sivan", "installed = " + installed + " wxFile = " + wxFile.getAbsolutePath() + " exist = " + wxFile.exists());
                    if (!installed && wxFile.exists()) {
                        Intent wxIntent = new Intent(ACTION_VIEW);
                        wxIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        wxIntent.setDataAndType(Uri.fromFile(wxFile), "application/vnd.android.package-archive");
                        startActivity(wxIntent);
                    }
                }
                break;
            case R.id.read_write:
                int write_state = 0;
                int read_state = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    write_state = mDpm.getPermissionGrantState(mAdminComponentName,
                            getPackageName(),
                            permission_write);
                    read_state = mDpm.getPermissionGrantState(mAdminComponentName,
                            getPackageName(),
                            permission_read);
                }
                File file = Environment.getExternalStorageDirectory();
                Log.d("sivan", "standard file " + file.getAbsolutePath() + " permission = " + file.canRead() + " " + file.canWrite() + " " + file.canExecute());
                File test = new File(file, "xxxx");
                try {
                    if (test.exists()) {
                        test.delete();
                    }
                    Log.d("sivan 1", "test = " + test.getAbsolutePath() + " exist = " + test.exists());
                    test.createNewFile();
                    Log.d("sivan 2", "test = " + test.getAbsolutePath() + " exist = " + test.exists());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "write state = " + write_state + " read_state =" + read_state, Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_vpn:
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                this.startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String vpn = mDpm.getAlwaysOnVpnPackage(mAdminComponentName);
                    Log.d("sivan", "vpn packageName = " + vpn);
                    try {
                        mDpm.setAlwaysOnVpnPackage(mAdminComponentName, inputVpn.getText().toString(), true);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
