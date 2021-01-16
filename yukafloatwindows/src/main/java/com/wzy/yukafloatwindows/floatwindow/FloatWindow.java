package com.wzy.yukafloatwindows.floatwindow;

import android.app.Application;
import android.content.res.Configuration;
import android.view.View;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yukafloatwindows.FloatWindowManager;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class FloatWindow implements View.OnClickListener {
    public int[] location;
    protected String tag;
    protected WeakReference<Application> applicationWeakReference;
    protected View mFloatWindowView;
    protected FloatWindowManager floatWindowManager;
    protected int index;

    public FloatWindow(int index, String tag, FloatWindowManager manager) {
        this.applicationWeakReference = manager.getApplicationWeakReference();
        this.tag = tag;
        this.index = index;
        this.location = new int[4];
        this.floatWindowManager = manager;
    }

    protected void setView(View view) {
        this.mFloatWindowView = view;
    }

    protected void setLocation() {
        mFloatWindowView.getLocationOnScreen(location);
        location[2] = location[0] + mFloatWindowView.getRight();
        location[3] = location[1] + mFloatWindowView.getBottom();
    }

    public int getIndex() {
        return this.index;
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

    public void reset() {
    }

    public void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    public void show() {
        EasyFloat.showAppFloat(tag);
    }

    @Override
    public void onClick(View v) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
