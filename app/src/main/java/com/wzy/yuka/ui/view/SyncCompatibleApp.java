package com.wzy.yuka.ui.view;

import android.graphics.drawable.Drawable;

/**
 * Created by Ziyan on 2020/7/10.
 */
public class SyncCompatibleApp {
    private Drawable icon;
    private String name;

    public SyncCompatibleApp(Drawable icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    Drawable getIcon() {
        return icon;
    }

    String getName() {
        return name;
    }
}
