package com.wzy.yuka.yuka;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.wzy.yuka.tools.params.LengthUtil;
import com.wzy.yuka.yuka.floatball.FloatBall;
import com.wzy.yuka.yuka.floatwindow.FloatWindow;
import com.wzy.yuka.yuka.floatwindow.SelectWindow_Auto;
import com.wzy.yuka.yuka.floatwindow.SelectWindow_Normal;
import com.wzy.yuka.yuka.floatwindow.SubtitleWindow;
import com.wzy.yuka.yuka.services.AudioService;
import com.wzy.yuka.yuka.services.ScreenShotService_Auto;
import com.wzy.yuka.yuka.services.ScreenShotService_Continue;
import com.wzy.yuka.yuka.services.ScreenShotService_Single;
import com.wzy.yuka.yuka.services.ScreenStatusService;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/30.
 */
public class FloatWindowManager {
    private static FloatWindowManager manager = null;
    private WeakReference<Application> applicationWeakReference;
    private Intent mData;
    private int[][] mLocation;
    private FloatBall[] mFloatBalls;
    private FloatWindow[] mFloatWindows;
    private Intent mScreenShotAutoService;
    private Intent mScreenShotContinueService;
    private Intent mScreenShotSingleService;
    private Intent mAudioService;
    private Intent mScreenStatusService;
    private int sum = 0;
    private String lastMode = "SWN_S";
    private ScreenStatusService.MyBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ScreenStatusService.MyBinder) service;
            binder.getService().setConfigurationListener(newConfig -> notifyConfigurationChanged(newConfig));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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

    public int[][] getmLocation(int index) throws FloatWindowManagerException {
        Log.e("TAG", "getmLocation: " + index);
        if (getNumOfFloatWindows() != 0) {
            if (index == 1000) {
                mLocation = new int[mFloatWindows.length][4];
                for (int i = 0; i < mFloatWindows.length; i++) {
                    mLocation[i] = mFloatWindows[i].location;
                }
            } else {
                mLocation = new int[1][4];
                mLocation[0] = mFloatWindows[index].location;
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

    private FloatWindowManager(Application application) {
        this.applicationWeakReference = new WeakReference<>(application);
        this.mScreenShotAutoService = new Intent(applicationWeakReference.get(), ScreenShotService_Auto.class);
        this.mScreenShotContinueService = new Intent(applicationWeakReference.get(), ScreenShotService_Continue.class);
        this.mScreenShotSingleService = new Intent(applicationWeakReference.get(), ScreenShotService_Single.class);
        this.mAudioService = new Intent(applicationWeakReference.get(), AudioService.class);
        this.mScreenStatusService = new Intent(applicationWeakReference.get(), ScreenStatusService.class);
    }

    public void add_FloatBall(FloatBall floatBall) {
        if (floatBall != null) {
            mFloatBalls = LengthUtil.appendIndex(mFloatBalls);
            mFloatBalls[mFloatBalls.length - 1] = floatBall;
        }
    }

    public void add_FloatWindow(FloatWindow floatWindow) {
        if (floatWindow != null) {
            mFloatWindows = LengthUtil.appendIndex(mFloatWindows);
            floatWindow.setIndex(mFloatWindows.length - 1);
            mFloatWindows[mFloatWindows.length - 1] = floatWindow;
        }
    }

    public static void init(Application application) {
        manager = new FloatWindowManager(application);
    }

    public FloatWindow get_FloatWindow(int index) throws FloatWindowManagerException {
        if (getNumOfFloatWindows() > index) {
            return mFloatWindows[index];
        } else {
            throw new FloatWindowManagerException("Index illegal");
        }
    }

    public FloatBall get_FloatBall(int index) throws FloatWindowManagerException {
        if (getNumOfFloatBalls() > index) {
            if (mFloatBalls[index].getClass().equals(FloatBall.class)) {
                return mFloatBalls[index];
            }
        }
        throw new FloatWindowManagerException("Index illegal");
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
        mFloatWindows = null;
    }

    public void remove_AllFloatBall() {
        if (getNumOfFloatBalls() != 0) {
            for (FloatBall floatBall : mFloatBalls) {
                floatBall.dismiss();
                floatBall = null;
            }
        }
        mFloatBalls = null;
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

    public WeakReference<Application> getApplicationWeakReference() {
        return applicationWeakReference;
    }

    public void add_FloatWindow(String mode) throws FloatWindowManagerException {
        int limit = 1;
        sum += 1;
        FloatWindow floatWindow;
        switch (mode) {
            case "SWN_S":
                limit = 5;
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    if (getNumOfFloatWindows() > 0) {
                        if (!mFloatWindows[0].getClass().equals(SelectWindow_Normal.class)) {
                            Toast.makeText(applicationWeakReference.get(), "暂不支持其他模式的多悬浮窗识别！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    floatWindow = new SelectWindow_Normal(applicationWeakReference.get(), 0, "floatwindow" + sum, false);
                    add_FloatWindow(floatWindow);
                    lastMode = mode;
                }
                break;
            case "SWN_C":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    floatWindow = new SelectWindow_Normal(applicationWeakReference.get(), 0, "floatwindow" + sum, true);
                    add_FloatWindow(floatWindow);
                    lastMode = mode;
                }
                break;
            case "SWA":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    floatWindow = new SelectWindow_Auto(applicationWeakReference.get(), 0, "floatwindow" + sum);
                    add_FloatWindow(floatWindow);
                    lastMode = mode;
                }
                break;
            case "SBW":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        Toast.makeText(applicationWeakReference.get(), "需要安卓10才可使用同步字幕", Toast.LENGTH_SHORT).show();
                    } else {
                        floatWindow = new SubtitleWindow(applicationWeakReference.get(), 0, "floatwindow" + sum);
                        add_FloatWindow(floatWindow);
                        lastMode = mode;
                    }
                }
                break;
            default:
                throw new FloatWindowManagerException("unknown mode: " + mode);
        }

    }

    public void start_ScreenShotTrans_normal(boolean ifContinue, int index) {
        if (judgeTypeOfFloatWindow(SelectWindow_Normal.class, 0)) {
            if (ifContinue) {
                //普通翻译-持续模式
                mLocation = new int[1][4];
                mLocation[0] = mFloatWindows[0].location;
                startService(mScreenShotContinueService);
            } else {
                mScreenShotSingleService.putExtra("index", index);
                //普通翻译-多悬浮窗模式
                if (index == 1000) {
                    //全部悬浮窗
                    startService(mScreenShotSingleService);
                } else {
                    //单悬浮窗
                    mScreenShotSingleService.putExtra(ScreenShotService_Single.index, index);
                    startService(mScreenShotSingleService);
                }
            }
        }
    }

    public void start_ScreenShotTrans_auto() {
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            mLocation = new int[1][4];
            mLocation[0] = mFloatWindows[0].location;
            startService(mScreenShotAutoService);
        }
    }

    public void start_RecordingTrans() {
        if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
            mLocation = new int[1][4];
            mLocation[0] = mFloatWindows[0].location;
            startService(mAudioService);
        }
    }

    public void stop_ScreenShotTrans_normal(boolean ifContinue) {
        if (ifContinue) {
            ScreenShotService_Continue.stopScreenshot();
            applicationWeakReference.get().stopService(mScreenShotContinueService);
        } else {
            applicationWeakReference.get().stopService(mScreenShotSingleService);
        }
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

    public void stop_ScreenShotTrans_auto() {
        applicationWeakReference.get().stopService(mScreenShotAutoService);
    }


    public void reset() {
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            mFloatWindows[0].reset();
            return;
        }
        remove_AllFloatWindow();
        mLocation = null;
        try {
            add_FloatWindow(lastMode);
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

    public void stop_RecordingTrans() {
        applicationWeakReference.get().stopService(mAudioService);
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
            Toast.makeText(applicationWeakReference.get(), "还没有悬浮窗初始化呢！", Toast.LENGTH_SHORT).show();
        }
    }

    private void startService(Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            applicationWeakReference.get().startService(intent);
        } else {
            applicationWeakReference.get().startForegroundService(intent);
        }
    }

    public void startScreenStatusService() {
        applicationWeakReference.get().getApplicationContext().startService(mScreenStatusService);
        applicationWeakReference.get().getApplicationContext().bindService(mScreenStatusService, connection, Service.BIND_AUTO_CREATE);
    }

    public void stopScreenStatusService() {
        applicationWeakReference.get().getApplicationContext().unbindService(connection);
        applicationWeakReference.get().getApplicationContext().stopService(mScreenStatusService);
    }

    private void notifyConfigurationChanged(Configuration newConfig) {
        if (getNumOfFloatBalls() > 0) {
            for (FloatBall fb : mFloatBalls) {
                fb.onConfigurationChanged(newConfig);
            }
        }
        if (getNumOfFloatWindows() > 0) {
            for (FloatWindow fw : mFloatWindows) {
                fw.onConfigurationChanged(newConfig);
            }
        }
    }

    public boolean isFullScreen() {
        return binder.isFullscreen();
    }
}
