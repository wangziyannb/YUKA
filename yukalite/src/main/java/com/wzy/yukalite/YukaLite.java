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
    public static void init(Context application, String uuid) {
        UserManager.init(application, uuid);
        try {
            UserManager.getUser();
        } catch (YukaUserManagerException e) {
            UserManager.setLogin(false);
        }

    }

    public static void yuka(@NotNull Callback callback) {
        YukaRequest.yuka(callback);
    }

    /**
     * @param config   识别器等参数文件
     * @param image    图片文件
     * @param callback 返回信息
     */
    public static void request(@NotNull YukaConfig config, @NotNull File image, @NotNull Callback callback) {
        try {
            YukaRequest.request(config, image, getUser(), callback);
        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }
    }

    public static void request(@NotNull YukaConfig config, @NotNull File[] images, @NotNull Callback[] callbacks) {
        try {
            YukaRequest.request(config, images, getUser(), callbacks);
        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(@NotNull String u_name, @NotNull String pwd) {
        UserManager.addUser(u_name, pwd);
    }

    public static void removeUser() {
        UserManager.removeUser();
    }

    public static void request(@NotNull YukaConfig config, @NotNull String origin, @NotNull Callback callback) {
        try {
            YukaRequest.request(config, origin, getUser(), callback);
        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }
    }

    public static void login(@NotNull String u_name, @NotNull String pwd, @NotNull Callback callback) throws YukaUserManagerException {
        UserManager.addUser(u_name, pwd);
        UserManager.login(callback);
    }

    public static void login(@NotNull Callback callback) throws YukaUserManagerException {
        UserManager.login(callback);
    }

    public static void logout(@NotNull Callback callback) throws YukaUserManagerException {
        UserManager.logout(callback);
    }

    public static void refreshInfo(@NotNull Callback callback) throws YukaUserManagerException {
        UserManager.refreshInfo(callback);
    }

    public static void register(String[] params, Callback callback) {
        UserManager.register(params, callback);
    }

    public static void forget_password(String[] params, Callback callback) {
        UserManager.forget_password(params, callback);
    }

    public static void check_feasibility(String mode, String param, Callback callback) {
        UserManager.check_feasibility(mode, param, callback);
    }

    public static void activate(String cdkey, Callback callback) throws YukaUserManagerException {
        UserManager.activate(cdkey, callback);
    }

    public static boolean isLogin() {
        return UserManager.isLogin();
    }

    public static void setLogin(boolean i) {
        UserManager.setLogin(i);
    }

    public static String[] getUser() throws YukaUserManagerException {
        return UserManager.getUser();
    }

    public static String getId() throws YukaUserManagerException {
        return UserManager.getId();
    }
}
