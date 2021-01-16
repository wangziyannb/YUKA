package com.wzy.yukafloatwindows;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;

import com.wzy.yukafloatwindows.floatball.FloatBall;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;
import com.wzy.yukafloatwindows.utils.LengthUtil;
import com.wzy.yukafloatwindows.utils.ScreenStatusService;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/30.
 */
public class FloatWindowManager {
    protected WeakReference<Application> applicationWeakReference;
    protected int[][] mLocation;
    protected FloatBall[] mFloatBalls;
    protected FloatWindow[] mFloatWindows;
    protected int sum = 0;
    private Intent mScreenStatusService;
    private ScreenStatusService.MyBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ScreenStatusService.MyBinder) service;
            binder.getService().setConfigurationListener(newConfig -> notifyConfigurationChanged(newConfig));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    public FloatWindowManager(Application application) {
        this.applicationWeakReference = new WeakReference<>(application);
        this.mScreenStatusService = new Intent(applicationWeakReference.get(), ScreenStatusService.class);
    }

    public int[][] getmLocation(int index) throws FloatWindowManagerException {
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

    public void add_FloatBall(FloatBall floatBall) {
        if (floatBall != null) {
            mFloatBalls = LengthUtil.appendIndex(mFloatBalls);
            floatBall.setIndex(mFloatBalls.length - 1);
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

    public FloatWindow get_FloatWindow(int index) throws FloatWindowManagerException {
        if (getNumOfFloatWindows() > index) {
            return mFloatWindows[index];
        }
        throw new FloatWindowManagerException("Index illegal");
    }

    public FloatBall get_FloatBall(int index) throws FloatWindowManagerException {
        if (getNumOfFloatBalls() > index) {
            return mFloatBalls[index];
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

    public void hide_all() throws FloatWindowManagerException {
        if (getNumOfFloatWindows() != 0) {
            for (FloatWindow floatWindows : mFloatWindows) {
                floatWindows.hide();
            }
        } else {
            throw new FloatWindowManagerException("No floatWindow initialized");
        }
    }

    public void hide_floatBall(int index) throws FloatWindowManagerException {
        get_FloatBall(index).hide();
    }


    public void show_all() throws FloatWindowManagerException {
        if (getNumOfFloatWindows() != 0) {
            for (FloatWindow floatWindows : mFloatWindows) {
                floatWindows.show();
            }
        } else {
            throw new FloatWindowManagerException("No floatWindow initialized");
        }
    }


    public void show_floatWindow(int index) throws FloatWindowManagerException {
        if (index == 1000) {
            show_all();
        } else {
            get_FloatWindow(index).show();
        }
    }

    public WeakReference<Application> getApplicationWeakReference() {
        return applicationWeakReference;
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
