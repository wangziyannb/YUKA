package com.wzy.yuka.tools.network;

import android.util.Log;

import com.wzy.yuka.tools.params.Encrypt;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRequest {
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static String Tag = HttpRequest.class.getSimpleName();
    private static OkHttpClient client = new OkHttpClient.Builder()
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
            .connectTimeout(10 * 1000, TimeUnit.MILLISECONDS)
            .readTimeout(10 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(10 * 1000, TimeUnit.MILLISECONDS)
            .build();

    public static void yuka(String[] params, String filePath, okhttp3.Callback callback) {
        File image = new File(filePath);
        MultipartBody body;
        if (params[0].equals("yuka")) {
            body = new MultipartBody.Builder("Yuka2016203023^")
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                            RequestBody.create(null, params[0])
                    )
                    .build();
        } else if (image.exists()) {
            body = new MultipartBody.Builder("Yuka2016203023^")
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"1.jpg\""),
                            RequestBody.create(MediaType.parse("image/jpeg"), image)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                            RequestBody.create(null, params[0])
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"model\""),
                            RequestBody.create(null, params[1])
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"translator\""),
                            RequestBody.create(null, params[2])
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"SBCS\""),
                            RequestBody.create(null, params[3])
                    )
                    .build();
        } else {
            Log.e(Tag, "filePath invalid");
            return;
        }
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka/")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void yuka(String[] params, String[] filePath, okhttp3.Callback[] callbacks) {
        if (filePath.length == callbacks.length) {
            for (int i = 0; i < filePath.length; i++) {
                yuka(params, filePath[i], callbacks[i]);
            }
        } else {
            Log.e(Tag, filePath.length + "");
            Log.e(Tag, callbacks.length + "");
            Log.e(Tag, "Number not match");
        }

    }

    /**
     * Login.
     *
     * @param params   账号、密码、uuid
     * @param callback the callback
     */
    public static void Login(String[] params, okhttp3.Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("id", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/login/")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void Logout(okhttp3.Callback callback) {
        RequestBody body = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/logout/")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void Register(String[] params, okhttp3.Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("id", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .add("u_name", params[3])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/regist/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}

