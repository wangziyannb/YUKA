package com.wzy.yuka;

import android.app.Application;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.tools.debug.CrashManager;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.user.UserManager;

import java.util.HashMap;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        GetParams.init(this);
        UserManager.init(this);
        SharedPreferencesUtil.init(this);
        FloatWindowManager.init(this);
        check();
    }

    private void check() {
        HashMap<String, String> hashMap = UserManager.get();
        if (!hashMap.containsKey("uuid")) {
            String uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            hashMap.put("uuid", uuid);
        }
        if (!hashMap.containsKey("isLogin")) {
            hashMap.put("isLogin", "false");
        }
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance();
        try {
            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
            assert manager != null;
            util.saveParam(SharedPreferenceCollection.application_touchExplorationEnabled, manager.isTouchExplorationEnabled());
        } catch (NullPointerException ignored) {
            util.saveParam(SharedPreferenceCollection.application_touchExplorationEnabled, false);
        }

        UserManager.update(hashMap);
    }
}
