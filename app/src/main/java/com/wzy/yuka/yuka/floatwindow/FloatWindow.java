package com.wzy.yuka.yuka.floatwindow;

import android.app.Application;
import android.content.res.Configuration;
import android.view.View;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class FloatWindow implements View.OnClickListener {
    protected String tag;
    WeakReference<Application> applicationWeakReference;
    protected View view;
    public int[] location;
    FloatWindowManager floatWindowManager;
    int index;

    FloatWindow(Application application, int index, String tag) throws FloatWindowManagerException {
        this.applicationWeakReference = new WeakReference<>(application);
        this.tag = tag;
        this.index = index;
        location = new int[4];
        floatWindowManager = FloatWindowManager.getInstance();
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

    public void showResults(String origin, String translation, double time) {
    }

    public void dismiss() {
        EasyFloat.dismissAppFloat(tag);
        floatWindowManager.remove_FloatWindow(index);
    }

    public void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    public void show() {
        EasyFloat.showAppFloat(tag);
    }

    public void reset() {

    }

    @Override
    public void onClick(View v) {
    }

    public void showInitGuide() {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
