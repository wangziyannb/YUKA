package com.wzy.yuka.yuka_lite.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
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
import com.wzy.yuka.yuka_lite.sender.ConfigBuilder;
import com.wzy.yuka.yuka_lite.sender.Processor;
import com.wzy.yuka.yuka_lite.utils.Screenshot;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukafloatwindows.floatwindow.FloatWindow;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ziyan on 2020/4/30.
 * Modified by Ziyan on 2021/1/12.
 */
public class ScreenShotService_Single extends Service implements GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;
    private YukaFloatWindowManager floatWindowManager;
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private final SingleBinder binder = new SingleBinder();

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
        Log.e("SSSS", response);
        try {
            JSONObject resultJson = new JSONObject(response);
            String origin = resultJson.getString("origin");
            String result = resultJson.getString("results");
            double time = resultJson.getDouble("time");
            floatWindowManager.get_FloatWindow(index).showResults(origin, result, time);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            floatWindowManager = YukaFloatWindowManager.getInstance();
            createNotificationChannel();
            globalHandler = GlobalHandler.getInstance();
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return Service.START_NOT_STICKY;
    }


    public void getScreenshot(FloatWindow[] floatWindows) {
        globalHandler.setHandleMsgListener(this);
        int[][] location = new int[floatWindows.length][4];
        int[] index = new int[floatWindows.length];
        for (int i = 0; i < floatWindows.length; i++) {
            location[i] = floatWindows[i].location;
            index[i] = floatWindows[i].getIndex();
        }
        Screenshot screenshot = new Screenshot(this, location, index);
        //性能不足可能会导致窗子不再出现（消失动画未完成）
        int delay = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.action_fastMode, false) ? 200 : 800;
        boolean save = (Boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.debug_savePic, true);
        if (!save) {
            //时间足够长，点击退出按钮会导致本过程失效
            globalHandler.postDelayed(screenshot::cleanImage, 6000);
        }
        screenshot.getScreenshot(false, delay, floatWindowManager.getData(), () -> {
            for (FloatWindow floatWindow : floatWindows) {
                floatWindow.show();
                floatWindow.showResults("before response", "目标图片已发送，请等待...", 0);
            }
            sendScreenshot(screenshot, save);
        });
    }

    public void getScreenshot(FloatWindow floatWindow) {
        FloatWindow[] floatWindows = new FloatWindow[]{floatWindow};
        getScreenshot(floatWindows);
    }

    /**
     * 在这里修改传递Screenshot的位置
     *
     * @param screenshot
     * @param save
     */
    private void sendScreenshot(Screenshot screenshot, boolean save) {
        Resources resources = this.getResources();
        String api = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.detect_api, resources.getStringArray(R.array.sender_api_value_detect)[0]);
        Processor processor = new Processor(this, screenshot, ConfigBuilder.translate, save);
        processor.single_main(api);
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
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    public class SingleBinder extends Binder {
        public ScreenShotService_Single getService() {
            return ScreenShotService_Single.this;
        }

    }
}
