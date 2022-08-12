package com.wzy.yuka.yuka_lite.sender;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.yuka_lite.utils.Screenshot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by Ziyan on 2021/8/10.
 */
public class TessOCR {
    public static String[] detect(Context context, Screenshot screenshot, String language, Set<String> sub_languages, String model) {
        if (!checkModel(context)) {
            return new String[]{"错误，未检测到Tess模型。请于设置中重新下载"};
        }
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
        Log.e("TAG", "detect: " + checkModel(context));
        if (!checkModel(context)) {
            return new String[]{"错误，未检测到Tess模型。请于设置中重新下载"};
        }
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
            if (language.contains("chi") || language.contains("jpn") || language.contains("kor")) {
                do {
                    List<Pair<String, Double>> results = iterator.getSymbolChoicesAndConfidence();
                    Log.e("TAG", "detect: " + results.size());
                    for (Pair<String, Double> r : results) {
                        builder.append(r.first);
                        Log.d("TAG", "    \"" + r.first + "\"    " + r.second);
                    }
                } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_SYMBOL));
            } else {
                builder.append(api.getUTF8Text().replace("\n", " "));
            }

            detected[i] = builder.toString();
            Log.d("TAG", detected[i]);
            api.clear();
        }
        api.recycle();
        return detected;
    }

    public static boolean checkModel(Context context) {
        File fastDir = context.getExternalFilesDir("models/fast/tessdata");
        if (fastDir.list() != null && fastDir.list().length < 12) {
            return false;
        }
        File bestDir = context.getExternalFilesDir("models/best/tessdata");
        return bestDir.list() == null || bestDir.list().length >= 12;
    }

    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        } else {
            for (File f : file.listFiles()) {
                deleteFile(f);
            }
        }
        return file.delete();
    }

    public static void downloadModel(Context context, TessOCRListener listener) {
        File modelDir = context.getExternalFilesDir("model");
        deleteFile(modelDir);
        HttpRequest.getModel(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    File dataDir = context.getExternalFilesDir("models");
                    File file = new File(dataDir.getAbsolutePath() + "/model.zip");
                    if (file.createNewFile()) {
                        BufferedSink sink = Okio.buffer(Okio.sink(file));
                        sink.writeAll(response.body().source());
                        sink.close();
                    }
                    unzip(file, dataDir);
                    deleteFile(file);
                    listener.onFinish();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }

    public interface TessOCRListener {
        void onFinish();
    }
}
