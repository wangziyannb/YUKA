package com.wzy.yuka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.Users;
import com.wzy.yukalite.YukaUserManagerException;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/5/16.
 */
public class SplashActivity extends Activity implements GlobalHandler.HandleMsgListener {

    private static final int GO_GUIDE = 2;
    private static final int ENTER_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        boolean isFirstOpen = (boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_GuideActivity, true);
        if (isFirstOpen) {
            SplashHandler splashHandler = new SplashHandler(this);
            splashHandler.sendEmptyMessageDelayed(GO_GUIDE, ENTER_DURATION);
        } else {
            GlobalHandler globalHandler = GlobalHandler.getInstance();
            globalHandler.setHandleMsgListener(this);
            globalHandler.postDelayed(runnable, ENTER_DURATION);
            try {
                Users.login();
            } catch (YukaUserManagerException e) {
                e.printStackTrace();
            }
        }
    }


    private void startGuideActivity() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private int what = 100;
    private Runnable runnable = () -> {
        Intent intent = new Intent(this, MainActivity.class);
        Message msg = Message.obtain();
        msg.what = what;
        intent.putExtra("msg", msg);
        startActivity(intent);
        if (what != 100) {
            Log.d("TAG", ": 登录完成");
        } else {
            Log.d("TAG", ": 超时");
        }
        finish();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static class SplashHandler extends Handler {
        WeakReference<Context> contextWeakReference;

        SplashHandler(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == GO_GUIDE) {
                SplashActivity activity = (SplashActivity) contextWeakReference.get();
                activity.startGuideActivity();
            }
        }
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                what = 200;
                break;
            case 601:
                what = 601;
                break;
        }
    }
}
