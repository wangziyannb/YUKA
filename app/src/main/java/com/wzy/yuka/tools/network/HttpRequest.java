package com.wzy.yuka.tools.network;

import android.os.StrictMode;
import android.util.Log;

import com.wzy.yuka.tools.params.Encrypt;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpRequest {
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static String Tag = HttpRequest.class.getSimpleName();
    static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                    for (Cookie cookie : list) {
                        Log.d(Tag, "cookie Name:" + cookie.name());
                        Log.d(Tag, "cookie value:" + cookie.value());
                        Log.d(Tag, "cookie path:" + cookie.path());
                    }
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
            .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
            .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
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
                .add("pwd", Encrypt.md5(params[1], params[2]))
                .add("uuid", params[2])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/login/")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String Logout() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/logout/")
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            //第五步，解析请求结果
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String Register(String[] params) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestBody requestBody = new FormBody.Builder()
                .add("id", params[0])
                .add("pwd", Encrypt.md5(params[1], params[2]))
                .add("uuid", params[2])
                .add("u_name", params[3])
                .build();
        Request request = new Request.Builder()
                .url("https://wangclimxnb.xyz/yuka_test/regist/")
                .post(requestBody)
                .build();
        //第四步,开始进行同步post请求
        try {
            Response response = client.newCall(request).execute();
            //第五步，解析请求结果
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

