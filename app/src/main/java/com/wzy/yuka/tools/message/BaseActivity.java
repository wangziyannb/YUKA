package com.wzy.yuka.tools.message;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Ziyan on 2020/5/18.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }
}
