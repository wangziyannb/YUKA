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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ziyan on 2020/5/21.
 */
public class AudioService extends Service {
    private final String TAG = "AudioService";
    private boolean mWhetherRecord;
    private File pcmFile;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: aaaaa");
        int index = intent.getIntExtra("index", 1000);
        createNotificationChannel();
        initRecord();
        return Service.START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initRecord() {
        Intent data = test.mData;
        MediaProjectionManager mediaProjectionManager = getSystemService(MediaProjectionManager.class);
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data);

        AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                .addMatchingUsage(AudioAttributes.USAGE_GAME)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                .build();
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(48000)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build();
        int size = AudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        AudioRecord record = new AudioRecord.Builder()
                .setAudioPlaybackCaptureConfig(config)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(size)
                .build();
        startRecord(record, size);
        record.startRecording();

    }

    private void startRecord(AudioRecord record, int bufferSize) {

        pcmFile = new File(getExternalFilesDir("audio"), "audioRecord.pcm");
        mWhetherRecord = true;
        new Thread(() -> {
            record.startRecording();//开始录制
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(pcmFile);
                byte[] bytes = new byte[bufferSize];
                while (mWhetherRecord) {
                    record.read(bytes, 0, bytes.length);//读取流
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();

                }
                Log.e("TAG", "run: 暂停录制");
                record.stop();//停止录制
                fileOutputStream.flush();
                fileOutputStream.close();
                addHeadData();//添加音频头部信息并且转成wav格式
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                record.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addHeadData() {
        pcmFile = new File(getExternalFilesDir("audio"), "audioRecord.pcm");
        File handlerWavFile = new File(getExternalFilesDir("audio"), "audioRecord_handler.wav");
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(48000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        pcmToWavUtil.pcmToWav(pcmFile.toString(), handlerWavFile.toString());
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
        stopForeground(true);
        super.onDestroy();
    }
}
