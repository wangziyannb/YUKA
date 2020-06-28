package com.wzy.yuka.core.screenshot;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;

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
//        bundle.getString("fileName");
//        bundle.getString("filePath");
//        bundle.getBoolean("save");
        FloatWindowManager.showResultsIndex("", bundle.getString("response"), 0, bundle.getInt("index", 0));
    }

    private void errorProcess(Message message) {
        Bundle bundle = message.getData();
        int index = bundle.getInt("index");
        String error = bundle.getString("error");
        FloatWindowManager.showResultsIndex("yuka error", error, 0, index);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        int index = intent.getIntExtra("index", 0);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        getScreenshot(index);
        return Service.START_NOT_STICKY;
    }

    private void getScreenshot(int index) {
        Screenshot screenshot = new Screenshot(this, FloatWindowManager.getLocation());
        //各项设置，包括快速模式、保存照片
        int[] params = GetParams.AdvanceSettings();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int delay = 800;
        boolean save = sharedPreferences.getBoolean("settings_debug_savePic", true);
        if (params[0] == 1) {
            //危险，性能不足会导致窗子不再出现（消失动画未完成）
            delay = 200;
        }
        if (!sharedPreferences.getBoolean("settings_debug_savePic", true)) {
            //时间足够长，点击退出按钮会导致本过程失效
            globalHandler.postDelayed(() -> screenshot.cleanImage(), 6000);
        }

        screenshot.getScreenshot(false, delay, FloatWindowManager.getData(), () -> {
            FloatWindowManager.showAllFloatWindow(true, index);
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
            HttpRequest.yuka_advance(GetParams.Yuka(), fileName, callback);
        });
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
