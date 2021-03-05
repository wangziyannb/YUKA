package com.wzy.yuka.yuka_lite.sender;


import android.util.Base64;
import android.util.Log;

import com.wzy.yuka.tools.params.Encrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Ziyan on 2021/2/1.
 */
public class YoudaoOCR {

    public static void request(String APP_KEY, String APP_SECRET, File[] file, Callback[] callbacks) {
        if (file.length == callbacks.length) {
            for (int i = 0; i < file.length; i++) {
                request(APP_KEY, APP_SECRET, file[i], callbacks[i]);
            }
        }
    }

    //可以同时应用于普通和自动模式
    public static void request(String APP_KEY, String APP_SECRET, File file, Callback callback) {
        String img = loadAsBase64(file);
        String YOUDAO_URL = "https://openapi.youdao.com/ocrapi";

        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String detectType = "10012";
        String imageType = "1";
        String langType = "auto";
        String docType = "json";
        String signType = "v3";
        String salt = UUID.randomUUID() + "";
        String sign = Encrypt.sha256(APP_KEY + truncate(img) + salt + curtime + APP_SECRET);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("img", img)
                .add("langType", langType)
                .add("detectType", detectType)
                .add("imageType", imageType)
                .add("curtime", curtime)
                .add("appKey", APP_KEY)
                .add("docType", docType)
                .add("signType", signType)
                .add("salt", salt)
                .add("sign", sign)
                .build();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(YOUDAO_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String single(String response, boolean punctuation) throws JSONException {
        Log.d("TAG", response);
        JSONArray array = new JSONObject(response).getJSONObject("Result").getJSONArray("regions");
        StringBuilder origin = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            JSONArray array1 = array.getJSONObject(i).getJSONArray("lines");
            for (int j = 0; j < array1.length(); j++) {
                origin.append(array1.getJSONObject(j).getString("text"));
                if (punctuation) {
                    origin.append(" ");
                }
            }
        }
        return origin.toString();
    }

//    public static String auto(String response,boolean punctuation,int toleration){
//        youdaoJsonBuilder builder=new youdaoJsonBuilder(response,punctuation,toleration);
//    }

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

    private static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    private static class youdaoJsonBuilder {
        String origin_dict;
        boolean punctuation;
        int toleration;
        //后期得猜语言
        ArrayList<String> lang = new ArrayList<>();

        public youdaoJsonBuilder(String origin_dict, boolean punctuation, int toleration) {
            this.origin_dict = origin_dict;
            this.punctuation = punctuation;
            this.toleration = toleration;
        }

        public JSONObject build() {
            return new JSONObject();
        }

//        private JSONObject main(){
//            try {
//                JSONArray array = new JSONObject(origin_dict).getJSONObject("Result").getJSONArray("regions");
//                int j=0;
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
    }
}

