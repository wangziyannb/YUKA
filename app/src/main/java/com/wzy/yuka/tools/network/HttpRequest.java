package com.wzy.yuka.tools.network;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRequest {
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final String Tag = HttpRequest.class.getSimpleName();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    Log.d(Tag, "saveFromResponse: " + httpUrl);
                    cookieStore.put(httpUrl.host(), list);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .build();

    public static void checkUpdate(Callback callback) {
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/latest_version/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void checkMarket(Callback callback) {
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/market/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void getModel(Callback callback) {
        Request request = new Request.Builder()
                .url("https://yuka-app-1305234451.cos.ap-shanghai.myqcloud.com/yuka_app/models.zip")
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}

