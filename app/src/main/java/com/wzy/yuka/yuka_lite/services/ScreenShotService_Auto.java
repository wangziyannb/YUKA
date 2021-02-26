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
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.sender.ConfigBuilder;
import com.wzy.yuka.yuka_lite.utils.Screenshot;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.config.YukaConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/6/6.
 * Modified by Ziyan on 2021/1/28.
 */
public class ScreenShotService_Auto extends Service implements GlobalHandler.HandleMsgListener {
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private final ScreenShotService_Auto.AutoBinder binder = new AutoBinder();
    private GlobalHandler globalHandler;
    private YukaFloatWindowManager floatWindowManager;

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
        Log.d("SSSA", response);
//        if (save) {
//            ResultOutput.appendResult(filePath + "/imgList.txt", fileName, result);
//        }
        try {
            floatWindowManager.get_FloatWindow(index).showResults("", response, 0);
        } catch (FloatWindowManagerException e) {
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        try {
            floatWindowManager = YukaFloatWindowManager.getInstance();
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        globalHandler = GlobalHandler.getInstance();
        return Service.START_NOT_STICKY;
    }

    public void getScreenshot(FloatWindow floatWindow) {
        Screenshot screenshot = new Screenshot(this, new int[][]{floatWindow.location}, new int[1]);
        //性能不足可能会导致窗子不再出现（消失动画未完成）
        int delay = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_fastMode, true) ? 200 : 800;
        boolean save = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.debug_savePic, true);
        if (!save) {
            //时间足够长，点击退出按钮会导致本过程失效
            globalHandler.postDelayed(screenshot::cleanImage, 6000);
        }
        screenshot.getScreenshot(false, delay, floatWindowManager.getData(), () -> {
            floatWindow.show();
            floatWindow.showResults("before response", "目标图片已发送，请等待...", 0);
            sendScreenshot(screenshot, save);
        });
        //各项设置，包括快速模式、保存照片
    }

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
        globalHandler.setHandleMsgListener(this);
        //todo
        //预置yukaConfig，说实话挺难用的
        YukaConfig yukaConfig = ConfigBuilder.yuka(this, ConfigBuilder.auto);
        File image = new File(fileName);
        YukaLite.request(yukaConfig, image, callback);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = "Yuka";
        String description = "自动识别翻译服务已启动";
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
        super.onDestroy();
    }

    public class AutoBinder extends Binder {
        public ScreenShotService_Auto getService() {
            return ScreenShotService_Auto.this;
        }

    }
}