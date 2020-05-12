package com.wzy.yuka.core.user;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/5/9.
 */
public class UserManager {
    private static GlobalHandler globalHandler = GlobalHandler.getInstance();
    private static Account account;
    private static HashMap<String, String> hashMap;

    public static void init(Context application) {
        account = new Account(application);
    }


    public static void login() {
        hashMap = account.get();
        hashMap.put("isLogin", "false");
        account.update(hashMap);
        if (!hashMap.containsKey("id")) {
            Bundle bundle = new Bundle();
            bundle.putString("response", "{\"origin\":\"601\"}");
            Message message = Message.obtain();
            message.what = 200;
            message.setData(bundle);
            globalHandler.sendMessage(message);
            return;
        }
        String[] params = new String[3];
        params[0] = hashMap.get("id");
        params[1] = hashMap.get("pwd");
        params[2] = hashMap.get("uuid");
        Log.d("TAG", "login: " + params[0] + params[1]);
        HttpRequest.Login(params, new okhttp3.Callback() {
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

    public static void logout() {
        hashMap = account.get();
        hashMap.put("isLogin", "false");
        account.update(hashMap);

        if (!hashMap.containsKey("id")) {
            Bundle bundle = new Bundle();
            bundle.putString("response", "{\"result\":\"未登录\"}");
            Message message = Message.obtain();
            message.what = 201;
            message.setData(bundle);
            globalHandler.sendMessage(message);
            return;
        }
        HttpRequest.Logout(new okhttp3.Callback() {
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
                Log.d("TAG", "onResponse: " + bundle.getString("response"));
                Message message = Message.obtain();
                message.what = 201;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        });
    }

    public static void register(String[] params) {
        HttpRequest.Register(params, new okhttp3.Callback() {
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
                message.what = 202;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        });
    }

    public static boolean checkLogin() {
        hashMap = get();
        return Boolean.parseBoolean(hashMap.get("isLogin"));
    }

    public static HashMap<String, String> get() {
        hashMap = account.get();
        return hashMap;
    }

    public static void update(@NotNull HashMap<String, String> hashMap) {
        account.update(hashMap);
    }
}
