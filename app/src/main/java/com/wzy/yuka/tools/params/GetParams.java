package com.wzy.yuka.tools.params;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;

import java.util.HashMap;

public class GetParams {
    //不用关注内存泄露，持有的是application的context
    private static Context context;

    public static void init(Context application) {
        context = application;
    }

    public static int[] Screen() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int[] size = new int[3];
        size[0] = point.x;
        size[1] = point.y;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        size[2] = resources.getDimensionPixelSize(resourceId);
        return size;
    }


    public static HashMap<String, String> Yuka() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();
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

        if (preferences.getBoolean("settings_auto_switch", false)) {
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
        } else {
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

    public static boolean[] FloatBall() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean[] params = new boolean[5];
        params[0] = preferences.getBoolean("settings_ball_autoHide", true);
        params[1] = preferences.getBoolean("settings_ball_autoClose", true);
        params[2] = preferences.getBoolean("settings_ball_openLock", true);
        params[3] = preferences.getBoolean("settings_ball_safeMode", true);
        params[4] = preferences.getBoolean("settings_ball_fluidMode", false);

        return params;
    }

    public static boolean[] SelectWindow() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean[] params = new boolean[3];
        params[0] = preferences.getBoolean("settings_window_textBlackBg", true);
        //params[1] = preferences.getInt("settings_window_opacityBg", 50);
        params[1] = preferences.getBoolean("settings_window_originalText", false);
        params[2] = preferences.getBoolean("settings_window_showTime", false);
        return params;
    }

    public static int[] AdvanceSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int[] params = new int[3];
        if (preferences.getBoolean("settings_fastMode", true)) {
            params[0] = 1;
        }
        if (preferences.getBoolean("settings_continuousMode", false)) {
            params[1] = 1;
        }
        params[2] = preferences.getInt("settings_continuousMode_interval", 6);
        return params;
    }
}
