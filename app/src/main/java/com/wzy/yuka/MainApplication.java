package com.wzy.yuka;

import android.app.Application;
import android.view.accessibility.AccessibilityManager;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.tools.debug.CrashManager;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yukalite.YukaLite;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        GetParams.init(this);
        YukaFloatWindowManager.init(this);
        YukaLite.init(this);
        SharedPreferencesUtil.init(this);
        check();
    }

    private void check() {
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance();
        try {
            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
            assert manager != null;
            util.saveParam(SharedPreferenceCollection.application_touchExplorationEnabled, manager.isTouchExplorationEnabled());
        } catch (NullPointerException ignored) {
            util.saveParam(SharedPreferenceCollection.application_touchExplorationEnabled, false);
        }
    }
}
