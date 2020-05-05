package com.wzy.yuka.core.screenshot;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.io.ResultOutput;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.ui.HomeFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ScreenShotService extends Service implements GlobalHandler.HandleMsgListener {
    private static final String TAG = ScreenShotService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private boolean continuous = false;
    private GlobalHandler globalHandler;
    private int interval;
    //todo 没能解决中止得问题，removeCallback无效
    private Runnable continuouslySS = () -> {
        Screenshot screenshot = new Screenshot(this, FloatWindowManager.getLocation());
        screenshot.getScreenshot(true, 300, HomeFragment.data, () -> {
            FloatWindowManager.showAllFloatWindow(true, 1000);
            Callback callback = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ScreenshotUtils", "Failure in" + 0);
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
                    bundle.putString("response", response.body().string());
                    Message message = Message.obtain();
                    message.what = 10;
                    message.setData(bundle);
                    globalHandler.sendMessage(message);
                }
            };
            HttpRequest.requestTowardsYukaServer(GetParams.getParamsForReq(this), screenshot.getFileNames()[0], callback);
        });
    };

    //todo 需要考虑无上限取词框对message的影响
    @Override
    public void handleMsg(Message msg) {
        TextView[] textViews = FloatWindowManager.getAllTextViews();
        Bundle bundle;
        String error;
        String response;
        String fileName;
        boolean save = true;
        switch (msg.what) {
            case 0:
                bundle = msg.getData();
                error = bundle.getString("error");
                textViews[0].setText(error);
                textViews[0].setTextColor(getResources().getColor(R.color.colorError));
                globalHandler.removeMessages(0);
                break;
            case 1:
                bundle = msg.getData();
                error = bundle.getString("error");
                textViews[1].setText(error);
                textViews[1].setTextColor(getResources().getColor(R.color.colorError));
                globalHandler.removeMessages(1);
                break;
            case 2:
                bundle = msg.getData();
                error = bundle.getString("error");
                textViews[2].setText(error);
                textViews[2].setTextColor(getResources().getColor(R.color.colorError));
                globalHandler.removeMessages(2);
                break;
            case 3:
                bundle = msg.getData();
                error = bundle.getString("error");
                textViews[3].setText(error);
                textViews[3].setTextColor(getResources().getColor(R.color.colorError));
                globalHandler.removeMessages(3);
                break;
            case 4:
                bundle = msg.getData();
                response = bundle.getString("response");
                fileName = bundle.getString("fileName");
                save = bundle.getBoolean("save");
                Log.d(TAG, response);
                globalHandler.removeMessages(4);
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String result = resultJson.getString("results");
                    textViews[0].setText(result);
                    textViews[0].setTextColor(Color.WHITE);
                    if (save) {
                        ResultOutput.appendResult(
                                this.getExternalFilesDir("screenshot").getAbsolutePath() + "/imgList.txt",
                                fileName, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                bundle = msg.getData();
                response = bundle.getString("response");
                fileName = bundle.getString("fileName");
                save = bundle.getBoolean("save");
                globalHandler.removeMessages(5);
                Log.d(TAG, response);
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String result = resultJson.getString("results");
                    textViews[1].setText(result);
                    textViews[1].setTextColor(Color.WHITE);
                    if (save) {
                        ResultOutput.appendResult(this.getExternalFilesDir("screenshot").getAbsolutePath() + "/imgList.txt", fileName, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 6:
                bundle = msg.getData();
                response = bundle.getString("response");
                fileName = bundle.getString("fileName");
                save = bundle.getBoolean("save");
                globalHandler.removeMessages(6);
                Log.d(TAG, response);
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String result = resultJson.getString("results");
                    textViews[2].setText(result);
                    textViews[2].setTextColor(Color.WHITE);
                    if (save) {
                        ResultOutput.appendResult(this.getExternalFilesDir("screenshot").getAbsolutePath() + "/imgList.txt", fileName, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 7:
                bundle = msg.getData();
                response = bundle.getString("response");
                fileName = bundle.getString("fileName");
                save = bundle.getBoolean("save");
                globalHandler.removeMessages(7);
                Log.d(TAG, response);
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String result = resultJson.getString("results");
                    textViews[3].setText(result);
                    textViews[3].setTextColor(Color.WHITE);
                    if (save) {
                        ResultOutput.appendResult(this.getExternalFilesDir("screenshot").getAbsolutePath() + "/imgList.txt", fileName, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 10:
                bundle = msg.getData();
                response = bundle.getString("response");
                globalHandler.removeMessages(10);
                Log.d(TAG, response);
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String result = resultJson.getString("results");
                    textViews[0].setText(result);
                    textViews[0].setTextColor(Color.WHITE);
                    if (continuous) {
                        getScreenshotContinuously();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        getScreenshot();
        return Service.START_NOT_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = "Yuka";
        String description = "服务已启动";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle(name).setContentText(description).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher_background)).setAutoCancel(true).build();
            startForeground(110, notification);
        } else {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            manager.createNotificationChannel(notificationChannel);
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle(name).setContentText(description).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher_background))
                    .setAutoCancel(true).build();
            startForeground(110, notification);
        }

    }

    private void getScreenshot() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Screenshot screenshot = new Screenshot(this, FloatWindowManager.getLocation());
        int delay = 800;
        boolean save = sharedPreferences.getBoolean("settings_debug_savePic", true);
        if (sharedPreferences.getBoolean("settings_fastMode", false)) {
            //危险，性能不足会导致窗子不再出现（消失动画未完成）
            delay = 200;
        }
        screenshot.getScreenshot(true, delay, HomeFragment.data, () -> {
            FloatWindowManager.showAllFloatWindow(true, 1000);
            Callback[] callbacks = new Callback[FloatWindowManager.getNumOfFloatWindows()];
            String[] fileNames = screenshot.getFileNames();
            for (int i = 0; i < callbacks.length; i++) {
                int a = i;
                String fileName = fileNames[i];
                callbacks[i] = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("ScreenshotUtils", "Failure in" + a);
                        Bundle bundle = new Bundle();
                        bundle.putString("error", e.toString());
                        Message message = Message.obtain();
                        message.what = a;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response.body().string());
                        bundle.putString("fileName", fileName);
                        bundle.putBoolean("save", save);
                        Message message = Message.obtain();
                        message.what = a + 4;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }
                };
            }
            HttpRequest.requestTowardsYukaServer(GetParams.getParamsForReq(this), screenshot.getFileNames(), callbacks);
        });
        //怎么中断这个过程？
        if (!sharedPreferences.getBoolean("settings_debug_savePic", true)) {
            globalHandler.postDelayed(() -> screenshot.cleanImage(), 6000);
        }
        if (sharedPreferences.getBoolean("settings_continuousMode", false)) {
            continuous = true;
            this.interval = sharedPreferences.getInt("settings_continuousMode_interval", 6);
            getScreenshotContinuously();
        }
    }

    private void getScreenshotContinuously() {
        if (continuous) {
            globalHandler.postDelayed(continuouslySS, interval * 1000);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        globalHandler.removeCallbacks(continuouslySS);
        continuous = false;
        stopForeground(true);
        super.onDestroy();
        globalHandler.removeCallbacks(null);
    }
}