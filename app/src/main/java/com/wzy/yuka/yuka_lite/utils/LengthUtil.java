package com.wzy.yuka.yuka_lite.utils;

/**
 * Created by Ziyan on 2020/8/15.
 */
public class LengthUtil {
    public static int[][] appendIndex(int[][] objects) {
        int[][] newObjects;
        if (objects == null) {
            newObjects = new int[1][4];
        } else {
            newObjects = new int[objects.length + 1][4];
            System.arraycopy(objects, 0, newObjects, 0, objects.length);
        }
        return newObjects;
    }

    public static String[] appendIndex(String[] objects) {
        String[] newObjects;
        if (objects == null) {
            newObjects = new String[1];
        } else {
            newObjects = new String[objects.length + 1];
            System.arraycopy(objects, 0, newObjects, 0, objects.length);
        }
        return newObjects;
    }
}
