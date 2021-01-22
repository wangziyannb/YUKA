package com.wzy.yukafloatwindows.floatball;

import android.app.Application;
import android.content.res.Configuration;
import android.view.View;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yukafloatwindows.FloatWindowManager;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/7/3.
 */
public class FloatBall implements View.OnClickListener, View.OnLongClickListener {
    protected String tag;
    protected WeakReference<Application> applicationWeakReference;
    protected View mFloatBallView;
    protected FloatWindowManager floatWindowManager;
    protected int index;

    public FloatBall(int index, String tag, FloatWindowManager manager) {
        this.applicationWeakReference = manager.getApplicationWeakReference();
        this.tag = tag;
        this.index = index;
        this.floatWindowManager = manager;
    }

    public void setView(View view) {
        this.mFloatBallView = view;
    }

    public void show() {
        EasyFloat.showAppFloat(tag);
    }

    public void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    public void dismiss() {
        EasyFloat.dismissAppFloat(tag);
    }

    public String getTag() {
        return tag;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void onClick(View v) {
    }

    protected void foldFloatBall() {
    }

    protected void expandFloatBall() {
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
