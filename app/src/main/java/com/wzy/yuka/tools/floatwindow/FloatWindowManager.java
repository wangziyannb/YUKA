package com.wzy.yuka.tools.floatwindow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.LengthUtil;
import com.wzy.yuka.tools.screenshot.ScreenShotService_Single;

/**
 * The type Float window.
 */
public class FloatWindowManager {
    static final String TAG = "FloatWindow";
    //location 0 1 2 3 = lA 0 1 + lB 0 1
    private static int[][] location;
    private static SelectWindow[] selectWindows;
    private static FloatBall floatBall;

    public static void initFloatWindow(Activity activity) {
        floatBall = new FloatBall(activity, "mainFloatBall");
    }

    /**
     * 增加一个取词窗对象
     *
     * @param activity the activity
     */
    static void addSelectWindow(Activity activity) {
        if (getNumOfFloatWindows() == 5) {
            Toast.makeText(activity, "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
        } else {
            location = LengthUtil.appendIndex(location);
            selectWindows = LengthUtil.appendIndex(selectWindows);
            selectWindows[selectWindows.length - 1] = new SelectWindow(activity, "selectWindow" + (selectWindows.length - 1), selectWindows.length - 1);
        }
    }

    /**
     * 开始截屏
     * 所有的窗口一起参与截屏
     * 现在由主悬浮球调用
     *
     * @param activity the activity
     */
    static void startScreenShot(Activity activity) {
        setLocation();
        Intent service = new Intent(activity, ScreenShotService_Single.class);
        if (getNumOfFloatWindows() != 0) {
            hideAllFloatWindow();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                activity.startService(service);
            } else {
                activity.startForegroundService(service);
            }
        }
    }

    /**
     * 开始截屏
     * 谁要求截屏谁提交自己位于数组中的index
     * 现在由SelectWindow对象调用
     *
     * @param activity intent使用
     * @param index    调用的对象的index
     */
    static void startScreenShot(Activity activity, int index) {
        location = new int[1][4];
        location[0] = selectWindows[index].location;
        Intent service = new Intent(activity, ScreenShotService_Single.class);
        service.putExtra("index", index);
        if (getNumOfFloatWindows() != 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                activity.startService(service);
            } else {
                activity.startForegroundService(service);
            }
        }
    }

    static void startScreenShot(Activity activity, boolean yes) {

    }

    /**
     * 获取location
     *
     * @return location
     */
    public static int[][] getLocation() {
        return location;
    }

    /**
     * 要求所有在数组中的SelectWindow对象上报自己的位置
     * 现在由 startSS(Activity activity) 调用
     */
    private static void setLocation() {
        if (getNumOfFloatWindows() != 0) {
            location = new int[selectWindows.length][4];
            for (int i = 0; i < selectWindows.length; i++) {
                location[i] = selectWindows[i].location;
            }
        } else {
            Log.e(TAG, "error in getLocation:selectWindows is not initialized");
        }
    }

    /**
     * 获取所有的在数组中的SelectWindow对象的TextView.
     *
     * @return TextView数组
     */
    public static TextView[] getAllTextViews() {
        if (getNumOfFloatWindows() != 0) {
            TextView[] textViews = new TextView[selectWindows.length];
            for (int i = 0; i < selectWindows.length; i++) {
                textViews[i] = selectWindows[i].getTextView();
            }
            return textViews;
        } else {
            Log.e(TAG, "error in getAllTextViews:selectWindows is not initialized");
        }
        return null;
    }

    /**
     * 隐藏所有在数组中的SelectWindow对象的悬浮框
     */
    public static void hideAllFloatWindow() {
        if (getNumOfFloatWindows() != 0) {
            for (SelectWindow selectWindow : selectWindows) {
                selectWindow.hide();
            }
        } else {
            Log.e(TAG, "error in hideAllFloatWindow:selectWindows is not initialized");
        }
    }

    /**
     * 显示所有在数组中的SelectWindow对象的悬浮框
     *
     * @param after 如果是截图后的，应当更改文字提示用户
     * @param index 如果提供了有意义的index，则代表仅一个选词窗调用了截图
     */
    public static void showAllFloatWindow(boolean after, int index) {
        if (getNumOfFloatWindows() != 0) {
            if (index != 1000 && after) {
                selectWindows[index].show();
                TextView textView = selectWindows[index].getTextView();
                textView.setText("目标图片已发送，请等待...");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundResource(R.color.blackBg);
            } else {
                for (SelectWindow selectWindow : selectWindows) {
                    selectWindow.show();
                    if (after) {
                        TextView textView = selectWindow.getTextView();
                        textView.setText("目标图片已发送，请等待...");
                        textView.setTextColor(Color.WHITE);
                        textView.setBackgroundResource(R.color.blackBg);
                    }
                }
            }
        }
    }

    /**
     * 删除所有在数组中的SelectWindow对象
     *
     * @param except 是否将主悬浮球也一并删除
     */
    public static void dismissAllFloatWindow(boolean except) {
        if (getNumOfFloatWindows() != 0) {
            for (SelectWindow selectWindow : selectWindows) {
                selectWindow.dismiss();
                selectWindow = null;
            }
        }
        if (!except) {
            floatBall.dismiss();
        }
    }

    /**
     * 重置功能：
     * 删除所有SelectWindow对象，
     * 将SelectWindow数组、已有的location数组置空，
     * 并初始化为一个窗口
     *
     * @param activity the activity
     */
    static void reset(Activity activity) {
        dismissAllFloatWindow(true);
        location = null;
        selectWindows = null;
        addSelectWindow(activity);
    }

    /**
     * 获得活动中的取词窗总数
     *
     * @return the int
     */
    public static int getNumOfFloatWindows() {
        if (selectWindows != null) {
            return selectWindows.length;
        } else {
            return 0;
        }
    }

}


