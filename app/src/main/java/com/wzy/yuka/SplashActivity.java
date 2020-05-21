package com.wzy.yuka;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.message.GlobalHandler;

/**
 * Created by Ziyan on 2020/5/16.
 */
public class SplashActivity extends Activity implements GlobalHandler.HandleMsgListener {

    private static final int GO_HOME = 1;
    private static final int GO_GUIDE = 2;
    private static final int ENTER_DURATION = 2000;
    private GlobalHandler globalHandler;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == GO_GUIDE) {
                startGuideActivity();
            }
        }
    };

    //    private void startHomeActivity() {
//        //HomeFragment错了
//
//        Intent intent = new Intent(SplashActivity.this, HomeFragment.class);
//        startActivity(intent);
//        finish();
//    }
    private Runnable runnable = () -> {
        Intent intent = new Intent(this, MainActivity.class);
        Message msg = Message.obtain();
        msg.what = 100;
        intent.putExtra("msg", msg);
        startActivity(intent);
        Log.d("TAG", ": 超时");
        finish();
    };

    private void startGuideActivity() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);

        boolean isFirstOpen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_open", true);
        if (isFirstOpen) {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, ENTER_DURATION);
        } else {
            globalHandler = GlobalHandler.getInstance();
            globalHandler.setHandleMsgListener(this);
            globalHandler.postDelayed(runnable, 2000);
            UserManager.login();
        }
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                globalHandler.removeCallbacks(runnable);
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                skip(200);
                break;
            case 601:
                globalHandler.removeCallbacks(runnable);
                Toast.makeText(this, "账户不存在", Toast.LENGTH_SHORT).show();
                skip(601);
                break;
            case 400:
                globalHandler.removeCallbacks(runnable);
                Toast.makeText(this, "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                skip(400);
                break;
        }
    }

    private void skip(int what) {
        Intent intent = new Intent(this, MainActivity.class);
        Message msg = Message.obtain();
        msg.what = what;
        intent.putExtra("msg", msg);
        startActivity(intent);
        finish();
    }
}
