package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.R;
import com.wzy.yuka.core.screenshot.ScreenShotService_Continue;
import com.wzy.yuka.tools.params.GetParams;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Created by Ziyan on 2020/4/29.
 */
public class SelectWindow implements View.OnClickListener {
    int[] location;
    private int index;
    private String tag;
    private Activity activity;

    SelectWindow(Activity activity, String tag, int index) {
        location = new int[4];
        this.activity = activity;
        this.tag = tag;
        this.index = index;
        EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.select_window, view1 -> {
                    if (GetParams.AdvanceSettings()[1] == 1) {
                        view1.findViewById(R.id.sw_addwindows).setVisibility(View.GONE);
                        view1.findViewById(R.id.sw_stopContinue).setVisibility(View.VISIBLE);
                    }
                    RelativeLayout rl = view1.findViewById(R.id.select_window_layout);
                    //改变悬浮框透明度
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    GradientDrawable drawable = (GradientDrawable) rl.getBackground();
                    int alpha=(int)Math.round(preferences.getInt("settings_window_opacityBg", 50)*2.55);
                    String alpha_hex=Integer.toHexString(alpha).toUpperCase();
                    if(alpha_hex.length()==1){
                        alpha_hex="0"+alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#"+alpha_hex+"000000"));
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rl.getLayoutParams();
                    ScaleImageView si = view1.findViewById(R.id.sw_scale);
                    si.setOnScaledListener((x, y, event) -> {
                        TextView textView = view1.findViewById(R.id.translatedText);
                        if (textView.getText().equals("选取目标位置后点识别" +
                                "\n右下角可改变框体大小" +
                                "\n每次调整大小后都必须位移选词窗")) {
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
                .setLocation(100, 100)
                .setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
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
                            view.findViewById(R.id.sw_stopContinue).setVisibility(View.VISIBLE);
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

    private void setLocation() {
        View view = EasyFloat.getAppFloatView(tag);
        view.getLocationOnScreen(location);
        location[2] = location[0] + view.getRight();
        location[3] = location[1] + view.getBottom();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    void showResults(String origin, String translation, double time){
        View view = EasyFloat.getAppFloatView(tag);
        TextView textView=view.findViewById(R.id.translatedText);
        boolean[] params = GetParams.SelectWindow();
        if(params[0]){
            textView.setBackgroundResource(R.color.blackBg);
        }else{
            textView.setBackgroundResource(0);
        }
        if(origin.equals("yuka error")){
            textView.setText(translation);
            textView.setTextColor(activity.getResources().getColor(R.color.colorError,null));
            return;
        }
        if(origin.equals("before response")){
            textView.setText(translation);
            textView.setTextColor(activity.getResources().getColor(R.color.text_color_DarkBg,null));
            return;
        }
        if(params[1]){
            textView.setText("原文： ");
            textView.append(origin);
            textView.append("\n译文： ");
            textView.append(translation);
        }else{
            textView.setText(translation);
        }
        if(params[2]){
            DecimalFormat df = new DecimalFormat("#0.000");
            Toast.makeText(activity,"耗时"+df.format(time)+"秒",Toast.LENGTH_SHORT).show();
        }
    }

    void dismiss() {
        FloatWindowManager.dismissFloatWindow(index);
        EasyFloat.dismissAppFloat(tag);
    }

    void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    void show() {
        EasyFloat.showAppFloat(tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_close:
                dismiss();
                break;
            case R.id.sw_translate:
                hide();
                FloatWindowManager.startScreenShot(activity, index);
                break;
            case R.id.sw_addwindows:
                FloatWindowManager.addSelectWindow(activity);
                break;
            case R.id.sw_stopContinue:
                ScreenShotService_Continue.stopScreenshot();
                break;
        }
    }
}
