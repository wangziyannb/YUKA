package com.wzy.yuka.yuka_lite;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.wzy.yuka.yuka_lite.floatball.MainFloatBall;
import com.wzy.yuka.yuka_lite.floatwindow.SelectWindow_Auto;
import com.wzy.yuka.yuka_lite.floatwindow.SelectWindow_Normal;
import com.wzy.yuka.yuka_lite.floatwindow.SubtitleWindow;
import com.wzy.yuka.yuka_lite.services.ScreenShotService_Single;
import com.wzy.yukafloatwindows.FloatWindowManager;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;

/**
 * Created by Ziyan on 2020/8/14.
 */
public class YukaFloatWindowManager extends FloatWindowManager {
    private static YukaFloatWindowManager manager = null;
    private Intent mData;
    private Intent mScreenShotAutoService;
    private Intent mScreenShotContinueService;
    private Intent mScreenShotSingleService;
    private Intent mAudioService;
    private int sum = 0;
    private String lastMode = "SWN_S";
    private ScreenShotService_Single.SingleBinder singleBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if (service.getClass().equals(ScreenShotService_Single.SingleBinder.class)) {
                singleBinder = (ScreenShotService_Single.SingleBinder) service;
            }
//            else if(service.getClass().equals(ScreenShotService_Single.SingleBinder.class)){
//                singleBinder = (ScreenShotService_Single.SingleBinder) service;
//            }else if(service.getClass().equals(ScreenShotService_Single.SingleBinder.class)){
//                singleBinder = (ScreenShotService_Single.SingleBinder) service;
//            }else if(service.getClass().equals(ScreenShotService_Single.SingleBinder.class)){
//                singleBinder = (ScreenShotService_Single.SingleBinder) service;
//            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            singleBinder = null;
        }
    };

    private YukaFloatWindowManager(Application application) {
        super(application);
//        this.mScreenShotAutoService = new Intent(applicationWeakReference.get(), ScreenShotService_Auto.class);
//        this.mScreenShotContinueService = new Intent(applicationWeakReference.get(), ScreenShotService_Continue.class);
        this.mScreenShotSingleService = new Intent(applicationWeakReference.get(), ScreenShotService_Single.class);
//        this.mAudioService = new Intent(applicationWeakReference.get(), AudioService.class);
    }

    public static YukaFloatWindowManager getInstance() throws FloatWindowManagerException {
        if (manager == null) {
            synchronized (YukaFloatWindowManager.class) {
                if (manager == null) {
                    // 使用双重同步锁
                    throw new FloatWindowManagerException("FloatWindowManager is not initial yet");
                }
            }
        }
        return manager;
    }

    public static YukaFloatWindowManager getInstance(Application application) {
        if (manager == null) {
            synchronized (YukaFloatWindowManager.class) {
                if (manager == null) {
                    // 使用双重同步锁
                    manager = new YukaFloatWindowManager(application);
                    return manager;
                }
            }
        }
        return manager;
    }

    public static void init(Application application) {
        manager = new YukaFloatWindowManager(application);
    }

    public Intent getData() {
        return mData;
    }

    public void setData(Intent data) {
        this.mData = data;
    }

    public void addFloatBall(String mode) {
        int limit = 1;
        switch (mode) {
            case "mainFloatBall":
                if (getNumOfFloatBalls() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮球啦！", Toast.LENGTH_SHORT).show();
                } else {
                    add_FloatBall(new MainFloatBall(0, "mainFloatBall", this));
                }
        }

    }

    public void addFloatWindow(String mode) throws FloatWindowManagerException {
        int limit = 1;

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
                    sum += 1;
                    floatWindow = new SelectWindow_Normal(0, "floatwindow" + sum, this, false);
                    add_FloatWindow(floatWindow);
                    lastMode = mode;
                }
                break;
            case "SWN_C":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    sum += 1;
                    floatWindow = new SelectWindow_Normal(0, "floatwindow" + sum, this, true);
                    add_FloatWindow(floatWindow);
                    lastMode = mode;
                }
                break;
            case "SWA":
                if (getNumOfFloatWindows() >= limit) {
                    Toast.makeText(applicationWeakReference.get(), "已经有太多的悬浮窗啦！", Toast.LENGTH_SHORT).show();
                } else {
                    sum += 1;
                    floatWindow = new SelectWindow_Auto(0, "floatwindow" + sum, this);
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
                        sum += 1;
                        floatWindow = new SubtitleWindow(0, "floatwindow" + sum, this);
                        add_FloatWindow(floatWindow);
                        lastMode = mode;
                    }
                }
                break;
            default:
                throw new FloatWindowManagerException("unknown mode: " + mode);
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
            Toast.makeText(applicationWeakReference.get(), "需要点击重置先初始化悬浮窗哦！", Toast.LENGTH_SHORT).show();
        }
    }

    public void start_ScreenShotTrans_normal(boolean ifContinue, int index) {
        if (judgeTypeOfFloatWindow(SelectWindow_Normal.class, 0)) {
            if (ifContinue) {
                //普通翻译-持续模式
//                mLocation = new int[1][4];
//                mLocation[0] = mFloatWindows[0].location;
//                startService(mScreenShotContinueService);
            } else {
                //普通翻译-多悬浮窗模式
                if (singleBinder == null) {
                    //服务还没启动
                    startService(mScreenShotSingleService);
                    applicationWeakReference.get().getApplicationContext().bindService(mScreenShotSingleService, connection, Service.BIND_AUTO_CREATE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    if (index == 1000) {
                        //全部悬浮窗
                        handler.postDelayed(() -> singleBinder.getService().getScreenshot(mFloatWindows), 100);
                    } else {
                        //单悬浮窗
                        handler.postDelayed(() -> singleBinder.getService().getScreenshot(mFloatWindows[index]), 100);
                    }
                } else {
                    //服务已经启动
                    if (index == 1000) {
                        //全部悬浮窗
                        singleBinder.getService().getScreenshot(mFloatWindows);
                    } else {
                        //单悬浮窗
                        singleBinder.getService().getScreenshot(mFloatWindows[index]);
                    }
                }
            }
        }
    }

    public void start_ScreenShotTrans_auto() {
//        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
//            mLocation = new int[1][4];
//            mLocation[0] = mFloatWindows[0].location;
//            startService(mScreenShotAutoService);
//        }
    }

    public void start_RecordingTrans() {
//        if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
//            mLocation = new int[1][4];
//            mLocation[0] = mFloatWindows[0].location;
//            startService(mAudioService);
//        }
    }

    public void stop_ScreenShotTrans_normal(boolean ifContinue) {
//        if (ifContinue) {
//            ScreenShotService_Continue.stopScreenshot();
//            applicationWeakReference.get().stopService(mScreenShotContinueService);
//        } else {
//            applicationWeakReference.get().stopService(mScreenShotSingleService);
//        }
    }

    public void stop_ScreenShotTrans_auto() {
//        applicationWeakReference.get().stopService(mScreenShotAutoService);
    }

    public void stop_RecordingTrans() {
//        applicationWeakReference.get().stopService(mAudioService);
    }


    private boolean judgeTypeOfFloatWindow(Class clazz, int index) {
        if (getNumOfFloatWindows() > index) {
            return mFloatWindows[index].getClass().equals(clazz);
        }
        return false;
    }

    private void startService(Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            applicationWeakReference.get().startService(intent);
        } else {
            applicationWeakReference.get().startForegroundService(intent);
        }
    }
}
