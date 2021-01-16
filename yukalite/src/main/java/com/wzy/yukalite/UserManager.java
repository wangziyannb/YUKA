package com.wzy.yukalite;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
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

    static void init(Context application) {
        account = new Account(application);
    }

    static void addUser(@NotNull String u_name, @NotNull String pwd, @NotNull String id) {
        hashMap = account.get();
        hashMap.put("u_name", u_name);
        hashMap.put("pwd", pwd);
        hashMap.put("id", id);
        account.update(hashMap);
    }

    static void removeUser() {
        hashMap = account.get();
        hashMap.remove("u_name");
        hashMap.remove("pwd");
        hashMap.remove("id");
        account.update(hashMap);
    }

    static String[] getUser() throws YukaUserManagerException {
        hashMap = account.get();
        String[] params = new String[3];
        params[0] = hashMap.get("u_name");
        params[1] = hashMap.get("pwd");
        params[2] = hashMap.get("uuid");
        if (params[0] == null || params[1] == null || params[2] == null) {
            throw new YukaUserManagerException("No User Available");
        }
        return params;
    }

    /**
     * Login.
     * 网络或服务器错误时，msg:400
     * 账户不存在时，msg:601
     * 账户名或密码错误时，msg:601
     * 成功登陆时，msg:200
     */
    static void login(Callback callback) throws YukaUserManagerException {
        hashMap = account.get();
        if ((!hashMap.containsKey("u_name")) || getUser()[0].equals("")) {
            throw new YukaUserManagerException("No User Available");
        }
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
        hashMap = account.get();
        if (!hashMap.containsKey("u_name")) {
            throw new YukaUserManagerException("No User Available");
        }
        String[] params = getUser();
        YukaRequest.logout(params, callback);
    }

    static void refreshInfo(Callback callback) throws YukaUserManagerException {
        String[] params = getUser();
        YukaRequest.check_info(params, callback);
    }

    private static class Account {
        private SharedPreferences mSharedPreferences;
        private JSONObject json;

        Account(Context context) {
            mSharedPreferences = context.getSharedPreferences("yuka", Context.MODE_PRIVATE);
            try {
                json = new JSONObject(mSharedPreferences.getString("account", ""));
            } catch (JSONException e) {
                Log.e("Account", "Account:load json failed");
                json = new JSONObject();
                HashMap<String, String> map = get();
                map.put("uuid", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
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

    static class YukaUserManagerException extends Exception {
        public YukaUserManagerException(String msg) {
            super(msg);
        }
    }
}
