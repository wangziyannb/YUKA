package com.wzy.yuka.tools.params;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Ziyan on 2020/5/22.
 */
public class SharedPreferencesUtil {
    private static SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;

    private static SharedPreferencesUtil preferencesUtil;
    private SharedPreferencesUtil() {

    }

    public static SharedPreferencesUtil getInstance() {
        if (preferencesUtil == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (preferencesUtil == null) {
                    // 使用双重同步锁
                    preferencesUtil = new SharedPreferencesUtil();
                }
            }
        }
        return preferencesUtil;
    }

    public static void init(Context Application) {
        preferences = PreferenceManager.getDefaultSharedPreferences(Application);
    }

    /**
     * 保存数据 , 所有的类型都适用
     *
     * @param key
     * @param object
     */
    public synchronized void saveParam(String key, Object object) {
        if (editor == null)
            editor = preferences.edit();
        // 得到object的类型
        String type = object.getClass().getSimpleName();
        switch (type) {
            case "String":
                // 保存String 类型
                editor.putString(key, (String) object);
                break;
            case "Integer":
                // 保存integer 类型
                editor.putInt(key, (Integer) object);
                break;
            case "Boolean":
                // 保存 boolean 类型
                editor.putBoolean(key, (Boolean) object);
                break;
            case "Float":
                // 保存float类型
                editor.putFloat(key, (Float) object);
                break;
            case "Long":
                // 保存long类型
                editor.putLong(key, (Long) object);
                break;
            default:
                if (!(object instanceof Serializable)) {
                    throw new IllegalArgumentException(object.getClass().getName() + " 必须实现Serializable接口!");
                }

                // 不是基本类型则是保存对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(object);
                    String productBase64 = Base64.encodeToString(
                            baos.toByteArray(), Base64.DEFAULT);
                    editor.putString(key, productBase64);
                    Log.d(this.getClass().getSimpleName(), "save object success");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(this.getClass().getSimpleName(), "save object error");
                }
                break;
        }
        editor.commit();
    }

    /**
     * 移除信息
     */
    public synchronized void remove(String key) {
        if (editor == null)
            editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }


    /**
     * 得到保存数据的方法，所有类型都适用
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject) {
        if (defaultObject == null) {
            return getObject(key);
        }
        String type = defaultObject.getClass().getSimpleName();
        switch (type) {
            case "String":
                return preferences.getString(key, (String) defaultObject);
            case "Integer":
                return preferences.getInt(key, (Integer) defaultObject);
            case "Boolean":
                return preferences.getBoolean(key, (Boolean) defaultObject);
            case "Float":
                return preferences.getFloat(key, (Float) defaultObject);
            case "Long":
                return preferences.getLong(key, (Long) defaultObject);
        }
        return getObject(key);
    }

    private Object getObject(String key) {
        String wordBase64 = preferences.getString(key, "");
        byte[] base64 = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            Object object = bis.readObject();
            Log.d(this.getClass().getSimpleName(), "Get object success");
            return object;
        } catch (Exception e) {

        }
        Log.e(this.getClass().getSimpleName(), "Get object is error");
        return null;
    }
}

