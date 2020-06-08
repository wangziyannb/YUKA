package com.wzy.yuka.core.floatwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.wzy.yuka.core.audio.AudioService;
import com.wzy.yuka.core.screenshot.ScreenShotService_Auto;
import com.wzy.yuka.core.screenshot.ScreenShotService_Continue;
import com.wzy.yuka.core.screenshot.ScreenShotService_Single;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.LengthUtil;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;


public class FloatWindowManager {
    private static final String TAG = "FloatWindow";
    //location 0 1 2 3 = lA 0 1 + lB 0 1
    private static int[][] location;
    private static FloatWindows[] FloatWindows;
    private static SubtitleWindow subtitleWindow;
    public static FloatBall floatBall;
    private static int sum = 0;
    private static Intent data;

    public static void initFloatWindow(Activity activity, Intent mdata) {
        data = mdata;
        floatBall = new FloatBall(activity, "mainFloatBall");
    }

    /**
     * 增加一个取词窗对象
     *
     * @param activity the activity
     */
    static void addSelectWindow(Activity activity) {
        boolean sync = (boolean) SharedPreferencesUtil.getInstance().getParam("settings_trans_syncMode", false);
        boolean auto = (boolean) SharedPreferencesUtil.getInstance().getParam("settings_auto_switch", false);
        if (sync) {
            if (getNumOfFloatWindows() != 0) {
                Toast.makeText(activity, "屏幕翻译和同传不能同时使用！", Toast.LENGTH_SHORT).show();
            } else {
                if (getNumOfSubtitleWindows() == 1) {
                    Toast.makeText(activity, "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    subtitleWindow = new SubtitleWindow(activity, "subtitleWindow");
                }
            }
        } else if (auto) {
            if (getNumOfFloatWindows() != 0) {
                Toast.makeText(activity, "普通翻译和自动翻译不能同时使用！", Toast.LENGTH_SHORT).show();
            } else {
                if (getNumOfSubtitleWindows() == 1) {
                    Toast.makeText(activity, "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    location = LengthUtil.appendIndex(location);
                    FloatWindows = LengthUtil.appendIndex(FloatWindows);
                    FloatWindows[FloatWindows.length - 1] = new SelectWindow_Auto(activity, "selectWindow" + sum, FloatWindows.length - 1);
                    sum += 1;
                }
            }
        } else {
            int limit = 5;
            if (GetParams.AdvanceSettings()[1] == 1) {
                limit = 1;
            }
            if (getNumOfFloatWindows() == limit) {
                Toast.makeText(activity, "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
            } else {
                location = LengthUtil.appendIndex(location);
                FloatWindows = LengthUtil.appendIndex(FloatWindows);
                FloatWindows[FloatWindows.length - 1] = new SelectWindow_Normal(activity, "selectWindow" + sum, FloatWindows.length - 1);
                sum += 1;
            }
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
        if (GetParams.AdvanceSettings()[1] == 1) {
            service = new Intent(activity, ScreenShotService_Continue.class);
        } else if ((boolean) SharedPreferencesUtil.getInstance().getParam("settings_auto_switch", false)) {
            service = new Intent(activity, ScreenShotService_Auto.class);
        }
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
     * 根据设置不同，开始的service不同
     *
     * @param activity intent使用
     * @param index    调用的对象的index
     */
    static void startScreenShot(Activity activity, int index) {
        location = new int[1][4];
        location[0] = FloatWindows[index].location;
        Intent service = new Intent(activity, ScreenShotService_Single.class);
        if (GetParams.AdvanceSettings()[1] == 1 && !(boolean) SharedPreferencesUtil.getInstance().getParam("settings_auto_switch", false)) {
            service = new Intent(activity, ScreenShotService_Continue.class);
        } else if ((boolean) SharedPreferencesUtil.getInstance().getParam("settings_auto_switch", false)) {
            service = new Intent(activity, ScreenShotService_Auto.class);
        }
        service.putExtra("index", index);
        if (getNumOfFloatWindows() != 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                activity.startService(service);
            } else {
                activity.startForegroundService(service);
            }
        }
    }

    static void startVoiceTrans(Activity activity) {
        Intent service = new Intent(activity, AudioService.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            activity.startService(service);
        } else {
            activity.startForegroundService(service);
        }
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
            location = new int[FloatWindows.length][4];
            for (int i = 0; i < FloatWindows.length; i++) {
                location[i] = FloatWindows[i].location;
            }
        } else {
            Log.e(TAG, "error in getLocation:selectWindows is not initialized");
        }
    }

    public static Intent getData() {
        return data;
    }

    /**
     * 显示结果于指定index的悬浮窗中
     * origin可指定参数，如yuka error为okhttp出现错误
     *
     * @param origin      原文，可能为参数
     * @param translation 译文，可能为参数对应字符串
     * @param time        耗时，错误时为0
     * @param index       the index
     */
    public static void showResultsIndex(String origin, String translation, double time, int index) {
        if (getNumOfFloatWindows() != 0) {
            FloatWindows[index].showResults(origin, translation, time);
        }
    }

    public static void showSubtitle(String origin, String translation) {
        subtitleWindow.showResults(origin, translation);
    }

    /**
     * 隐藏所有在数组中的SelectWindow对象的悬浮框
     */
    public static void hideAllFloatWindow() {
        if (getNumOfFloatWindows() != 0) {
            for (FloatWindows floatWindows : FloatWindows) {
                floatWindows.hide();
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
                FloatWindows[index].show();
                FloatWindows[index].showResults("before response", "目标图片已发送，请等待...", 0);
            } else {
                for (FloatWindows floatWindow : FloatWindows) {
                    floatWindow.show();
                    if (after) {
                        floatWindow.showResults("before response", "目标图片已发送，请等待...", 0);
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
            for (FloatWindows floatWindow : FloatWindows) {
                floatWindow.dismiss();
                floatWindow = null;
            }
        }
        if (getNumOfSubtitleWindows() != 0) {
            subtitleWindow.dismiss();
        }
        if (!except) {
            floatBall.dismiss();
            floatBall = null;
        }
    }

    /**
     * 根据index删除一个SelectWindow对象
     * 目前由SelectWindow对象调用
     *
     * @param index the index
     */
    static void dismissFloatWindow(int index) {

        if (getNumOfFloatWindows() != 0) {
            FloatWindows[index] = null;
        }
        FloatWindows = LengthUtil.discardNull(FloatWindows);
    }

    static void dismissSubtitleWindow() {
        subtitleWindow = null;
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
        if (getNumOfFloatWindows() == 1) {
            if (FloatWindows[0].getClass().equals(SelectWindow_Auto.class)) {
                ((SelectWindow_Auto) FloatWindows[0]).removeLittleWindows();
                ((SelectWindow_Auto) FloatWindows[0]).showMain();
                return;
            }
        }

        dismissAllFloatWindow(true);
        location = null;
        FloatWindows = null;
        subtitleWindow = null;
        addSelectWindow(activity);
    }

    /**
     * 获得活动中的取词窗总数
     *
     * @return the int
     */
    public static int getNumOfFloatWindows() {
        if (FloatWindows != null) {
            return FloatWindows.length;
        } else {
            return 0;
        }
    }

    public static int getNumOfSubtitleWindows() {
        if (subtitleWindow != null) {
            return 1;
        } else {
            return 0;
        }
    }
}


