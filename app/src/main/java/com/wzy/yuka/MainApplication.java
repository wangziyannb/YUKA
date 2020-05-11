package com.wzy.yuka;

import android.app.Application;
import android.util.Log;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.debug.CrashManager;
import com.wzy.yuka.tools.params.GetParams;

import java.util.HashMap;
import java.util.UUID;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        GetParams.init(this);
        UserManager.init(this);
        checkUUID();
    }

    private void checkUUID() {
        HashMap<String, String> hashMap = UserManager.get();
        if (!hashMap.containsKey("uuid")) {
            String uuid = UUID.randomUUID().toString();
            Log.d("Init", "初次安装,uuid:" + uuid);
            hashMap.put("uuid", uuid);
            UserManager.update(hashMap);
        } else {
            Log.d("Init", "已初始化uuid");
        }
    }
}
