package com.profile.ui;

import com.profileownerdemo.R;
import com.profileownerdemo.Util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class AppShowActivity extends AppCompatActivity {
    private static final String TAG = "AppShowActivity";
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapter adapter;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.activity_main2);

        adapter = new MyAdapter(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        //设置LayoutManager为LinearLayoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //设置 adapter
        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

        List<String> installedPkgs = Util.getInstalledApps(this);
        if (installedPkgs.isEmpty()) {
            return;
        }

        for (String pkg : installedPkgs) {
            Log.e(TAG, "pkg name = " + pkg);
            AppBean bean = new AppBean();
            try {
                bean.setDrawable(this.getPackageManager().getApplicationIcon(pkg));
                ApplicationInfo info = this.getPackageManager().getApplicationInfo(pkg, 0);
                bean.setText(this.getPackageManager().getApplicationLabel(info).toString());
                adapter.addItem(bean);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public static Context getContext() {
        return context;
    }
}
