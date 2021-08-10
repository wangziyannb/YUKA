package com.wzy.yuka.yuka_lite.sender;

import android.content.Context;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.wzy.yuka.yuka_lite.utils.Screenshot;

import java.io.File;

/**
 * Created by Ziyan on 2021/8/10.
 */
public class TessOCR {
    public static String[] detect(Context context, Screenshot screenshot, String language, String[] sub_languages, String model) {
        if (sub_languages != null) {
            StringBuilder languageBuilder = new StringBuilder(language);
            for (String s : sub_languages) {
                languageBuilder.append("+").append(s);
            }
            language = languageBuilder.toString();
        }
        return detect(context, screenshot, language, model);
    }

    public static String[] detect(Context context, Screenshot screenshot, String language, String model) {
        String[] fullFileNames = screenshot.getFullFileNames();
        String model_path;
        if (model.equals("fast")) {
            model_path = context.getExternalFilesDir("models/fast").getAbsoluteFile() + "/";
        } else {
            model_path = context.getExternalFilesDir("models/best").getAbsoluteFile() + "/";
        }
        String[] detected = new String[fullFileNames.length];
        TessBaseAPI api = new TessBaseAPI();
        api.init(model_path, language, TessBaseAPI.OEM_LSTM_ONLY);
        for (int i = 0; i < fullFileNames.length; i++) {
            api.setImage(new File(fullFileNames[i]));
            detected[i] = api.getUTF8Text();
            Log.d("TAG", detected[i]);
            api.clear();
        }
        return detected;
    }
}
