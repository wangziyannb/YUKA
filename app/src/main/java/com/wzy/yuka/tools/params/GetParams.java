package com.wzy.yuka.tools.params;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;

import java.lang.ref.WeakReference;

public class GetParams {
    static WeakReference<Context> mContext;

    public static void setContext(Context context) {
        mContext = new WeakReference<>(context);
    }

    public static int[] Screen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int[] size = new int[2];
        size[0] = point.x;
        size[1] = point.y;
        return size;
    }

    public static String[] Yuka(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //0:mode 1:model 2:translator 3:SBCS
        String[] params = new String[4];
        Resources resources = context.getResources();
        //默认为 ocr - google -google - 0
        params[0] = resources.getStringArray(R.array.mode)[0];
        params[1] = resources.getStringArray(R.array.model)[0];
        params[2] = resources.getStringArray(R.array.translator)[0];
        params[3] = resources.getString(R.string.False);

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
        } else {
            //未启用翻译
            switch (preferences.getString("settings_detect_language", resources.getStringArray(R.array.model)[0])) {
                case "google":
                    break;
                case "chn":
                    params[1] = resources.getStringArray(R.array.model)[1];
                    break;
                case "eng":
                    params[1] = resources.getStringArray(R.array.model)[2];
                    break;
            }
        }
        return params;
    }

    public static boolean[] FloatBall(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean[] params = new boolean[5];
        params[0] = preferences.getBoolean("settings_ball_autoHide", true);
        params[1] = preferences.getBoolean("settings_ball_autoClose", true);
        params[2] = preferences.getBoolean("settings_ball_openLock", true);
        params[3] = preferences.getBoolean("settings_ball_safeMode", true);
        params[4] = preferences.getBoolean("settings_ball_fluidMode", false);

        return params;
    }

    public static boolean[] SelectWindow(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean[] params = new boolean[3];
        params[0] = preferences.getBoolean("settings_window_textBlackBg", true);
        //params[1] = preferences.getInt("settings_window_opacityBg", 50);
        params[1] = preferences.getBoolean("settings_window_originalText", false);
        params[2] = preferences.getBoolean("settings_window_showTime", false);
        return params;
    }

    public static int[] AdvanceSettings(Context context) {
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

    public static String[] Account(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String[] params = new String[4];
        params[0] = preferences.getString("id", "");
        params[1] = preferences.getString("pwd", "");
        params[2] = preferences.getString("uuid", "");
        params[3] = preferences.getString("u_name", "");
        return params;
    }
}
