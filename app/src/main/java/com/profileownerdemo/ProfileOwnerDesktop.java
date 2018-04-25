package com.profileownerdemo;

import android.app.Application;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author sivan
 */
public class ProfileOwnerDesktop extends AppCompatActivity {

    private SetProfileOwner setProfileOwner = new SetProfileOwner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_owenr_desktop);
        try {
            Class<?> cls = IPackageManager.class;
            Method getApplicationInfo = cls.getDeclaredMethod("getApplicationInfo", String.class, int.class, int.class);
            getApplicationInfo.setAccessible(true);
            List<Application> applications = (List<Application>) getApplicationInfo.invoke(cls, "com.github.shadowsocks", 0, 0);

            PackageManager packageManager = (PackageManager)this.getSystemService(PackageManager.);
            for (Application application : applications) {
                Log.e("ggg", "appliaction = " + application.getPackageName());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // getApplicationInfo(String packageName, int flags, int userId)

    }
}
