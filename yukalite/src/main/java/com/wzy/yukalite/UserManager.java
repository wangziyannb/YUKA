package com.wzy.yukalite;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Callback;

/**
 * Created by Ziyan on 2020/5/9.
 */
class UserManager {
    private static Account account;
    private static HashMap<String, String> hashMap;

    static void init(Context application, @NotNull String id) {
        account = new Account(application, id);
    }

    static void addUser(@NotNull String u_name, @NotNull String pwd) {
        hashMap = account.get();
        hashMap.put("u_name", u_name);
        hashMap.put("pwd", pwd);
        account.update(hashMap);
    }

    static void removeUser() {
        hashMap = account.get();
        hashMap.remove("u_name");
        hashMap.remove("pwd");
        account.update(hashMap);
    }

    static String[] getUser() throws YukaUserManagerException {
        hashMap = account.get();
        String[] params = new String[3];
        params[0] = hashMap.get("u_name");
        params[1] = hashMap.get("pwd");
        params[2] = hashMap.get("uuid");
        if (params[0] == null || params[0].equals("")
                || params[1] == null || params[1].equals("")
                || params[2] == null || params[2].equals("")) {
            throw new YukaUserManagerException(YukaUserManagerException.NO_USER);
        }
        return params;
    }

    /**
     * Login.在登录前加入user吧
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 账户名或密码错误时，msg:601
     * 成功登陆时，msg:200
     */
    static void login(Callback callback) throws YukaUserManagerException {
        String[] params = getUser();
        YukaRequest.login(params, callback);
    }

    /**
     * Logout.
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 未登录或设备未注册，msg:602 （不用管）
     * 成功登出时，msg:200
     */
    static void logout(Callback callback) throws YukaUserManagerException {
        String[] params = getUser();
        YukaRequest.logout(params, callback);
    }

    /**
     * Register.
     * 网络或服务器错误时，msg:400
     * 用户名已注册时，msg:600
     * 成功注册时，msg:200 data:id
     *
     * @param params the params
     */
    static void register(String[] params, Callback callback) {
        YukaRequest.register(params, callback);
    }

    static void forget_password(String[] params, Callback callback) {
        YukaRequest.forget_password(params, callback);
    }

    /**
     * feasibility.
     * 网络或服务器错误时，msg:400
     * 用户名或者本机已注册时，msg:600
     * 可以注册时，msg:200 data:id
     *
     * @param mode  "u_name" or "uuid"
     * @param param 相应的值
     */
    static void check_feasibility(String mode, String param, Callback callback) {
        YukaRequest.check_feasibility(mode, param, callback);
    }

    static void activate(String cdkey, Callback callback) throws YukaUserManagerException {
        String[] params = new String[2];
        params[0] = getUser()[0];
        params[1] = cdkey;
        YukaRequest.activate(params, callback);

    }

    static void refreshInfo(Callback callback) throws YukaUserManagerException {
        String[] params = getUser();
        YukaRequest.check_info(params, callback);
    }

    static boolean isLogin() {
        hashMap = account.get();
        return Boolean.parseBoolean(hashMap.get("isLogin"));
    }

    static void setLogin(boolean i) {
        hashMap = account.get();
        hashMap.put("isLogin", Boolean.toString(i));
        account.update(hashMap);
    }


    private static class Account {
        private final SharedPreferences mSharedPreferences;
        private JSONObject json;

        Account(Context context, String id) {
            mSharedPreferences = context.getSharedPreferences("yuka", Context.MODE_PRIVATE);
            try {
                json = new JSONObject(mSharedPreferences.getString("account", ""));
            } catch (JSONException e) {
                Log.e("Account", "Account:load json failed");
                json = new JSONObject();
                HashMap<String, String> map = get();
                map.put("uuid", id);
                update(map);
            }
        }

        HashMap<String, String> get() {
            HashMap<String, String> hashMap = new HashMap<>();
            Iterator k = json.keys();
            while (k.hasNext()) {
                String key = k.next().toString();
                try {
                    String value = json.get(key).toString();
                    hashMap.put(key, value);
                } catch (JSONException ignored) {
                }
            }
            return hashMap;
        }

        void update(@NotNull HashMap<String, String> hashMap) {
            json = new JSONObject(hashMap);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("account", json.toString());
            editor.commit();
        }
    }
}
