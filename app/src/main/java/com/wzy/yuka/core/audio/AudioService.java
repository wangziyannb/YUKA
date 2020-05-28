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
import com.wzy.yuka.tools.network.WebsocketRequest;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;

import java.nio.ByteBuffer;
import java.util.Timer;


/**
 * Created by Ziyan on 2020/5/26.
 */
public class AudioService extends Service {
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private boolean mWhetherRecord;
    private byte[] bytes;

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

    private WebsocketRequest websocketRequest;
    private Timer timer;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        initRecord();
        return Service.START_NOT_STICKY;
    }

    private void startRecord(AudioRecord record, int bufferSize) {
        mWhetherRecord = true;
        websocketRequest = new WebsocketRequest(
                (String) sharedPreferencesUtil.getParam("settings_trans_sync_o", "zh-CHS"),
                (String) sharedPreferencesUtil.getParam("settings_trans_sync_t", "en"));
        websocketRequest.start();
        new Thread(() -> {
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
        websocketRequest.close();
        mWhetherRecord = false;
        stopForeground(true);
        super.onDestroy();
    }
}
