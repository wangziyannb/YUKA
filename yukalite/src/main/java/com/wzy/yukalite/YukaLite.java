package com.wzy.yukalite;

import android.content.Context;

import com.wzy.yukalite.config.YukaConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.Callback;


/**
 * Created by Ziyan on 2020/8/14.
 */
public class YukaLite {
    public static void init(Context application) {
        UserManager.init(application);
    }


    /**
     * @param config   识别器等参数文件
     * @param image    图片文件
     * @param callback 返回信息
     */
    public static void request(@NotNull YukaConfig config, @NotNull File image, @NotNull Callback callback) {
        YukaRequest.request(config, image, getUser(), callback);
    }

//    public static void request(@NotNull YukaConfig config,@NotNull String filePath,@NotNull Callback callback){
//        if (filePath.length == callbacks.length) {
//            for (int i = 0; i < filePath.length; i++) {
//                yuka(params, filePath[i], callbacks[i]);
//            }
//        } else {
//            Log.e(Tag, filePath.length + "");
//            Log.e(Tag, callbacks.length + "");
//            Log.e(Tag, "Number not match");
//        }
//    }
//
//    public static void request(@NotNull YukaConfig config,@NotNull String[] filePath,@NotNull Callback[] callbacks){
//        if (filePath.length == callbacks.length) {
//            for (int i = 0; i < filePath.length; i++) {
//                request(config, filePath[i], callbacks[i]);
//            }
//        } else {
//            Log.e(Tag, filePath.length + "");
//            Log.e(Tag, callbacks.length + "");
//            Log.e(Tag, "Number not match");
//        }
//    }

    public static void request(@NotNull YukaConfig config, @NotNull File[] images, @NotNull Callback[] callbacks) {
        YukaRequest.request(config, images, getUser(), callbacks);
    }

    public static void request(@NotNull YukaConfig config, @NotNull String origin, @NotNull Callback callback) {
        YukaRequest.request(config, origin, getUser(), callback);
    }

    public static void login(@NotNull String u_name, @NotNull String pwd, @NotNull String id, @NotNull Callback callback) {
        UserManager.addUser(u_name, pwd, id);
        try {
            UserManager.login(callback);
        } catch (UserManager.YukaUserManagerException e) {
            e.printStackTrace();
        }
    }

    public static void login(@NotNull Callback callback) {
        try {
            UserManager.login(callback);
        } catch (UserManager.YukaUserManagerException e) {
            e.printStackTrace();
        }
    }

    public static void logout(@NotNull Callback callback) {
        try {
            UserManager.logout(callback);
        } catch (UserManager.YukaUserManagerException e) {
            e.printStackTrace();
        }
        UserManager.removeUser();
    }

    public static void refreshInfo(Callback callback) {
        try {
            UserManager.refreshInfo(callback);
        } catch (UserManager.YukaUserManagerException e) {
            e.printStackTrace();
        }
    }


    public static String[] getUser() {
        try {
            return UserManager.getUser();
        } catch (UserManager.YukaUserManagerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
