package com.profileownerdemo;

import com.profile.ui.AppShowActivity;

import android.app.Application;
import android.content.Intent;
import android.content.pm.IPackageManager;
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
        Intent intent = new Intent(this, AppShowActivity.class);
        startActivity(intent);
        try {
            Class<?> cls = IPackageManager.class;
            Method getApplicationInfo = cls.getDeclaredMethod("getApplicationInfo", String.class, int.class, int.class);
            getApplicationInfo.setAccessible(true);
            List<Application> applications = (List<Application>) getApplicationInfo.invoke(cls, "com.github.shadowsocks", 0, 0);

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
