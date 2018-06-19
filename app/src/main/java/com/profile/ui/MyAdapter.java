package com.profile.ui;

import com.profileownerdemo.R;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyg on 2018/4/25.
 */
public class MyAdapter extends RecyclerView.Adapter {
    // 要在Item上显示的数据
    private List<AppBean> mDataSet = new ArrayList<>();
    private Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
    }

    // 用于获取ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.app_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    // 将数据与ViewHolder绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        AppBean itemBean = mDataSet.get(position);
        mHolder.tv_pkg_name.setText(itemBean.getText());
        mHolder.icon.setImageDrawable(itemBean.getDrawable());
    }

    // 获取Item的数量
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // 以下五个方法是我自定义的，用来对数据进行一系列操作
    public void refreshItems(List<AppBean> items) {
        mDataSet.clear();
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }

    public void addItems(List<AppBean> items) {
        mDataSet.addAll(items);
    }

    public void addItem(AppBean item) {
        mDataSet.add(item);
    }

    public void deleteItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, mDataSet.size() - 1);
    }

    // ViewHolder用于获取Item上的控件
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tv_pkg_name;
        Button bt_open;

        MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_icon);
            tv_pkg_name = itemView.findViewById(R.id.tv_pkg_name);
            bt_open = itemView.findViewById(R.id.bt_open);

            bt_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppBean bean = mDataSet.get(getPosition());
                    Intent intent = AppShowActivity.getContext().getPackageManager().getLaunchIntentForPackage(bean.getText());
                    AppShowActivity.getContext().startActivity(intent);
                }
            });
        }
    }
}

