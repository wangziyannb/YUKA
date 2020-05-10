package com.wzy.yuka;

import android.app.Application;
import android.util.Log;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.core.user.Account;
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
        checkUUID();
        GetParams.init(this);
        UserManager.init(this);
    }

    private void checkUUID() {
        Account account = new Account(this);
        HashMap<String, String> hashMap = account.get();
        if (!hashMap.containsKey("uuid")) {
            String uuid = UUID.randomUUID().toString();
            Log.d("Init", "初次安装,uuid:" + uuid);
            hashMap.put("uuid", uuid);
            account.update(hashMap);
        } else {
            Log.d("Init", "已初始化uuid");
        }
    }
}
