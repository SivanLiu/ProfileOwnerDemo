package com.profileownerdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_DENIED;
import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;
import static android.content.Intent.ACTION_VIEW;
import static android.os.Build.VERSION.SDK_INT;

public class PermissionManager extends AppCompatActivity {
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

    private boolean installed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_manager);

        enableCamera = findViewById(R.id.enable_camera);
        disableCmaera = findViewById(R.id.disable_camera);
        install = findViewById(R.id.install);
        read_write = findViewById(R.id.read_write);
        mDpm = (DevicePolicyManager) this.getSystemService(DEVICE_POLICY_SERVICE);
        mAdminComponentName = BasicDeviceAdminReceiver.getComponentName(this);

        mDpm.clearUserRestriction(mAdminComponentName, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Bundle bundle = mDpm.getUserRestrictions(mAdminComponentName);
            Log.e("ggg", "bundle = " + bundle.get(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES));
        }

        mDpm.setSecureSetting(mAdminComponentName,
                Settings.Secure.INSTALL_NON_MARKET_APPS, "1");
//        mDpm.addUserRestriction(mAdminComponentName, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES);
        packageManager = getPackageManager();

        final File wxFile = new File(Environment.getExternalStorageDirectory(), "wx.apk");
        Log.e("ggg", "wxFile path = " + wxFile.getAbsolutePath());
        try {
            boolean create = false;
            if (wxFile.exists()) {
                wxFile.delete();
            }
            create = wxFile.createNewFile();
            Log.e("ggg", "  create = " + create);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = this.getAssets().open("wx.apk");
            Log.e("ggg", "assets = " + inputStream.available());
            outputStream = new FileOutputStream(wxFile);
            IoUtil.write(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.closeQuietly(outputStream);
            IoUtil.closeQuietly(inputStream);
        }

        Log.e("ggg", "wx apk = " + wxFile.getAbsolutePath() + " exist = " + wxFile.exists() + " size = " + wxFile.length());

        List<ApplicationInfo> pkgs = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : pkgs) {
//            Log.e("ggg", "pkg name  = " + applicationInfo.packageName);
            if (pkgName.equals(applicationInfo.packageName)) {
                installed = true;
            }
        }

//        ArrayList<String> arrayList = getStorageDirectories();
//        for (String file : arrayList) {
//            Log.e("ggg", "file sss = " + file);
//        }
//
//        File s = new File(arrayList.get(1));
//        Log.e("ggg", "sd file = " + s.getAbsolutePath() + s.exists() + s.canExecute() + s.canWrite() + s.canRead());

        if (wxFile.exists()) {
            install.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ggg", "installed = " + installed + " wxFile = " + wxFile.getAbsolutePath() + " exist = " + wxFile.exists());
                    if (!installed && wxFile.exists()) {
                        Intent wxIntent = new Intent(ACTION_VIEW);
                        wxIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        wxIntent.setDataAndType(Uri.fromFile(wxFile), "application/vnd.android.package-archive");
                        startActivity(wxIntent);
                    }
                }
            });
        }

        enableCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDpm.setPermissionGrantState(mAdminComponentName,
                            pkgName, permission
                            , PERMISSION_GRANT_STATE_GRANTED);
                    int state = mDpm.getPermissionGrantState(mAdminComponentName,
                            pkgName,
                            permission);
                    Log.e("ggg", "camera state  = " + state);
                }
            }
        });

        disableCmaera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    String[] permission = packageManager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS).requestedPermissions;
//                    for (String p : permission) {
//                        Log.e("ggg", "permission = " + p);
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }

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
                    Log.e("ggg", "camera state = " + state);
                }
            }
        });

        File file = Environment.getExternalStorageDirectory();
        Log.e("ggg", "standard file " + file.getAbsolutePath() + " permission = " + file.canRead() + " " + file.canWrite() + " " + file.canExecute());
        File test = new File(file, "xxxx");
        try {
            if (test.exists()) {
                test.delete();
            }
            Log.e("ggg 1", "test = " + test.getAbsolutePath() + " exist = " + test.exists());
            test.createNewFile();
            Log.e("ggg 2", "test = " + test.getAbsolutePath() + " exist = " + test.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }

        read_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!checkStoragePermission()) {
//                    Log.e("ggg", "request");
//                    requestStoragePermission();
//                }

                int write_state = mDpm.getPermissionGrantState(mAdminComponentName,
                        getPackageName(),
                        permission_write);
                int read_state = mDpm.getPermissionGrantState(mAdminComponentName,
                        getPackageName(),
                        permission_read);
                File file = Environment.getExternalStorageDirectory();
                Log.e("ggg", "standard file " + file.getAbsolutePath() + " permission = " + file.canRead() + " " + file.canWrite() + " " + file.canExecute());
                File test = new File(file, "xxxx");
                try {
                    if (test.exists()) {
                        test.delete();
                    }
                    Log.e("ggg 1", "test = " + test.getAbsolutePath() + " exist = " + test.exists());
                    test.createNewFile();
                    Log.e("ggg 2", "test = " + test.getAbsolutePath() + " exist = " + test.exists());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "write state = " + write_state + " read_state =" + read_state, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public synchronized ArrayList<String> getStorageDirectories() {
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        Log.e("ggg", "rawExternalStorage = " + rawExternalStorage);
        Log.e("ggg", "rawSecondaryStoragesStr = " + rawSecondaryStoragesStr);
        Log.e("ggg", "rawEmulatedStorageTarget = " + rawEmulatedStorageTarget);
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = (Pattern.compile("/")).split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }

            Log.e("ggg", "path = " + rawEmulatedStorageTarget + File.separator + rawUserId);
        }


        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }

        Log.e("ggg", "check = " + checkStoragePermission());
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }
        if (SDK_INT >= Build.VERSION_CODES.M && checkStoragePermission())
            rv.clear();
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String strings[] = getExtSdCardPathsForActivity(this);
            for (String s : strings) {
                File f = new File(s);
                if (!rv.contains(s) && canListFiles(f)) {
                    Log.e("ggg", "write = " + f.canWrite() + " execute = " + f.canExecute());
                    rv.add(s);
                    rv.add("/storage/sdcard0");
                }
            }
        }
        return rv;
    }

    public boolean checkStoragePermission() {
        // Verify that all required contact permissions have been granted.
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 77);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 77);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String[] getExtSdCardPathsForActivity(Context context) {
        List<String> paths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("ggg", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) paths.add("/storage/sdcard1");
        return paths.toArray(new String[0]);
    }

    public static boolean canListFiles(File f) {
        return f.canRead() && f.isDirectory();
    }
}
