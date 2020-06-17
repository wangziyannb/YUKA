package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.wzy.yuka.core.audio.AudioService;
import com.wzy.yuka.core.screenshot.ScreenShotService_Continue;
import com.wzy.yuka.core.screenshot.ScreenShotService_Single;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Ziyan on 2020/4/30.
 */
public class FloatBall implements View.OnClickListener {
    private String tag;
    private Activity activity;
    private View FloatBallView;

    FloatBall(Activity activity, String tag) {
        this.activity = activity;
        this.tag = tag;
        boolean[] params1 = GetParams.FloatBall();
        EasyFloat.Builder fb = EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.float_ball, v -> {
                    FloatBallView = v;
                    ImageButton imageButton = v.findViewById(R.id.test1);
                    imageButton.getBackground().setAlpha(50);
                    v.findViewById(R.id.test1).setOnClickListener(this);
                })
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .registerCallbacks(new OnFloatCallbacks() {
                    //WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                    WindowManager windowManager = activity.getWindowManager();
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
                        if (GetParams.FloatBall()[0]) {
                            handler.postDelayed(runnable, 3000);
                        }
                    }
                })
                .setLocation(100, 500);
        if (!params1[4]) {
            fb.setDisplayHeight(context -> {
                boolean[] params = GetParams.FloatBall();
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                int[] size = GetParams.Screen();
                if (params[3]) {
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

    void dismiss() {
        EasyFloat.dismissAppFloat(tag);
    }

    @Override
    public void onClick(View v) {
        Intent service_Single = new Intent(activity, ScreenShotService_Single.class);
        Intent service_Continue = new Intent(activity, ScreenShotService_Continue.class);
        Intent service_audio = new Intent(activity, AudioService.class);
        ImageButton imageButton = FloatBallView.findViewById(R.id.test1);
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) FloatBallView.getLayoutParams();
        try {
            int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
            layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040);
        } catch (Exception e) {
            //do nothing. Probably using other version of android
        }
        boolean[] params = GetParams.FloatBall();
        switch (v.getId()) {
            case R.id.test1:
                FloatBallLayout FloatBallLayout = FloatBallView.findViewById(R.id.test);

                WindowManager windowManager = activity.getWindowManager();
                int[] size = GetParams.Screen();

                if (FloatBallLayout.isDeployed) {
                    //展开则关闭
                    EasyFloat.appFloatDragEnable(true, "mainFloatBall");
                    FloatBallLayout.fold();
                    imageButton.setBackgroundResource(R.drawable.main);
                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(FloatBallView.getContext(), 52);
                    if (layoutParams.x > size[0] / 2) {
                        layoutParams.x = layoutParams.x + SizeUtil.dp2px(FloatBallView.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                    }
                    windowManager.updateViewLayout(FloatBallView, layoutParams);
                } else {
                    if (params[2]) {
                        EasyFloat.appFloatDragEnable(false, "mainFloatBall");
                    } else {
                        EasyFloat.appFloatDragEnable(true, "mainFloatBall");
                    }
                    if (layoutParams.x > size[0] / 2) {
                        FloatBallLayout.setIsLeft(false);
                        layoutParams.x = layoutParams.x - SizeUtil.dp2px(FloatBallView.getContext(), (float) (52 / 2 * Math.sqrt(3)));
                    } else {
                        FloatBallLayout.setIsLeft(true);
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
                            imageButtons[i] = new ImageButton(activity);
                            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(SizeUtil.dp2px(activity, 44),
                                    SizeUtil.dp2px(activity, 44));
                            imageButtons[i].setLayoutParams(lp);
                            switch (i) {
                                case 0:
                                    imageButtons[i].setId(R.id.settings_button);
                                    imageButtons[i].setBackgroundResource(R.drawable.floatmenu_settings);
                                    imageButtons[i].setOnClickListener(this);
                                    break;
                                case 1:
                                    imageButtons[i].setId(R.id.detect_button);
                                    imageButtons[i].setBackgroundResource(R.drawable.floatwindow_detect);
                                    imageButtons[i].setOnClickListener(this);
                                    break;
                                case 2:
                                    imageButtons[i].setId(R.id.reset_button);
                                    imageButtons[i].setBackgroundResource(R.drawable.floatmenu_reset);
                                    imageButtons[i].setOnClickListener(this);
                                    break;
                                case 3:
                                    imageButtons[i].setId(R.id.exit_button);
                                    imageButtons[i].setBackgroundResource(R.drawable.floatmenu_exit);
                                    imageButtons[i].setOnClickListener(this);
                                    break;
                            }
                            FloatBallLayout.addView(imageButtons[i]);
                        }
                    }, 30);
                }
                break;
            case R.id.settings_button:
                activity.finish();
                Intent intent = new Intent(activity, com.wzy.yuka.ui.setting.SettingsActivity.class);
                activity.startActivity(intent);
                if (params[1]) {
                    imageButton.performClick();
                }
                break;
            case R.id.detect_button:
                if (FloatWindowManager.getNumOfFloatWindows() > 0) {
                    FloatWindowManager.hideAllFloatWindow();
                    FloatWindowManager.startScreenShot(activity);
                    //params[]是获取的关于悬浮窗的设置！
                    if (params[1]) {
                        imageButton.performClick();
                    }
                } else {
                    Toast.makeText(activity, "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset_button:
                activity.stopService(service_Continue);
                activity.stopService(service_Single);
                activity.stopService(service_audio);
                FloatWindowManager.reset(activity);
                if (params[1]) {
                    imageButton.performClick();
                }
                break;
            case R.id.exit_button:
                activity.stopService(service_Continue);
                activity.stopService(service_Single);
                activity.stopService(service_audio);
                activity.finishAffinity();
                System.exit(0);
                break;
        }
    }

    private void showInitGuide() {
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferencesUtil.FIRST_INVOKE_FloatBall, true)) {
            GuideManager guideManager = new GuideManager((FragmentActivity) activity);
            CurtainFlow cf = new CurtainFlow.Builder()
                    .with(11, guideManager.weaveCurtain(FloatBallView, new CircleShape(), 32, R.layout.guide))
                    .create();
            cf.start(new CurtainFlow.CallBack() {
                @Override
                public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                    FloatBallLayout fbl = FloatBallView.findViewById(R.id.test);
                    switch (currentId) {
                        case 11:
                            curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
                                fbl.findViewById(R.id.test1).performClick();
                                fbl.setFloatBallLayoutListener(new FloatBallLayout.FloatBallLayoutListener() {
                                    @Override
                                    public void deployed() {
                                        Log.e("TAG", "deployed: ");
                                        cf.addCurtain(12, guideManager.weaveCurtain(FloatBallView, new RoundShape(12), 32, R.layout.guide));
                                        curtainFlow.push();
                                    }

                                    @Override
                                    public void folded() {
                                        Log.e("TAG", "folded: ");
                                    }
                                });
                            });
                            break;
                        case 12:
                            fbl.removeFloatBallLayoutListener();
                            curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
                                curtainFlow.finish();
                            });
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    Toast.makeText(activity, "演示3完成", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

