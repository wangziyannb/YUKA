package com.wzy.yuka.yuka.floatwindow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.CurtainActivity;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.ui.view.SubtitleFlowView;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Ziyan on 2020/5/23.
 */
public class SubtitleWindow extends FloatWindow implements View.OnClickListener {
    private int mode = 1;
    private boolean isPlay = false;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    public SubtitleWindow(Activity activity, int index, String tag) throws FloatWindowManagerException {
        super(activity, index, tag);
        EasyFloat.with(activityWeakReference.get())
                .setTag(tag)
                .setLayout(R.layout.floatwindow_subtitle, (view1) -> {
                    setView(view1);


                    ConstraintLayout rl = view1.findViewById(R.id.floatwindow_subtitle);

                    //改变悬浮框透明度
                    GradientDrawable drawable = (GradientDrawable) rl.getBackground();
                    int a1 = (int) SharedPreferencesUtil.getInstance().getParam("settings_window_opacityBg", 50);
                    int alpha = (int) Math.round(a1 * 2.55);
                    String alpha_hex = Integer.toHexString(alpha).toUpperCase();
                    if (alpha_hex.length() == 1) {
                        alpha_hex = "0" + alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#" + alpha_hex + "000000"));

                    //改变悬浮框宽度
                    int[] size = GetParams.Screen();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl.getLayoutParams();
                    params.width = (int) (size[0] * 0.7);
                    rl.setLayoutParams(params);

                    view1.findViewById(R.id.sbw_close).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_pap).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_change).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_hide).setOnClickListener(this);
                })
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(GetParams.Screen()[0] / 2 - SizeUtil.dp2px(activityWeakReference.get(), 300) / 2, GetParams.Screen()[1] / 2 - SizeUtil.dp2px(activityWeakReference.get(), 100) / 2)
                .setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
                            showInitGuide();
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
                        if (view.findViewById(R.id.sbw_close).getVisibility() == View.GONE) {
                            view.findViewById(R.id.sbw_close).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.sbw_pap).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.sbw_change).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.sbw_hide).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {

                    }

                    @Override
                    public void dragEnd(@NotNull View view) {

                    }
                })
                .show();

    }

    @Override
    public void dismiss() {
        floatWindowManager.stop_RecordingTrans();
        super.dismiss();
    }

    @Override
    public void showResults(String origin, String translation, double time) {
        SubtitleFlowView ori = view.findViewById(R.id.sbw_originalText);
        SubtitleFlowView result = view.findViewById(R.id.sbw_translatedText);

        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.window_textBlackBg, false)) {
            ori.setBackgroundResource(R.color.blackBg);
            result.setBackgroundResource(R.color.blackBg);
        } else {
            ori.setBackgroundResource(0);
            result.setBackgroundResource(0);
        }

        ori.setText(origin);
        result.setText(translation);
    }

    private View getView() {
        return EasyFloat.getAppFloatView(tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sbw_close:
                dismiss();
                break;
            case R.id.sbw_pap:
                if (!isPlay) {
                    floatWindowManager.start_RecordingTrans();
                    isPlay = true;
                    ((ImageView) v).setImageResource(R.drawable.floatwindow_stop);
                } else {
                    floatWindowManager.stop_RecordingTrans();
                    isPlay = false;
                    ((ImageView) v).setImageResource(R.drawable.floatwindow_start);
                }
                break;
            case R.id.sbw_change:
                ConstraintSet constraintSet = new ConstraintSet();
                View view = getView();
                ConstraintLayout constraintLayout = view.findViewById(R.id.floatwindow_subtitle);
                constraintSet.clone(constraintLayout);
                TransitionManager.beginDelayedTransition(constraintLayout);
                switch (mode) {
                    case 0:
                        constraintSet.setVisibility(R.id.sbw_originalText, ConstraintSet.GONE);
                        constraintSet.setVisibility(R.id.sbw_translatedText, ConstraintSet.VISIBLE);
                        constraintSet.centerVertically(R.id.sbw_translatedText, ConstraintSet.PARENT_ID);
                        constraintSet.applyTo(constraintLayout);
                        mode += 1;
                        break;
                    case 1:
                        constraintSet.setVisibility(R.id.sbw_originalText, ConstraintSet.VISIBLE);
                        constraintSet.setVisibility(R.id.sbw_translatedText, ConstraintSet.GONE);
                        constraintSet.centerVertically(R.id.sbw_originalText, ConstraintSet.PARENT_ID);
                        constraintSet.applyTo(constraintLayout);
                        mode += 1;
                        break;
                    case 2:
                        constraintSet.setVisibility(R.id.sbw_originalText, ConstraintSet.VISIBLE);
                        constraintSet.setVisibility(R.id.sbw_translatedText, ConstraintSet.VISIBLE);
                        constraintSet.setVerticalBias(R.id.sbw_originalText, 0.324f);
                        constraintSet.setVerticalBias(R.id.sbw_translatedText, 0.676f);
                        constraintSet.applyTo(constraintLayout);
                        mode = 0;
                        break;
                }
                break;
            case R.id.sbw_hide:
                View view1 = getView();
                view1.findViewById(R.id.sbw_close).setVisibility(View.GONE);
                view1.findViewById(R.id.sbw_pap).setVisibility(View.GONE);
                view1.findViewById(R.id.sbw_change).setVisibility(View.GONE);
                view1.findViewById(R.id.sbw_hide).setVisibility(View.GONE);
                break;
        }
    }

    private void showInitGuide() {

        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_SubtitleWindow, true)) {
            Intent intent = new Intent(activityWeakReference.get(), CurtainActivity.class);
            intent.putExtra(CurtainActivity.name, "SBW");
            intent.putExtra(CurtainActivity.index, index);
            activityWeakReference.get().startActivity(intent);
        }
    }
}

