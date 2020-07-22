package com.wzy.yuka.yuka.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

/**
 * Created by Ziyan on 2020/7/17.
 */
public class ScreenStatusService extends Service {
    private ConfigurationListener mConfigurationListener;
    private MyBinder binder = new MyBinder();
    private View mCheckFullScreenView = null;

    @Override
    public void onCreate() {
        Context context = getApplicationContext();
        mCheckFullScreenView = new View(context);
        mCheckFullScreenView.setBackgroundColor(Color.parseColor("#00FFFFFF"));

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //创建非模态、不可碰触
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //放在左上角
        params.gravity = Gravity.START | Gravity.TOP;
        params.height = 0;
        params.width = 0;
        //设置弹出View类型
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        windowManager.addView(mCheckFullScreenView, params);
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mConfigurationListener != null) {
            mConfigurationListener.onConfigurationChanged(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    public void setConfigurationListener(ConfigurationListener listener) {
        mConfigurationListener = listener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface ConfigurationListener {
        void onConfigurationChanged(Configuration newConfig);
    }

    public class MyBinder extends Binder {
        public ScreenStatusService getService() {
            return ScreenStatusService.this;
        }

        public boolean isFullscreen() {
            int[] location = new int[2];
            mCheckFullScreenView.getLocationOnScreen(location);
            return location[0] == 0 && location[1] == 0;
        }
    }
}
