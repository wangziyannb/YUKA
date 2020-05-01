package com.wzy.yuka.tools.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.wzy.yuka.MainActivity;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.tools.screenshot.ScreenShotService;

/**
 * Created by Ziyan on 2020/4/30.
 */
class FloatBall implements View.OnClickListener {
    private String tag;
    private Intent service;

    FloatBall(Activity activity, String tag) {
        this.tag = tag;
        service = new Intent(activity, ScreenShotService.class);
        boolean[] params1 = GetParams.getParamsForFloatBall(activity);
        if (!params1[2]) {
            EasyFloat.with(activity)
                    .setTag(tag)
                    .setLayout(R.layout.test, v -> {
                        ImageButton imageButton = v.findViewById(R.id.test1);
                        imageButton.getBackground().setAlpha(50);
                        v.findViewById(R.id.test1).setOnClickListener(v1 -> {
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) v.getLayoutParams();
                            try {
                                int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
                                layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
                            } catch (Exception e) {
                                //do nothing. Probably using other version of android
                            }
                            FloatBallLayout FloatBallLayout = v.findViewById(R.id.test);
                            View view = EasyFloat.getAppFloatView("mainFloatBall");

                            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                            Point size = new Point();
                            windowManager.getDefaultDisplay().getSize(size);
                            //判断左右
                            if (layoutParams.x > size.x / 2) {
                                //右边
                                if (SizeUtil.px2dp(v.getContext(), v.getWidth()) > 45) {
                                    //展开则关闭
                                    EasyFloat.appFloatDragEnable(true, "mainFloatBall");
                                    do {
                                        FloatBallLayout.removeViewAt(FloatBallLayout.getChildCount() - 1);
                                    } while (FloatBallLayout.getChildCount() != 1);
                                    imageButton.setBackgroundResource(R.drawable.main);
                                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(v.getContext(), 52);
                                    layoutParams.x = layoutParams.x + SizeUtil.dp2px(v.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                                    windowManager.updateViewLayout(v, layoutParams);
                                } else {
                                    EasyFloat.appFloatDragEnable(false, "mainFloatBall");
                                    FloatBallLayout.setIsLeft(false);
                                    layoutParams.x = layoutParams.x - SizeUtil.dp2px(v.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                                    layoutParams.y = layoutParams.y - SizeUtil.dp2px(v.getContext(), 52);
                                    windowManager.updateViewLayout(view, layoutParams);
                                    imageButton.setVisibility(View.INVISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        imageButton.setVisibility(View.VISIBLE);
                                        imageButton.setBackgroundResource(R.drawable.close);
                                        ImageButton[] imageButtons = new ImageButton[4];
                                        for (int i = 0; i < imageButtons.length; i++) {
                                            imageButtons[i] = new ImageButton(activity);
                                            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(activity, 44),
                                                    SizeUtil.dp2px(activity, 44));
                                            imageButtons[i].setLayoutParams(lp);
                                            switch (i) {
                                                case 0:
                                                    imageButtons[i].setId(R.id.settings_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.settings);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        Intent intent = new Intent(activity, MainActivity.class);
                                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                        intent.setAction(Intent.ACTION_MAIN);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                                        activity.startActivity(intent);
                                                        MainActivity.navController.navigate(R.id.nav_settings);
                                                    });
                                                    break;
                                                case 1:
                                                    imageButtons[i].setId(R.id.detect_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.detect);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        if (FloatWindowManager.getNumOfFloatWindows() > 0) {
                                                            FloatWindowManager.hideAllFloatWindow();
                                                            FloatWindowManager.startScreenShot(activity);
                                                        } else {
                                                            Toast.makeText(activity, "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    break;
                                                case 2:
                                                    imageButtons[i].setId(R.id.reset_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.reset);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        activity.stopService(service);
                                                        FloatWindowManager.reset(activity);
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
                                            FloatBallLayout.addView(imageButtons[i]);
                                        }
                                    }, 30);
                                }
                            } else {
                                //左边
                                if (SizeUtil.px2dp(v.getContext(), v.getWidth()) > 45) {
                                    //展开后关闭
                                    EasyFloat.appFloatDragEnable(true, "mainFloatBall");
                                    do {
                                        FloatBallLayout.removeViewAt(FloatBallLayout.getChildCount() - 1);
                                    } while (FloatBallLayout.getChildCount() != 1);
                                    imageButton.setBackgroundResource(R.drawable.main);
                                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(v.getContext(), 52);
                                    windowManager.updateViewLayout(v, layoutParams);
                                } else {
                                    EasyFloat.appFloatDragEnable(false, "mainFloatBall");
                                    FloatBallLayout.setIsLeft(true);
                                    layoutParams.y = layoutParams.y - SizeUtil.dp2px(v.getContext(), 52);
                                    windowManager.updateViewLayout(view, layoutParams);
                                    imageButton.setVisibility(View.INVISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        imageButton.setVisibility(View.VISIBLE);
                                        imageButton.setBackgroundResource(R.drawable.close);
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
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        Intent intent = new Intent(activity, MainActivity.class);
                                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                        intent.setAction(Intent.ACTION_MAIN);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                                        activity.startActivity(intent);
                                                        MainActivity.navController.navigate(R.id.nav_settings);
                                                    });
                                                    break;
                                                case 1:
                                                    imageButtons[i].setId(R.id.detect_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.detect);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        if (FloatWindowManager.getNumOfFloatWindows() > 0) {
                                                            FloatWindowManager.hideAllFloatWindow();
                                                            FloatWindowManager.startScreenShot(activity);
                                                            boolean[] params = GetParams.getParamsForFloatBall(activity);
                                                            if (params[0]) {
                                                                //todo
                                                                imageButton.performClick();
                                                            }
                                                        } else {
                                                            Toast.makeText(activity, "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    break;
                                                case 2:
                                                    imageButtons[i].setId(R.id.reset_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.reset);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        activity.stopService(service);
                                                        FloatWindowManager.reset(activity);
                                                        boolean[] params = GetParams.getParamsForFloatBall(activity);
                                                        if (params[0]) {
                                                            //todo
                                                            imageButton.performClick();
                                                        }
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
                                            FloatBallLayout.addView(imageButtons[i]);
                                        }
                                    }, 30);
                                }
                            }
                        });
                    })
                    .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .setDragEnable(true)
                    .setDisplayHeight(context -> {
                        boolean[] params = GetParams.getParamsForFloatBall(activity);
                        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        if (params[1]) {

                            if (size.x < size.y) {
                                //竖屏
                                return size.y - SizeUtil.dp2px(context, 52 + 11);
                            } else {
                                return size.y - SizeUtil.dp2px(context, 52 + 11);
                            }
                        } else {
                            return size.y;
                        }

                    })
                    .setLocation(100, 100).show();
        } else {
            EasyFloat.with(activity)
                    .setTag(tag)
                    .setLayout(R.layout.test, v -> {
                        ImageButton imageButton = v.findViewById(R.id.test1);
                        imageButton.getBackground().setAlpha(50);
                        v.findViewById(R.id.test1).setOnClickListener(v1 -> {
                            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) v.getLayoutParams();
                            try {
                                int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
                                layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
                            } catch (Exception e) {
                                //do nothing. Probably using other version of android
                            }
                            FloatBallLayout FloatBallLayout = v.findViewById(R.id.test);
                            View view = EasyFloat.getAppFloatView("mainFloatBall");

                            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                            Point size = new Point();
                            windowManager.getDefaultDisplay().getSize(size);
                            //判断左右
                            if (layoutParams.x > size.x / 2) {
                                //右边
                                if (SizeUtil.px2dp(v.getContext(), v.getWidth()) > 45) {
                                    //展开则关闭
                                    EasyFloat.appFloatDragEnable(true, "mainFloatBall");
                                    do {
                                        FloatBallLayout.removeViewAt(FloatBallLayout.getChildCount() - 1);
                                    } while (FloatBallLayout.getChildCount() != 1);
                                    imageButton.setBackgroundResource(R.drawable.main);
                                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(v.getContext(), 52);
                                    layoutParams.x = layoutParams.x + SizeUtil.dp2px(v.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                                    windowManager.updateViewLayout(v, layoutParams);
                                } else {
                                    EasyFloat.appFloatDragEnable(false, "mainFloatBall");
                                    FloatBallLayout.setIsLeft(false);
                                    layoutParams.x = layoutParams.x - SizeUtil.dp2px(v.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                                    layoutParams.y = layoutParams.y - SizeUtil.dp2px(v.getContext(), 52);
                                    windowManager.updateViewLayout(view, layoutParams);
                                    imageButton.setVisibility(View.INVISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        imageButton.setVisibility(View.VISIBLE);
                                        imageButton.setBackgroundResource(R.drawable.close);
                                        ImageButton[] imageButtons = new ImageButton[4];
                                        for (int i = 0; i < imageButtons.length; i++) {
                                            imageButtons[i] = new ImageButton(activity);
                                            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(activity, 44),
                                                    SizeUtil.dp2px(activity, 44));
                                            imageButtons[i].setLayoutParams(lp);
                                            switch (i) {
                                                case 0:
                                                    imageButtons[i].setId(R.id.settings_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.settings);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        Intent intent = new Intent(activity, MainActivity.class);
                                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                        intent.setAction(Intent.ACTION_MAIN);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                                        activity.startActivity(intent);
                                                        MainActivity.navController.navigate(R.id.nav_settings);
                                                    });
                                                    break;
                                                case 1:
                                                    imageButtons[i].setId(R.id.detect_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.detect);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        if (FloatWindowManager.getNumOfFloatWindows() > 0) {
                                                            FloatWindowManager.hideAllFloatWindow();
                                                            FloatWindowManager.startScreenShot(activity);
                                                        } else {
                                                            Toast.makeText(activity, "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    break;
                                                case 2:
                                                    imageButtons[i].setId(R.id.reset_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.reset);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        activity.stopService(service);
                                                        FloatWindowManager.reset(activity);
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
                                            FloatBallLayout.addView(imageButtons[i]);
                                        }
                                    }, 30);
                                }
                            } else {
                                //左边
                                if (SizeUtil.px2dp(v.getContext(), v.getWidth()) > 45) {
                                    //展开后关闭
                                    EasyFloat.appFloatDragEnable(true, "mainFloatBall");
                                    do {
                                        FloatBallLayout.removeViewAt(FloatBallLayout.getChildCount() - 1);
                                    } while (FloatBallLayout.getChildCount() != 1);
                                    imageButton.setBackgroundResource(R.drawable.main);
                                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(v.getContext(), 52);
                                    windowManager.updateViewLayout(v, layoutParams);
                                } else {
                                    EasyFloat.appFloatDragEnable(false, "mainFloatBall");
                                    FloatBallLayout.setIsLeft(true);
                                    layoutParams.y = layoutParams.y - SizeUtil.dp2px(v.getContext(), 52);
                                    windowManager.updateViewLayout(view, layoutParams);
                                    imageButton.setVisibility(View.INVISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        imageButton.setVisibility(View.VISIBLE);
                                        imageButton.setBackgroundResource(R.drawable.close);
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
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        Intent intent = new Intent(activity, MainActivity.class);
                                                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                        intent.setAction(Intent.ACTION_MAIN);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                                        activity.startActivity(intent);
                                                        MainActivity.navController.navigate(R.id.nav_settings);
                                                    });
                                                    break;
                                                case 1:
                                                    imageButtons[i].setId(R.id.detect_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.detect);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        if (FloatWindowManager.getNumOfFloatWindows() > 0) {
                                                            FloatWindowManager.hideAllFloatWindow();
                                                            FloatWindowManager.startScreenShot(activity);
                                                            boolean[] params = GetParams.getParamsForFloatBall(activity);
                                                            if (params[0]) {
                                                                //todo
                                                                imageButton.performClick();
                                                            }
                                                        } else {
                                                            Toast.makeText(activity, "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    break;
                                                case 2:
                                                    imageButtons[i].setId(R.id.reset_button);
                                                    imageButtons[i].setBackgroundResource(R.drawable.reset);
                                                    imageButtons[i].setOnClickListener(v2 -> {
                                                        activity.stopService(service);
                                                        FloatWindowManager.reset(activity);
                                                        boolean[] params = GetParams.getParamsForFloatBall(activity);
                                                        if (params[0]) {
                                                            //todo
                                                            imageButton.performClick();
                                                        }
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
                                            FloatBallLayout.addView(imageButtons[i]);
                                        }
                                    }, 30);
                                }
                            }
                        });
                    })
                    .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .setDragEnable(true)
                    .setLocation(100, 100).show();
        }
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

    @Override
    public void onClick(View v) {

    }
}
