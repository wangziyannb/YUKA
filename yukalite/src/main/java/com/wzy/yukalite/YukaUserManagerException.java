package com.wzy.yukalite;

/**
 * Created by Ziyan on 2021/1/17.
 */
public class YukaUserManagerException extends Exception {
    final public static String NO_USER = "No User Available";

    public YukaUserManagerException(String msg) {
        super(msg);
    }
}
