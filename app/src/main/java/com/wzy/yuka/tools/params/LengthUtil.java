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

    public static SelectWindow[] decreaseIndex(SelectWindow[] objects) {
        SelectWindow[] newObjects;
        if (objects == null) {
            newObjects = objects;
        } else {
            newObjects = new SelectWindow[objects.length - 1];
            for (int i = 0; i < newObjects.length; i++) {
                newObjects[i] = objects[i];
            }
        }
        return newObjects;
    }

    public static SelectWindow[] discardNull(SelectWindow[] objects) {
        SelectWindow[] newObjects = null;
        if (objects == null) {
            return newObjects;
        } else {
            int j = 0;
            int length = objects.length;
            do {
                if (objects[j] == null) {
                    j += 1;
                } else {
                    newObjects = appendIndex(newObjects);
                    newObjects[newObjects.length - 1] = objects[j];
                    newObjects[newObjects.length - 1].setIndex(newObjects.length - 1);
                    j += 1;
                }
            } while (j < length);
        }
        return newObjects;
    }

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
