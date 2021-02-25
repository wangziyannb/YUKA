package com.wzy.yuka.yuka_lite.sender;

import com.wzy.yuka.tools.params.Encrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Ziyan on 2021/2/25.
 */
public class YoudaoTranslator {

    public static void request(String APP_KEY, String APP_SECRET, String origin, Callback callback) {
        String YOUDAO_URL = "https://openapi.youdao.com/api";

        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String from = "auto";
        String to = "auto";

        String signType = "v3";
        String salt = UUID.randomUUID() + "";
        String sign = Encrypt.sha256(APP_KEY + truncate(origin) + salt + curtime + APP_SECRET);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("from", from)
                .add("to", to)
                .add("q", origin)
                .add("curtime", curtime)
                .add("appKey", APP_KEY)
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

    private static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    public static String single(String response) throws JSONException {
        JSONArray array = new JSONObject(response).getJSONArray("translation");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            result.append(array.getString(i));
        }
        return result.toString();
    }

}
