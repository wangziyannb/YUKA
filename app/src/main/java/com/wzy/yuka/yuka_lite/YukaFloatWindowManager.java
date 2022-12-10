package com.wzy.yuka.yuka_lite;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.wzy.yuka.yuka_lite.floatball.MainFloatBall;
import com.wzy.yuka.yuka_lite.floatwindow.SelectWindow_Auto;
import com.wzy.yuka.yuka_lite.floatwindow.SelectWindow_Normal;
import com.wzy.yuka.yuka_lite.floatwindow.SubtitleWindow;
import com.wzy.yuka.yuka_lite.services.AudioService;
import com.wzy.yuka.yuka_lite.services.MediaProjectionService;
import com.wzy.yuka.yuka_lite.services.ScreenShotService_Auto;
import com.wzy.yuka.yuka_lite.services.ScreenShotService_Continue;
import com.wzy.yuka.yuka_lite.services.ScreenShotService_Single;
import com.wzy.yuka.yuka_lite.utils.TTS;
import com.wzy.yukafloatwindows.FloatWindowManager;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;

/**
 * Created by Ziyan on 2020/8/14.
 */
public class YukaFloatWindowManager extends FloatWindowManager {
    private static YukaFloatWindowManager manager = null;
    private final Intent mScreenShotAutoService;
    private final Intent mScreenShotContinueService;
    private final Intent mScreenShotSingleService;
    private final Intent mAudioService;
    private final Intent mMediaProjectionService;
    private int sum = 0;
    private String lastMode = "SWN_S";
    private ScreenShotService_Single.SingleBinder singleBinder;
    private ScreenShotService_Continue.ContinueBinder continueBinder;
    private ScreenShotService_Auto.AutoBinder autoBinder;
    private AudioService.AudioBinder audioBinder;
    private MediaProjectionService.MPBinder mpBinder;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service.getClass().equals(ScreenShotService_Single.SingleBinder.class)) {
                singleBinder = (ScreenShotService_Single.SingleBinder) service;
            } else if (service.getClass().equals(ScreenShotService_Continue.ContinueBinder.class)) {
                continueBinder = (ScreenShotService_Continue.ContinueBinder) service;
            } else if (service.getClass().equals(ScreenShotService_Auto.AutoBinder.class)) {
                autoBinder = (ScreenShotService_Auto.AutoBinder) service;
            } else if (service.getClass().equals(AudioService.AudioBinder.class)) {
                audioBinder = (AudioService.AudioBinder) service;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            singleBinder = null;
            continueBinder = null;
            autoBinder = null;
            audioBinder = null;
        }
    };

    private final ServiceConnection mp_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service.getClass().equals(MediaProjectionService.MPBinder.class)) {
                mpBinder = (MediaProjectionService.MPBinder) service;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mpBinder = null;
        }
    };

    private YukaFloatWindowManager(Application application) {
        super(application);
        this.mScreenShotAutoService = new Intent(applicationWeakReference.get(), ScreenShotService_Auto.class);
        this.mScreenShotContinueService = new Intent(applicationWeakReference.get(), ScreenShotService_Continue.class);
        this.mScreenShotSingleService = new Intent(applicationWeakReference.get(), ScreenShotService_Single.class);
        this.mAudioService = new Intent(applicationWeakReference.get(), AudioService.class);
        this.mMediaProjectionService = new Intent(applicationWeakReference.get(), MediaProjectionService.class);
        startService(mMediaProjectionService);
        applicationWeakReference.get().getApplicationContext()
                .bindService(mMediaProjectionService, mp_connection, Service.BIND_AUTO_CREATE);
        TTS.init(application);
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
        return mpBinder.getService().getData();
    }

    public void setData(Intent data) {
        mpBinder.getService().setData(data);
    }

    public MediaProjection getMediaProjection() throws FloatWindowManagerException {
        return mpBinder.getService().getMediaProjection();
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
                start_ScreenShotTrans_normal(((SelectWindow_Normal) mFloatWindows[0]).isContinue, 1000);
            } else if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
                start_ScreenShotTrans_auto();
            } else if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    start_RecordingTrans();
                }
            }
        } catch (FloatWindowManagerException e) {
            Toast.makeText(applicationWeakReference.get(), "需要点击重置（第三个按钮）先初始化悬浮窗哦！", Toast.LENGTH_SHORT).show();
        }
    }

    public void reset() {
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            mFloatWindows[0].reset();
            return;
        }
        remove_AllFloatWindow();
        mLocation = null;
        try {
            addFloatWindow(lastMode);
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }
    }

    public void start_ScreenShotTrans_normal(boolean ifContinue, int index) {
        if (judgeTypeOfFloatWindow(SelectWindow_Normal.class, 0)) {
            if (ifContinue) {
                //普通翻译-持续模式
                if (continueBinder == null) {
                    //服务还没启动
                    startService(mScreenShotContinueService);
                    applicationWeakReference.get().getApplicationContext()
                            .bindService(mScreenShotContinueService, connection, Service.BIND_AUTO_CREATE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> continueBinder.getService().startScreenshot(50), 100);
                } else {
                    //服务已经启动
                    continueBinder.getService().startScreenshot(50);
                }
            } else {
                //普通翻译-多悬浮窗模式
                if (singleBinder == null) {
                    //服务还没启动
                    startService(mScreenShotSingleService);
                    applicationWeakReference.get().getApplicationContext()
                            .bindService(mScreenShotSingleService, connection, Service.BIND_AUTO_CREATE);
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
        if (judgeTypeOfFloatWindow(SelectWindow_Auto.class, 0)) {
            //自动翻译-普通模式
            if (autoBinder == null) {
                //服务还没启动
                startService(mScreenShotAutoService);
                applicationWeakReference.get().getApplicationContext()
                        .bindService(mScreenShotAutoService, connection, Service.BIND_AUTO_CREATE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> autoBinder.getService().getScreenshot(mFloatWindows[0]), 100);
            } else {
                //服务已经启动
                autoBinder.getService().getScreenshot(mFloatWindows[0]);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void start_RecordingTrans() {
        if (judgeTypeOfFloatWindow(SubtitleWindow.class, 0)) {
            if (audioBinder == null) {
                //服务还没启动
                startService(mAudioService);
                applicationWeakReference.get().getApplicationContext()
                        .bindService(mAudioService, connection, Service.BIND_AUTO_CREATE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> audioBinder.getService().initRecord(), 100);
            } else {
                //服务已经启动
                audioBinder.getService().initRecord();
            }
            startService(mAudioService);
        }
    }

    public void stop_ScreenShotTrans_normal(boolean ifContinue) {
        if (ifContinue) {
            if (continueBinder != null) {
                continueBinder.getService().stopScreenshot();
                applicationWeakReference.get().getApplicationContext().unbindService(connection);
                applicationWeakReference.get().stopService(mScreenShotContinueService);
                continueBinder = null;
            }
        } else {
            if (singleBinder != null) {
                applicationWeakReference.get().getApplicationContext().unbindService(connection);
                applicationWeakReference.get().stopService(mScreenShotSingleService);
                singleBinder = null;
            }
        }
    }

    public void stop_ScreenShotTrans_auto() {
        if (autoBinder != null) {
            applicationWeakReference.get().getApplicationContext().unbindService(connection);
            applicationWeakReference.get().stopService(mScreenShotAutoService);
            autoBinder = null;
        }
    }

    public void stop_RecordingTrans() {
        if (audioBinder != null) {
            applicationWeakReference.get().getApplicationContext().unbindService(connection);
            applicationWeakReference.get().stopService(mAudioService);
            audioBinder = null;
        }
    }

    public void stop_MP_service() {
        if (mpBinder != null) {
            applicationWeakReference.get().getApplicationContext().unbindService(mp_connection);
            applicationWeakReference.get().stopService(mMediaProjectionService);
            mpBinder = null;
        }
        stop_ScreenShotTrans_normal(false);
        stop_ScreenShotTrans_normal(true);
        stop_ScreenShotTrans_auto();
        stop_RecordingTrans();
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
            if (intent.equals(mMediaProjectionService)) {
                applicationWeakReference.get().startForegroundService(intent);
            } else {
                applicationWeakReference.get().startService(intent);
            }

        }
    }
}
