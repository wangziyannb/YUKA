package com.wzy.yuka.tools.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.tools.screenshot.ScreenShotService;

/**
 * Created by Ziyan on 2020/4/30.
 */
class FloatBall {
    private String tag;
    private Intent service;

    FloatBall(Activity activity, String tag) {
        this.tag = tag;
        service = new Intent(activity, ScreenShotService.class);
        EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.test, v -> {
                    ImageButton imageButton = v.findViewById(R.id.test1);
                    imageButton.getBackground().setAlpha(0);
                    v.findViewById(R.id.test1).setOnClickListener(v1 -> {
                        FloatBallLayout floatWindows = v.findViewById(R.id.test);
                        if (SizeUtil.px2dp(v.getContext(), v.getWidth()) > 45) {
                            do {
                                floatWindows.removeViewAt(floatWindows.getChildCount() - 1);
                            } while (floatWindows.getChildCount() != 1);
                            View view = EasyFloat.getAppFloatView("mainFloatBall");
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                            try {
                                int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
                                layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
                            } catch (Exception e) {
                                //do nothing. Probably using other version of android
                            }
                            layoutParams.y = layoutParams.y + SizeUtil.dp2px(v.getContext(), 52);
                            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                            windowManager.updateViewLayout(view, layoutParams);
                        } else {
                            ImageButton[] imageButtons = new ImageButton[4];
                            for (int i = 0; i < imageButtons.length; i++) {
                                imageButtons[i] = new ImageButton(activity);
                                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(activity, 45),
                                        SizeUtil.dp2px(activity, 44));
                                imageButtons[i].setLayoutParams(lp);
                                switch (i) {
                                    case 0:
                                        imageButtons[i].setId(R.id.settings_button);
                                        imageButtons[i].setBackgroundResource(R.drawable.settings);
                                        break;
                                    case 1:
                                        imageButtons[i].setId(R.id.detect_button);
                                        imageButtons[i].setBackgroundResource(R.drawable.detect);
                                        imageButtons[i].setOnClickListener(v2 -> {
                                            if (FloatWindowManager.getNumOfFloatWindows() > 0) {
                                                FloatWindowManager.hideAllFloatWindow();
                                                FloatWindowManager.startSS(activity);
                                            } else {
                                                Toast.makeText(activity, "还没有悬浮窗初始化呢，请从控制中启用悬浮窗", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case 2:
                                        imageButtons[i].setId(R.id.reset_button);
                                        imageButtons[i].setBackgroundResource(R.drawable.reset);
                                        imageButtons[i].setOnClickListener(v2 -> {
                                            activity.stopService(service);
                                            FloatWindowManager.reset(activity);
//                                            v.findViewById(R.id.reset_button).performClick();
                                        });
                                        break;
                                    case 3:
                                        imageButtons[i].setId(R.id.exit_button);
                                        imageButtons[i].setBackgroundResource(R.drawable.exit);
                                        imageButtons[i].setOnClickListener(v2 -> {
                                            activity.stopService(service);
                                            activity.finishAffinity();
                                            System.exit(0);
                                        });
                                        break;
                                }
                                floatWindows.addView(imageButtons[i]);
                            }
                            View view = EasyFloat.getAppFloatView("mainFloatBall");
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                            try {
                                int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
                                layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
                            } catch (Exception e) {
                                //do nothing. Probably using other version of android
                            }
                            layoutParams.y = layoutParams.y - SizeUtil.dp2px(v.getContext(), 52);
                            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                            windowManager.updateViewLayout(view, layoutParams);
                        }
                    });
                })
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .setLocation(100, 100).show();
    }

    void dismiss() {
        EasyFloat.dismissAppFloat(tag);
    }

    void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    void show() {
        EasyFloat.showAppFloat(tag);
    }
}
