package com.wzy.yuka.yuka.floatwindow;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.CurtainActivity;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.ui.view.ScaleImageView;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Ziyan on 2020/4/29.
 */
public class SelectWindow_Normal extends FloatWindow {

    public boolean isGuiding = false;
    private boolean isPlay = false;

    public boolean isContinue = false;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    public SelectWindow_Normal(Activity activity, int index, String tag) throws FloatWindowManagerException {
        super(activity, index, tag);
        EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.floatwindow_main, view1 -> {
                    setView(view1);
                    changeClass(false);
                    RelativeLayout rl = view1.findViewById(R.id.select_window_layout);
                    //改变悬浮框透明度
                    GradientDrawable drawable = (GradientDrawable) rl.getBackground();
                    int alpha = (int) Math.round((int) (sharedPreferencesUtil.getParam("settings_window_opacityBg", 50)) * 2.55);
                    String alpha_hex = Integer.toHexString(alpha).toUpperCase();
                    if (alpha_hex.length() == 1) {
                        alpha_hex = "0" + alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#" + alpha_hex + "000000"));
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl.getLayoutParams();
                    ScaleImageView si = view1.findViewById(R.id.sw_scale);
                    si.setOnScaledListener((x, y, event) -> {
                        TextView textView = view1.findViewById(R.id.translatedText);
                        if (textView.getText().equals("选取目标位置后点识别")) {
                            textView.setText("等待选取...");
                        }
                        params.width += (int) x;
                        params.height += (int) y;
                        if (params.width < 100) {
                            params.width = 100;
                        }
                        if (params.height < 100) {
                            params.height = 100;
                        }
                        rl.setLayoutParams(params);
                        setLocation();
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                    });
                    view1.findViewById(R.id.sw_close).setOnClickListener(this);
                    view1.findViewById(R.id.sw_pap).setOnClickListener(this);
                    view1.findViewById(R.id.sw_addwindows).setOnClickListener(this);
                })
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(GetParams.Screen()[0] / 2 - SizeUtil.dp2px(activityWeakReference.get(), 250) / 2,
                        (int) ((GetParams.Screen()[1] + 1.5 * GetParams.Screen()[2]) / 2 - SizeUtil.dp2px(activityWeakReference.get(), 120) / 2))
                .setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    Handler handler = new Handler();
                    View view;
                    Runnable r = () -> {
                        if (view != null) {
                            String str_t = ((TextView) this.view.findViewById(R.id.translatedText)).getText() + "";
                            if ((!TextUtils.isEmpty(str_t)) && (!str_t.equals("选取目标位置后点识别")) && (!str_t.equals("等待选取..."))) {
                                // 得到剪贴板管理器
                                ClipboardManager cm = (ClipboardManager) activityWeakReference.get().getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                                ClipData clipData = ClipData.newPlainText("yuka", str_t);
                                // 把数据集设置（复制）到剪贴板
                                cm.setPrimaryClip(clipData);
                                Toast.makeText(activityWeakReference.get(), "已复制选择的文本至剪切板", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
                            this.view = view;
                            setLocation();
                            showInitGuide();
                        }
                    }

                    @Override
                    public void show(@NotNull View view) {
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                        setLocation();
                    }

                    @Override
                    public void hide(@NotNull View view) {
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                        setLocation();
                    }

                    @Override
                    public void dismiss() {

                    }

                    @Override
                    public void touchEvent(@NotNull View view, @NotNull MotionEvent motionEvent) {
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                        setLocation();
                        changeClass(true);
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            handler.postDelayed(r, 1000);
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            handler.removeCallbacks(r);
                            handler.postDelayed(r, 1000);
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            handler.removeCallbacks(r);
                        }
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {
                        setLocation();
//                        handler.removeCallbacks(r);
                    }

                    @Override
                    public void dragEnd(@NotNull View view) {
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                        setLocation();
                    }
                }).show();


    }

    @Override
    public void showResults(String origin, String translation, double time) {
        TextView textView = view.findViewById(R.id.translatedText);

        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.window_textBlackBg, false)) {
            textView.setBackgroundResource(R.color.blackBg);
        } else {
            textView.setBackgroundResource(0);
        }

        if (origin.equals("yuka error")) {
            textView.setText(translation);
            textView.setTextColor(activityWeakReference.get().getResources().getColor(R.color.colorError, null));
            return;
        }
        if (origin.equals("before response")) {
            textView.setText(translation);
            textView.setTextColor(activityWeakReference.get().getResources().getColor(R.color.text_color_DarkBg, null));
            return;
        }

        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.window_originalText, false)) {
            textView.setText("原文： ");
            textView.append(origin);
            textView.append("\r\n译文： ");
            textView.append(translation);
        } else {
            textView.setText(translation);
        }
    }

    @Override
    public void dismiss() {
        floatWindowManager.stop_ScreenShotTrans_normal(true);
        floatWindowManager.stop_ScreenShotTrans_normal(false);
        super.dismiss();
    }

    private void changeClass(boolean check) {
        if ((Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_continuousMode, false)) {
            //持续模式
            isContinue = true;
            ((ImageView) view.findViewById(R.id.sw_pap)).setImageResource(R.drawable.floatwindow_start);
            view.findViewById(R.id.sw_addwindows).setVisibility(View.GONE);
        } else {
            //普通模式
            isContinue = false;
            ((ImageView) view.findViewById(R.id.sw_pap)).setImageResource(R.drawable.floatwindow_translate);
            view.findViewById(R.id.sw_addwindows).setVisibility(View.VISIBLE);
        }
        if (check) {
            showInitGuide();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_close:
                dismiss();
                break;
            case R.id.sw_pap:
                if (isContinue) {
                    if (!isPlay) {
                        hide();
                        floatWindowManager.start_ScreenShotTrans_normal(true, index);
                        isPlay = true;
                        ((ImageView) v).setImageResource(R.drawable.floatwindow_stop);
                    } else {
                        floatWindowManager.stop_ScreenShotTrans_normal(true);
                        isPlay = false;
                        ((ImageView) v).setImageResource(R.drawable.floatwindow_start);
                    }
                } else {
                    isPlay = false;
                    hide();
                    floatWindowManager.start_ScreenShotTrans_normal(false, index);
                }
                break;
            case R.id.sw_addwindows:
                try {
                    if (isContinue) {
                        floatWindowManager.add_FloatWindow("SWN_C");
                    } else {
                        floatWindowManager.add_FloatWindow("SWN_S");
                    }
                } catch (FloatWindowManagerException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void showInitGuide() {
        if (!isGuiding) {
            SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
            isGuiding = true;
            String str = "";
            if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_SelectWindow_N_1, true) && !isContinue) {
                str = "SWN_S";
            } else if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_SelectWindow_N_2, true) && isContinue) {
                str = "SWN_C";
            }
            if (!str.equals("")) {
                Intent intent = new Intent(activityWeakReference.get(), CurtainActivity.class);
                intent.putExtra(CurtainActivity.name, str);
                intent.putExtra(CurtainActivity.index, index);
                activityWeakReference.get().startActivity(intent);
            }
        }
    }
}
