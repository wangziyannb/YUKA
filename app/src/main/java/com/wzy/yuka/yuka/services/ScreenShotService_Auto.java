package com.wzy.yuka.yuka.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;
import com.wzy.yuka.yuka.utils.Screenshot;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class ScreenShotService_Auto extends Service implements GlobalHandler.HandleMsgListener {
    private final String TAG = "AutoScreenShotService";
    private GlobalHandler globalHandler;
    private FloatWindowManager floatWindowManager;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
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
        floatWindowManager.show_result_auto("", bundle.getString("response"));
    }

    private void errorProcess(Message message) {
        Bundle bundle = message.getData();
        int index = bundle.getInt("index");
        String error = bundle.getString("error");
        floatWindowManager.show_result_auto("yuka error", error);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        try {
            floatWindowManager = FloatWindowManager.getInstance();
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        getScreenshot();
        return Service.START_NOT_STICKY;
    }

    private void getScreenshot() {
        try {
            Screenshot screenshot = new Screenshot(this, floatWindowManager.getmLocation(0));
            //性能不足可能会导致窗子不再出现（消失动画未完成）
            int delay = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_fastMode, false) ? 200 : 800;
            if (!(Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.debug_savePic, true)) {
                //时间足够长，点击退出按钮会导致本过程失效
                globalHandler.postDelayed(screenshot::cleanImage, 6000);
            }
            screenshot.getScreenshot(false, delay, floatWindowManager.getData(), () -> {
                try {
                    floatWindowManager.show_all(true, 0);
                } catch (FloatWindowManagerException e) {
                    e.printStackTrace();
                }
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
                        Message message = Message.obtain();
                        message.what = 1;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }
                };
                HttpRequest.yuka_advance(GetParams.Yuka("SWA"), fileName, callback);
            });


        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //各项设置，包括快速模式、保存照片


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
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
