package com.wzy.yuka.yuka.floatball;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentActivity;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.qw.curtain.lib.CurtainFlow;
import com.qw.curtain.lib.flow.CurtainFlowInterface;
import com.qw.curtain.lib.shape.CircleShape;
import com.qw.curtain.lib.shape.RoundShape;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.GuideManager;
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
    private WeakReference<Activity> mActivityRef;
    private View FloatBallView;
    private int index;
    private FloatWindowManager floatWindowManager;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private boolean isInChoosing = false;

    public void show() {
        EasyFloat.showAppFloat(tag);
    }

    public void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    public void dismiss() {
        EasyFloat.dismissAppFloat(tag);
    }

    public FloatBall(Activity activity, String tag) throws FloatWindowManagerException {
        this.mActivityRef = new WeakReference<>(activity);
        this.tag = tag;
        floatWindowManager = FloatWindowManager.getInstance();

        EasyFloat.Builder fb = EasyFloat.with(mActivityRef.get())
                .setTag(tag)
                .setLayout(R.layout.float_ball, v -> {
                    FloatBallView = v;
                    ImageButton imageButton = v.findViewById(R.id.floatball_main);
                    imageButton.getBackground().setAlpha(50);
                    v.findViewById(R.id.floatball_main).setOnClickListener(this);
                })
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .registerCallbacks(new OnFloatCallbacks() {
                    WindowManager windowManager = mActivityRef.get().getWindowManager();
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                        int[] size = GetParams.Screen();
                        View view = EasyFloat.getAppFloatView(tag);
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                        if (layoutParams.x > size[0] / 2) {
                            //右边
                            layoutParams.x += 70;
                        } else {
                            layoutParams.x = -70;
                        }
                        windowManager.updateViewLayout(view, layoutParams);
                    };

                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
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
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {

                    }

                    @Override
                    public void dragEnd(@NotNull View view) {
                        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.ball_autoHide, true)) {
                            handler.postDelayed(runnable, 3000);
                        }
                    }
                })
                .setLocation(100, 500);
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
        fb.show();
    }

    @Override
    public void onClick(View v) {
        ImageButton imageButton = FloatBallView.findViewById(R.id.floatball_main);

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) FloatBallView.getLayoutParams();
        try {
            int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
            layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
        } catch (Exception e) {
            //do nothing. Probably using other version of android
        }

        switch (v.getId()) {
            case R.id.floatball_main:
                FloatBallLayout floatBallLayout = FloatBallView.findViewById(R.id.floatball_layout);
                if (isInChoosing) {
                    isInChoosing = false;
                    if (floatBallLayout.isDeployed) {
                        //展开则关闭
                        floatBallLayout.findViewById(R.id.floatball_top).setBackgroundResource(R.drawable.floatmenu_settings);
                        floatBallLayout.findViewById(R.id.floatball_mid1).setBackgroundResource(R.drawable.floatmenu_detect);
                        floatBallLayout.findViewById(R.id.floatball_mid2).setBackgroundResource(R.drawable.floatmenu_reset);
                        floatBallLayout.findViewById(R.id.floatball_bottom).setBackgroundResource(R.drawable.floatmenu_exit);
                    }
                } else {
                    WindowManager windowManager = mActivityRef.get().getWindowManager();
                    int[] size = GetParams.Screen();

                    if (floatBallLayout.isDeployed) {
                        //展开则关闭
                        EasyFloat.appFloatDragEnable(true, tag);
                        floatBallLayout.fold();
                        imageButton.setBackgroundResource(R.drawable.main);
                        layoutParams.y = layoutParams.y + SizeUtil.dp2px(FloatBallView.getContext(), 52);
                        if (layoutParams.x > size[0] / 2) {
                            layoutParams.x = layoutParams.x + SizeUtil.dp2px(FloatBallView.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                        }
                        windowManager.updateViewLayout(FloatBallView, layoutParams);
                    } else {
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
                            ImageButton[] imageButtons = new ImageButton[4];
                            for (int i = 0; i < imageButtons.length; i++) {
                                imageButtons[i] = new ImageButton(mActivityRef.get());
                                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(mActivityRef.get(), 44),
                                        SizeUtil.dp2px(mActivityRef.get(), 44));
                                imageButtons[i].setLayoutParams(lp);
                                switch (i) {
                                    case 0:
                                        imageButtons[i].setId(R.id.floatball_top);
                                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_settings);
                                        imageButtons[i].setOnClickListener(this);
                                        break;
                                    case 1:
                                        imageButtons[i].setId(R.id.floatball_mid1);
                                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_detect);
                                        imageButtons[i].setOnClickListener(this);
                                        break;
                                    case 2:
                                        imageButtons[i].setId(R.id.floatball_mid2);
                                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_reset);
                                        imageButtons[i].setOnClickListener(this);
                                        imageButtons[i].setOnLongClickListener(this);
                                        break;
                                    case 3:
                                        imageButtons[i].setId(R.id.floatball_bottom);
                                        imageButtons[i].setBackgroundResource(R.drawable.floatmenu_exit);
                                        imageButtons[i].setOnClickListener(this);
                                        break;
                                }
                            }
                            floatBallLayout.expand(imageButtons);
                        }, 30);
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
                    mActivityRef.get().finish();
                    Intent intent = new Intent(mActivityRef.get(), com.wzy.yuka.ui.setting.SettingsActivity.class);
                    mActivityRef.get().startActivity(intent);
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
                    mActivityRef.get().finishAffinity();
                    System.exit(0);
                }
                break;
        }
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
                floatBallLayout.findViewById(R.id.floatball_top).setBackgroundResource(R.drawable.floatmenu_normal);
                floatBallLayout.findViewById(R.id.floatball_mid1).setBackgroundResource(R.drawable.floatmenu_continue);
                floatBallLayout.findViewById(R.id.floatball_mid2).setBackgroundResource(R.drawable.floatmenu_auto);
                floatBallLayout.findViewById(R.id.floatball_bottom).setBackgroundResource(R.drawable.floatmenu_subtitle);
                break;
        }
        return true;
    }

    private void showInitGuide() {
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_FloatBall, true)) {
            GuideManager guideManager = new GuideManager((FragmentActivity) mActivityRef.get());
            CurtainFlow cf = new CurtainFlow.Builder()
                    .with(11, guideManager.weaveCurtain(FloatBallView, new CircleShape(), 32, R.layout.guide))
                    .with(12, guideManager.weaveCurtain(FloatBallView, new RoundShape(12), 32, R.layout.guide))
                    .create();
            cf.start(new CurtainFlow.CallBack() {
                ConstraintLayout layout;
                ImageView img;
                ConstraintLayout.LayoutParams params_img;
                ConstraintSet set = new ConstraintSet();
                FloatBallLayout fbl = FloatBallView.findViewById(R.id.floatball_layout);

                @Override
                public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                    switch (currentId) {
                        case 11:
                            fbl.setFloatBallLayoutListener(new FloatBallLayout.FloatBallLayoutListener() {
                                @Override
                                public void deployed() {
                                    curtainFlow.push();
                                }

                                @Override
                                public void folded() {
                                    curtainFlow.finish();
                                }
                            });
                            layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_layout);
                            layout.setOnClickListener(v -> {
                                v.setOnClickListener(null);

                                fbl.findViewById(R.id.floatball_main).performClick();

                            });
                            img = layout.findViewById(R.id.guide_2);
                            img.setImageResource(R.drawable.guide_floatball_folded);
                            params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();

                            params_img.width = SizeUtil.dp2px(mActivityRef.get(), 335);
                            params_img.height = SizeUtil.dp2px(mActivityRef.get(), 242);

                            params_img.topMargin = SizeUtil.dp2px(mActivityRef.get(), 10);
                            params_img.rightMargin = SizeUtil.dp2px(mActivityRef.get(), 10);
                            img.setLayoutParams(params_img);

                            set.clone(layout);
                            set.clear(R.id.guide_interpret_img, ConstraintSet.RIGHT);
                            set.applyTo(layout);
                            break;
                        case 12:
                            layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_layout);
                            layout.setOnClickListener(v -> {
                                curtainFlow.finish();
                                v.setOnClickListener(null);
                            });
                            img = layout.findViewById(R.id.guide_2);
                            img.setImageResource(R.drawable.guide_floatball_deployed);
                            params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();
                            params_img.width = SizeUtil.dp2px(mActivityRef.get(), 335);
                            params_img.height = SizeUtil.dp2px(mActivityRef.get(), 242);
                            params_img.topMargin = SizeUtil.dp2px(mActivityRef.get(), 10);
                            params_img.rightMargin = SizeUtil.dp2px(mActivityRef.get(), 10);
                            img.setLayoutParams(params_img);
                            set.clone(layout);
                            set.clear(R.id.guide_interpret_img, ConstraintSet.RIGHT);
                            set.applyTo(layout);
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    fbl.removeFloatBallLayoutListener();
                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_FloatBall, false);
                }
            });

        }
    }


}
