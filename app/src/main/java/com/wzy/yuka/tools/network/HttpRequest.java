package com.wzy.yuka.tools.network;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.Encrypt;
import com.wzy.yuka.yuka.user.UserManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
            .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .build();

    public static void yuka(HashMap<String, String> params, String origin) {
        RequestBody body = new FormBody.Builder()
                .add("mode", Objects.requireNonNull(params.get("mode")))
                .add("translator", Objects.requireNonNull(params.get("translator")))
                .add("SBCS", Objects.requireNonNull(params.get("SBCS")))
                .add("origin", origin)
                .add("u_name", UserManager.getUser()[0])
                .add("uuid", UserManager.getUser()[2])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/yuka/yuka_v1")
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

    public static void yuka(HashMap<String, String> params, String filePath, Callback callback) {
        File image = new File(filePath);
        MultipartBody body;
        if (Objects.equals(params.get("mode"), "yuka")) {
            body = new MultipartBody.Builder("Yuka2016203023^")
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                            RequestBody.create(Objects.requireNonNull(params.get("mode")), null)
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
                            RequestBody.create(Objects.requireNonNull(params.get("mode")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"model\""),
                            RequestBody.create(Objects.requireNonNull(params.get("model")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"translator\""),
                            RequestBody.create(Objects.requireNonNull(params.get("translator")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"SBCS\""),
                            RequestBody.create(Objects.requireNonNull(params.get("SBCS")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"punctuation\""),
                            RequestBody.create(Objects.requireNonNull(params.get("punctuation")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"vertical\""),
                            RequestBody.create(Objects.requireNonNull(params.get("vertical")), null)
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
                .url("https://yukacn.xyz/yuka/yuka/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void yuka(HashMap<String, String> params, String[] filePath, Callback[] callbacks) {
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

    public static void yuka_advance(HashMap<String, String> params, String filePath, Callback callback) {
        File image = new File(filePath);
        MultipartBody body;
        if (image.exists()) {
            body = new MultipartBody.Builder("Yuka2016203023^")
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"1.jpg\""),
                            RequestBody.create(image, MediaType.parse("image/jpeg"))
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                            RequestBody.create(Objects.requireNonNull(params.get("mode")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"model\""),
                            RequestBody.create(Objects.requireNonNull(params.get("model")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"translator\""),
                            RequestBody.create(Objects.requireNonNull(params.get("translator")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"SBCS\""),
                            RequestBody.create(Objects.requireNonNull(params.get("SBCS")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"vertical\""),
                            RequestBody.create(Objects.requireNonNull(params.get("vertical")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"punctuation\""),
                            RequestBody.create(Objects.requireNonNull(params.get("punctuation")), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"toleration\""),
                            RequestBody.create(Objects.requireNonNull(params.get("toleration")), null)
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
                .url("https://yukacn.xyz/yuka/yuka_advance/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void login(String[] params, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/login/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void logout(String[] params, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", params[0])
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/logout/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void register(String[] params, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_name", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/regist/yuka_v1")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void forget_password(String[] params, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_name", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/forget_pwd/yuka_v1")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void activate(String[] params, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_name", params[0])
                .add("CDKEY", params[1])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/activate/yuka_v1")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void check_info(String[] params, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_name", params[0])
                .build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/account/yuka_v1")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void checkUpdate(Callback callback) {
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("https://yukacn.xyz/yuka/latest_version/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}

