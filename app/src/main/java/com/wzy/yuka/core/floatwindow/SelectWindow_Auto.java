package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.LengthUtil;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SelectWindow_Auto extends FloatWindows {
    String[] Tags = new String[1];

    SelectWindow_Auto(Activity activity, String tag, int index) {
        super(activity, tag, index);
        EasyFloat.with(activity)
                .setTag(tag)
                .setLayout(R.layout.floatwindow_main, view1 -> {
                    setView(view1);
                    view1.findViewById(R.id.sw_addwindows).setVisibility(View.GONE);
                    view1.findViewById(R.id.sw_stopContinue).setVisibility(View.GONE);
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
                    Toast.makeText(activityWeakReference.get(), "Yuka的ocr识别余额不足，请联系开发者QQ1269586767让他购买", Toast.LENGTH_SHORT).show();
                }
                int[] location_json = new int[4];
                for (int j = 0; j < location.length(); j++) {
                    location_json[j] = location.getInt(j);
                }
                initLittleWindows(translationx, location_json, index);
            }
            Toast.makeText(activityWeakReference.get(), "使用时间：" + total_time, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initLittleWindows(String translation, int[] locations, int index) {
        boolean statusBar_offset = (boolean) SharedPreferencesUtil.getInstance().getParam("settings_auto_offset", false);
        int statusBar = 0;
        if (statusBar_offset) {
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
        EasyFloat.with(activityWeakReference.get())
                .setTag(thisTag)
                .setDragEnable(false)
                .setLayout(R.layout.floatwindow_auto, view -> {
                    ConstraintLayout constraintlayoutview = view.findViewById(R.id.floatwindow_auto);
                    ViewGroup.LayoutParams layoutParams1 = constraintlayoutview.getLayoutParams();
                    layoutParams1.width = locations[2] - locations[0];
                    layoutParams1.height = locations[3] - locations[1];
                    constraintlayoutview.setLayoutParams(layoutParams1);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activityWeakReference.get());
                    GradientDrawable drawable = (GradientDrawable) constraintlayoutview.getBackground();
                    int alpha = (int) Math.round(preferences.getInt("settings_window_opacityBg", 50) * 2.55);
                    String alpha_hex = Integer.toHexString(alpha).toUpperCase();
                    if (alpha_hex.length() == 1) {
                        alpha_hex = "0" + alpha_hex;
                    }
                    drawable.setColor(Color.parseColor("#" + alpha_hex + "000000"));
                    AppCompatTextView textView = view.findViewById(R.id.little_textView);
                    textView.setTextColor(activityWeakReference.get().getResources().getColor(R.color.text_color_DarkBg, null));
                    textView.setText(translation);
                    boolean[] params = GetParams.SelectWindow();
                    if (params[0]) {
                        textView.setBackgroundResource(R.color.blackBg);
                    } else {
                        textView.setBackgroundResource(0);
                    }
                })
                .setLocation(locations[0], locations[1])
                .setShowPattern(ShowPattern.ALL_TIME)
                .setAppFloatAnimator(null).show();
    }

    void removeLittleWindows() {
        for (String tagx : Tags) {
            EasyFloat.dismissAppFloat(tagx);
        }
    }

    @Override
    void dismiss() {
        removeLittleWindows();
        super.dismiss();
    }

    @Override
    void show() {
        Toast.makeText(activityWeakReference.get(), "目标图片已发送，请等待...", Toast.LENGTH_SHORT).show();
    }

    void showMain() {
        super.show();
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
                break;
        }
    }

    private void showInitGuide() {

    }
}
