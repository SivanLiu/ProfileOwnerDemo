package com.profile.ui;

import android.graphics.drawable.Drawable;

/**
 * Created by lyg on 2018/4/25.
 */
public class ItemBean {
    private String text;
    private Drawable drawable;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}