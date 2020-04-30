package com.wzy.yuka.tools.floatwindow;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Ziyan on 2020/4/29.
 */
public class SelectWindow {
    int[] location;
    int index;
    private String tag;

    SelectWindow(Activity activity, String tag, int index) {
        location = new int[4];
        this.tag = tag;
        this.index = index;
        EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.select_window, view1 -> {
                    RelativeLayout rl = view1.findViewById(R.id.select_window_layout);
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
                        if (params.width < 0) {
                            params.width = 50;
                        }
                        if (params.height < 0) {
                            params.height = 50;
                        }
                        rl.setLayoutParams(params);
                        setLocation();
                        //locationA[0]左上角对左边框，locationA[1]左上角对上边框
                    });
                    view1.findViewById(R.id.sw_close).setOnClickListener(v1 -> dismiss());
                    view1.findViewById(R.id.sw_translate).setOnClickListener(v1 -> {
                        hide();
                        FloatWindowManager.startSS(activity, index);
                    });
                    view1.findViewById(R.id.sw_addwindows).setOnClickListener(v1 -> {
                        FloatWindowManager.addSelectWindow(activity);
                    });
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
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {

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

    TextView getTextView() {
        View view = EasyFloat.getAppFloatView(tag);
        return view.findViewById(R.id.translatedText);
    }

    void dismiss() {
        EasyFloat.dismissAppFloat(tag);
    }

    void hide() {
        EasyFloat.hideAppFloat(tag);
    }

    void show() {
        EasyFloat.showAppFloat(tag);
    }
}
