package com.wzy.yuka.tools.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.tools.screenshot.ScreenShotService;

import org.jetbrains.annotations.NotNull;

/**
 * The type Float window.
 */
public class FloatWindow {
    static final String TAG = "FloatWindow";
    //location 0 1 2 3 = lA 0 1 + lB 0 1
    public static int[][] location;
    private static String[] tags;
    public static int NumOfFloatWindows = 0;

    public static void initFloatWindow(Activity activity) {
        Intent service = new Intent(activity, ScreenShotService.class);
        NumOfFloatWindows += 1;
//        EasyFloat.with(activity).setTag("startBtn")
//                .setLayout(R.layout.start, view -> {
//                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
//                    view.findViewById(R.id.button1).setOnClickListener(v -> {
//                        if ((((Button) v).getText()).equals("关闭")) {
//                            ((Button) v).setText("控制");
//                        } else {
//                            ((Button) v).setText("关闭");
//                        }
//                        EasyFloat.dismissAppFloat("control");
//                        EasyFloat.with(activity)
//                                .setTag("control")
//                                .setLayout(R.layout.control_floatwindow, v1 -> {
//                                    v1.findViewById(R.id.button3).setOnClickListener(v2 -> {
//                                        if ((((Button) v2).getText().equals("启用"))) {
//                                            if (pref.getBoolean("settings_window_multiple", false)) {
//                                                location = new int[pref.getInt("settings_window_number", 1)][4];
//                                                tags = new String[pref.getInt("settings_window_number", 1)];
//                                                if (NumOfFloatWindows == 1) {
//                                                    NumOfFloatWindows += pref.getInt("settings_window_number", 1);
//                                                }
//                                                multiFloatWindow(activity, pref.getInt("settings_window_number", 1));
//                                            } else {
//                                                location = new int[1][4];
//                                                tags = new String[1];
//                                                if (NumOfFloatWindows == 1) {
//                                                    NumOfFloatWindows += 1;
//                                                }
//                                                multiFloatWindow(activity, 1);
//                                            }
//                                            showAllFloatWindow(false);
//                                            ((Button) v2).setText("隐藏");
//                                        } else if ((((Button) v2).getText().equals("隐藏"))) {
//                                            hideAllFloatWindow();
//                                            ((Button) v2).setText("启用");
//                                        }
//                                    });
//                                    v1.findViewById(R.id.button4).setOnClickListener(v2 -> {
//                                        activity.stopService(service);
//                                        activity.finishAffinity();
//                                        System.exit(0);
//                                    });
//                                    v1.findViewById(R.id.button7).setOnClickListener(v2 -> {
//                                        dismissAllFloatWindow(true);
//                                        activity.stopService(service);
//                                        ((Button) v2.findViewById(R.id.button3)).setText("显示");
//                                        v2.findViewById(R.id.button3).performClick();
//                                    });
//                                    v1.findViewById(R.id.button8).setOnClickListener(v2 -> {
//                                        EasyFloat.with(activity)
//                                                .setTag("settings_panel")
//                                                .setLayout(R.layout.settings_floatwindow, v3 -> {
//                                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
//                                                    SharedPreferences.Editor editor = preferences.edit();
//                                                    v3.findViewById(R.id.settings_hide).setOnClickListener(v4 -> {
//                                                        EasyFloat.dismissAppFloat("settings_panel");
//                                                    });
//                                                    v3.findViewById(R.id.settings_trans_translator).setOnClickListener(v4 -> {
//                                                        if ((((Button) v4).getText()).equals("关闭")) {
//                                                            ((Button) v4).setText("进阶设置");
//                                                        } else {
//                                                            ((Button) v4).setText("关闭");
//                                                        }
//                                                        EasyFloat.dismissAppFloat("settings_more");
//                                                        EasyFloat.with(activity)
//                                                                .setTag("settings_more")
//                                                                .setLayout(R.layout.empty, v5 -> {
//                                                                    LinearLayout linearLayout = v5.findViewById(R.id.empty_linearLayout);
//                                                                    String[] translator_name = activity.getResources().getStringArray(R.array.translator_name);
//                                                                    String[] translator = activity.getResources().getStringArray(R.array.translator);
//                                                                    Button button[] = new Button[translator_name.length];
//                                                                    for (int i = 0; i < translator_name.length; i++) {
//                                                                        button[i] = new Button(activity);
//                                                                        button[i].setText(translator_name[i]);
//                                                                        int finalI = i;
//                                                                        button[i].setOnClickListener(vx -> {
//                                                                            editor.putString("settings_trans_translator", translator[finalI]);
//                                                                            editor.commit();
//                                                                            EasyFloat.dismissAppFloat("settings_more");
//                                                                        });
//                                                                        linearLayout.addView(button[i]);
//                                                                    }
//
//                                                                })
//                                                                .setLocation(700, 0)
//                                                                .setDragEnable(false)
//                                                                .setShowPattern(ShowPattern.ALL_TIME)
//                                                                .show();
//                                                    });
//                                                    v3.findViewById(R.id.settings_advance).setOnClickListener(v4 -> {
//                                                        if ((((Button) v4).getText()).equals("关闭")) {
//                                                            ((Button) v4).setText("进阶设置");
//                                                        } else {
//                                                            ((Button) v4).setText("关闭");
//                                                        }
//                                                        EasyFloat.dismissAppFloat("settings_more");
//                                                        EasyFloat.with(activity)
//                                                                .setTag("settings_more")
//                                                                .setLayout(R.layout.empty, v5 -> {
//                                                                    LinearLayout linearLayout = v5.findViewById(R.id.empty_linearLayout);
//                                                                    Switch switch_fastMode = new Switch(activity);
//                                                                    switch_fastMode.setText("快速模式：");
//                                                                    switch_fastMode.setChecked(preferences.getBoolean("settings_fastMode", false));
//                                                                    switch_fastMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                                        if (isChecked) {
//                                                                            editor.putBoolean("settings_fastMode", true);
//                                                                            editor.commit();
//                                                                        } else {
//                                                                            editor.putBoolean("settings_fastMode", false);
//                                                                            editor.commit();
//                                                                        }
//                                                                    });
//                                                                    linearLayout.addView(switch_fastMode);
//                                                                    Switch switch_continuousMode = new Switch(activity);
//                                                                    switch_continuousMode.setText("半自动模式：");
//                                                                    switch_continuousMode.setChecked(preferences.getBoolean("settings_continuousMode", false));
//                                                                    switch_continuousMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                                                                        if (isChecked) {
//                                                                            EasyFloat.getAppFloatView("startBtn")
//                                                                                    .findViewById(R.id.button6).setVisibility(View.VISIBLE);
//                                                                            editor.putBoolean("settings_continuousMode", true);
//                                                                            editor.commit();
//                                                                        } else {
//                                                                            EasyFloat.getAppFloatView("startBtn")
//                                                                                    .findViewById(R.id.button6).setVisibility(View.GONE);
//                                                                            editor.putBoolean("settings_continuousMode", false);
//                                                                            editor.commit();
//                                                                        }
//                                                                    });
//                                                                    linearLayout.addView(switch_continuousMode);
//                                                                })
//                                                                .setLocation(700, 0)
//                                                                .setDragEnable(false)
//                                                                .setShowPattern(ShowPattern.ALL_TIME)
//                                                                .show();
//                                                    });
//                                                })
//                                                .setShowPattern(ShowPattern.ALL_TIME)
//                                                .setLocation(300, 0)
//                                                .setDragEnable(false)
//                                                .show();
//
//                                    });
//                                })
//                                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
//                                .setShowPattern(ShowPattern.ALL_TIME)
//                                .setLocation(0, 500)
//                                .setDragEnable(true)
//                                .show();
//                    });
//                    view.findViewById(R.id.button5).setOnClickListener(v -> {
//                        if (NumOfFloatWindows > 1) {
//                            hideAllFloatWindow();
//                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                                activity.startService(service);
//                            } else {
//                                activity.startForegroundService(service);
//                            }
//                        } else {
//                            Toast.makeText(activity, "还没有悬浮窗初始化呢，请从控制中启用悬浮窗", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    if (pref.getBoolean("settings_continuousMode", false)) {
//                        Button button6 = view.findViewById(R.id.button6);
//                        button6.setVisibility(View.VISIBLE);
//                        button6.setOnClickListener((v2) -> {
//                            try {
//                                ScreenShotService.continuous = false;
//                            } catch (Exception e) {
//                            }
//                        });
//                    }
//                })
//                .setShowPattern(ShowPattern.ALL_TIME)
//                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
//                .setLocation(100, 100).show();
        EasyFloat.with(activity)
                .setTag("mainFloatBall")
                .setLayout(R.layout.test, v -> {
                    ImageButton imageButton = v.findViewById(R.id.test1);
                    imageButton.getBackground().setAlpha(0);
                    v.findViewById(R.id.test1).setOnClickListener(v1 -> {
                        FloatBall floatWindows = v.findViewById(R.id.test);
                        if (SizeUtil.px2dp(v.getContext(), v.getWidth()) > 45) {
                            while (true) {
                                floatWindows.removeViewAt(floatWindows.getChildCount() - 1);
                                if (floatWindows.getChildCount() == 1) {
                                    break;
                                }
                            }
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
                                            if (NumOfFloatWindows > 1) {
                                                hideAllFloatWindow();
                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                                    activity.startService(service);
                                                } else {
                                                    activity.startForegroundService(service);
                                                }
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
                                            reset(activity);
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

    private static void multiFloatWindow(Activity activity, int num) {
        //todo 需要修改以支持单独添加窗口的多窗口模式
        for (int i = 0; i < num; i++) {
            tags[i] = "selectWindow" + i;
            singleFloatWindow(activity, i);
        }
    }


    private static void singleFloatWindow(Activity activity, int index) {
        if (!(tags.length > index)) {
            return;
        } else {
            NumOfFloatWindows += 1;
            tags[index] = "selectWindow" + index;
            EasyFloat.with(activity)
                    .setTag(tags[index])
                    .setLayout(R.layout.select_window, view1 -> {
                        RelativeLayout rl = view1.findViewById(R.id.testFloatScale);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl.getLayoutParams();
                        ScaleImageView si = view1.findViewById(R.id.sw_scale);
                        si.setOnScaledListener((x, y, event) -> {
                            params.width += (int) x;
                            params.height += (int) y;
                            rl.setLayoutParams(params);
                            //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                        });
                        view1.findViewById(R.id.sw_close).setOnClickListener(v1 -> {
                            EasyFloat.hideAppFloat("selectWindow" + index);
                        });
                    })
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .setLocation(100 + index * 40, 100 + index * 40)
                    .setAppFloatAnimator(null)
                    .registerCallbacks(new OnFloatCallbacks() {
                        @Override
                        public void createdResult(boolean b, @org.jetbrains.annotations.Nullable String s, @org.jetbrains.annotations.Nullable View view) {
                            if (b) {
                                getLocation(view, index);
                            }
                        }

                        @Override
                        public void show(@NotNull View view) {
                            //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                            getLocation(view, index);
                        }

                        @Override
                        public void hide(@NotNull View view) {
                            //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                            getLocation(view, index);
                        }

                        @Override
                        public void dismiss() {

                        }

                        @Override
                        public void touchEvent(@NotNull View view, @NotNull MotionEvent motionEvent) {
                            //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                            getLocation(view, index);
                        }

                        @Override
                        public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {

                        }

                        @Override
                        public void dragEnd(@NotNull View view) {
                            //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                            getLocation(view, index);
                        }
                    })
                    .show();
        }
    }

    private static void getLocation(View view, int index) {
        view.getLocationOnScreen(location[index]);
        location[index][2] = location[index][0] + view.getRight();
        location[index][3] = location[index][1] + view.getBottom();
    }

    public static TextView[] getAllTextViews() {
        TextView[] textViews = new TextView[tags.length];
        for (int i = 0; i < tags.length; i++) {
            textViews[i] = EasyFloat.getAppFloatView(tags[i]).findViewById(R.id.translatedText);
        }
        return textViews;
    }

    //准备隐藏以截图
    public static void hideAllFloatWindow() {
        if (NumOfFloatWindows > 1) {
            for (String tag : tags) {
                EasyFloat.hideAppFloat(tag);
            }
        }
    }


    /**
     * Show all float window.
     *
     * @param after 如果是截图后的，应当更改文字提示用户
     */
    public static void showAllFloatWindow(boolean after) {
        if (NumOfFloatWindows > 1) {
            for (String tag : tags) {
                EasyFloat.showAppFloat(tag);
                if (after) {
                    TextView textView = EasyFloat.getAppFloatView(tag).findViewById(R.id.translatedText);
                    textView.setText("目标图片已发送，请等待...");
                    textView.setTextColor(Color.WHITE);
                }
            }
        }
    }

    /**
     * Dismiss all float window.
     *
     * @param except 是否将主悬浮球也一并删除
     */
    public static void dismissAllFloatWindow(boolean except) {
        if (NumOfFloatWindows > 1) {
            for (String tag : tags) {
                EasyFloat.dismissAppFloat(tag);
                NumOfFloatWindows -= 1;
            }
            if (!except) {
                EasyFloat.dismissAppFloat("startBtn");
                NumOfFloatWindows = 0;
            }
        } else if (NumOfFloatWindows == 1) {
            if (!except) {
                EasyFloat.dismissAppFloat("startBtn");
                NumOfFloatWindows = 0;
            }
        }
    }

    //caibitajinxiong
    private static void reset(Activity activity) {
        dismissAllFloatWindow(true);
        location = new int[1][4];
        tags = new String[1];
        NumOfFloatWindows = 1;
        singleFloatWindow(activity, 0);
    }
}


