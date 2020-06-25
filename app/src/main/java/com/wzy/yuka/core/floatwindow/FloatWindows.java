package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.view.View;

import com.lzf.easyfloat.EasyFloat;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class FloatWindows implements View.OnClickListener {
    protected String tag;
    protected WeakReference<Activity> activityWeakReference;
    protected View view;
    int[] location;
    int index;

    FloatWindows(Activity activity, String tag, int index) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.tag = tag;
        this.index = index;
        location = new int[4];
    }

    void setView(View view) {
        this.view = view;
    }

    void setLocation() {
        view.getLocationOnScreen(location);
        location[2] = location[0] + view.getRight();
        location[3] = location[1] + view.getBottom();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    void showResults(String origin, String translation, double time) {
    }

    void dismiss() {
        FloatWindowManager.dismissFloatWindow(index);
        EasyFloat.dismissAppFloat(tag);
    }

    void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    void show() {
        EasyFloat.showAppFloat(tag);
    }

    @Override
    public void onClick(View v) {

    }

}
