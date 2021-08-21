package com.wzy.yuka.yuka_lite.sender;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.wzy.yuka.yuka_lite.utils.Screenshot;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by Ziyan on 2021/8/10.
 */
public class TessOCR {
    public static String[] detect(Context context, Screenshot screenshot, String language, Set<String> sub_languages, String model) {
        if (sub_languages != null) {
            StringBuilder languageBuilder = new StringBuilder(language);
            for (String s : sub_languages) {
                languageBuilder.append("+").append(s);
            }
            language = languageBuilder.toString();
        }
        Log.d("TAG", "detect: " + language);
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
            Log.d("TAG", api.getUTF8Text());
            StringBuilder builder = new StringBuilder();
            ResultIterator iterator = api.getResultIterator();
            //方块字-->RIL_SYMBOL
            //方块字词组（如“输入”）-->RIL_WORD
            //字母字-->RIL_WORD

            int pageIteratorLevel;
            if (language.contains("chi") || language.contains("jpn") || language.contains("kor")) {
                Log.e("TAG", "detect:选用RIL_SYMBOL");
                pageIteratorLevel = TessBaseAPI.PageIteratorLevel.RIL_SYMBOL;
            } else {
                Log.e("TAG", "detect:选用RIL_WORD");
                pageIteratorLevel = TessBaseAPI.PageIteratorLevel.RIL_WORD;
            }
            do {
                List<Pair<String, Double>> results = iterator.getSymbolChoicesAndConfidence();
                for (Pair<String, Double> r : results) {
                    builder.append(r.first);
                    Log.d("TAG", "    \"" + r.first + "\"    " + r.second);
                }
            } while (iterator.next(pageIteratorLevel));
            detected[i] = builder.toString();
            Log.d("TAG", detected[i]);
            api.clear();
        }
        api.recycle();
        return detected;
    }
}
