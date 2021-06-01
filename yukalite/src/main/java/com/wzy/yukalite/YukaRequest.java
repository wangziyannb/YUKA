package com.wzy.yukalite;

import android.util.Log;

import com.wzy.yukalite.config.YukaConfig;
import com.wzy.yukalite.tools.Encrypt;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Ziyan on 2020/8/11.
 */
class YukaRequest {
    final static String base_url = "https://yukacn.xyz/yuka/";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
            .build();

    static void yuka(@NotNull Callback callback) {
        MultipartBody body = new MultipartBody.Builder("Yuka2016203023^")
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                        RequestBody.create("yuka", null)
                )
                .build();
        Request request = new Request.Builder()
                .url(base_url + "yuka/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    static void request(@NotNull YukaConfig config, @NotNull File image, @NotNull String[] user, @NotNull Callback callback) {
        MultipartBody body;
        if (image.exists()) {
            body = new MultipartBody.Builder("YukaRandomBoundary^^")
                    .setType(MultipartBody.FORM)
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"1.jpg\""),
                            RequestBody.create(image, MediaType.parse("image/jpeg"))
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"mode\""),
                            RequestBody.create(config.mode, null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"model\""),
                            RequestBody.create(config.model, null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"translator\""),
                            RequestBody.create(config.translator, null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"SBCS\""),
                            RequestBody.create(config.SBCS ? "1" : "0", null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"punctuation\""),
                            RequestBody.create(config.punctuation ? "1" : "0", null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"vertical\""),
                            RequestBody.create(config.vertical ? "1" : "0", null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"toleration\""),
                            RequestBody.create(String.valueOf(config.toleration), null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"u_name\""),
                            RequestBody.create(user[0], null)
                    )
                    .addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"uuid\""),
                            RequestBody.create(user[2], null)
                    )
                    .build();
            Request.Builder request = new Request.Builder().post(body);
            if (config.mode.equals("auto")) {
                request.url(base_url + "yuka_advance/yuka_v1");
            } else {
                request.url(base_url + "yuka/yuka_v1");
            }
            Call call = client.newCall(request.build());
            call.enqueue(callback);
        } else {
            Log.e("Yuka", "file invalid");
        }
    }

    static void request(@NotNull YukaConfig config, @NotNull File[] images, @NotNull String[] user, @NotNull Callback[] callbacks) {
        if (images.length == callbacks.length && !config.mode.equals("auto")) {
            for (int i = 0; i < images.length; i++) {
                request(config, images[i], user, callbacks[i]);
            }
        }
    }

    static void request(@NotNull YukaConfig config, @NotNull String origin, @NotNull String[] user, @NotNull Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("mode", config.mode)
                .add("translator", config.translator)
                .add("SBCS", config.SBCS ? "1" : "0")
                .add("origin", origin)
                .add("u_name", user[0])
                .add("uuid", user[2])
                .build();
        Request request = new Request.Builder()
                .url(base_url + "yuka/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    static void login(@NotNull String[] user, @NotNull Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", user[0])
                .add("pwd", Encrypt.md5(user[1], user[0]))
                .add("uuid", user[2])
                .build();
        Request request = new Request.Builder()
                .url(base_url + "login/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    static void logout(@NotNull String[] user, @NotNull Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", user[0])
                .add("uuid", user[2])
                .build();
        Request request = new Request.Builder()
                .url(base_url + "logout/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    static void check_info(@NotNull String[] user, @NotNull Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("u_name", user[0])
                .build();
        Request request = new Request.Builder()
                .url(base_url + "account/yuka_v1")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    static void register(String[] params, Callback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("u_name", params[0])
                .add("pwd", Encrypt.md5(params[1], params[0]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url(base_url + "regist/yuka_v1")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    static void check_feasibility(String mode, String params, Callback callback) {
        FormBody.Builder formBody = new FormBody.Builder().add("mode", mode);
        switch (mode) {
            case "u_name":
                formBody.add("u_name", params);
                break;
            case "uuid":
                formBody.add("uuid", params);
                break;
        }
        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder()
                .url(base_url + "check/yuka_v1")
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
                .url(base_url + "forget_pwd/yuka_v1")
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
                .url(base_url + "activate/yuka_v1")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
