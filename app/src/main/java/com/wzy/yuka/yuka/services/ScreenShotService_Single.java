package com.wzy.yuka.yuka.services;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.io.ResultOutput;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;
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
 * Created by Ziyan on 2020/4/30.
 */
public class ScreenShotService_Single extends Service implements GlobalHandler.HandleMsgListener {
    private final String TAG = "SingleScreenShotService";
    private GlobalHandler globalHandler;
    private FloatWindowManager floatWindowManager;

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
        Log.e(TAG, response);
        try {
            JSONObject resultJson = new JSONObject(response);
            String origin = resultJson.getString("origin");
            String result = resultJson.getString("results");
            double time = resultJson.getDouble("time");
            floatWindowManager.show_result_normal(origin, result, time, index);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            floatWindowManager = FloatWindowManager.getInstance();
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        int index = intent.getIntExtra("index", 1000);
        createNotificationChannel();
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        getScreenshot(index);
        return Service.START_NOT_STICKY;
    }

    /**
     * 截图+翻译
     * index为1000==intent没传值,为全体悬浮窗截图
     * 有实际意义值的时候，location必定为[1][4]
     *
     * @param index 悬浮窗的index，如果是1000则是全体悬浮窗
     */
    private void getScreenshot(int index) {
        Screenshot screenshot = null;
        try {
            screenshot = new Screenshot(this, floatWindowManager.getmLocation());
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //各项设置，包括快速模式、保存照片
        int[] params = GetParams.AdvanceSettings();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int delay = 800;
        boolean save = sharedPreferences.getBoolean("settings_debug_savePic", true);
        if (params[0] == 1) {
            //危险，性能不足会导致窗子不再出现（消失动画未完成）
            delay = 200;
        }
        Screenshot finalScreenshot = screenshot;
        if (!sharedPreferences.getBoolean("settings_debug_savePic", true)) {
            //时间足够长，点击退出按钮会导致本过程失效

            globalHandler.postDelayed(() -> finalScreenshot.cleanImage(), 6000);
        }

        screenshot.getScreenshot(false, delay, floatWindowManager.getData(), () -> {
            try {
                floatWindowManager.show_all(true, index);
            } catch (FloatWindowManagerException e) {
                e.printStackTrace();
            }
            Callback[] callbacks = new Callback[0];
            try {
                callbacks = new Callback[floatWindowManager.getmLocation().length];
            } catch (FloatWindowManagerException e) {
                e.printStackTrace();
            }
            String[] fileNames = finalScreenshot.getFullFileNames();
            String filePath = finalScreenshot.getFilePath();
            for (int i = 0; i < callbacks.length; i++) {
                int a = i;
                String fileName = fileNames[i];
                callbacks[i] = new Callback() {
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
                        if (index == 1000) {
                            bundle.putInt("index", a);
                        } else {
                            bundle.putInt("index", index);
                        }
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
            }
            HttpRequest.yuka(GetParams.Yuka(), finalScreenshot.getFullFileNames(), callbacks);
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = "Yuka";
        String description = "单次截屏服务已启动";
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
