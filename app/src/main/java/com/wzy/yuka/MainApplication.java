package com.wzy.yuka;

import android.app.Application;
import android.view.accessibility.AccessibilityManager;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.tools.debug.CrashManager;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yukalite.YukaLite;

import java.util.Objects;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        YukaFloatWindowManager.init(this);
        SharedPreferencesUtil.init(this);
        if ((Boolean) SharedPreferencesUtil.getInstance().getParam(SharedPreferenceCollection.Agreement, false)) {
            //id registration
            DeviceIdentifier.register(this);
            String id = DeviceIdentifier.getAndroidID(this);
            if (Objects.equals(id, "0000000000000000") || Objects.equals(id, "")) {
                id = DeviceIdentifier.getGUID(this);
                if (DeviceID.supportedOAID(this) && !Objects.equals(DeviceIdentifier.getOAID(this), "")) {
                    id = DeviceIdentifier.getOAID(this);
                }
            }
            YukaLite.init(this, id);
        }
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
        this.getExternalFilesDir("logs");
    }

}
