package com.wzy.yuka.yuka.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Ziyan on 2020/5/8.
 * include:
 * u_name,pwd,uuid,isLogin
 */
class Account {
    private SharedPreferences mSharedPreferences;
    private JSONObject json;

    Account(Context context) {
        WeakReference<Context> mContext = new WeakReference<>(context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.get());
        try {
            json = new JSONObject(mSharedPreferences.getString("account", ""));
        } catch (JSONException e) {
            json = new JSONObject();
            e.printStackTrace();
            Log.d("Account", "Account:load json failed");
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
