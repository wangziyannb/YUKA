package com.wzy.yuka.core.user;

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
 */
public class Account {
    WeakReference<Context> mContext;
    SharedPreferences mSharedPreferences;
    JSONObject json;

    public Account(Context context) {
        mContext = new WeakReference<>(context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.get());
        try {
            json = new JSONObject(mSharedPreferences.getString("account", ""));
        } catch (JSONException e) {
            json = new JSONObject();
            Log.e("Account", "Account:load json failed");
        }
    }

    public HashMap<String, String> getInformation() {
        HashMap<String, String> hashMap = new HashMap<>();
        Iterator it = json.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            String value = it.next().toString();
            hashMap.put(key, value);
        }
        return hashMap;
    }

    public void setInformation(@NotNull HashMap<String, String> hashMap) {
        json = new JSONObject(hashMap);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("account", json.toString());
        editor.commit();
    }
}
