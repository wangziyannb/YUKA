package com.wzy.yuka.yuka_lite.floatball;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
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
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatball.FloatBall;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Ziyan on 2020/8/14.
 */
public class MainFloatBall extends FloatBall {
    public boolean isInGuiding = false;
    private final WindowManager windowManager;
    private boolean isInChoosing = false;
    private final SharedPreferencesUtil spUtil;
    private final YukaFloatWindowManager mFloatWindowManager;

    public MainFloatBall(int index, String tag, YukaFloatWindowManager manager) {
        super(index, tag, manager);
        mFloatWindowManager = manager;
        spUtil = SharedPreferencesUtil.getInstance();
        windowManager = (WindowManager) applicationWeakReference.get().getSystemService(Context.WINDOW_SERVICE);
        EasyFloat.Builder fb = EasyFloat.with(applicationWeakReference.get().getApplicationContext())
                .setTag(tag)
                .setLayout(R.layout.float_ball, v -> {
                    setView(v);
                    ImageButton imageButton = v.findViewById(R.id.floatball_main);
                    imageButton.getBackground().setAlpha(255);
                    v.findViewById(R.id.floatball_main).setOnClickListener(this);
                })
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .registerCallbacks(new OnFloatCallbacks() {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    final Runnable runnable = () -> {
                        int[] size = SizeUtil.Screen(applicationWeakReference.get());
                        View view = EasyFloat.getAppFloatView(tag);
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                        ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
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
                            initFloatBallLayout();
                            floatWindowManager.startScreenStatusService();
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
                        ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
                        imageButton.getBackground().setAlpha(255);
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {
                        ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
                        imageButton.getBackground().setAlpha(255);
                    }

                    @Override
                    public void dragEnd(@NotNull View view) {
                        if ((boolean) spUtil.getParam(SharedPreferenceCollection.ball_autoHide, true)) {
                            handler.postDelayed(runnable, 3000);
                        } else {
                            handler.postDelayed(() -> {
                                ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
                                imageButton.getBackground().setAlpha(50);
                            }, 3000);
                        }
                    }
                })
                .setLocation(100, 500);
        fb.show();
    }

    public void removeOnClickListeners() {
        mFloatBallView.findViewById(R.id.floatball_main).setOnClickListener(null);
        try {
            mFloatBallView.findViewById(R.id.floatball_top).setOnClickListener(null);
            mFloatBallView.findViewById(R.id.floatball_mid1).setOnClickListener(null);
            mFloatBallView.findViewById(R.id.floatball_mid2).setOnClickListener(null);
            mFloatBallView.findViewById(R.id.floatball_bottom).setOnClickListener(null);
            mFloatBallView.findViewById(R.id.floatball_mid2).setOnLongClickListener(null);
        } catch (NullPointerException ignored) {

        }
    }

    public void setOnClickListeners() {
        mFloatBallView.findViewById(R.id.floatball_main).setOnClickListener(this);
        try {
            mFloatBallView.findViewById(R.id.floatball_top).setOnClickListener(this);
            mFloatBallView.findViewById(R.id.floatball_mid1).setOnClickListener(this);
            mFloatBallView.findViewById(R.id.floatball_mid2).setOnClickListener(this);
            mFloatBallView.findViewById(R.id.floatball_bottom).setOnClickListener(this);
            mFloatBallView.findViewById(R.id.floatball_mid2).setOnLongClickListener(this);
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onClick(View v) {
        ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
        switch (v.getId()) {
            case R.id.floatball_main:
                FloatBallLayout floatBallLayout = mFloatBallView.findViewById(R.id.floatball_layout);
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
                        mFloatWindowManager.remove_AllFloatWindow();
                        mFloatWindowManager.addFloatWindow("SWN_S");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(applicationWeakReference.get(), com.wzy.yuka.ui.setting.SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    applicationWeakReference.get().startActivity(intent);
                    if ((boolean) spUtil.getParam(SharedPreferenceCollection.ball_autoClose, true)) {
                        imageButton.performClick();
                    }
                }
                break;
            case R.id.floatball_mid1:
                if (isInChoosing) {
                    try {
                        mFloatWindowManager.remove_AllFloatWindow();
                        mFloatWindowManager.addFloatWindow("SWN_C");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
                    ((YukaFloatWindowManager) floatWindowManager).detect();
                    if ((boolean) spUtil.getParam(SharedPreferenceCollection.ball_autoClose, true)) {
                        imageButton.performClick();
                    }
                }
                break;
            case R.id.floatball_mid2:
                if (isInChoosing) {
                    try {
                        mFloatWindowManager.remove_AllFloatWindow();
                        mFloatWindowManager.addFloatWindow("SWA");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
//                    mFloatWindowManager.reset();
                    if ((boolean) spUtil.getParam(SharedPreferenceCollection.ball_autoClose, true)) {
                        imageButton.performClick();
                    }
                }
                break;
            case R.id.floatball_bottom:
                if (isInChoosing) {
                    try {
                        mFloatWindowManager.remove_AllFloatWindow();
                        mFloatWindowManager.addFloatWindow("SBW");
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                } else {
//                    mFloatWindowManager.stop_RecordingTrans();
//                    mFloatWindowManager.stop_ScreenShotTrans_normal(true);
//                    mFloatWindowManager.stop_ScreenShotTrans_normal(false);
//                    mFloatWindowManager.stop_ScreenShotTrans_auto();
                    mFloatWindowManager.stopScreenStatusService();
                    System.exit(0);
                }
                break;
        }
    }

    private void initFloatBallLayout() {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatBallView.getLayoutParams();
        try {
            int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
            layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
        } catch (Exception e) {
            //do nothing. Probably using other version of android
        }
        FloatBallLayout floatBallLayout = mFloatBallView.findViewById(R.id.floatball_layout);
        ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
        int[] size = SizeUtil.Screen(applicationWeakReference.get());
        floatBallLayout.setFloatBallLayoutListener(new FloatBallLayout.FloatBallLayoutListener() {
            @Override
            public void deployed() {

            }

            @Override
            public void onDeploy() {
                EasyFloat.appFloatDragEnable(false, tag);
                if (layoutParams.x > size[0] / 2) {
                    floatBallLayout.setIsLeft(false);
                    layoutParams.x = layoutParams.x - SizeUtil.dp2px(applicationWeakReference.get(), (float) (52 / 2 * Math.sqrt(3)));
                } else {
                    floatBallLayout.setIsLeft(true);
                }
                layoutParams.y = layoutParams.y - SizeUtil.dp2px(applicationWeakReference.get(), 52);
                windowManager.updateViewLayout(mFloatBallView, layoutParams);
                setOnClickListeners();
            }

            @Override
            public void onFold() {

            }

            @Override
            public void folded() {
                EasyFloat.appFloatDragEnable(true, tag);
                imageButton.setBackgroundResource(R.drawable.main);
                imageButton.setContentDescription("Yuka悬浮球");
                layoutParams.y = layoutParams.y + SizeUtil.dp2px(applicationWeakReference.get(), 52);
                if (layoutParams.x > size[0] / 2) {
                    layoutParams.x = layoutParams.x + SizeUtil.dp2px(applicationWeakReference.get(), (float) (52 / 2 * Math.sqrt(3)));
                }
                windowManager.updateViewLayout(mFloatBallView, layoutParams);
                setOnClickListeners();
            }
        });
    }

    @Override
    protected void foldFloatBall() {
        removeOnClickListeners();
        FloatBallLayout floatBallLayout = mFloatBallView.findViewById(R.id.floatball_layout);
        floatBallLayout.fold();
    }

    @Override
    protected void expandFloatBall() {
        FloatBallLayout floatBallLayout = mFloatBallView.findViewById(R.id.floatball_layout);
        ImageButton imageButton = mFloatBallView.findViewById(R.id.floatball_main);
        imageButton.setVisibility(View.INVISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setBackgroundResource(R.drawable.floatmenu_close);
            imageButton.setContentDescription("关闭展开的悬浮球");
            ImageButton[] imageButtons = new ImageButton[4];
            for (int i = 0; i < imageButtons.length; i++) {
                imageButtons[i] = new ImageButton(applicationWeakReference.get());
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(applicationWeakReference.get(), 44),
                        SizeUtil.dp2px(applicationWeakReference.get(), 44));
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
                FloatBallLayout floatBallLayout = mFloatBallView.findViewById(R.id.floatball_layout);
                flipAnimation(floatBallLayout.findViewById(R.id.floatball_mid2), 100, R.drawable.floatmenu_auto, "自动模式悬浮窗初始化");
                Handler handler = new Handler(Looper.getMainLooper());
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

    public View getView() {
        return mFloatBallView;
    }

    public void setMainOnClickListeners() {
        mFloatBallView.findViewById(R.id.floatball_main).setOnClickListener(this);
    }

    private void showInitGuide() {
        if ((boolean) spUtil.getParam(SharedPreferenceCollection.FIRST_FloatBall, true)) {
            isInGuiding = true;
            Intent intent = new Intent(applicationWeakReference.get(), CurtainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CurtainActivity.name, "FB");
            intent.putExtra(CurtainActivity.index, index);
            applicationWeakReference.get().startActivity(intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFloatBallView != null) {
            FloatBallLayout floatBallLayout = mFloatBallView.findViewById(R.id.floatball_layout);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (floatBallLayout.isDeployed) {
                    foldFloatBall();
                }
            }
        }
    }


}
