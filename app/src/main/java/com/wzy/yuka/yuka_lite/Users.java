package com.wzy.yuka.yuka_lite;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.YukaUserManagerException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2021/1/17.
 */
public class Users {
    public static GlobalHandler globalHandler;

    /**
     * Login.
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 账户名或密码错误时，msg:601
     * 成功登陆时，msg:200
     */
    public static void login() throws YukaUserManagerException {
        globalHandler = GlobalHandler.getInstance();
        YukaLite.setLogin(false);
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
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
                        YukaLite.setLogin(true);
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
        };
        YukaLite.login(callback);
    }

    /**
     * Logout.
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 未登录或设备未注册，msg:602 （不用管）
     * 成功登出时，msg:200 （但是返回的是201）
     */
    public static void logout() throws YukaUserManagerException {
        globalHandler = GlobalHandler.getInstance();
        YukaLite.setLogin(false);
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
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
                        YukaLite.setLogin(false);
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
        };
        YukaLite.logout(callback);
    }

    /**
     * Register.
     * 网络或服务器错误时，msg:400
     * 用户名已注册时，msg:600
     * 成功注册时，msg:200 data:id  （但是id没有用，且返回的是202 XD）
     *
     * @param params the params
     */
    public static void register(String[] params) {
        globalHandler = GlobalHandler.getInstance();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
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
                        YukaLite.addUser(params[0], params[1]);
                        message.what = 202;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("600")) {
                        message.what = 600;
                        globalHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (!res.equals("")) {
                        try {
                            JSONObject resultJson = new JSONObject(res);
                            String error_code = resultJson.getString("error_code");
                            if (error_code.equals("429")) {
                                message.what = 429;
                                globalHandler.sendMessage(message);
                            }
                        } catch (JSONException ex) {
                            onFailure(call, new IOException());
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        YukaLite.register(params, callback);
    }

    public static void forget_password(String[] params) {
        globalHandler = GlobalHandler.getInstance();
        YukaLite.forget_password(params, new okhttp3.Callback() {
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
                    if (!origin.equals("400")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("results", result);
                        message.what = Integer.parseInt(origin);
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    } else {
                        onFailure(call, new IOException());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFailure(call, new IOException());
                }
            }
        });
    }

    public static void activate(String cdkey) throws YukaUserManagerException {
        globalHandler = GlobalHandler.getInstance();
        YukaLite.activate(cdkey, new Callback() {
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
                        message.what = 200;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("400")) {
                        message.what = 400;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("603")) {
                        message.what = 603;
                        globalHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFailure(call, new IOException());
                }
            }
        });

    }

    public static void refreshInfo() {
        globalHandler = GlobalHandler.getInstance();
        try {
            YukaLite.refreshInfo(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Message message = Message.obtain();
                    message.what = 400;
                    globalHandler.sendMessage(message);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String res = response.body().string();
                    Log.d("TAG", "onResponse: " + res);
                    Message message = Message.obtain();
                    try {
                        JSONObject resultJson = new JSONObject(res);
                        String origin = resultJson.getString("origin");
                        if (origin.equals("200")) {
                            Bundle bundle = new Bundle();
                            String results = resultJson.getString("results");
                            resultJson = new JSONObject(results);
                            bundle.putString("time", resultJson.getString("time"));

                            bundle.putDouble("remain", resultJson.getDouble("remain"));
                            bundle.putDouble("remain_advancetimes", resultJson.getDouble("remain_advancetimes"));
                            bundle.putDouble("sync_time", resultJson.getDouble("sync_time"));

                            message.what = 201;
                            message.setData(bundle);
                            globalHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onFailure(call, new IOException());
                    }
                }
            });
        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }

    }

    public static void check_feasibility(String mode, String param) {
        globalHandler = GlobalHandler.getInstance();
        YukaLite.check_feasibility(mode, param, new okhttp3.Callback() {
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
                        message.what = 209;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("600")) {
                        message.what = 605;
                        globalHandler.sendMessage(message);
                    }
                    if (origin.equals("400")) {
                        onFailure(call, new IOException());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onFailure(call, new IOException());
                }
            }
        });
    }

    public static void addUser(@NotNull String u_name, @NotNull String pwd) {
        YukaLite.addUser(u_name, pwd);
    }

}

