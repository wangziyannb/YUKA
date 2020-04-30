package com.wzy.yuka.tools.params;

import com.wzy.yuka.tools.floatwindow.SelectWindow;

/**
 * Created by Ziyan on 2020/4/30.
 */
public class LengthUtil {
    public static SelectWindow[] appendIndex(SelectWindow[] objects) {
        SelectWindow[] newObjects;
        if (objects == null) {
            newObjects = new SelectWindow[1];
        } else {
            newObjects = new SelectWindow[objects.length + 1];
            for (int i = 0; i < objects.length; i++) {
                newObjects[i] = objects[i];
            }
        }
        return newObjects;
    }

    public static int[][] appendIndex(int[][] objects) {
        int[][] newObjects;
        if (objects == null) {
            newObjects = new int[1][4];
        } else {
            newObjects = new int[objects.length + 1][4];
            for (int i = 0; i < objects.length; i++) {
                newObjects[i] = objects[i];
            }
        }
        return newObjects;
    }
}
