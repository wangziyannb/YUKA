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
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.io.ResultOutput;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;

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
    private final String TAG = "SingleScreenShotService";
    private GlobalHandler globalHandler;
    private static boolean continuous = false;
    private Runnable runnable=()->{
        FloatWindowManager.hideAllFloatWindow();
        Screenshot screenshot = new Screenshot(this, FloatWindowManager.getLocation());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int delay = 800;
        int[] params = GetParams.AdvanceSettings();
        if (params[0] == 1) {
            //危险，性能不足会导致窗子不再出现（消失动画未完成）
            delay = 200;
        }
        boolean save = sharedPreferences.getBoolean("settings_debug_savePic", true);
        if (!save) {
            //时间足够长，点击退出按钮会导致本过程失效
            globalHandler.postDelayed(() -> screenshot.cleanImage(), 6000);
        }
        if(continuous){
            screenshot.getScreenshot(true, delay, FloatWindowManager.getData(), () -> {
                FloatWindowManager.showAllFloatWindow(true, 0);
                String fileName = screenshot.getFileNames()[0];
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
                        bundle.putBoolean("save", save);
                        Message message = Message.obtain();
                        message.what = 1;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }
                };
                HttpRequest.yuka(GetParams.Yuka(), fileName, callback);
            });
        } else {
            FloatWindowManager.showAllFloatWindow(false, 0);
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
        boolean save = bundle.getBoolean("save");
        Log.d(TAG, response);
        try {
            JSONObject resultJson = new JSONObject(response);
            String origin = resultJson.getString("origin");
            String result = resultJson.getString("results");
            double time = resultJson.getDouble("time");
            FloatWindowManager.showResultsIndex(origin, result, time, index);
            int[] params = GetParams.AdvanceSettings();
            if(params[1]==1&&continuous){
                startScreenshot(params[2]*1000);
            }
            if (save) {
                ResultOutput.appendResult(this.getExternalFilesDir("screenshot").getAbsolutePath() + "/imgList.txt", fileName, result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void errorProcess(Message message) {
        Bundle bundle = message.getData();
        int index = bundle.getInt("index");
        String error = bundle.getString("error");
        FloatWindowManager.showResultsIndex("yuka error", error, 0, index);
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
        super.onDestroy();
        globalHandler.removeCallbacks(runnable);
        globalHandler.removeCallbacks(null);
    }
}
