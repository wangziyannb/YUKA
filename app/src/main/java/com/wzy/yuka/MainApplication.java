package com.wzy.yuka;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.debug.CrashManager;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;

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
        check();
    }

    private void check() {
        HashMap<String, String> hashMap = UserManager.get();
        if (!hashMap.containsKey("uuid")) {
            //String uuid = UUID.randomUUID().toString();
            String uuid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("Init", "初次安装,uuid:" + uuid);
            hashMap.put("uuid", uuid);
        } else {
            Log.d("Init", "已初始化uuid");
        }
        if (!hashMap.containsKey("isLogin")) {
            Log.d("Init", "初次安装,无登录状态");
            hashMap.put("isLogin", "false");
        } else {
            Log.d("Init", "有登陆状态");
        }
        UserManager.update(hashMap);
    }
}
