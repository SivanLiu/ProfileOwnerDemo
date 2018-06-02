package com.profile.ui;

import com.profileownerdemo.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAUNCHER_APPS_SERVICE;

/**
 * Created by lyg on 2018/4/25.
 */
public class MyAdapter extends RecyclerView.Adapter {
    // 要在Item上显示的数据
    List<ItemBean> mDataSet = new ArrayList<>();
    Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
    }

    // 用于获取ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    // 将数据与ViewHolder绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        ItemBean itemBean = mDataSet.get(position);
        mHolder.textView.setText(itemBean.getText());
        mHolder.drawable.setImageDrawable(itemBean.getDrawable());
    }

    // 获取Item的数量
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // 以下五个方法是我自定义的，用来对数据进行一系列操作
    public void refreshItems(List<ItemBean> items) {
        mDataSet.clear();
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(List<ItemBean> items) {
        mDataSet.addAll(items);
    }

    public void addItem(ItemBean item) {
        mDataSet.add(item);
    }

    public void deleteItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, mDataSet.size() - 1);
    }

    // ViewHolder用于获取Item上的控件
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ImageView drawable;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_text);
            drawable = (ImageView) itemView.findViewById(R.id.drawable);

            //添加点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ItemBean bean = mDataSet.get(getPosition());
                    Log.e("ggg", " id = " + bean.getText());
                    Intent intent = Main2Activity.getContext().getPackageManager().getLaunchIntentForPackage(bean.getText());
                    Main2Activity.getContext().startActivity(intent);

                    UserManager devicePolicyManager = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
                    UserHandle mainUser = null;
                    for (UserHandle userHandle : devicePolicyManager.getUserProfiles()) {
                        if (userHandle.hashCode() == 0) {
                            mainUser = userHandle;
                            Log.e("ggg", "userHandle = "+userHandle.toString());
                            startLauncherActivity(mContext, "com.android.calendar", mainUser);
                            return;
                        }
                    }
                }
            });
        }
    }

    public static boolean startLauncherActivity(Context context, String packageName, UserHandle userHandle) {
        LauncherApps launcherApps = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            launcherApps = (LauncherApps) context.getSystemService(LAUNCHER_APPS_SERVICE);
        }

        if (launcherApps != null) {
            List<LauncherActivityInfo> list = launcherApps.getActivityList(packageName, userHandle);

            if (null != list && !list.isEmpty()) {
                launcherApps.startMainActivity((list.get(0)).getComponentName(), userHandle, null, null);
                return true;
            }
        }
        return false;
    }
}

