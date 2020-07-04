package com.wzy.yuka.yuka;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.LengthUtil;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka.floatball.FloatBall;
import com.wzy.yuka.yuka.floatwindow.FloatWindow;
import com.wzy.yuka.yuka.floatwindow.SelectWindow_Auto;
import com.wzy.yuka.yuka.floatwindow.SelectWindow_Normal;
import com.wzy.yuka.yuka.floatwindow.SubtitleWindow;
import com.wzy.yuka.yuka.services.AudioService;
import com.wzy.yuka.yuka.services.ScreenShotService_Auto;
import com.wzy.yuka.yuka.services.ScreenShotService_Continue;
import com.wzy.yuka.yuka.services.ScreenShotService_Single;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/30.
 */
public class FloatWindowManager {
    private static FloatWindowManager manager = null;
    private WeakReference<Activity> mActivity_wr;
    private Intent mData;
    private int[][] mLocation;
    private FloatBall[] mFloatBalls;
    private FloatWindow[] mFloatWindows;
    private Intent sssa;
    private Intent sssc;
    private Intent ssss;
    private Intent as;
    private int sum = 0;

    private FloatWindowManager(Activity activity) {
        this.mActivity_wr = new WeakReference<>(activity);
        this.sssa = new Intent(mActivity_wr.get(), ScreenShotService_Auto.class);
        this.sssc = new Intent(mActivity_wr.get(), ScreenShotService_Continue.class);
        this.ssss = new Intent(mActivity_wr.get(), ScreenShotService_Single.class);
        this.as = new Intent(mActivity_wr.get(), AudioService.class);
    }

    public static void init(Activity Activity) {
        manager = new FloatWindowManager(Activity);
    }

    public static FloatWindowManager getInstance() throws FloatWindowManagerException {
        if (manager == null) {
            synchronized (FloatWindowManager.class) {
                if (manager == null) {
                    // 使用双重同步锁
                    throw new FloatWindowManagerException("FloatWindowManager is not initial yet");
                }
            }
        }
        return manager;
    }

    public Intent getData() {
        return mData;
    }

    public void setData(Intent data) {
        this.mData = data;
    }

    public int[][] getmLocation() throws FloatWindowManagerException {
        if (getNumOfFloatWindows() != 0) {
            mLocation = new int[mFloatWindows.length][4];
            for (int i = 0; i < mFloatWindows.length; i++) {
                mLocation[i] = mFloatWindows[i].location;
            }
        } else {
            throw new FloatWindowManagerException("No floatWindow initialized");
        }
        return mLocation;
    }

    public int getNumOfFloatWindows() {
        if (mFloatWindows != null) {
            return mFloatWindows.length;
        } else {
            return 0;
        }
    }

    public int getNumOfFloatBalls() {
        if (mFloatBalls != null) {
            return mFloatBalls.length;
        } else {
            return 0;
        }
    }

    public WeakReference<Activity> getActivityWeakRef() {
        return mActivity_wr;
    }

    public void add_FloatBall(FloatBall floatBall) {
        if (floatBall != null) {
            Log.e("TAG", "add_FloatBall: ");
            mFloatBalls = LengthUtil.appendIndex(mFloatBalls);
            mFloatBalls[mFloatBalls.length - 1] = floatBall;
            Log.e("TAG", "add_FloatBall: " + getNumOfFloatBalls());
        }
    }

    public void add_FloatWindow(String mode) throws FloatWindowManagerException {
        int limit = 1;
        sum += 1;
        FloatWindow floatWindow;
        switch (mode) {
            case "SWN_S":
                limit = 5;
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(mActivity_wr.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    if (getNumOfFloatWindows() > 0) {
                        if (!mFloatWindows[0].getClass().equals(SelectWindow_Normal.class)) {
                            Toast.makeText(mActivity_wr.get(), "暂不支持其他模式的多悬浮窗识别！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    mFloatWindows = LengthUtil.appendIndex(mFloatWindows);
                    floatWindow = new SelectWindow_Normal(mActivity_wr.get(), mFloatWindows.length - 1, "floatwindow" + sum);
                    mFloatWindows[mFloatWindows.length - 1] = floatWindow;
                }
                break;
            case "SWN_C":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(mActivity_wr.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    mFloatWindows = LengthUtil.appendIndex(mFloatWindows);
                    floatWindow = new SelectWindow_Normal(mActivity_wr.get(), mFloatWindows.length - 1, "floatwindow" + sum);
                    mFloatWindows[mFloatWindows.length - 1] = floatWindow;
                }
                break;
            case "SWA":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(mActivity_wr.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    mFloatWindows = LengthUtil.appendIndex(mFloatWindows);
                    floatWindow = new SelectWindow_Auto(mActivity_wr.get(), mFloatWindows.length - 1, "floatwindow" + sum);
                    mFloatWindows[mFloatWindows.length - 1] = floatWindow;
                }
                break;
            case "SBW":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(mActivity_wr.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    mFloatWindows = LengthUtil.appendIndex(mFloatWindows);
                    floatWindow = new SubtitleWindow(mActivity_wr.get(), mFloatWindows.length - 1, "floatwindow" + sum);
                    mFloatWindows[mFloatWindows.length - 1] = floatWindow;
                }
                break;
            default:
                throw new FloatWindowManagerException("unknown mode: " + mode);
        }

    }

    public void remove_FloatWindow(int index) {
        if (getNumOfFloatWindows() != 0) {
            mFloatWindows[index] = null;
        }
        mFloatWindows = LengthUtil.discardNull(mFloatWindows);
    }

    public void remove_FloatBall(int index) {
        if (getNumOfFloatBalls() != 0) {
            mFloatBalls[index].dismiss();
            mFloatBalls[index] = null;
        }
        mFloatBalls = LengthUtil.discardNull(mFloatBalls);
    }

    public void remove_AllFloatWindow() {
        if (getNumOfFloatWindows() != 0) {
            for (FloatWindow floatWindow : mFloatWindows) {
                floatWindow.dismiss();
                floatWindow = null;
            }
        }
    }

    public void remove_AllFloatBall() {
        if (getNumOfFloatBalls() != 0) {
            for (FloatBall floatBall : mFloatBalls) {
                floatBall.dismiss();
                floatBall = null;
            }
        }
    }

    public void show_all(boolean after, int index) throws FloatWindowManagerException {
        if (getNumOfFloatWindows() != 0) {
            if (index != 1000 && after) {
                mFloatWindows[index].show();
                mFloatWindows[index].showResults("before response", "目标图片已发送，请等待...", 0);
            } else {
                for (FloatWindow floatWindow : mFloatWindows) {
                    floatWindow.show();
                    if (after) {
                        floatWindow.showResults("before response", "目标图片已发送，请等待...", 0);
                    }
                }
            }
        } else {
            throw new FloatWindowManagerException("No floatWindow initialized");
        }
    }

    public void hide_all() throws FloatWindowManagerException {
        if (getNumOfFloatWindows() != 0) {
            for (FloatWindow floatWindows : mFloatWindows) {
                floatWindows.hide();
            }
        } else {
            throw new FloatWindowManagerException("No floatWindow initialized");
        }
    }


    public void start_ScreenShotTrans_normal(boolean ifContinue, int index) {
        if (judgeTypeOfFloatWindow(SelectWindow_Normal.class, 0)) {
            if (ifContinue) {
                //普通翻译-持续模式
                mLocation = new int[1][4];
                mLocation[0] = mFloatWindows[0].location;
                startService(sssc);
            } else {
                ssss.putExtra("index", index);
                //普通翻译-多悬浮窗模式
                if (index == 1000) {
                    //全部悬浮窗
                    startService(ssss);
                } else {
                    //单悬浮窗
                    startService(ssss);
                }
            }
        }
    }

    public void start_ScreenShotTrans_auto() {
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            mLocation = new int[1][4];
            mLocation[0] = mFloatWindows[0].location;
            startService(sssa);
        }
    }

    public void start_RecordingTrans() {
        if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
            mLocation = new int[1][4];
            mLocation[0] = mFloatWindows[0].location;
            startService(as);
        }
    }

    public void stop_ScreenShotTrans_normal(boolean ifContinue) {
        if (ifContinue) {
            ScreenShotService_Continue.stopScreenshot();
            mActivity_wr.get().stopService(sssc);
        } else {
            mActivity_wr.get().stopService(ssss);
        }
    }

    public void stop_ScreenShotTrans_auto() {
        mActivity_wr.get().stopService(sssa);
    }

    public void stop_RecordingTrans() {
        mActivity_wr.get().stopService(as);
    }


    public void show_result_normal(String origin, String translation, double time, int index) {
        if (judgeTypeOfFloatWindow(SelectWindow_Normal.class, index)) {
            mFloatWindows[index].showResults(origin, translation, time);
        }
    }

    public void show_result_auto(String origin, String translation) {
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            mFloatWindows[0].showResults(origin, translation, 0);
        }
    }

    public void show_result_subtitle(String origin, String translation) {
        if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
            mFloatWindows[0].showResults(origin, translation, 0);
        }
    }

    public void detect() {
        try {
            hide_all();
            if (judgeTypeOfFloatWindow(SelectWindow_Normal.class, 0)) {
                if (((SelectWindow_Normal) mFloatWindows[0]).isContinue) {
                    start_ScreenShotTrans_normal(true, 1000);
                } else {
                    start_ScreenShotTrans_normal(false, 1000);
                }
            } else if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
                start_ScreenShotTrans_auto();
            } else if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
                start_RecordingTrans();
            }
        } catch (FloatWindowManagerException e) {
            Toast.makeText(mActivity_wr.get(), "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
        }
    }

    public void reset() {
        boolean sync = (boolean) SharedPreferencesUtil.getInstance().getParam("settings_trans_syncMode", false);
        boolean auto = (boolean) SharedPreferencesUtil.getInstance().getParam("settings_auto_switch", false);
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            mFloatWindows[0].reset();
            return;
        }
        remove_AllFloatWindow();
        mLocation = null;
        mFloatWindows = null;
        try {
            if (sync) {
                add_FloatWindow("SBW");
            } else if (auto) {
                add_FloatWindow("SWA");
            } else if (GetParams.AdvanceSettings()[1] == 1) {
                add_FloatWindow("SWN_C");
            } else {
                add_FloatWindow("SWN_S");
            }
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }
    }


    private boolean judgeTypeOfFloatWindow(Class clazz, int index) {
        if (getNumOfFloatWindows() > index) {
            return mFloatWindows[index].getClass().equals(clazz);
        }
        return false;
    }

    private void startService(Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mActivity_wr.get().startService(intent);
        } else {
            mActivity_wr.get().startForegroundService(intent);
        }
    }
}
