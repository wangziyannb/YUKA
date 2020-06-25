package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.qw.curtain.lib.Curtain;
import com.qw.curtain.lib.IGuide;
import com.wzy.yuka.R;
import com.wzy.yuka.core.screenshot.ScreenShotService_Continue;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Created by Ziyan on 2020/4/29.
 */
public class SelectWindow_Normal extends FloatWindows {
    SelectWindow_Normal(Activity activity, String tag, int index) {
        super(activity, tag, index);
        EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.floatwindow_main, view1 -> {
                    setView(view1);
                    if (GetParams.AdvanceSettings()[1] == 1) {
                        view1.findViewById(R.id.sw_addwindows).setVisibility(View.GONE);
                    }
                    RelativeLayout rl = view1.findViewById(R.id.select_window_layout);
                    //改变悬浮框透明度
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    GradientDrawable drawable = (GradientDrawable) rl.getBackground();
                    int alpha = (int) Math.round(preferences.getInt("settings_window_opacityBg", 50) * 2.55);
                    String alpha_hex = Integer.toHexString(alpha).toUpperCase();
                    if (alpha_hex.length() == 1) {
                        alpha_hex = "0" + alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#" + alpha_hex + "000000"));
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl.getLayoutParams();
                    ScaleImageView si = view1.findViewById(R.id.sw_scale);
                    si.setOnScaledListener((x, y, event) -> {
                        TextView textView = view1.findViewById(R.id.translatedText);
                        if (textView.getText().equals("选取目标位置后点识别" +
                                "\n右下角可改变框体大小")) {
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
                    view1.findViewById(R.id.sw_translate).setOnClickListener(this);
                    view1.findViewById(R.id.sw_addwindows).setOnClickListener(this);
                    view1.findViewById(R.id.sw_stopContinue).setOnClickListener(this);
                })
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(GetParams.Screen()[0] / 2 - SizeUtil.dp2px(activityWeakReference.get(), 250) / 2,
                        (int) ((GetParams.Screen()[1] + 1.5 * GetParams.Screen()[2]) / 2 - SizeUtil.dp2px(activityWeakReference.get(), 120) / 2))
                .setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
                            showInitGuide();
                            setLocation();
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
                        if (GetParams.AdvanceSettings()[1] == 1) {
                            view.findViewById(R.id.sw_addwindows).setVisibility(View.GONE);
                        } else {
                            view.findViewById(R.id.sw_addwindows).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {
                        setLocation();
                    }

                    @Override
                    public void dragEnd(@NotNull View view) {
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                        setLocation();
                    }
                })
                .show();

    }

    @Override
    void showResults(String origin, String translation, double time) {
        TextView textView = view.findViewById(R.id.translatedText);
        boolean[] params = GetParams.SelectWindow();
        if (params[0]) {
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
        if (params[1]) {
            textView.setText("原文： ");
            textView.append(origin);
            textView.append("\n译文： ");
            textView.append(translation);
        } else {
            textView.setText(translation);
        }
        if (params[2]) {
            DecimalFormat df = new DecimalFormat("#0.000");
            Toast.makeText(activityWeakReference.get(), "耗时" + df.format(time) + "秒", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_close:
                dismiss();
                break;
            case R.id.sw_translate:
                hide();
                FloatWindowManager.startScreenShot(activityWeakReference.get(), index);
                if (GetParams.AdvanceSettings()[1] == 1) {
                    v.setVisibility(View.GONE);
                    view.findViewById(R.id.sw_stopContinue).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.sw_addwindows:
                FloatWindowManager.addSelectWindow(activityWeakReference.get());
                break;
            case R.id.sw_stopContinue:
                ScreenShotService_Continue.stopScreenshot();
                v.setVisibility(View.GONE);
                view.findViewById(R.id.sw_translate).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showInitGuide() {
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferencesUtil.FIRST_INVOKE_SelectWindow_N, true)) {
            GuideManager guideManager = new GuideManager((FragmentActivity) activityWeakReference.get());
            guideManager.weaveCurtain((canvas, paint, info) -> {
                    }, 0, R.layout.guide_interpret,
                    view.findViewById(R.id.sw_close), view.findViewById(R.id.sw_scale),
                    view.findViewById(R.id.sw_addwindows), view.findViewById(R.id.sw_translate))
                    .setCallBack(new Curtain.CallBack() {
                        @Override
                        public void onShow(IGuide iGuide) {
                            ConstraintLayout layout = iGuide.findViewByIdInTopView(R.id.guide_interpret_layout);
                            ImageView img = layout.findViewById(R.id.guide_interpret_img);
                            img.setImageResource(R.drawable.guide_floatwindow_normal);
                            ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();

                            params_img.width = SizeUtil.dp2px(activityWeakReference.get(), 335);
                            params_img.height = SizeUtil.dp2px(activityWeakReference.get(), 242);

                            params_img.topMargin = SizeUtil.dp2px(activityWeakReference.get(), 10);
                            params_img.rightMargin = SizeUtil.dp2px(activityWeakReference.get(), 10);
                            img.setLayoutParams(params_img);
                        }

                        @Override
                        public void onDismiss(IGuide iGuide) {

                        }
                    })
                    .show();
        }
    }
}

//    private void showInitGuide() {
//        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
//        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferencesUtil.FIRST_INVOKE_SelectWindow_N, true)) {
//
//            GuideManager guideManager = new GuideManager((FragmentActivity) activityWeakReference.get());
//            CurtainFlow cf = new CurtainFlow.Builder()
//                    .with(21, guideManager.weaveCurtain(view, new RoundShape(12), 32, R.layout.guide))
//                    .with(22, guideManager.weaveCurtain(view.findViewById(R.id.sw_close), new CircleShape(), 32, R.layout.guide))
//                    .with(23, guideManager.weaveCurtain(view.findViewById(R.id.sw_addwindows), new CircleShape(), 32, R.layout.guide))
//                    .with(24, guideManager.weaveCurtain(view.findViewById(R.id.sw_translate), new CircleShape(), 32, R.layout.guide))
//                    .create();
//            cf.start(new CurtainFlow.CallBack() {
//                @Override
//                public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
//                    switch (currentId) {
//                        case 21:
//                            curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
//                                curtainFlow.push();
//                            });
//                            break;
//                        case 22:
//                            curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
//
//                                if (view.findViewById(R.id.sw_addwindows).getVisibility() == View.GONE) {
//                                    curtainFlow.toCurtainById(24);
//                                } else {
//                                    curtainFlow.push();
//                                }
//                            });
//                            break;
//                        case 23:
//                            curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
//                                curtainFlow.push();
//                            });
//                            break;
//                        case 24:
//                            curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
//                                curtainFlow.finish();
//                            });
//                            break;
//
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                    Toast.makeText(activityWeakReference.get(), "普通悬浮窗引导完成", Toast.LENGTH_SHORT).show();
//                    sharedPreferencesUtil.saveParam(SharedPreferencesUtil.FIRST_INVOKE_SelectWindow_N, false);
//                }
//            });
//
//        }
//    }
//}
