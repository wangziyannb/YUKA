package com.wzy.yuka.ui.setting;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.wzy.yuka.R;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;

/**
 * Created by Ziyan on 2020/5/5.
 */
public class SettingsActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floatwindow_settings);
        int[] size = SizeUtil.Screen(this);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        attributes.dimAmount = 0.6f; //设置窗口之外部分透明程度
        attributes.x = 0;
        attributes.y = 0;
        attributes.width = (int) (size[0] * 0.8);
        attributes.height = (int) (size[1] * 0.6);
        getWindow().setAttributes(attributes);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //只有app内能用
        super.onConfigurationChanged(newConfig);
        this.finish();
    }
}
