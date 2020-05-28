package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.R;
import com.wzy.yuka.core.audio.AudioService;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/5/23.
 */
public class SubtitleWindow implements View.OnClickListener {
    private WeakReference<Activity> activityWeakReference;
    private String tag;
    private int mode = 1;
    private boolean nextPlay = false;

    SubtitleWindow(Activity activity, String tag) {
        activityWeakReference = new WeakReference<>(activity);
        this.tag = tag;
        EasyFloat.with(activityWeakReference.get())
                .setTag(tag)
                .setLayout(R.layout.floatwindow_subtitle, (view1) -> {
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

                    view1.findViewById(R.id.sbw_close).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_pap).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_change).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_hide).setOnClickListener(this);
                })
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(100, 100)
                .setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
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

    void dismiss() {
        FloatWindowManager.dismissSubtitleWindow();
        EasyFloat.dismissAppFloat(tag);
    }

    void showResults(String origin, String translation) {
        View view = EasyFloat.getAppFloatView(tag);

        TextView ori = view.findViewById(R.id.sbw_originalText);
        TextView result = view.findViewById(R.id.sbw_translatedText);

        if (GetParams.SelectWindow()[0]) {
            ori.setBackgroundResource(R.color.blackBg);
        } else {
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
        Intent intent = new Intent(activityWeakReference.get(), AudioService.class);
        switch (v.getId()) {
            case R.id.sbw_close:
                activityWeakReference.get().stopService(intent);
                dismiss();
                break;
            case R.id.sbw_pap:
                if (!nextPlay) {
                    FloatWindowManager.startVoiceTrans(activityWeakReference.get());
                    nextPlay = true;
                    ((ImageView) v).setImageResource(R.drawable.floatwindow_stop);
                } else {
                    activityWeakReference.get().stopService(intent);
                    nextPlay = false;
                    ((ImageView) v).setImageResource(R.drawable.floatwindow_translate);
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
}
