package com.profile.ui;

import com.profileownerdemo.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class Main2Activity extends AppCompatActivity {
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //设置LayoutManager为LinearLayoutManager
        layoutManager = new LinearLayoutManager(this);

        List<PackageInfo> packageInfoList = this.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            ItemBean itemBean = new ItemBean();
//            itemBean.setText(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            itemBean.setText(packageInfo.packageName);
            itemBean.setDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
            adapter.addItem(itemBean);
        }

        //设置LayoutManager和Adapter
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static Context getContext() {
        return context;
    }
}
