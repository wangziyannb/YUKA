package com.wzy.yuka.yuka.floatball;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.CurtainActivity;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/7/3.
 */
public class FloatBall implements View.OnClickListener, View.OnLongClickListener {
    private String tag;
    private WeakReference<Application> mApplicationRef;
    private View FloatBallView;
    public boolean isInGuiding = false;
    private FloatWindowManager floatWindowManager;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private boolean isInChoosing = false;
    private WindowManager windowManager;
    private int index = 0;

    public FloatBall(Application application, String tag) throws FloatWindowManagerException {
        this.mApplicationRef = new WeakReference<>(application);
        this.tag = tag;
        floatWindowManager = FloatWindowManager.getInstance();
        windowManager = (WindowManager) mApplicationRef.get().getSystemService(Context.WINDOW_SERVICE);
        EasyFloat.Builder fb = EasyFloat.with(mApplicationRef.get().getApplicationContext())
                .setTag(tag)
                .setLayout(R.layout.float_ball, v -> {
                    FloatBallView = v;
                    ImageButton imageButton = v.findViewById(R.id.floatball_main);
                    imageButton.getBackground().setAlpha(255);
                    v.findViewById(R.id.floatball_main).setOnClickListener(this);
                })
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .registerCallbacks(new OnFloatCallbacks() {
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                        int[] size = GetParams.Screen();
                        View view = EasyFloat.getAppFloatView(tag);
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
                        imageButton.getBackground().setAlpha(50);
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                        if (layoutParams.x > size[0] / 2) {
                            //右边
                            layoutParams.x += 80;
                        } else {
                            layoutParams.x = -80;
                        }
                        windowManager.updateViewLayout(view, layoutParams);
                    };

                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
                            floatWindowManager.startScreenStatusService();
                            handler.postDelayed(() -> {
                                showInitGuide();
                            }, 500);

                        }
                    }

                    @Override
                    public void show(@NotNull View view) {

                    }

                    @Override
                    public void hide(@NotNull View view) {

                    }

                    @Override
                    public void dismiss() {

                    }

                    @Override
                    public void touchEvent(@NotNull View view, @NotNull MotionEvent motionEvent) {
                        handler.removeCallbacks(runnable);
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        windowManager.updateViewLayout(view, layoutParams);
                        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
                        imageButton.getBackground().setAlpha(255);
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {
                        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
                        imageButton.getBackground().setAlpha(255);
                    }

                    @Override
                    public void dragEnd(@NotNull View view) {
                        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_autoHide, true)
                                && !isInGuiding
                                && !(boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.application_touchExplorationEnabled, false)) {
                            handler.postDelayed(runnable, 3000);
                        } else {
                            handler.postDelayed(() -> {
                                ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
                                imageButton.getBackground().setAlpha(50);
                            }, 3000);
                        }
                    }
                });
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.application_touchExplorationEnabled, false)) {
            fb.setLocation(0, GetParams.Screen()[1] / 2);
        } else {
            fb.setLocation(100, 500);
        }
        if (!(boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.application_touchExplorationEnabled, false)) {
            if (!(boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_fluidMode, false)) {
                fb.setDisplayHeight(context -> {
                    int[] size = GetParams.Screen();
                    if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_safeMode, true)) {
                        if (size[0] < size[1]) {
                            //竖屏
                            return size[1] - SizeUtil.dp2px(context, 52 + 11);
                        } else {
                            return size[1] - SizeUtil.dp2px(context, 52 + 11);
                        }
                    } else {
                        return size[1];
                    }
                });
            }
        }
        fb.show();
    }

    @Nullable
    public View getView() {
        return FloatBallView;
    }

    public void show() {
        EasyFloat.showAppFloat(tag);
    }

    public void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    public void dismiss() {
        EasyFloat.dismissAppFloat(tag);
    }

    public String getTag() {
        return tag;
    }

    public void removeOnClickListeners() {
        FloatBallView.findViewById(R.id.floatball_main).setOnClickListener(null);
        try {
            FloatBallView.findViewById(R.id.floatball_top).setOnClickListener(null);
            FloatBallView.findViewById(R.id.floatball_mid1).setOnClickListener(null);
            FloatBallView.findViewById(R.id.floatball_mid2).setOnClickListener(null);
            FloatBallView.findViewById(R.id.floatball_bottom).setOnClickListener(null);
            FloatBallView.findViewById(R.id.floatball_mid2).setOnLongClickListener(null);
        } catch (NullPointerException ignored) {

        }
    }

    private void setOnClickListeners() {
        FloatBallView.findViewById(R.id.floatball_main).setOnClickListener(this);
        try {
            FloatBallView.findViewById(R.id.floatball_top).setOnClickListener(this);
            FloatBallView.findViewById(R.id.floatball_mid1).setOnClickListener(this);
            FloatBallView.findViewById(R.id.floatball_mid2).setOnClickListener(this);
            FloatBallView.findViewById(R.id.floatball_bottom).setOnClickListener(this);
            FloatBallView.findViewById(R.id.floatball_mid2).setOnLongClickListener(this);
        } catch (NullPointerException ignored) {
        }
    }

    public void setMainOnClickListeners() {
        FloatBallView.findViewById(R.id.floatball_main).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) FloatBallView.getLayoutParams();
        try {
            int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
            layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
        } catch (Exception e) {
            //do nothing. Probably using other version of android
        }
        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
        switch (v.getId()) {
            case R.id.floatball_main:
                FloatBallLayout floatBallLayout = FloatBallView.findViewById(R.id.floatball_layout);
                if (isInChoosing) {
                    isInChoosing = false;
                    if (floatBallLayout.isDeployed) {
                        //取消二级面板
                        flipAnimation(floatBallLayout.findViewById(R.id.floatball_top), 100, R.drawable.floatmenu_settings, "设置");
                        flipAnimation(floatBallLayout.findViewById(R.id.floatball_mid1), 100, R.drawable.floatmenu_detect, "识别");
                        flipAnimation(floatBallLayout.findViewById(R.id.floatball_mid2), 100, R.drawable.floatmenu_reset, "初始化悬浮窗");
                        flipAnimation(floatBallLayout.findViewById(R.id.floatball_bottom), 100, R.drawable.floatmenu_exit, "退出");
                    }
                } else {
                    if (floatBallLayout.isDeployed) {
                        foldFloatBall();
                    } else {
                        expandFloatBall();
                    }
                }
                break;
            case R.id.floatball_top:
                if (isInChoosing) {
                    try {
                        floatWindowManager.remove_AllFloatWindow();
                        floatWindowManager.add_FloatWindow("SWN_S");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(mApplicationRef.get(), com.wzy.yuka.ui.setting.SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mApplicationRef.get().startActivity(intent);
                    if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_autoClose, true)) {
                        imageButton.performClick();
                    }
                }
                break;
            case R.id.floatball_mid1:
                if (isInChoosing) {
                    try {
                        floatWindowManager.remove_AllFloatWindow();
                        floatWindowManager.add_FloatWindow("SWN_C");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
                    floatWindowManager.detect();
                    if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_autoClose, true)) {
                        imageButton.performClick();
                    }
                }
                break;
            case R.id.floatball_mid2:
                if (isInChoosing) {
                    try {
                        floatWindowManager.remove_AllFloatWindow();
                        floatWindowManager.add_FloatWindow("SWA");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
                    floatWindowManager.reset();
                    if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_autoClose, true)) {
                        imageButton.performClick();
                    }
                }
                break;
            case R.id.floatball_bottom:
                if (isInChoosing) {
                    try {
                        floatWindowManager.remove_AllFloatWindow();
                        floatWindowManager.add_FloatWindow("SBW");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
                    floatWindowManager.stop_RecordingTrans();
                    floatWindowManager.stop_ScreenShotTrans_normal(true);
                    floatWindowManager.stop_ScreenShotTrans_normal(false);
                    floatWindowManager.stop_ScreenShotTrans_auto();
                    floatWindowManager.stopScreenStatusService();
                    System.exit(0);
                }
                break;
        }
    }

    private void foldFloatBall() {
        FloatBallLayout floatBallLayout = FloatBallView.findViewById(R.id.floatball_layout);
        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) FloatBallView.getLayoutParams();
        int[] size = GetParams.Screen();
        if (isInGuiding) {
            removeOnClickListeners();
            floatBallLayout.fold();
        } else if (floatBallLayout.isDeployed) {
            //展开则关闭
            removeOnClickListeners();
            floatBallLayout.setFloatBallLayoutListener(new FloatBallLayout.FloatBallLayoutListener() {
                @Override
                public void deployed() {

                }

                @Override
                public void folded() {
                    floatBallLayout.removeFloatBallLayoutListener();
                    EasyFloat.appFloatDragEnable(true, tag);
                    imageButton.setBackgroundResource(R.drawable.main);
                    imageButton.setContentDescription("Yuka悬浮球");
                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(FloatBallView.getContext(), 52);
                    if (layoutParams.x > size[0] / 2) {
                        layoutParams.x = layoutParams.x + SizeUtil.dp2px(FloatBallView.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                    }
                    windowManager.updateViewLayout(FloatBallView, layoutParams);
                    setOnClickListeners();
                }
            });
            floatBallLayout.fold();
        }

    }

    private void expandFloatBall() {
        FloatBallLayout floatBallLayout = FloatBallView.findViewById(R.id.floatball_layout);
        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) FloatBallView.getLayoutParams();
        int[] size = GetParams.Screen();
        removeOnClickListeners();
        EasyFloat.appFloatDragEnable(false, tag);
        if (layoutParams.x > size[0] / 2) {
            floatBallLayout.setIsLeft(false);
            layoutParams.x = layoutParams.x - SizeUtil.dp2px(FloatBallView.getContext(), (float) (52 / 2 * Math.sqrt(3)));
        } else {
            floatBallLayout.setIsLeft(true);
        }
        layoutParams.y = layoutParams.y - SizeUtil.dp2px(FloatBallView.getContext(), 52);
        windowManager.updateViewLayout(FloatBallView, layoutParams);
        imageButton.setVisibility(View.INVISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setBackgroundResource(R.drawable.floatmenu_close);
            imageButton.setContentDescription("关闭展开的悬浮球");
            ImageButton[] imageButtons = new ImageButton[4];
            for (int i = 0; i < imageButtons.length; i++) {
                imageButtons[i] = new ImageButton(mApplicationRef.get());
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(mApplicationRef.get(), 44),
                        SizeUtil.dp2px(mApplicationRef.get(), 44));
                imageButtons[i].setLayoutParams(lp);
                switch (i) {
                    case 0:
                        imageButtons[i].setId(R.id.floatball_top);
                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_settings);
                        imageButtons[i].setContentDescription("设置");
                        imageButtons[i].setOnClickListener(this);
                        break;
                    case 1:
                        imageButtons[i].setId(R.id.floatball_mid1);
                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_detect);
                        imageButtons[i].setContentDescription("识别");
                        imageButtons[i].setOnClickListener(this);
                        break;
                    case 2:
                        imageButtons[i].setId(R.id.floatball_mid2);
                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_reset);
                        imageButtons[i].setContentDescription("初始化悬浮窗");
                        imageButtons[i].setOnClickListener(this);
                        imageButtons[i].setOnLongClickListener(this);
                        break;
                    case 3:
                        imageButtons[i].setId(R.id.floatball_bottom);
                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_exit);
                        imageButtons[i].setContentDescription("退出");
                        imageButtons[i].setOnClickListener(this);
                        break;
                }
            }
            floatBallLayout.expand(imageButtons);
            if (!isInGuiding) {
                setOnClickListeners();
            } else {
                removeOnClickListeners();
            }

        }, 30);
    }

    @Override
    public boolean onLongClick(View v) {
        if (isInChoosing) {
            return false;
        }
        switch (v.getId()) {
            case R.id.floatball_mid2:
                isInChoosing = true;
                FloatBallLayout floatBallLayout = FloatBallView.findViewById(R.id.floatball_layout);
                flipAnimation(floatBallLayout.findViewById(R.id.floatball_mid2), 100, R.drawable.floatmenu_auto, "自动模式悬浮窗初始化");
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    flipAnimation(floatBallLayout.findViewById(R.id.floatball_mid1), 100, R.drawable.floatmenu_continue, "持续模式悬浮窗初始化");
                    flipAnimation(floatBallLayout.findViewById(R.id.floatball_bottom), 100, R.drawable.floatmenu_subtitle, "同步字幕模式悬浮窗初始化");
                }, 100);
                handler.postDelayed(() -> {
                    flipAnimation(floatBallLayout.findViewById(R.id.floatball_top), 100, R.drawable.floatmenu_normal, "单/多悬浮窗初始化");
                }, 250);
                break;
        }
        return true;
    }

    private void flipAnimation(View view, long time, int new_background, CharSequence charSequence) {
        ObjectAnimator objectAnimator1 = ObjectAnimator
                .ofFloat(view, "rotationY", 0, 90);
        objectAnimator1.setDuration(time)
                .addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setBackgroundResource(new_background);
                        view.setContentDescription(charSequence);
                        ObjectAnimator
                                .ofFloat(view, "rotationY", -90, 0)
                                .setDuration(time)
                                .start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        objectAnimator1.start();

    }

    private void showInitGuide() {
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_FloatBall, true)) {
            isInGuiding = true;
            Intent intent = new Intent(mApplicationRef.get(), CurtainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CurtainActivity.name, "FB");
            intent.putExtra(CurtainActivity.index, index);
            mApplicationRef.get().startActivity(intent);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (FloatBallView != null) {
            FloatBallLayout floatBallLayout = FloatBallView.findViewById(R.id.floatball_layout);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (floatBallLayout.isDeployed) {
                    foldFloatBall();
                }
            }
        }
    }
}
