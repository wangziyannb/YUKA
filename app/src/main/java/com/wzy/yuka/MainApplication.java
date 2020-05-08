package com.wzy.yuka;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.tools.debug.CrashManager;

import java.util.UUID;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        checkUUID();
    }

    private void checkUUID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("uuid", "").equals("")) {
            String uuid = UUID.randomUUID().toString();
            Log.d("Init", "初次安装,uuid:" + uuid);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("uuid", uuid);
            editor.commit();
        } else {
            Log.d("Init", "已初始化uuid");
        }
    }
}
