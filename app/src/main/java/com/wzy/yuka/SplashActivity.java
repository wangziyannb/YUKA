package com.wzy.yuka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.handler.GlobalHandler;

/**
 * Created by Ziyan on 2020/5/16.
 */
public class SplashActivity extends Activity implements GlobalHandler.HandleMsgListener {
    private Runnable runnable = () -> {
        Intent intent = new Intent(this, MainActivity.class);
        Message msg = Message.obtain();
        msg.what = 100;
        intent.putExtra("msg", msg);
        startActivity(intent);
        Log.d("TAG", ": 超时");
        finish();
    };

    private GlobalHandler globalHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        globalHandler.postDelayed(runnable, 2000);
        UserManager.login();
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
