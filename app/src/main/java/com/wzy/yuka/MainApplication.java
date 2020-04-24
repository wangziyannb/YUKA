package com.wzy.yuka;

import android.app.Application;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.tools.debug.CrashManager;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }
}
