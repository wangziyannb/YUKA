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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.io.ResultOutput;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;
import com.wzy.yuka.yuka.utils.Screenshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/5/2.
 */
public class ScreenShotService_Continue extends Service implements GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;
    private FloatWindowManager floatWindowManager;
    private static boolean continuous = false;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    private Runnable runnable = () -> {
        try {
            floatWindowManager.hide_all();
            Screenshot screenshot = new Screenshot(this, floatWindowManager.getmLocation(0));

            int delay = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_fastMode, false) ? 200 : 800;
            boolean save = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.debug_savePic, true);
            if (!save) {
                //时间足够长，点击退出按钮会导致本过程失效
                globalHandler.postDelayed(screenshot::cleanImage, 6000);
            }
            if (continuous) {
                screenshot.getScreenshot(true, delay, floatWindowManager.getData(), () -> {
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
                            bundle.putString("fileName", fileName);
                            bundle.putString("filePath", filePath);
                            bundle.putBoolean("save", save);
                            Message message = Message.obtain();
                            message.what = 1;
                            message.setData(bundle);
                            globalHandler.sendMessage(message);
                        }
                    };
                    HttpRequest.yuka(GetParams.Yuka("SWN_C"), fileName, callback);
                });
            } else {
                floatWindowManager.show_all(false, 0);
            }
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    };

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
            floatWindowManager.show_result_normal(origin, result, time, index);

            if (continuous) {
                startScreenshot((int) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_continuousModeInterval, 6) * 1000);
            }
            if (save) {
                ResultOutput.appendResult(filePath + "/imgList.txt", fileName, result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void errorProcess(Message message) {
        Bundle bundle = message.getData();
        int index = bundle.getInt("index");
        String error = bundle.getString("error");
        floatWindowManager.show_result_normal("yuka error", error, 0, index);
    }

    public static void stopScreenshot() {
        continuous = false;
    }

    /**
     * 持续截图的第一个流程开始
     * 注意一定只有一个取词窗
     */
    private void startScreenshot(int interval) {

        globalHandler.postDelayed(runnable, interval);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        continuous = true;
        createNotificationChannel();
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        try {
            floatWindowManager = FloatWindowManager.getInstance();
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }
        startScreenshot(50);
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
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        globalHandler.removeCallbacks(runnable);
        super.onDestroy();
    }
}