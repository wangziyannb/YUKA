package com.wzy.yuka.yuka_lite.sender;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.io.PictureCopy;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.utils.Screenshot;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.config.YukaConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2021/2/4.
 */
public class Processor {
    private final GlobalHandler globalHandler;
    private final WeakReference<Context> contextWeakReference;
    private final SharedPreferencesUtil util;
    private final Resources resources;
    private String mode;
    private final Screenshot screenshot;
    private final boolean save;
    long time;
    private String[] auto_trans;
    private boolean error = false;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Message message = Message.obtain();
            switch (msg.what) {
                case 0x0:
                    //任意一个步骤出现问题，原样把错误送出去
                    if (!error) {
                        message.setData(msg.getData());
                        message.what = 0;
                        globalHandler.sendMessage(message);
                        error = true;
                    }
                    break;
                case 0x1:
                    //获得了正确的"普通"（包括持续和单多悬浮窗）识别结果，无论是从何种ocr得到。交给下一步
                    single_get_result(msg.getData());
                    break;
                case 0x2:
                    //获得了正确的翻译结果，无论是从何种ocr得到。送出结果
                    try {
                        Bundle data = msg.getData();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("origin", data.getString("ocr"));
                        jsonObject.put("results", data.getString("translate"));
                        jsonObject.put("time", 0);
                        data.putString("response", jsonObject.toString());
                        message.setData(data);
                        message.what = 1;
                        globalHandler.sendMessage(message);
                    } catch (JSONException e) {
                        Toast.makeText(contextWeakReference.get(), "在显示结果时出现了未知错误！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x3:
                    //获得了正确的"自动"识别结果，无论是从何种ocr得到。交给下一步
                    auto_get_result(msg.getData());
                    break;
                case 0x4:
                    //获得了正确的翻译结果，无论是从何种ocr得到。先判断是否完成了所有的翻译。完成后送出结果
                    Bundle data = msg.getData();
                    auto_trans[data.getInt("auto_index")] = data.getString("translate");
                    boolean flag = true;
                    for (String a : auto_trans) {
                        if (a == null) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        try {
                            JSONObject ocr = new JSONObject(data.getString("ocr"));
                            JSONArray array = ocr.getJSONArray("values");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject src = array.getJSONObject(i).getJSONObject("src");
                                src.put("translation", auto_trans[i]);
                            }
                            ocr.put("total_time", (double) (System.currentTimeMillis() - time) / 1000);
                            data.putString("response", ocr.toString());
                            message.setData(data);
                            message.what = 1;
                            globalHandler.sendMessage(message);
                        } catch (JSONException e) {
                            Toast.makeText(contextWeakReference.get(), "在显示结果时出现了未知错误！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };

    /**
     * @param context    一般是service
     * @param screenshot 截图对象
     * @param save       是否保存（仅对自动有效）
     */
    public Processor(Context context, Screenshot screenshot, boolean save) {
        globalHandler = GlobalHandler.getInstance();
        util = SharedPreferencesUtil.getInstance();
        this.contextWeakReference = new WeakReference<>(context);
        this.screenshot = screenshot;
        this.save = save;
        resources = contextWeakReference.get().getResources();
    }

    public void single_main() {
        this.mode = Modes.translate;
        String api = (String) util.getParam(SharedPreferenceCollection.detect_api, resources.getStringArray(R.array.sender_api_value_detect)[0]);
        switch (api) {
            case "yuka_v1":
                single_get_all_yuka();
                break;
            case "other":
                String model = (String) util.getParam(SharedPreferenceCollection.detect_other_model, resources.getStringArray(R.array.other_detect_modelset)[0]);
                if (model.equals(resources.getStringArray(R.array.other_detect_modelset)[0])) {
                    single_get_origin_youdao();
                } else if (model.equals(resources.getStringArray(R.array.other_detect_modelset)[1])) {
                    single_get_origin_baidu();
                }
                break;
            case "tess":
                single_tess_get_origin();
                break;
            case "share":
                single_share();
                break;
        }
    }

    private void single_get_all_yuka() {
        Callback[] callbacks = new Callback[screenshot.getLocation().length];
        String[] fileNames = screenshot.getFullFileNames();
        String filePath = screenshot.getFilePath();
        File[] images = new File[fileNames.length];
        //yuka_v1可以直接把原文和译文一起发过来，所以不需要分成两步来做了
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            int finalI = i;
            callbacks[i] = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.toString());
                    Message message = Message.obtain();
                    message.what = 0;
                    message.setData(bundle);
                    globalHandler.sendMessage(message);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", screenshot.getIndex()[finalI]);
                    bundle.putString("response", response.body().string());
                    bundle.putString("fileName", fileName);
                    bundle.putString("filePath", filePath);
                    bundle.putBoolean("save", save);
                    bundle.putString("api", "yuka_v1");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.setData(bundle);
                    globalHandler.sendMessage(message);
                }
            };
            images[i] = new File(fileNames[i]);
        }
        YukaConfig yukaConfig = ConfigBuilder.yuka(contextWeakReference.get(), mode);
        YukaLite.request(yukaConfig, images, callbacks);
    }

    private void single_get_origin_youdao() {
        //有道
        Callback[] callbacks = new Callback[screenshot.getLocation().length];
        String[] fileNames = screenshot.getFullFileNames();
        String filePath = screenshot.getFilePath();
        File[] images = new File[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            int finalI = i;
            callbacks[i] = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.toString());
                    Message message = Message.obtain();
                    message.what = 0x0;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", screenshot.getIndex()[finalI]);
                    bundle.putBoolean("save", save);
                    String resp = response.body().string();
                    try {
                        String result = YoudaoOCR.single(resp, (Boolean) util.getParam(SharedPreferenceCollection.detect_other_punctuation, false));
                        bundle.putString("ocr_initial", resp);
                        bundle.putString("ocr", result);
                        bundle.putString("fileName", fileName);
                        bundle.putString("filePath", filePath);
                        bundle.putString("model", "youdao");
                        Message message = Message.obtain();
                        message.what = 0x1;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        Message message = Message.obtain();
                        message.what = 0x0;
                        bundle.putString("error", "识别器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            };
            images[i] = new File(fileNames[i]);
        }

        String APP_KEY = (String) util.getParam(SharedPreferenceCollection.detect_other_youdao_key, "");
        String APP_SECRET = (String) util.getParam(SharedPreferenceCollection.detect_other_youdao_appsec, "");
        if (APP_KEY.isEmpty() || APP_SECRET.isEmpty()) {
            Toast.makeText(contextWeakReference.get(), "未填写有道的应用id或密钥，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
            single_get_all_yuka();
        } else {
            YoudaoOCR.request(APP_KEY, APP_SECRET, images, callbacks);
        }
    }

    private void single_get_origin_baidu() {
        //百度，需要先鉴权。那一边写的是同步+异步，记得另起线程
        new Thread(() -> {
            Callback[] callbacks = new Callback[screenshot.getLocation().length];
            String[] fileNames = screenshot.getFullFileNames();
            String filePath = screenshot.getFilePath();
            File[] images = new File[fileNames.length];

            for (int i = 0; i < fileNames.length; i++) {
                String fileName = fileNames[i];
                int finalI = i;
                callbacks[i] = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Bundle bundle = new Bundle();
                        bundle.putString("error", e.toString());
                        Message message = Message.obtain();
                        message.what = 0x0;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", screenshot.getIndex()[finalI]);
                        bundle.putBoolean("save", save);
                        String resp = response.body().string();
                        Log.d("TAG", "onResponse: " + resp);
                        try {
                            String result = BaiduOCR.single(resp, (Boolean) util.getParam(SharedPreferenceCollection.detect_other_punctuation, false), (Boolean) util.getParam(SharedPreferenceCollection.detect_other_vertical, false));
                            bundle.putString("ocr_initial", resp);
                            bundle.putString("ocr", result);
                            bundle.putString("fileName", fileName);
                            bundle.putString("filePath", filePath);
                            bundle.putString("model", "baidu");
                            Message message = Message.obtain();
                            message.what = 0x1;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            Message message = Message.obtain();
                            message.what = 0x0;
                            bundle.putString("error", "识别器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                            message.setData(bundle);
                            handler.sendMessage(message);
                            e.printStackTrace();
                        }
                    }
                };
                images[i] = new File(fileNames[i]);
            }
            String API_Key = (String) util.getParam(SharedPreferenceCollection.detect_other_baidu_key, "");
            String Secret_Key = (String) util.getParam(SharedPreferenceCollection.detect_other_baidu_appsec, "");
            if (API_Key.isEmpty() || Secret_Key.isEmpty()) {
                Toast.makeText(contextWeakReference.get(), "未填写百度的API Key或Secret Key，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
                single_get_all_yuka();
            } else {
                BaiduOCR.request(API_Key, Secret_Key, images, callbacks, handler);
            }
        }).start();
    }

    private void single_tess_get_origin() {
        String model = (String) util.getParam(SharedPreferenceCollection.detect_tess_model, resources.getStringArray(R.array.tess_detect_modelset)[0]);
        String langs = (String) util.getParam(SharedPreferenceCollection.detect_tess_lang, resources.getStringArray(R.array.tess_langset)[0]);

        String[] sub_langs = (String[]) util.getParam(SharedPreferenceCollection.detect_tess_lang_sub, resources.getStringArray(R.array.tess_langset_sub)[0]);

        if (sub_langs.equals(new String[]{"null"})) {

        }
        String[] origins = TessOCR.detect(contextWeakReference.get(), screenshot, langs, sub_langs, model);
    }

    private void single_share() {
        String[] fullFileNames = screenshot.getFullFileNames();
        String[] fileNames = screenshot.getFileNames();
        Uri[] uris = new Uri[fullFileNames.length];
        for (int i = 0; i < fullFileNames.length; i++) {
            //先存到DCIM下
            Date nowTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String time = dateFormat.format(nowTime);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uris[i] = new PictureCopy().copyFileToDownloadDir(contextWeakReference.get(), fullFileNames[i], "yuka" + File.separator + time);
            } else {
                uris[i] = Uri.parse(fullFileNames[i]);
            }
        }
        for (Uri uri : uris) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            Intent share = Intent.createChooser(intent, "分享yuka截下的图以供翻译");
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contextWeakReference.get().startActivity(share);
        }
    }

    private void single_get_result(Bundle bundle) {
        String translator = (String) util.getParam(SharedPreferenceCollection.trans_other_translator, resources.getStringArray(R.array.other_trans_modelset)[0]);
        boolean SBCS = (boolean) util.getParam(SharedPreferenceCollection.trans_other_baidu_SBCS, false);
        switch (translator) {
            case "youdao":
                single_get_result_youdao(bundle);
                break;
            case "baidu":
                single_get_result_baidu(bundle, SBCS);
                break;
        }

    }

    private void single_get_result_baidu(Bundle bundle, boolean SBCS) {
        String origin = bundle.getString("ocr");
        String APP_KEY = (String) util.getParam(SharedPreferenceCollection.trans_other_baidu_key, "");
        String APP_SECRET = (String) util.getParam(SharedPreferenceCollection.trans_other_baidu_appsec, "");
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 0x0;
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                try {
                    String result = BaiduTranslator.single(resp);
                    bundle.putString("trans_initial", resp);
                    bundle.putString("translate", result);
                    bundle.putString("translator", "baidu");
                    Message message = Message.obtain();
                    message.what = 0x2;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    message.what = 0x0;
                    bundle.putString("error", "翻译器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

            }
        };

        if (APP_KEY.isEmpty() || APP_SECRET.isEmpty()) {
            Toast.makeText(contextWeakReference.get(), "未填写应用id或密钥，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
            single_get_all_yuka();
        } else {
            BaiduTranslator.request(APP_KEY, APP_SECRET, origin, SBCS, callback);
        }
    }

    private void single_get_result_youdao(Bundle bundle) {
        String origin = bundle.getString("ocr");
        String APP_KEY = (String) util.getParam(SharedPreferenceCollection.trans_other_youdao_key, "");
        String APP_SECRET = (String) util.getParam(SharedPreferenceCollection.trans_other_youdao_appsec, "");
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 0x0;
                message.setData(bundle);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                try {
                    String result = YoudaoTranslator.single(resp);
                    bundle.putString("trans_initial", resp);
                    bundle.putString("translate", result);
                    bundle.putString("translator", "youdao");
                    Message message = Message.obtain();
                    message.what = 0x2;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Message message = Message.obtain();
                    message.what = 0x0;
                    bundle.putString("error", "翻译器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

            }
        };

        if (APP_KEY.isEmpty() || APP_SECRET.isEmpty()) {
            Toast.makeText(contextWeakReference.get(), "未填写应用id或密钥，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
            single_get_all_yuka();
        } else {
            YoudaoTranslator.request(APP_KEY, APP_SECRET, origin, callback);
        }
    }

    public void auto_main() {
        this.mode = Modes.auto;
        time = System.currentTimeMillis();
        String api = (String) util.getParam(SharedPreferenceCollection.auto_api, resources.getStringArray(R.array.sender_api_value_auto)[0]);
        switch (api) {
            case "yuka_v1":
                auto_get_all_yuka();
                break;
            case "other":
                String model = (String) util.getParam(SharedPreferenceCollection.auto_other_model, resources.getStringArray(R.array.other_auto_modelset)[0]);
                if (model.equals(resources.getStringArray(R.array.other_auto_modelset)[0])) {
                    auto_get_origin_youdao();
                } else if (model.equals(resources.getStringArray(R.array.other_auto_modelset)[1])) {
                    //                   single_get_origin_baidu();
                }
                break;
        }
    }

    private void auto_get_all_yuka() {
        String fileName = screenshot.getFullFileNames()[0];
        String filePath = screenshot.getFilePath();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 0;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putInt("index", 0);
                bundle.putString("response", response.body().string());
                bundle.putString("fileName", fileName);
                bundle.putString("filePath", filePath);
                bundle.putBoolean("save", save);
                bundle.putString("api", "yuka_v1");
                Message message = Message.obtain();
                message.what = 1;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        };
        //预置yukaConfig，说实话挺难用的
        YukaConfig yukaConfig = ConfigBuilder.yuka(contextWeakReference.get(), mode);
        File image = new File(fileName);
        YukaLite.request(yukaConfig, image, callback);
    }

    private void auto_get_origin_youdao() {
        //有道
        Callback[] callbacks = new Callback[screenshot.getLocation().length];
        String[] fileNames = screenshot.getFullFileNames();
        String filePath = screenshot.getFilePath();
        File[] images = new File[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            int finalI = i;
            callbacks[i] = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.toString());
                    Message message = Message.obtain();
                    message.what = 0x0;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", screenshot.getIndex()[finalI]);
                    bundle.putBoolean("save", save);
                    String resp = response.body().string();
                    try {
                        String result = YoudaoOCR.auto(resp,
                                (Boolean) util.getParam(SharedPreferenceCollection.auto_other_punctuation, false),
                                (int) util.getParam(SharedPreferenceCollection.auto_other_toleration, 1) * 15);
                        bundle.putString("ocr_initial", resp);
                        bundle.putString("ocr", result);
                        bundle.putString("fileName", fileName);
                        bundle.putString("filePath", filePath);
                        bundle.putString("model", "youdao");
                        Message message = Message.obtain();
                        message.what = 0x3;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        Message message = Message.obtain();
                        message.what = 0x0;
                        bundle.putString("error", "识别器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            };
            images[i] = new File(fileNames[i]);
        }

        String APP_KEY = (String) util.getParam(SharedPreferenceCollection.auto_other_youdao_key, "");
        String APP_SECRET = (String) util.getParam(SharedPreferenceCollection.auto_other_youdao_appsec, "");
        if (APP_KEY.isEmpty() || APP_SECRET.isEmpty()) {
            Toast.makeText(contextWeakReference.get(), "未填写有道的应用id或密钥，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
            auto_get_all_yuka();
        } else {
            YoudaoOCR.request(APP_KEY, APP_SECRET, images, callbacks);
        }
    }

    private void auto_get_result(Bundle bundle) {
        String translator = (String) util.getParam(SharedPreferenceCollection.trans_other_translator, resources.getStringArray(R.array.other_trans_modelset)[0]);
        boolean SBCS = (boolean) util.getParam(SharedPreferenceCollection.trans_other_baidu_SBCS, false);
        switch (translator) {
            case "youdao":
                auto_get_result_youdao(bundle);
                break;
            case "baidu":
                auto_get_result_baidu(bundle, SBCS);
        }
    }

    private void auto_get_result_youdao(Bundle bundle) {
        String origin = bundle.getString("ocr");
        String APP_KEY = (String) util.getParam(SharedPreferenceCollection.trans_other_youdao_key, "");
        String APP_SECRET = (String) util.getParam(SharedPreferenceCollection.trans_other_youdao_appsec, "");
        try {
            JSONObject origin_j = new JSONObject(origin);
            JSONArray array = origin_j.getJSONArray("values");
            String[] sa = new String[array.length()];
            auto_trans = new String[array.length()];
            Callback[] callbacks = new Callback[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject src = array.getJSONObject(i).getJSONObject("src");
                sa[i] = src.getString("words");
                int finalI = i;
                callbacks[i] = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Bundle bundle = new Bundle();
                        bundle.putString("error", e.toString());
                        Message message = Message.obtain();
                        message.what = 0x0;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String resp = response.body().string();
                        try {
                            String result = YoudaoTranslator.single(resp);
                            bundle.putString("trans_initial", resp);
                            bundle.putString("translate", result);
                            bundle.putString("translator", "youdao");
                            bundle.putInt("auto_index", finalI);
                            Message message = Message.obtain();
                            message.what = 0x4;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            Message message = Message.obtain();
                            message.what = 0x0;
                            bundle.putString("error", "翻译器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                };
            }
            if (APP_KEY.isEmpty() || APP_SECRET.isEmpty()) {
                Toast.makeText(contextWeakReference.get(), "未填写应用id或密钥，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
                auto_get_all_yuka();
            } else {
                for (int i = 0; i < array.length(); i++) {
                    YoudaoTranslator.request(APP_KEY, APP_SECRET, sa[i], callbacks[i]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void auto_get_result_baidu(Bundle bundle, boolean SBCS) {
        String origin = bundle.getString("ocr");
        String APP_KEY = (String) util.getParam(SharedPreferenceCollection.trans_other_baidu_key, "");
        String APP_SECRET = (String) util.getParam(SharedPreferenceCollection.trans_other_baidu_appsec, "");
        try {
            JSONObject origin_j = new JSONObject(origin);
            JSONArray array = origin_j.getJSONArray("values");
            String[] sa = new String[array.length()];
            auto_trans = new String[array.length()];
            Callback[] callbacks = new Callback[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject src = array.getJSONObject(i).getJSONObject("src");
                sa[i] = src.getString("words");
                int finalI = i;
                callbacks[i] = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Bundle bundle = new Bundle();
                        bundle.putString("error", e.toString());
                        Message message = Message.obtain();
                        message.what = 0x0;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String resp = response.body().string();
                        try {
                            String result = BaiduTranslator.single(resp);
                            bundle.putString("trans_initial", resp);
                            bundle.putString("translate", result);
                            bundle.putString("translator", "baidu");
                            bundle.putInt("auto_index", finalI);
                            Message message = Message.obtain();
                            message.what = 0x4;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            Message message = Message.obtain();
                            message.what = 0x0;
                            bundle.putString("error", "翻译器出现错误，请前往相应官网文档查询错误原因。\n返回的内容为:" + resp);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                };
            }
            if (APP_KEY.isEmpty() || APP_SECRET.isEmpty()) {
                Toast.makeText(contextWeakReference.get(), "未填写应用id或密钥，将使用yuka_v1来替代", Toast.LENGTH_SHORT).show();
                auto_get_all_yuka();
            } else {
                for (int i = 0; i < array.length(); i++) {
                    BaiduTranslator.request(APP_KEY, APP_SECRET, sa[i], SBCS, callbacks[i]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
