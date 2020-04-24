package com.wzy.yuka.tools.params;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;

public class GetClientParams {

    public static String[] getParamsForReq(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //0:mode 1:model 2:translator 3:SBCS
        String[] params = new String[4];
        Resources resources = context.getResources();
        //默认为 ocr - google -google - 0
        params[0] = resources.getStringArray(R.array.mode)[0];
        params[1] = resources.getStringArray(R.array.model)[0];
        params[2] = resources.getStringArray(R.array.translator)[0];
        params[3] = resources.getString(R.string.False);

        if (preferences.getBoolean("settings_trans_switch", false)) {
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
        for (String str : params) {
            Log.d("params", str);
        }
        return params;
    }
}
