package com.wzy.yuka.yuka_lite.sender;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.utils.Screenshot;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.config.YukaConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

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
    private Screenshot screenshot;
    private boolean save;


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Message message = Message.obtain();
            switch (msg.what) {
                case 0x0:
                    //任意一个步骤出现问题，原样把错误送出去
                    message.setData(msg.getData());
                    message.what = 0;
                    globalHandler.sendMessage(message);
                    break;
                case 0x1:
                    //获得了正确的识别结果，无论是从何种ocr得到。交给下一步
                    single_get_result(msg.getData());
                    break;
                case 0x2:
                    //获得了正确的翻译结果，无论是从何种ocr得到。送出结果
                    try {
                        Bundle data = msg.getData();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("origin", msg.getData().getString("ocr"));
                        jsonObject.put("results", msg.getData().getString("translate"));
                        jsonObject.put("time", 0);
                        data.putString("response", jsonObject.toString());
                        message.setData(data);
                        message.what = 1;
                        globalHandler.sendMessage(message);
                    } catch (JSONException e) {
                        Toast.makeText(contextWeakReference.get(), "在显示结果时出现了未知错误！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public Processor(Context context, Screenshot screenshot, String mode, boolean save) {
        globalHandler = GlobalHandler.getInstance();
        util = SharedPreferencesUtil.getInstance();
        this.contextWeakReference = new WeakReference<>(context);
        this.screenshot = screenshot;
        this.mode = mode;
        this.save = save;
        resources = contextWeakReference.get().getResources();
    }

    public void single_main(String api) {
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

    private void single_get_result(Bundle bundle) {
        String response = bundle.getString("ocr");
        String translator = (String) util.getParam(SharedPreferenceCollection.trans_other_translator, resources.getStringArray(R.array.other_trans_modelset)[0]);
        switch (translator) {
            case "youdao":
                single_get_result_youdao(bundle, response);
        }

    }

    private void single_get_result_youdao(Bundle bundle, String origin) {
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
}
