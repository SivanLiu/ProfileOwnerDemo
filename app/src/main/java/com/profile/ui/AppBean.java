package com.profile.ui;

import android.graphics.drawable.Drawable;

/**
 * Created by lyg on 2018/4/25.
 */
public class AppBean {
    //icon
    private Drawable icon;

    //包名
    private String packageName;

    public Drawable getDrawable() {
        return icon;
    }

    public void setDrawable(Drawable drawable) {
        this.icon = drawable;
    }

    public void setText(String text) {
        this.packageName = text;
    }

    public String getText() {
        return packageName;
    }
}