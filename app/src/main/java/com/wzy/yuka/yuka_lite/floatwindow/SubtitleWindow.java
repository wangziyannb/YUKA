package com.wzy.yuka.yuka_lite.floatwindow;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Ziyan on 2020/5/23.
 */
public class SubtitleWindow extends FloatWindow implements View.OnClickListener {
    private int mode = 1;
    private boolean isPlay = false;
    private final SharedPreferencesUtil spUtil;
    private final YukaFloatWindowManager mFloatWindowManager;

    public SubtitleWindow(int index, String tag, YukaFloatWindowManager manager) throws FloatWindowManagerException {
        super(index, tag, manager);
        mFloatWindowManager = manager;
        spUtil = SharedPreferencesUtil.getInstance();
        EasyFloat.with(applicationWeakReference.get())
                .setTag(tag)
                .setLayout(R.layout.floatwindow_subtitle, (view1) -> {
                    setView(view1);
                    ConstraintLayout rl = view1.findViewById(R.id.floatwindow_subtitle);
                    //改变悬浮框透明度
                    GradientDrawable drawable = (GradientDrawable) rl.getBackground();
                    int alpha = (int) Math.round((int) (spUtil.getParam(SharedPreferenceCollection.window_opacityBg, 50)) * 2.55);
                    String alpha_hex = Integer.toHexString(alpha).toUpperCase();
                    if (alpha_hex.length() == 1) {
                        alpha_hex = "0" + alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#" + alpha_hex + "000000"));
                    changeWidth();

                    view1.findViewById(R.id.sbw_close).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_pap).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_change).setOnClickListener(this);
                    view1.findViewById(R.id.sbw_hide).setOnClickListener(this);
                })
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(SizeUtil.Screen(applicationWeakReference.get())[0] / 2 - SizeUtil.dp2px(applicationWeakReference.get(), 300) / 2, SizeUtil.Screen(applicationWeakReference.get())[1] / 2 - SizeUtil.dp2px(applicationWeakReference.get(), 100) / 2)
                .setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {

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

    private void changeWidth() {
        //改变悬浮框宽度
        ConstraintLayout rl = mFloatWindowView.findViewById(R.id.floatwindow_subtitle);
        int[] size = SizeUtil.Screen(applicationWeakReference.get());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl.getLayoutParams();
        params.width = (int) (size[0] * 0.7);
        rl.setLayoutParams(params);
    }

    @Override
    public void dismiss() {
        mFloatWindowManager.stop_RecordingTrans();
        super.dismiss();
    }

    @Override
    public void showResults(String origin, String translation, double time) {
        SubtitleFlowView ori = mFloatWindowView.findViewById(R.id.sbw_originalText);
        SubtitleFlowView result = mFloatWindowView.findViewById(R.id.sbw_translatedText);

        if ((boolean) spUtil.getParam(SharedPreferenceCollection.window_textBlackBg, false)) {
            ori.setBackgroundResource(R.color.blackBg);
            result.setBackgroundResource(R.color.blackBg);
        } else {
            ori.setBackgroundResource(0);
            result.setBackgroundResource(0);
        }

        ori.setText(origin);
        result.setText(translation);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sbw_close:
                dismiss();
                break;
            case R.id.sbw_pap:
                if (!isPlay) {
                    mFloatWindowManager.start_RecordingTrans();
                    isPlay = true;
                    ((ImageView) v).setImageResource(R.drawable.floatwindow_stop);
                } else {
                    mFloatWindowManager.stop_RecordingTrans();
                    isPlay = false;
                    ((ImageView) v).setImageResource(R.drawable.floatwindow_start);
                }
                break;
            case R.id.sbw_change:
                ConstraintSet constraintSet = new ConstraintSet();
                ConstraintLayout constraintLayout = mFloatWindowView.findViewById(R.id.floatwindow_subtitle);
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
                mFloatWindowView.findViewById(R.id.sbw_close).setVisibility(View.GONE);
                mFloatWindowView.findViewById(R.id.sbw_pap).setVisibility(View.GONE);
                mFloatWindowView.findViewById(R.id.sbw_change).setVisibility(View.GONE);
                mFloatWindowView.findViewById(R.id.sbw_hide).setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //改变悬浮框宽度
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeWidth();
        }
    }
}

