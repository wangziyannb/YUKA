package com.wzy.yuka.core.audio;

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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.youdao.ydasr.ASRParams;
import com.youdao.ydasr.AsrListener;
import com.youdao.ydasr.AsrManager;
import com.youdao.ydasr.asrengine.model.AsrResult;
import com.youdao.ydasr.asrengine.model.AsrResultCode;

import org.jetbrains.annotations.NotNull;


/**
 * Created by Ziyan on 2020/5/21.
 */
public class AudioService extends Service {
    private final String TAG = "AudioService";
    private boolean mWhetherRecord;
    private AsrListener mAsr = new AsrListener() {
        // 开始识别回调
        @Override
        public void onAsrStart() {
            Log.d(TAG, "onAsrStart: !!!");
        }

        // 重连后再次连接成功回调
        @Override
        public void onAsrRestart() {
            Log.d(TAG, "onAsrRestart: !!!");
        }

        // 结束识别回调
        @Override
        public void onAsrStop() {
            Log.d(TAG, "onAsrStop: !!!");
        }

        // 正在重连提示
        @Override
        public void onAsrReconnecting() {
            Log.d(TAG, "onAsrReconnecting: !!!");
        }

        // 错误回调
        @Override
        public void onAsrError(@NotNull AsrResultCode error) {

            Log.d(TAG, "onAsrError: " + error.toString());
        }

        // ASR结果回调 识别结果：result.getResult().getContext()
        // 翻译结果：result.getResult().getTranContent()
        @Override
        public void onAsrNext(@NotNull AsrResult result, boolean isPartial) {
//            Log.d(TAG, "onAsrNext: " + result.getResult().get);
//            Log.d(TAG, "onAsrNext: " + result.getResult().getTranContent());
//            Log.d(TAG, "onAsrNext: " + result.getResult().getContext());
//            Log.d(TAG, "onAsrNext: " + result.getResult().getTranContent());
//            Log.d(TAG, "onAsrNext: " + result.getResult().getContext());
//            Log.d(TAG, "onAsrNext: " + result.getResult().getTranContent());
            FloatWindowManager.showSubtitle(result.getResult().getContext(), result.getResult().getTranContent());
//            Message message=Message.obtain();
//            message.what=126;
//            globalHandler
//            globalHandler.handleMessage();
        }

        // 音量变化回调
        @Override
        public void onAsrVolumeChange(float volume) {
        }

        // 后端点静音回调
        @Override
        public void onAsrSilentEnd() {
        }

        // 前端点静音回调
        @Override
        public void onAsrSilentStart() {
        }

        // 连接上蓝牙麦克风提示
        @Override
        public void onBluetoothAudioConnected() {
        }

        // 蓝牙麦克风断开提示
        @Override
        public void onBluetoothAudioDisconnected() {
        }
    };

    private AsrManager asrManager;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        ASRParams asrParams = new ASRParams.Builder()
                .transPattern((String) sharedPreferencesUtil.getParam("translator_modeset", "stream"))
                .timeoutStart(5000L)
                .timeoutEnd(10000L)
                .sentenceTimeout(3000L)
                .connectTimeout(10000L)
                .isWaitServerDisconnect(true)
                .build();

        asrManager = AsrManager.getInstance(this, "5c44137c3b4c2e0f", asrParams, mAsr);
        asrManager.addWavHead = true;
        asrManager.setASRLanguage(
                (String) sharedPreferencesUtil.getParam("settings_trans_sync_o", "zh-CHS"),
                (String) sharedPreferencesUtil.getParam("settings_trans_sync_t", "en"));
        asrManager.startConnect();


        initRecord();
        return Service.START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initRecord() {
        MediaProjectionManager mediaProjectionManager = getSystemService(MediaProjectionManager.class);
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, FloatWindowManager.getData());

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
        startRecord(record, size);

    }

    private void startRecord(AudioRecord record, int bufferSize) {
        mWhetherRecord = true;
        new Thread(() -> {
            record.startRecording();//开始录制
            byte[] bytes = new byte[bufferSize];
            while (mWhetherRecord) {
                record.read(bytes, 0, bytes.length);//读取流
                asrManager.insertAudioBytes(bytes);
            }
            Log.e("TAG", "run: 暂停录制");
            asrManager.stop();
            asrManager.destroy();
            record.stop();//停止录制
        }).start();
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
        return null;
    }

    @Override
    public void onDestroy() {
        mWhetherRecord = false;
        asrManager.stop();
        asrManager.destroy();
        stopForeground(true);
        super.onDestroy();
    }


}
