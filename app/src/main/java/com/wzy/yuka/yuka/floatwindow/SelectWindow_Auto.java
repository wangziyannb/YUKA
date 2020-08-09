package com.wzy.yuka.yuka.floatwindow;


import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.CurtainActivity;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.LengthUtil;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.ui.view.ScaleImageView;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SelectWindow_Auto extends FloatWindow {
    private String[] Tags = new String[1];
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    public SelectWindow_Auto(Application application, int index, String tag) throws FloatWindowManagerException {
        super(application, index, tag);
        EasyFloat.with(applicationWeakReference.get())
                .setTag(tag)
                .setLayout(R.layout.floatwindow_main, view1 -> {
                    setView(view1);
                    view1.findViewById(R.id.sw_addwindows).setVisibility(View.GONE);
                    RelativeLayout rl = view1.findViewById(R.id.select_window_layout);
                    //改变悬浮框透明度
                    GradientDrawable drawable = (GradientDrawable) rl.getBackground();
                    int alpha = (int) Math.round((int) (SharedPreferencesUtil.getInstance().getParam(SharedPreferenceCollection.window_opacityBg, 50)) * 2.55);
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
                })
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(GetParams.Screen()[0] / 2 - SizeUtil.dp2px(applicationWeakReference.get(), 250) / 2,
                        (int) ((GetParams.Screen()[1] + 1.5 * GetParams.Screen()[2]) / 2 - SizeUtil.dp2px(applicationWeakReference.get(), 120) / 2)).setAppFloatAnimator(null)
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
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
    public void showResults(String origin, String translation, double time) {
        if (origin.equals("before response")) {
            return;
        }
        hide();
        try {
            JSONObject jsono = new JSONObject(translation);
            Log.e("TAG", translation);
            JSONArray x = jsono.getJSONArray("values");
            double total_time = jsono.getDouble("total_time");
            for (int i = 0; i < x.length(); i++) {
                JSONObject c = (JSONObject) x.get(i);
                JSONObject src = c.getJSONObject("src");
                int index = c.getInt("index");
                if (index >= Tags.length) {
                    Tags = LengthUtil.appendIndex(Tags);
                }
                String words = src.getString("words");
                String translationx = src.getString("translation");
                JSONArray location = src.getJSONArray("location");
                if (src.getString("location").equals("[0,0,0,0]")) {
                    Toast.makeText(applicationWeakReference.get(), "Yuka的ocr识别余额不足，请联系开发者QQ1269586767让他购买", Toast.LENGTH_SHORT).show();
                }
                int[] location_json = new int[4];
                for (int j = 0; j < location.length(); j++) {
                    location_json[j] = location.getInt(j);
                }
                initLittleWindows(words, translationx, location_json, index);
            }
            Toast.makeText(applicationWeakReference.get(), "使用时间：" + total_time, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            try {
                JSONObject jsono = new JSONObject(translation);
                String origin_json = jsono.getString("origin");
                if (origin_json.equals("602")) {
                    Toast.makeText(applicationWeakReference.get(), "剩余自动识别次数不足", Toast.LENGTH_SHORT).show();
                    shows();
                }
            } catch (JSONException e1) {
                e.printStackTrace();
            }

        }
    }


    private void initLittleWindows(String origin, String translation, int[] locations, int index) {
        int statusBar;
        if (floatWindowManager.isFullScreen()) {
            statusBar = 0;
        } else {
            statusBar = GetParams.Screen()[2];
        }
        int offset_L = location[0];
        int offset_T = location[1];
        locations[0] += offset_L;
        locations[1] += offset_T - statusBar;
        locations[2] += offset_L;
        locations[3] += offset_T - statusBar;
        String thisTag = "little" + index;
        Tags[index] = thisTag;
        EasyFloat.with(applicationWeakReference.get())
                .setTag(thisTag)
                .setDragEnable(true)
                .setLayout(R.layout.floatwindow_auto, v -> {
                    ConstraintLayout constraintlayoutview = v.findViewById(R.id.floatwindow_auto);
                    ViewGroup.LayoutParams layoutParams1 = constraintlayoutview.getLayoutParams();
                    layoutParams1.width = locations[2] - locations[0];
                    layoutParams1.height = locations[3] - locations[1];
                    constraintlayoutview.setLayoutParams(layoutParams1);
                    GradientDrawable drawable = (GradientDrawable) constraintlayoutview.getBackground();
                    int alpha = (int) Math.round((int) (SharedPreferencesUtil.getInstance().getParam("settings_window_opacityBg", 50)) * 2.55);
                    String alpha_hex = Integer.toHexString(alpha).toUpperCase();
                    if (alpha_hex.length() == 1) {
                        alpha_hex = "0" + alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#" + alpha_hex + "000000"));
                    AppCompatTextView textView = v.findViewById(R.id.little_textView);
                    AppCompatTextView textView2 = v.findViewById(R.id.little_origin);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        textView.setTextColor(applicationWeakReference.get().getResources().getColor(R.color.text_color_DarkBg));
                    } else {
                        textView.setTextColor(applicationWeakReference.get().getResources().getColor(R.color.text_color_DarkBg, null));
                    }


                    textView.setText(translation);
                    textView2.setText(origin);

                    if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.window_textBlackBg, false)) {
                        textView.setBackgroundResource(R.color.blackBg);
                    } else {
                        textView.setBackgroundResource(0);
                    }

                })
                .registerCallbacks(new OnFloatCallbacks() {
                    Handler handler = new Handler();
                    View view;
                    Runnable r = () -> {
                        if (view != null) {
                            String str_t = ((TextView) this.view.findViewById(R.id.little_textView)).getText() + "";
                            String str_o = ((TextView) this.view.findViewById(R.id.little_origin)).getText() + "";
                            if ((!TextUtils.isEmpty(str_t)) && (!TextUtils.isEmpty(str_o))) {
                                // 得到剪贴板管理器
                                ClipboardManager cm = (ClipboardManager) applicationWeakReference.get().getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                                ClipData clipData = ClipData.newPlainText("yuka", "原文：" + str_o + "\r\n" + "译文：" + str_t);
                                // 把数据集设置（复制）到剪贴板
                                cm.setPrimaryClip(clipData);
                                Toast.makeText(applicationWeakReference.get(), "已复制选择的原文及译文至剪切板", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    @Override
                    public void createdResult(boolean b, @Nullable String s, @Nullable View view) {
                        if (b) {
                            this.view = view;
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
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            handler.postDelayed(r, 500);
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                            handler.removeCallbacks(r);
                            handler.postDelayed(r, 500);
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            handler.removeCallbacks(r);
                        }
                    }

                    @Override
                    public void drag(@NotNull View view, @NotNull MotionEvent motionEvent) {

                    }

                    @Override
                    public void dragEnd(@NotNull View view) {

                    }
                })
                .setLocation(locations[0], locations[1])
                .setShowPattern(ShowPattern.ALL_TIME)
                .setAppFloatAnimator(null).show();
    }

    private void removeLittleWindows() {
        for (String tagx : Tags) {
            EasyFloat.dismissAppFloat(tagx);
        }
    }

    @Override
    public void dismiss() {
        removeLittleWindows();
        floatWindowManager.stop_ScreenShotTrans_auto();
        super.dismiss();
    }


    @Override
    public void reset() {
        removeLittleWindows();
        shows();
    }

    public void shows() {
        super.show();
    }

    @Override
    public void show() {
        Toast.makeText(applicationWeakReference.get(), "目标图片已发送，请等待...", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_close:
                dismiss();
                break;
            case R.id.sw_pap:
                hide();
                floatWindowManager.start_ScreenShotTrans_auto();
                break;
        }
    }

    @Override
    public void showInitGuide() {
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_SelectWindow_A, true)) {
            Intent intent = new Intent(applicationWeakReference.get(), CurtainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CurtainActivity.name, "SWA");
            intent.putExtra(CurtainActivity.index, index);
            applicationWeakReference.get().startActivity(intent);
        }
    }
}
