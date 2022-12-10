package com.wzy.yuka.yuka_lite.services;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.SyncAudio;
import com.wzy.yuka.tools.network.WebsocketRequest;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.sender.YoudaoAudio;
import com.wzy.yuka.yuka_lite.utils.YoudaoAsrResolver;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.YukaUserManagerException;

import java.nio.ByteBuffer;

/**
 * Created by Ziyan on 2020/5/26.
 * Modified by Ziyan on 2021/1/28
 */
public class AudioService extends Service implements GlobalHandler.HandleMsgListener {
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private final GlobalHandler globalHandler = GlobalHandler.getInstance();
    private final AudioService.AudioBinder binder = new AudioBinder();
    private YoudaoAsrResolver resolver;
    private boolean mWhetherRecord;
    private byte[] bytes;
    private YukaFloatWindowManager floatWindowManager;
    private SyncAudio websocketRequest;

    @Override
    public void handleMsg(Message msg) {
        Bundle bundle;
        String json;
        switch (msg.what) {
            case 250:
                bundle = msg.getData();
                json = bundle.getString("syncMessage");
                resolver = new YoudaoAsrResolver(json);
                try {
                    floatWindowManager.get_FloatWindow(0).showResults(resolver.getContext(), resolver.getTranContent(), 0);
                } catch (FloatWindowManagerException e) {
                    e.printStackTrace();
                }
                break;
            case 251:
                Toast.makeText(this, "成功连接！现在开始语音同传", Toast.LENGTH_SHORT).show();
                break;
            case 252:
                bundle = msg.getData();
                json = bundle.getString("syncMessage");
                resolver = new YoudaoAsrResolver(json);
                Toast.makeText(this, "同步字幕结束\n" + "使用时间：" + resolver.getTotal_time(), Toast.LENGTH_SHORT).show();
                break;
            case 253:
                bundle = msg.getData();
                json = bundle.getString("syncMessage");
                resolver = new YoudaoAsrResolver(json);
                try {
                    switch (resolver.getErrorCode()) {
                        case "601":
                            floatWindowManager.get_FloatWindow(0)
                                    .showResults("用户名或密码错误", "用户名或密码错误", 0);
                            break;
                        case "602":
                            floatWindowManager.get_FloatWindow(0)
                                    .showResults("本机未登录", "本机未登录", 0);
                            break;
                        case "603":
                            floatWindowManager.get_FloatWindow(0)
                                    .showResults("同传时间已经用尽，请返回充值", "同传时间已经用尽，请返回充值", 0);
                            break;
                        default:
                            floatWindowManager.get_FloatWindow(0)
                                    .showResults("连接有道错误，错误代码：" + resolver.getErrorCode(), "连接有道错误，错误代码：" + resolver.getErrorCode(), 0);
                            break;
                    }
                } catch (FloatWindowManagerException e) {
                    e.printStackTrace();
                }
                floatWindowManager.stop_RecordingTrans();
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void initRecord() {
        try {
            floatWindowManager = YukaFloatWindowManager.getInstance();
            MediaProjectionManager mediaProjectionManager = getSystemService(MediaProjectionManager.class);
            MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, floatWindowManager.getData());

            AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                    .addMatchingUsage(AudioAttributes.USAGE_GAME)
                    .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                    .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                    .build();
            AudioFormat audioFormat = new AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(16000)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build();
            int size = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            AudioRecord record = new AudioRecord.Builder()
                    .setAudioPlaybackCaptureConfig(config)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(size)
                    .build();
            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                Toast.makeText(this, "未准备好录音，请关闭录音悬浮窗并重试", Toast.LENGTH_SHORT).show();
            } else {
                startRecord(record, size);
            }
        } catch (FloatWindowManagerException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        createNotificationChannel();
        return Service.START_NOT_STICKY;
    }

    private void startRecord(AudioRecord record, int bufferSize) {
        globalHandler.setHandleMsgListener(this);
        try {
            String o = "zh-CHS";
            String t = "en";
            String mode = "stream";
            String api = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_api, getResources().getStringArray(R.array.sender_api_value_sync)[0]);
            switch (api) {
                case "yuka_v1":
                    websocketRequest = new WebsocketRequest(YukaLite.getUser());
                    o = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_o, "zh-CHS");
                    t = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_t, "en");
                    mode = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_modes, "stream");
                    break;
                case "other":
                    //todo 添加更多实时语音识别可选服务
                    String APP_KEY = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_other_youdao_key, "");
                    String APP_SECRET = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_other_youdao_appsec, "");
                    websocketRequest = new YoudaoAudio(APP_KEY, APP_SECRET);
                    o = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_other_o, "zh-CHS");
                    t = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_other_t, "en");
                    mode = (String) sharedPreferencesUtil.getParam(SharedPreferenceCollection.sync_other_modes, "stream");
                    break;
            }

            websocketRequest.start(o, t, mode);
            new Thread(() -> {
                mWhetherRecord = true;
                record.startRecording();//开始录制
                bytes = new byte[bufferSize];
                while (mWhetherRecord) {
                    while (websocketRequest.isClosed()) {

                    }
                    if (record.read(bytes, 0, bytes.length) != -1) {
                        if (websocketRequest.isRunning()) {
                            websocketRequest.send(ByteBuffer.wrap(bytes));
                        }
                    }
                    //int i =;//读取流
                }
                Log.e("TAG", "run: 暂停录制");

                record.stop();//停止录制
                record.release();
            }).start();

        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = "Yuka";
        String description = "视频同传服务已启动";
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
        websocketRequest.close();
        mWhetherRecord = false;
        resolver = null;
//        stopForeground(true);
        super.onDestroy();
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }

    }

}