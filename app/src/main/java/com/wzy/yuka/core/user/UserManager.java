package com.wzy.yuka.core.user;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void addUser(@NotNull String u_name, @NotNull String pwd) {
        hashMap = account.get();
        hashMap.put("u_name", u_name);
        hashMap.put("pwd", pwd);
        account.update(hashMap);
    }

    public static String[] getUser() {
        hashMap = account.get();
        String[] params = new String[3];
        params[0] = hashMap.get("u_name") != null ? hashMap.get("u_name") : "";
        params[1] = hashMap.get("pwd") != null ? hashMap.get("pwd") : "";
        params[2] = hashMap.get("uuid");
        return params;
    }

    /**
     * Login.
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 账户名或密码错误时，msg:601
     * 成功登陆时，msg:200
     */
    public static void login() {
        hashMap = account.get();
        hashMap.put("isLogin", "false");
        account.update(hashMap);

        if (!hashMap.containsKey("u_name")) {
            Message message = Message.obtain();
            message.what = 601;
            globalHandler.sendMessage(message);
            return;
        }

        String[] params = getUser();

        Log.d("TAG", "login: " + params[0] + params[1] + params[2]);
        HttpRequest.Login(params, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = 400;
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Message message = Message.obtain();
                try {
                    JSONObject resultJson = new JSONObject(res);
                    String origin = resultJson.getString("origin");
                    if (origin.equals("200")) {
                        HashMap<String, String> hashMap = UserManager.get();
                        hashMap.put("isLogin", "true");
                        UserManager.update(hashMap);
                        message.what = 200;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("601")) {
                        message.what = 601;
                        globalHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFailure(call, new IOException());
                }
            }
        });
    }

    /**
     * Logout.
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 未登录或设备未注册，msg:602 （不用管）
     * 成功登出时，msg:200
     */
    public static void logout() {
        hashMap = account.get();
        hashMap.put("isLogin", "false");
        account.update(hashMap);

        if (!hashMap.containsKey("u_name")) {
            Message message = Message.obtain();
            message.what = 601;
            globalHandler.sendMessage(message);
            return;
        }

        String[] params = getUser();

        HttpRequest.Logout(params, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = 400;
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Message message = Message.obtain();
                try {
                    JSONObject resultJson = new JSONObject(res);
                    String origin = resultJson.getString("origin");
                    if (origin.equals("200")) {
                        message.what = 201;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("602")) {
                        message.what = 602;
                        globalHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFailure(call, new IOException());
                }
            }
        });
    }

    /**
     * Register.
     * 网络或服务器错误时，msg:400
     * 用户名已注册时，msg:600
     * 成功注册时，msg:200 data:id
     *
     * @param params the params
     */
    public static void register(String[] params) {
        HttpRequest.Register(params, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = 400;
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Message message = Message.obtain();
                try {
                    JSONObject resultJson = new JSONObject(res);
                    String origin = resultJson.getString("origin");
                    String result = resultJson.getString("results");
                    if (origin.equals("200")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", result);
                        addUser(params[0], params[1]);
                        message.what = 202;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("600")) {
                        message.what = 600;
                        globalHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFailure(call, new IOException());
                }
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
