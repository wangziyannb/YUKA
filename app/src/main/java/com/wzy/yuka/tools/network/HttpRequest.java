package com.wzy.yuka.tools.network;

import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRequest {
    private static String Tag = HttpRequest.class.getSimpleName();

    /**
     * @param params   请求参数
     * @param filePath 文件路径
     */
    public static void requestTowardsYukaServer(String[] params, String filePath, okhttp3.Callback callback) {
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
                .url("https://wangclimxnb.xyz/yuka_main")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void requestTowardsYukaServer(String[] params, String[] filePath, okhttp3.Callback[] callbacks) {
        if (filePath.length == callbacks.length) {
            for (int i = 0; i < filePath.length; i++) {
                requestTowardsYukaServer(params, filePath[i], callbacks[i]);
            }
        } else {
            Log.e(Tag, filePath.length + "");
            Log.e(Tag, callbacks.length + "");
            Log.e(Tag, "Number not match");
        }

    }

}

