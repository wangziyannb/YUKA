package com.wzy.yuka.yuka_lite.sender;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ziyan on 2021/2/26.
 */
public class BaiduOCR {
    private static String access_token;

    //暂时没得好办法
    public static void request(String API_Key, String Secret_Key, File[] file, Callback[] callbacks, Handler handler1) {
        if (access_token != null) {
            if (file.length == callbacks.length) {
                for (int i = 0; i < file.length; i++) {
                    request(file[i], access_token, callbacks[i]);
                }
            }
        } else {
            Log.d("TAG", "获取token");
            String access_token_url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + API_Key + "&client_secret=" + Secret_Key;
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .url(access_token_url)
                    .post(new FormBody.Builder().build())
                    .build();
            try {
                Response response = client.newCall(request).execute();
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String token = object.optString("access_token", "");
                    if (token.isEmpty()) {
                        String err_desc = object.optString("error_description", "未知原因");
                        String err_res;
                        switch (err_desc) {
                            case "unknown client id":
                                err_res = "API Key不正确";
                                break;
                            case "Client authentication failed":
                                err_res = "Secret Key不正确";
                                break;
                            default:
                                err_res = "未知错误";
                        }
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("error", "识别器获取token失败，原因为：" + err_res);
                        message.what = 0x0;
                        message.setData(bundle);
                        //送出错误message给Processor
                        handler1.sendMessage(message);
                    } else {
                        access_token = token;
                        if (file.length == callbacks.length) {
                            for (int i = 0; i < file.length; i++) {
                                request(file[i], access_token, callbacks[i]);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new IOException("返回的值非json数据！可能是yuka版本太低");
                }
            } catch (IOException e) {
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("error", "识别器获取token失败，原因为：" + e.toString());
                message.what = 0x0;
                message.setData(bundle);
                //送出错误message给Processor
                handler1.sendMessage(message);
            }
        }
    }

    public static void request(File file, String access_token, Callback callback) {
        String image = loadAsBase64(file);
        String BAIDU_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + access_token;
        String language_type = "auto_detect";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("image", image)
                .add("language_type", language_type)
                .build();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(BAIDU_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String single(String response, boolean punctuation, boolean vertical) throws JSONException {
        JSONArray array = new JSONObject(response).getJSONArray("words_result");
        StringBuilder origin = new StringBuilder();
        if (vertical) {
            for (int i = array.length() - 1; i > -1; i--) {
                origin.append(array.getJSONObject(i).getString("words"));
                if (punctuation) {
                    origin.append("、");
                }
            }
        } else {
            for (int i = 0; i < array.length(); i++) {
                origin.append(array.getJSONObject(i).getString("words"));
                if (punctuation) {
                    origin.append("、");
                }
            }
        }
        return origin.toString();
    }

    public static String loadAsBase64(File file) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        if (!file.exists()) {
            return null;
        }
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);//返回Base64编码过的字节数组字符串
    }
}
