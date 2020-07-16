package com.wzy.yuka.tools.params;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class GetParams {
    private static WeakReference<Context> context;

    public static void init(Context application) {
        context = new WeakReference<>(application);
    }

    public static int[] Screen() {
        WindowManager windowManager = (WindowManager) context.get().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int[] size = new int[3];
        size[0] = point.x;
        size[1] = point.y;
        Resources resources = context.get().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        size[2] = resources.getDimensionPixelSize(resourceId);
        return size;
    }

    public static int[] ScreenDp() {
        int[] params = Screen();
        int height = SizeUtil.px2dp(context.get(), params[0]);
        int width = SizeUtil.px2dp(context.get(), params[1]);
        return params;
    }

    public static HashMap<String, String> Yuka(String mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.get());
        Resources resources = context.get().getResources();
        HashMap<String, String> params = new HashMap<>();

        //初始化设置
        params.put("mode", resources.getStringArray(R.array.mode)[1]);
        params.put("model", resources.getStringArray(R.array.detect_modelset)[0]);
        params.put("precise", resources.getString(R.string.False));
        params.put("vertical", resources.getString(R.string.False));
        params.put("punctuation", resources.getString(R.string.False));
        params.put("reverse", resources.getString(R.string.False));
        params.put("translator", resources.getStringArray(R.array.translator)[0]);
        params.put("SBCS", resources.getString(R.string.False));

        switch (mode) {
            case "NONE":
            case "SWN_S":
            case "SWN_C":
                switch (preferences.getString("settings_detect_model", resources.getStringArray(R.array.detect_modelset)[0])) {
                    case "google":
                        break;
                    case "baidu":
                        params.put("model", resources.getStringArray(R.array.detect_modelset)[1]);
                        if (preferences.getBoolean("settings_baidu_precise", false)) {
                            //高精度模式
                            params.put("precise", resources.getString(R.string.True));
                        }
                        if (preferences.getBoolean("settings_baidu_punctuation", false)) {
                            //竖排标点优化
                            params.put("punctuation", resources.getString(R.string.True));
                        }
                        if (preferences.getBoolean("settings_baidu_reverse", false)) {
                            //阅读顺序逆转
                            params.put("reverse", resources.getString(R.string.True));
                        }
                        break;
                }
                break;
            case "SWA":
                params.put("mode", resources.getStringArray(R.array.mode)[3]);
                if (preferences.getBoolean("settings_baidu_punctuation", false)) {
                    //标点优化
                    params.put("punctuation", resources.getString(R.string.True));
                }
                if (preferences.getBoolean("settings_auto_vertical", false)) {
                    //横竖排文字
                    params.put("vertical", resources.getString(R.string.True));
                }
                if (preferences.getBoolean("settings_baidu_reverse", false)) {
                    //阅读顺序逆转
                    params.put("reverse", resources.getString(R.string.True));
                }
                break;
            case "SBW":

                break;
        }
        switch (preferences.getString("settings_trans_translator", resources.getStringArray(R.array.translator)[0])) {
            case "google":
                break;
            case "baidu":
                params.put("translator", resources.getStringArray(R.array.translator)[1]);
                if (preferences.getBoolean("settings_trans_SBCS", false)) {
                    //日韩文字启用全角
                    params.put("SBCS", resources.getString(R.string.True));
                }
                break;
            case "youdao":
                params.put("translator", resources.getStringArray(R.array.translator)[2]);
                break;
        }
        return params;

    }
}
