package com.profileownerdemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.profileownerdemo.Util.ACROSS_INTENT_ACTION;
import static com.profileownerdemo.Util.PASS_DATA;
import static com.profileownerdemo.Util.PASS_DATA_KEY;
import static com.profileownerdemo.Util.PASS_INTENT_ACTION;

/**
 * Created by lyg on 2018/6/7.
 */
public class OnePiexlActivity extends Activity {
    private static final String TAG = "OnePiexlActivity";

    public static AtomicBoolean showHome = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        final WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        Intent intent = getIntent();
        Log.e(TAG, "receive intent " + (intent == null ? null : intent.getAction()));
        if (intent != null && ACROSS_INTENT_ACTION.equals(intent.getAction())) {
            String action = intent.getAction();
            Bundle receiveBundle = intent.getBundleExtra(PASS_DATA);
            String result = receiveBundle.getString(PASS_DATA_KEY);
            Log.e(TAG, "UserHandler = " + Process.myUserHandle().toString() + " action = " + action + " result = " + result);

            Intent sendIntent = new Intent(this, MonitorService.class);
            sendIntent.setAction(PASS_INTENT_ACTION);
            sendIntent.putExtra(PASS_DATA, receiveBundle);
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Log.e(TAG, "sendBroadcast intent to .... " + handleRemoteFile(imageUri));
            sendBroadcast(sendIntent);
            this.startService(sendIntent);
        }

//        if (showHome.get()) {
//            openLauncherUi();
//        } else {
//            Intent intents = new Intent(this, SetProfileOwner.class);
//            intents.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intents);
//        }

        finish();
    }

    private String handleRemoteFile(final Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        String filename = cursor.getString(nameIndex);
        long filesize = cursor.getLong(sizeIndex);
        File downloadedCloudFile = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            downloadedCloudFile = new File(this.getFilesDir(), filename);
            if (downloadedCloudFile.exists()) {
                downloadedCloudFile.delete();
            }
            FileOutputStream out = new FileOutputStream(downloadedCloudFile);
            IoUtil.write(is, out);


            Log.e(TAG, Process.myUserHandle() + " nameIndex = " + nameIndex + " sizeIndex = " + sizeIndex + " fileName = " + filename +
                    "  " + downloadedCloudFile.getAbsolutePath() + "  fileSize = " + filesize + "  content = " + new String(IoUtil.readFile(downloadedCloudFile)) + "  uri path = " + uri.getPath() + "  type : " + getContentResolver().getType(uri));
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadedCloudFile.getAbsolutePath();
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onePixelActivity destroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onePixelActivity onResume");
//        showHome();
    }

    public static String getTopPacakge(Context mContext) {
        try {
            ActivityManager am = (ActivityManager) mContext
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean openLauncherUi() {
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
}
