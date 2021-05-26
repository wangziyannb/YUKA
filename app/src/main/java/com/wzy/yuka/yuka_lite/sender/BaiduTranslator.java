package com.wzy.yuka.yuka_lite.sender;

import com.wzy.yuka.tools.params.Encrypt;
import com.wzy.yuka.yuka_lite.utils.BCConvert;

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
 * Created by Ziyan on 2021/5/26.
 */
class BaiduTranslator {

    public static void request(String APP_KEY, String APP_SECRET, String origin, boolean SBCS, Callback callback) {
        String BAIDU_URL = "https://api.fanyi.baidu.com/api/trans/vip/translate";
        origin = (SBCS ? BCConvert.bj2qj(origin) : origin);
        String from = "auto";
        String to = "zh";

        String salt = UUID.randomUUID() + "";
        String sign = Encrypt.md5(APP_KEY + origin + salt + APP_SECRET);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("from", from)
                .add("to", to)
                .add("q", origin)
                .add("appid", APP_KEY)
                .add("salt", salt)
                .add("sign", sign)
                .build();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(BAIDU_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }


    public static String single(String response) throws JSONException {
        JSONArray array = new JSONObject(response).getJSONArray("trans_result");
        return ((JSONObject) array.get(0)).get("dst").toString();
    }


}
