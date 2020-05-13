package com.wzy.yuka.tools.network;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.params.Encrypt;
import com.wzy.yuka.tools.params.GetParams;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
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
import okhttp3.Response;

public class HttpRequest {
    private static GlobalHandler globalHandler = GlobalHandler.getInstance();
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
                            RequestBody.create(params[0], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"u_name\""),
                            RequestBody.create(UserManager.getUser()[0], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"uuid\""),
                            RequestBody.create(UserManager.getUser()[2], null)
                    )
                    .build();
        } else if (image.exists()) {
            body = new MultipartBody.Builder("Yuka2016203023^")
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"1.jpg\""),
                            RequestBody.create(image, MediaType.parse("image/jpeg"))
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                            RequestBody.create(params[0], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"model\""),
                            RequestBody.create(params[1], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"translator\""),
                            RequestBody.create(params[2], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"SBCS\""),
                            RequestBody.create(params[3], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"u_name\""),
                            RequestBody.create(UserManager.getUser()[0], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"uuid\""),
                            RequestBody.create(UserManager.getUser()[2], null)
                    )
                    .build();
        } else {
            Log.e(Tag, "filePath invalid");
            return;
        }
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/yuka/")
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

    public static void yuka(String origin) {
        RequestBody body = new FormBody.Builder()
                .add("mode", "text")
                .add("translator", GetParams.Yuka()[2])
                .add("SBCS", GetParams.Yuka()[3])
                .add("origin", origin)
                .add("u_name", UserManager.getUser()[0])
                .add("uuid", UserManager.getUser()[2])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/yuka/")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 400;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("response", response.body().string());
                Message message = Message.obtain();
                message.what = 200;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        });
    }

    /**
     * Login.
     *
     * @param params 账号、密码、uuid
     */
    public static void Login(String[] params, okhttp3.Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", params[0])
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

    public static void Logout(String[] params, okhttp3.Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", params[0])
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/logout/")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }


    public static void Register(String[] params, okhttp3.Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_name", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/regist/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}

