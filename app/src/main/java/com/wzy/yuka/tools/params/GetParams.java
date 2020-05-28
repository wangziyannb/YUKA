package com.wzy.yuka.tools.params;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;

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
        int[] size = new int[2];
        size[0] = point.x;
        size[1] = point.y;
        return size;
    }

    public static String[] Yuka() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //0:mode 1:model 2:translator 3:SBCS 4:precise 5:vertical 6:reverse
        String[] params = new String[7];
        Resources resources = context.getResources();
        //默认为 ocr - google -google - 0 - 0 - 0 - 0
        params[0] = resources.getStringArray(R.array.mode)[0];
        params[1] = resources.getStringArray(R.array.detect_modelset)[0];
        params[2] = resources.getStringArray(R.array.translator)[0];
        params[3] = resources.getString(R.string.False);
        params[4] = resources.getString(R.string.False);
        params[5] = resources.getString(R.string.False);
        params[6] = resources.getString(R.string.False);

        switch (preferences.getString("settings_detect_model", resources.getStringArray(R.array.detect_modelset)[0])) {
            case "google":
                break;
            case "baidu":
                params[1] = resources.getStringArray(R.array.detect_modelset)[1];
                if (preferences.getBoolean("settings_baidu_precise", false)) {
                    //高精度模式
                    params[4] = resources.getString(R.string.True);
                }
                if (preferences.getBoolean("settings_baidu_vertical", false)) {
                    //竖排标点优化
                    params[5] = resources.getString(R.string.True);
                }
                if (preferences.getBoolean("settings_baidu_reverse", false)) {
                    //阅读顺序逆转
                    params[6] = resources.getString(R.string.True);
                }
                break;
        }

        if (preferences.getBoolean("settings_trans_switch", true)) {
            //启用翻译
            params[0] = resources.getStringArray(R.array.mode)[1];
            switch (preferences.getString("settings_trans_translator", resources.getStringArray(R.array.translator)[0])) {
                case "google":
                    break;
                case "baidu":
                    params[2] = resources.getStringArray(R.array.translator)[1];
                    if (preferences.getBoolean("settings_trans_SBCS", false)) {
                        //日韩文字启用全角
                        params[3] = resources.getString(R.string.True);
                    }
                    break;
                case "youdao":
                    params[2] = resources.getStringArray(R.array.translator)[2];
                    break;
            }
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
        if(preferences.getBoolean("settings_fastMode", true)){
            params[0] = 1;
        }
        if(preferences.getBoolean("settings_continuousMode", false)){
            params[1] = 1;
        }
        params[2] = preferences.getInt("settings_continuousMode_interval", 6);
        return params;
    }
}
