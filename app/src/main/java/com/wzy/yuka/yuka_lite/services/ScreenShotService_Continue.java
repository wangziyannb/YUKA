package com.wzy.yuka.yuka_lite.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.io.ResultOutput;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.utils.Screenshot;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.config.Mode;
import com.wzy.yukalite.config.Model;
import com.wzy.yukalite.config.YukaConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/5/2.
 */
public class ScreenShotService_Continue extends Service implements GlobalHandler.HandleMsgListener {
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private final ScreenShotService_Continue.ContinueBinder binder = new ScreenShotService_Continue.ContinueBinder();
    private GlobalHandler globalHandler;
    private YukaFloatWindowManager floatWindowManager;
    private boolean continuous = false;
    private final Runnable runnable = () -> {
        try {
            globalHandler.setHandleMsgListener(this);
            floatWindowManager.hide_all();
            Screenshot screenshot = new Screenshot(this, floatWindowManager.getmLocation(0), new int[1]);

            int delay = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_fastMode, false) ? 200 : 800;
            boolean save = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.debug_savePic, true);
            if (!save) {
                //时间足够长，点击退出按钮会导致本过程失效
                globalHandler.postDelayed(screenshot::cleanImage, 6000);
            }
            if (continuous) {
                screenshot.getScreenshot(true, delay, floatWindowManager.getData(), () -> {
                    try {
                        FloatWindow floatWindow = floatWindowManager.get_FloatWindow(0);
                        floatWindow.show();
                        floatWindow.showResults("before response", "目标图片已发送，请等待...", 0);
                    } catch (FloatWindowManagerException e) {
                        e.printStackTrace();
                    }
                    sendScreenshot(screenshot, save);
                });
            } else {
                FloatWindow floatWindow = floatWindowManager.get_FloatWindow(0);
                floatWindow.show();
                floatWindow.showResults("before response", "目标图片已发送，请等待...", 0);
            }
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    };

    /**
     * 在这里修改传递Screenshot的位置
     *
     * @param screenshot
     * @param save
     */
    private void sendScreenshot(Screenshot screenshot, boolean save) {
        String fileName = screenshot.getFullFileNames()[0];
        String filePath = screenshot.getFilePath();
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 0;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putInt("index", 0);
                bundle.putString("response", response.body().string());
                bundle.putString("fileName", fileName);
                bundle.putString("filePath", filePath);
                bundle.putBoolean("save", save);
                Message message = Message.obtain();
                message.what = 1;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        };
        //todo
        //预置yukaConfig，说实话挺难用的
        YukaConfig yukaConfig = new YukaConfig.Builder().setMode(Mode.translate).setOCR(Model.baidu, false, false).build();
        File image = new File(fileName);
        YukaLite.request(yukaConfig, image, callback);
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 0:
                errorProcess(msg);
                break;
            case 1:
                responseProcess(msg);
                break;
        }
    }

    private void responseProcess(Message message) {
        Bundle bundle = message.getData();
        int index = bundle.getInt("index");
        String response = bundle.getString("response");
        String fileName = bundle.getString("fileName");
        String filePath = bundle.getString("filePath");
        boolean save = bundle.getBoolean("save");
        Log.d("SSSC", response);
        try {
            JSONObject resultJson = new JSONObject(response);
            String origin = resultJson.getString("origin");
            String result = resultJson.getString("results");
            double time = resultJson.getDouble("time");
            floatWindowManager.get_FloatWindow(index).showResults(origin, result, time);
            if (continuous) {
                startScreenshot((int) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_continuousModeInterval, 6) * 1000);
            }
            if (save) {
                ResultOutput.appendResult(filePath + "/imgList.txt", fileName, result);
            }
        } catch (JSONException | FloatWindowManagerException e) {
            e.printStackTrace();
        }

    }

    private void errorProcess(Message message) {
        Bundle bundle = message.getData();
        int index = bundle.getInt("index");
        String error = bundle.getString("error");
        try {
            floatWindowManager.get_FloatWindow(index).showResults("yuka error", error, 0);
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }
    }

    public void stopScreenshot() {
        continuous = false;
    }

    /**
     * 持续截图的第一个流程开始
     * 注意一定只有一个取词窗
     */
    public void startScreenshot(int interval) {
        globalHandler.postDelayed(runnable, interval);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        continuous = true;
        createNotificationChannel();
        globalHandler = GlobalHandler.getInstance();
        try {
            floatWindowManager = YukaFloatWindowManager.getInstance();
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = "Yuka";
        String description = "持续截屏服务已启动";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle(name).setContentText(description).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_radius).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher_radius)).setAutoCancel(true).build();
            startForeground(110, notification);
        } else {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            manager.createNotificationChannel(notificationChannel);
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle(name).setContentText(description).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_radius).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher_radius))
                    .setAutoCancel(true).build();
            startForeground(110, notification);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        globalHandler.removeCallbacks(runnable);
        super.onDestroy();
    }

    public class ContinueBinder extends Binder {
        public ScreenShotService_Continue getService() {
            return ScreenShotService_Continue.this;
        }

    }
}